package tgdh.tree;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;

import tgdh.TgdhException;
import tgdh.TgdhUtil;
import tgdh.crypto.TgdhPrivateKey;
import tgdh.crypto.TgdhPublicKey;


/**
 * This class implements the binary tree with cryptographic property  
 * that used for TGDH in specification  
 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class Tree extends BasicTree {
	
	private boolean updated;
	
	
	/**
	 * The key parameters, it contains the g, p and q.   
	 */
	private DSAParams keyParams;
	
	/**
	 * Constructor who builds a tree with <t>root</t> as the only
	 * member of the tree
	 */
	public Tree(LeafNode root){	    
	    super(root); 
	}
	
	/**
	 * Constructor who builds a tree from the <t>tinfo</t>. Only the 
	 * Preorder and Postorder are supported.
	 * 
	 * @param tinfo TreeInfo which contains the information of the tree, 
	 *              it MUST contain the key parameter.
	 */
	public Tree(TreeInfo tinfo) {
	    super(tinfo);
		
		keyParams 	   = tinfo.getKeyParams();
	}

	/**
	 * Joins a node <t>node</t> in the tree whose root is at position <t>position</t>.
	 * Removes all keys and bkeys related th the sponsor to the root node,
	 * The sponsor should additionlly generates new share and computes all [key, bkey] 
	 * pairs on the key path.
	 * @param  node new Node 
	 * @param  position the root's position of the subtree
	 * @return null if the owner is not the sponsor, else the tree inforamtion 
	 *         of the changed tree                    
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidAlgorithmParameterException
	 */
	public TreeInfo join(LeafNode node, int position) 
		throws TgdhException
	{	    
		LeafNode sponsor =  super.join(node, position);		
		removeKeys(sponsor);

		if(sponsor != owner){	
			return null;
		}
	
		// if it is sponsor, generate new share a computes all
		// [key, bkey]-pairs on the key path
		try {
            // generate new share
            genOwnerKeyPair();
        } catch (Exception e) {
            throw new TgdhException(e);
        }						
            
		computeKeys(owner.getParent());			
		return treeInfo(TreeInfo.PREORDER);
	}


	/**
	 * the same as join(node, 1), @see #join(LeafNode, int).
	 */
	public TreeInfo join(LeafNode node) throws TgdhException{		
		return join(node, 1);		
	}

	
	/**
	 * Replaces the bkeys of subtree with root <t>dest</t> from subtree with 
	 * root <t>src</t>, if the bkey of the node in destination tree if null.
	 * @param dest Node
	 * @param src Node
	 */
	private void updatePublics(Node src, Node dest)
		throws TgdhException
	{
		if(dest == null){
		    if(src == null) return;
		    else 
		        throw new TgdhException("the source and destination are not the same.");
		}
		
		if(dest.publicKey == null && src.publicKey != null){
			updated = true;
			dest.publicKey = src.publicKey; 
		}	   
		
		updatePublics(src.getLeft(),  dest.getLeft());
		updatePublics(src.getRight(), dest.getRight());
	}
	
	/**
	 * Replaced the bkeys of nodes in tree, if bkeys are null, with bkeys in <t>updatedTree</t> 
	 * of the same nodes.   
     *
	 * @param   updatedTree the tree contains perhaps new bkeys of the nodes in tree 
     *
	 * @throws TgdhException
	 */
	public void updatePublics(Tree updatedTree) 
		throws TgdhException
	{
		// if the tree structur not equal, return
		if(!updatedTree.toString().equals(this.toString())){
			throw new TgdhException("updatedTree not same as the tree");
		}
		
		// will be changed to true, if at least one bkey updated
		updated = false;			
		updatePublics(updatedTree.root, root);		
	}
	
	
	/**
	 * Deletes the node <t>node</t> from the tree. Removes all [key, bkey] pairs
	 * from the leaf node related to the sponsor to the root node. Sponsor additionly
	 * generates new share and computes all [key, bkey] pairs on the key path. 
	 * 
	 * @param node node which wants to leave the tree
	 * @return TreeInfo of the changed tree for Sponsor, null for others
	 * @throws TgdhException
	 */
	public TreeInfo leave(LeafNode node) throws TgdhException{	
		LeafNode sponsor = super.leave(node);		
		if(sponsor == null){
		    return null;		
		}
		
		// removes all keys an dbkeys from the leaf node related 
		// to the sponsor to the root node
		removeKeys(sponsor);
		
		if(sponsor != owner){ 		// not sponsor
			return null;	
		}
		
		// the sponsor additionaly generate new share and compute all 
		// [key, bkey] paris on the key path
		try {
			genOwnerKeyPair();
		} catch (Exception e) {
			throw new TgdhException(e.getCause());
		}
		
		computeKeys(sponsor.getParent());					
		return treeInfo(TreeInfo.PREORDER);		
	}

	/**
	 * Deletes the leaving member nodes<t>nodes</t> and their parent nodes 
	 * from the tree, removes all keys and bkeys from the leaf node related 
	 * to the sponsor to the root node. If it is sponsor, it computes all 
	 * [key, bkey] pairs in the key path until it can proceed. The rightmost
	 * sponsor must generate a new share before the computing.
	 * @param nodes the leaving member nodes
	 * @return TreeInfo of the changed tree for the sponsor, else null.
	 */	
	public TreeInfo partition(Node[] nodes) throws TgdhException{
		LeafNode[] sponsors = super.basicPartition(nodes);
		
		//find the right most sponsor, it is in sponsors[rmIndex]
		String[] positionBS = new String[sponsors.length];
		for(int i = 0; i < positionBS.length; i++){
			positionBS[i] = Integer.toBinaryString(sponsors[i].getPosition());		
		}
		int rmIndex = 0;
		for(int i = 1; i < positionBS.length; i++){
			if(positionBS[i].compareTo(positionBS[rmIndex]) > 0){
				rmIndex = i;
			}
		}
				
		// remove all keys and bkeys from the leaf node related to the
		// sponsor to the root node
		for(int i = 0; i < sponsors.length; i++){
			if(sponsors[i] != owner){
				removeKeys(sponsors[i]);
			} else{
				removeKeys(owner.getParent());
			}			
		}
		
		// not sponsor, return	
		if(!TgdhUtil.contains(sponsors, owner)){
			return null;
		}
				
		// if the owner is the shallowest rightmost sponsor, generates 
		// new share
		if(owner == sponsors[rmIndex]){		    
		    try {
				genOwnerKeyPair();						
			} catch (Exception e) {
				throw new TgdhException(e.getCause());
			}
		}					
		//computers all [key, bkey] pairs on the key path until it can proceed
		computeKeys(owner.getParent());
		return treeInfo(TreeInfo.PREORDER);
	}
	
	/**
	 * merge the trees into this tree. For non-sponsor, it removes all keys and 
	 * bkeys from the leaf node related to the sponsor to the root node.
	 * For the sponsor, it removes all keys and bkeys from its parent to the root
	 * node, and computes the [key, bkey] pairs until it can proceed.
	 * Because of the complexity and instability of this function, it will be NOT 
	 * supported.  
	 * @param trees2merge Trees to merge
	 * @return TreeInfo of changed tree for sponsor, else null
	 * @throws TgdhException
	 */
	public TreeInfo merge(Tree[] trees2merge) throws TgdhException{
	 	throw new UnsupportedOperationException("merge more than 2 trees not supported");
	 /*   lastAction = TgdhMessage.MERGE;
	    
	    LeafNode[] sponsors = super.merge(trees2merge);
			
		// remove all keys and bkeys from the leaf node related to the 
		// sponsor to the root node
		LeafNode owner = owner();
		for(int i = 0; i < sponsors.length; i++){
			if(owner != sponsors[i]){
				sponsors[i].backupKeys(true);				
				removeKeysFrom(sponsors[i].getParent());
			}			 		
		}

		// not sponsor, return
		if(!TgdhUtil.contains(sponsors, owner)){
		    expectedAction = TgdhMessage.UPDATE;
		    return null;
		}

	    // If it is sponsor, computes all[key, bkey] pairs on the key path 
		// until it can proceed
		computeKeysFrom( owner.getParent());
		
		if(!allPublicKeys()){
		    expectedAction = TgdhMessage.UPDATE;
		}
		
		return treeInfo(TreeInfo.PREORDER);
		*/						
	}
	
	/**
	 * merge the tree into this tree. For non-sponsor, it removes all keys and 
	 * bkeys from the leaf node related to the sponsor to the root node.
	 * For the sponsor, it removes all keys and bkeys from its parent to the root
	 * node, and computes the [key, bkey] pairs until it can proceed.
	 * @param  anotherTree tree to merge
	 * @return TreeInfo of changed tree for sponsor, else null
	 * @throws TgdhException
	 */
	public TreeInfo merge(Tree anotherTree) throws TgdhException {
		LeafNode sponsor = super.merge(anotherTree);				
		// remove all keys and bkeys from the leaf node related to the 
		// sponsor to the root node
		removeKeys(sponsor);
		if(sponsor != owner){
			return null;	
		}
		
		genOwnerKeyPair();
		computeKeys(sponsor.getParent());
    	return treeInfo(TreeInfo.PREORDER);						
	}
	

	/**
	 * The same as treeInfo(PREORDER)
	 */	
	public TreeInfo treeInfo() {
	    return treeInfo(TreeInfo.PREORDER);
	}
	
	/**
	 * Returns the TreeInfo which contains the key paramter and the nodes
	 * according order <t>order</t>
	 * @param order Ordnung
	 * @return <code>TreeInfo</code> nach der Ordnung <i>order</i>
	 */
	public TreeInfo treeInfo(int order) {
	    TreeInfo tinfo = (TreeInfo) super.basicTreeInfo(order);		
		tinfo.keyParams 	= keyParams;
		return tinfo;				
	}
	
	/**
	 * Remove all keys and bkeys from <t>start</t> to root
	 */
	public void removeKeys(Node start){
		Node aNode = start;
		
		while(start != null){
			start.privateKey = null;
			start.publicKey = null;
			start = start.getParent();
		}
	}
	
	
	/**
	 * Generate new share for node <t>owner</t>
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws Exception
	 */	
	public void genOwnerKeyPair() 
		throws 	TgdhException 
	{		    
		try {
			KeyPairGenerator pairgen = KeyPairGenerator.getInstance("DSA");
			DSAParameterSpec paramSpec = new DSAParameterSpec(
				keyParams.getP(), keyParams.getQ(), keyParams.getG());
			pairgen.initialize(paramSpec);
			KeyPair keypair = pairgen.generateKeyPair();
    		
			DSAPrivateKey privateKey = (DSAPrivateKey) keypair.getPrivate();
			DSAPublicKey  publicKey  = (DSAPublicKey)  keypair.getPublic();    		
    		
			owner.privateKey = new TgdhPrivateKey(keyParams, privateKey.getX());
			owner.publicKey  = new TgdhPublicKey (keyParams, publicKey .getY());		
		} catch (NoSuchAlgorithmException e) {
			throw new TgdhException(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			throw new TgdhException(e.getMessage());
		}
	}
    

	
	/**
     * Computes [key, bkey] pairs in from node <t>start</t> until it can proceed.
	 */
	public void computeKeys(Node start){
		Node aNode = start;		
		boolean canProceed = true;
		
		while(aNode != null && canProceed){
			canProceed = aNode.computeKeys();
			aNode = aNode.getParent();
		}		
	}
	
	/**
	 * Returns the symmetric group key with <t>keyBits</t>-bit
	 * @param  keyBits bitnumber of the key, between 1 and 512
	 * @return the symmetric key with <t>keyBits</t>-bit
	 */
	public byte[] getSymmetricKey(int keyBits){
	    if(keyBits <1 || keyBits > 512) 
	    	throw new IllegalArgumentException("bit length not positiv");
		
		if(root.privateKey == null){
			computeKeys(owner.getParent());	
		}		
		
	    try {
		    MessageDigest md = null;
			// get an instance of MessageDigest
		    
		    if(keyBits <= 160){
				md = MessageDigest.getInstance("SHA-1");
		    }
		    else if(keyBits <= 256){
		        md = MessageDigest.getInstance("SHA-256");
		    }
		    else{
		        md = MessageDigest.getInstance("SHA-512");
		    }
		
		    // get the key of root 
			BigInteger rootKey = root.getPrivate().getX();
			//get the hashvaluerivate
			byte[] digest = md.digest(rootKey.toByteArray());
			int keyBytes = (keyBits + 7) / 8;
			byte[] output = new byte[keyBytes];
			System.arraycopy(digest, 0, output, 0, output.length);
			output[0]  &= (0xff >>> (8 * keyBytes - keyBits));			
			return output;			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Returns the asymmetric group key
	 * @return the asymmetric group key
	 */
	public DSAPrivateKey getAsymmetricKey(){
		if(root.publicKey == null) {
			return null;
		}
		
		if(root.privateKey == null) {
			computeKeys(owner.getParent());
		}
		
	    return root.privateKey;
	}

	/**
	 * Returns key parameter of this group
	 * @return key parameter of this group
	 */
	public DSAParams getKeyParams(){
		return keyParams;
	}
	
	/**
	 * Sets key parameter of group
	 * @param keyParams key parameter of group
	 */
	public void setKeyParams(DSAParams keyParams){
		this.keyParams = keyParams;
	}
	
}
