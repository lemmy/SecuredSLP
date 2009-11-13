package tgdh.tree;

import java.util.ArrayList;
import java.util.List;

import tgdh.TgdhException;
import tgdh.TgdhUtil;


/**
 * This class implements the basic binary tree without cryptographic property  
 * that used for TGDH in specification  
 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class BasicTree {
	/**
	 * the root of tree
	 */
	protected Node root;

	/**
	 * the leafnode who has the view of the tree 
	 */	
	protected LeafNode owner;

	private BasicTree() {
		
	}

	/**
	 * Constructor who builds a tree with <t>root</t> as the only
	 * member of the tree
	 */
	public BasicTree(LeafNode root) {
	   this.root   = root;
	   root.clearRelation(); 
       root.setPosition(1);
	   owner = root; 
	}
	
	
	/**
	 * Constructor who builds a tree from the <t>tinfo</t>. Only the 
	 * Preorder and Postorder are supported.
	 * 
	 * @param tinfo TreeInfo which contains the information of the tree
	 */
	public BasicTree(TreeInfo tinfo) {
		if(tinfo == null) return;		
		int order = tinfo.getOrder();		
		if(order == TreeInfo.INORDER){
			throw new IllegalArgumentException("Inorder is not supported!");			
		}
		else if (order != TreeInfo.PREORDER && order != TreeInfo.POSTORDER){
		    throw new IllegalArgumentException("unknown order type " + tinfo.getOrder());		
		}		
				
		Node[] nodes = tinfo.nodes;	
		for(int i = 0; i < nodes.length; i++){
			nodes[i].clearRelation();
		}

		if(order == TreeInfo.PREORDER){
			this.root = nodes[0];			// the first node is root
			Node node = new Node();
			node = this.root;
				
			for(int i = 1; i < nodes.length;i++){
				Node tmpNode = nodes[i];				
				tmpNode.setParent(node);			
				if(tmpNode instanceof LeafNode){
					if(node.getLeft() == null)
						node.setLeft(tmpNode);
					else{
						node.setRight(tmpNode);
						while(node.getRight() != null && node != this.root)
							node = node.getParent();
					}
				}
				else{
					if(node.getLeft() == null)
						node.setLeft(tmpNode);
					else
						node.setRight(tmpNode);					
					node = tmpNode;
				}
			}
		}

		else{	// Postorder
			this.root = nodes[nodes.length - 1];// the last node is root
			Node node = new Node();
			node = this.root;
						
			for(int i = nodes.length - 2; i >= 0; i--){
				Node tmpNode = nodes[i];
				tmpNode.setParent(node);									
				
				if(tmpNode instanceof LeafNode){				
					if(node.getRight() == null)
						node.setRight(tmpNode);				
					else{
						node.setLeft(tmpNode);
						while(node.getLeft() != null && node!= this.root)
							node = (Node) node.getParent();
					}
				}
				else{
					if(node.getRight() == null)
						node.setRight(tmpNode);				
					else
						node.setLeft(tmpNode);
					node = tmpNode;
				}			
			}
		}
		setPositionFrom(root);
		
		owner = leafNode(tinfo.getOwnername());
	}

	/**
	 * Returns heigt of the tree whose root is <t>node</t>
	 * @param node Root of the tree
	 * @return heigt of the tree whose root is <t>node</t>
	 */
	private int height(Node node){
		if(node == null) return -1;
		
		int u = height(node.getLeft());
		int v = height(node.getRight());
		
		if(u > v)
			return u + 1;
		else
			return v + 1;
	}

	/**
	 * Returns the count of all nodes in the tree whose root is <t>node</t>.
	 * @param node Root of the tree
	 * @return  count of all nodes in the tree whose root is <t>node</t>.
	 */
	private int count(Node node){
		if(node == null) return 0;
		return count(node.getLeft()) + count(node.getRight()) + 1;
	}
	
	/**
	 * Deposits in <t>list</t> the leafnodes of the subtree whose root is <t>node</t>.
	 */	
	private void leafnodes(
	        Node node, 
	        List list)
	{
		if(node.getLeft() == null){
			list.add(node);
			return;
		}
		leafnodes(node.getLeft(), list);
		leafnodes(node.getRight(), list);		
	}
	
	/**
	 * Returns the count of all nodes in the tree
	 * @return the count of all nodes in the tree
	 */
	public int count(){
		return count(root);
	}
	
	/**
	 * Returns the count of all leafnodes in the tree
	 * @return the count of all leafnodes in the tree
	 */
	public int leafCount(){
		return leafNodes().length;
	}
	
	/**
	 * Returns the count of all inner nodes in the tree
	 * @return the count of all inner nodes in the tree
	 */
	public int innodeCount(){
		return count()-leafCount();
	}
	
	/**
	 * Sets the position of all members in the subtree whose root is <t>root</t>, 
	 * if root has no parent, sets its position to 1, else its position will not 
	 * changed.
	 * @param root root of the subtree
	 */
	private void setPositionFrom(Node root){
	    if(root == null) return;
	    
		// Is h root, then set the heigt = 1
		if(root.getParent() == null) 
			root.setPosition(1);
		
		if(root.getLeft() == null) return;
		
		int rootPosition = root.getPosition();
		root.getLeft() .setPosition (rootPosition * 2);
		root.getRight().setPosition (rootPosition * 2 + 1);
		setPositionFrom(root.getLeft());
		setPositionFrom(root.getRight());		
	}
	
	/**
	 * Finds the insert position in the subtree whose root is <t>root</t>.
	 * It is the shallowst and rightmost node of this subtree.
	 * @param root Root of the subtree
	 * @return insert position
	 */
	private Node insertPosition(Node root){
		if(root.getLeft() == null) return root;
				
		Node r = insertPosition(root.getRight());
		Node l = insertPosition(root.getLeft());

		if(l.getCoordinate().getLevel() < r.getCoordinate().getLevel())
			return l;
		else
			return r;
	}
	
	/**
	 * Finds the position to merge a tree to the subtree with root <t>root</t>. 
	 * It ist the rightmost shallowst node, which does not increase the height.
	 * If we have to increase the heigth of the key tree, it is the root.
	 * @param root               root of the subtree
	 * @param heightOfLowerTree  heigth of the lower tree
	 * @param maxLevel           the greater level of the to merged tree and the tree
	 *                           with root <t>root</t>. 
	 * @return merge             position 
	 */
	private Node mergePosition(
	        Node root, 
	        int heightOfLowerTree, 
	        int maxLevel)
	{
		int a = root.getCoordinate().getLevel() + height(root) + 1;
		int b = root.getCoordinate().getLevel() + heightOfLowerTree  + 1; 
		if(a <= maxLevel && b <= maxLevel)		
			return root;
		if(root.getLeft() == null) 
			return null;
			
		Node r = mergePosition(root.getRight(),  heightOfLowerTree, maxLevel);
		if(r == null)
			return mergePosition(root.getLeft(), heightOfLowerTree, maxLevel);
		return r;
	}
	
	/**
	 * Returns the height of the tree
	 * @return the height of this tree
	 */
	public int height(){
		return height(root);
	}
	
	/**
	 * Returns the root of the tree
	 * @return root of the tree
	 */
	public Node getRoot(){
		return root;
	}
	
	/**
	 * Returns node at position <t>position</t>
	 * @param  position position of the node
	 * @return node at position <t>position</t>
	 */
	public Node node(int position){
		if(position < 1) return null;
		
		String s = Integer.toBinaryString(position);
		//Node n = new Node();
		//n = this.root;
		Node n = this.root;
		
		for(int i = 1; i < s.length(); i++){
			n = (s.charAt(i) == '0' ? n.getLeft():n.getRight());
			if(n == null){
				return null; 
			}
		}
		
		return n;		
	}
	
	/**
	 * Returns node at coordinate <t>coordinate</t>
	 * @param  coordinate coordinate of the node
	 * @return node at coordinate <t>coordinate</t>
	 */
	public Node node(Coordinate coordinate){
		return node((1 << (coordinate.getLevel())) + coordinate.getOrdinal()); 
	}
	
	/**
	 * Sets the root
	 * @param root the root of the tree 
	 */
	public void setRoot(Node root){
		this.root = root; 
	}
	
	/**
	 * Returns the leftmost leafnode
	 * @return the leftmost leafnode
	 */
	public LeafNode leftMost(){
		Node aNode = root;
		
		while(!(aNode instanceof LeafNode))	aNode = aNode.getLeft();
		return (LeafNode) aNode;
	}
	
	/**
	 * Returns the rightmost leafnode
	 * @return the rightmost leafnode
	 */
	public LeafNode rightMost(){
		Node aNode = root;

		while(!(aNode instanceof LeafNode))	aNode = aNode.getRight();
		return (LeafNode) aNode;
	}

	/**
	 * Returns the leftmost leafnode of the subtree whose root is 
	 * <t>subtreeRoot</t>.
	 * @return the leftmost leafnode of the subtree whose root is 
	 *         <t>subtreeRoot</t>.
	 */	
	public LeafNode leftMostOfSubtree(Node subtreeRoot){
		Node aNode = subtreeRoot;
		
		while(!(aNode instanceof LeafNode))	aNode = aNode.getLeft();
		return (LeafNode) aNode;
	}
		
	/**
	 * Returns the rightmost leafnode of the subtree whose root is 
	 * <t>subtreeRoot</t>.
	 * @return the rightmost leafnode of the subtree whose root is 
	 *         <t>subtreeRoot</t>.
	 */	
	public LeafNode rightMostOfSubtree(Node subtreeRoot){
		Node aNode = subtreeRoot;

		while(!(aNode instanceof LeafNode))	aNode = aNode.getRight();
		return (LeafNode) aNode;
	}

	/**
	 * Returns the sibling of <t>aNode</t>
	 * @return the sibling of <t>aNode</t>
	 */
	protected Node sibling(Node aNode){
		Node parent = aNode.getParent();
		if(parent == null)
			return null;
	
		if(parent.getLeft() == aNode)
			return parent.getRight();
		else
			return parent.getLeft();
	}
	
	/**
	 * Returns the rightmost leafnode of the subtree whose root is the sibling 
	 * of <t>node</t>
	 * @return the rightmost leafnode of the subtree whose root is the sibling 
	 * of <t>node</t>
	 */
	public LeafNode sponsor(Node node){
		if(node.getParent() == null)	return null; 		// if the node is root			
		Node sibling;
		
		if(node.getParent().getLeft() == node)
			sibling = node.getParent().getRight();
		else
			sibling = node.getParent().getLeft();
		
		while(!(sibling instanceof LeafNode)) sibling = sibling.getRight();
		
		return (LeafNode) sibling;			
	}
	
	/**
	 * Returns a subtree whose root is <t>subtreeRoot</t>
	 * @return a subtree whose root is <t>subtreeRoot</t>
	 */
	public BasicTree subtree(Node subtreeRoot){
		BasicTree subtree = new BasicTree();
		subtree.root = subtreeRoot;
		return subtree;		
	}
	
	
	/**
	 * Join a new node in the tree.
	 * Example:<pre>
	 *          a                  a   
	 *       /   \     --\      /   \   
	 *      s     x    --/     s'    x 
	 *                       /   \     
	 *                      s    node 
	 *</pre>
	 * @param toJoin new Node
	 * @return return sponsor of this action
	 */
	public LeafNode join(Node toJoin)
			throws TgdhException
	{
		return join(toJoin, 1);
	}

	/**
	 * Join a new node in the subtree whose root has the position <t>position</t>,
	 * if no node has this position, join it in the tree.
	 * @param newNode new Node
	 * @param position join position
	 * @return sponsor of this action 
	 */
	public LeafNode join(
	        Node newNode, 
	        int position)
			throws TgdhException
	{	    
	    String name = ((LeafNode)newNode).getName(); 
		if(leafNode(name) != null){
			throw new TgdhException("name " + name + " exists in tree.");
		}
		
		// get the node at the position
		Node nodeAtPos = node(position);
		
		// if no node at the position, join in the tree.
		if(nodeAtPos == null){
			nodeAtPos = this.root;
		}
		
		// join in the subtree whose root is nodeAtPos.
		Node sponsor;
		BasicTree subtree = subtree(nodeAtPos);
		
		// if fully balaced, join to root
		if(subtree.leafCount() == (1 << subtree.height())){
			sponsor = (Node) subtree.rightMost();
			Node n = new Node();
			n.setParent(nodeAtPos.getParent());
			 
			// The new node becomes root			
			if(nodeAtPos.getParent() == null){
				this.root = n;
			}
			else if(nodeAtPos.getParent().getLeft() == nodeAtPos){
				nodeAtPos.getParent().setLeft(n);				
			}
			else{
				nodeAtPos.getParent().setRight(n);
			}
			
			n.setLeft(nodeAtPos);
			nodeAtPos.setParent(n);			
			n.setRight(newNode);
			newNode.setParent(n);
			
			n.setPosition(nodeAtPos.getPosition());
			setPositionFrom(n);
			
			return (LeafNode) sponsor;				
		}
		
		// find the sponsor
		sponsor = insertPosition(nodeAtPos);
			 
		Node p = sponsor.getParent();			
		Node n = new Node();
		int sponPos =sponsor.getPosition(); 
		n.setPosition(sponPos);
		n.setParent(sponsor.getParent());
		
		if(p.getLeft() == sponsor)
			p.setLeft(n);			
		else{
			p.setRight(n);
		}
						
		n.setLeft(sponsor);
		sponsor.setParent(n);
			
		n.setRight(newNode);
		newNode.setParent(n);
		
		// increment the height of the node and position
		sponsor.setPosition(sponPos * 2);	
		newNode.setPosition(sponPos * 2 + 1);
		
		return (LeafNode) sponsor;
	}
	
	
	/**
	 * Removes <t>node</t> from the tree
	 * @param node node who want to leave the tree
	 * @return sponsor of this action
	 */
	public LeafNode leave(Node node)
			throws TgdhException
	{
	    if(node(node.getPosition()) == null)
	        	throw new TgdhException("node not exists in tree");	    
		
		// if there is only a node in the ree
		if(node.getParent() == null){
			this.root = null;
			return null;
		}
		
		// find the sponsor
		Node sponsor = new Node();
		
		if(node.getParent().getLeft() == node)
			sponsor = node.getParent().getRight();
		else
			sponsor = node.getParent().getLeft();
		
		while(!(sponsor instanceof LeafNode)) sponsor = sponsor.getRight();
				
		Node parent = node.getParent();
		
		/*           root
		 *          /    \   			-\    subtree
		 *     subtree   node to leave  -/ 
		 */
		if(parent == this.root){					
			if(this.root.getLeft() == node)
				this.root = this.root.getRight();
			else 
				this.root = this.root.getLeft();				
			this.root.setParent(null);
			setPositionFrom(this.root);

			return (LeafNode) sponsor;
		}
			
		// the normal 
		Node sister = new Node();
	 	
		if(parent.getLeft() == node){
			sister = parent.getRight();
		}
		else{
			sister = parent.getLeft();
		}
		
		int posOfParent = parent.getParent().getPosition();
		if(parent.getParent().getLeft() == parent){ 
			parent.getParent().setLeft(sister);
			sister.setPosition(posOfParent * 2);
		}
		else{
			parent.getParent().setRight(sister);
			sister.setPosition (posOfParent * 2 + 1);
		}			
		sister.setParent(parent.getParent());
		setPositionFrom(sister);
					
		return (LeafNode) sponsor;
	}
	

	/**
	 * Removes the nodes <t>nodes2partition</t> from the tree
	 * @param nodes2partition the nodes who want to leave the tree
	 * @return sponsors of this action
	 * @throws TgdhException
	 */
	public LeafNode[] basicPartition(Node[] nodes2partition) 
			throws TgdhException
	{
		/*
		 * All leaving nodes are sorted by depth order. Starting at the deepest level, 
		 * each pair of leaving siblings is collapsed into its parent which is then 
		 * marked as leaving. This node is reinserted into the leaving nodes list.
		 * The above is repeated until all leaving nodes are processed, i.e., there 
		 * are no more leaving nodes that can be collapsed.
		 * The resulting tree has a number of leaving (leaf) nodes but every such node
		 * has a remaining sibling node. Now, for each leaving node we identify a 
		 * sponsor using the same criteria as described in Section 5.3.
		 */
		
		// Get the position of all nodes in the list
		int[] height = new int[nodes2partition.length];
		for(int i = 0; i < height.length; i++){
			height[i] = ((Node)nodes2partition[i]).getPosition();
		}
		
		// sort the position, beginn with the greatest 
		for(int i = 0; i < height.length - 1; i++){
			for(int j = i+1; j < height.length; j++){
				if(height[i] < height[j]){
					int tmp = height[i];
					height[i] = height[j];
					height[j] = tmp;
				}					
			}
		}		
		
		// collapse the sibling nodes and insert their parent to the list
		List list = new ArrayList();
		for(int i=0; i < height.length; i++){
			list.add(new Integer(height[i]));
		}		
		boolean changed = true;
		while(changed){
			changed = false;					
			int h1 = ((Integer)list.get(0)).intValue();

			for(int i = 0; i < list.size() - 1; i++){
				int h2 = ((Integer)list.get(i+1)).intValue();
				if(h1 % 2 == 1 & (h1 - h2) == 1){
					changed = true;
					list.remove(i+1);
					list.remove(i);
					h2 = h1 / 2; // parent height 
					
					int j;
					for(j = i; j < list.size(); j ++){
						h1 = ((Integer)list.get(j)).intValue();
						if(h2 > h1)	break;
					}
					list.add(j, new Integer(h2));

					break;
				}
				h1 = h2;
			}
			
		}
		
		// get the list of nodes to partition		
		List to_leaves = new ArrayList();    		
		for(int i = 0; i < list.size(); i++){
			to_leaves.add( node(
					( (Integer) list.get(i) ).	
					intValue()));					
		}
		
		// get the sponsors
		List sponsors = new ArrayList();		
		for(int i = 0; i < to_leaves.size(); i++){
			Node aSponsor = sponsor((Node)to_leaves.get(i));
			if(!to_leaves.contains(aSponsor)){
				sponsors.add(aSponsor);
			}
		}
		
		// delete the nodes in the list
		for(int i=0; i < to_leaves.size(); i++)
			leave((Node) to_leaves.get(i));
			
		// store the sponsors in an array
		LeafNode[] ln = new LeafNode[sponsors.size()];
		for(int i= 0; i < sponsors.size(); i++){
			ln[i] = (LeafNode) sponsors.get(i);
		}
					
		// return sponsors
		return ln;
	}


	/**
	 * Merge trees <t>th</t> and <t>tl</t>. 
	 * @param th higher tree 
	 * @param tl lower tree
	 * @param sponsors a list who deposits the sponsors 
	 * @return the merged tree of <t>th</t> and <t>tl</t>.
	 */
	private BasicTree merge(
	        BasicTree th, 
	        BasicTree tl, 
	        List sponsors)
	{ 			
		Node position = null;
		
		// Differenz der H&ouml;he
		int hd = th.height() - tl.height();
		
		// If the height of the smaller tree is less than the maximal height of all aubtrees
		if(hd > 1){
			position = mergePosition(th.root, tl.height(), 
					th.root.getCoordinate().getLevel() + th.height());					
			
			// if it can find a positon to merge
			if(position != null){ 
				Node parent = position.getParent();
				Node newnode = new Node();
				
				if(parent.getLeft() == position)
					parent.setLeft(newnode);
				else
					parent.setRight(newnode);
				newnode.setParent(parent);
			
				newnode.setLeft(position);
				position.setParent(newnode);
					
				newnode.setRight(tl.root);
				tl.root.setParent(newnode);
				
				// change the position
				setPositionFrom(parent);
				
				// add the sponsor
				Node sponsor = th.rightMostOfSubtree(position);
				if(!sponsors.contains(sponsor)){ 
					sponsors.add(sponsor);				
				}
				
				// finished, return the changed tree
				return th;
			}
		}	

		// If we have to increase the height of the key tree, we simply join to the root.
		// The tree th will be the left subtree of the new root, and the tree tl will be the
		// right subtree.
		
		// add the sponsor 
		Node sponsor = th.rightMostOfSubtree(th.root);
		if(!sponsors.contains(sponsor)){ 
			sponsors.add(sponsor);				
		}
		
		Node newnode = new Node();
		newnode.setLeft(th.root);
		th.root.setParent(newnode);
		newnode.setRight(tl.root);
		tl.root.setParent(newnode);
        
        // change the positon
		setPositionFrom(newnode);		
		th.root = newnode;		
		
		// finished, return the changed tree
		return th;
	}
	
	/**
	 * merges <t>trees</t>
	 * @param  trees    the to merged tree, it MUST contain own tree
	 * @return sponsors of this action
	 * @throws TgdhException
	 */
	public LeafNode[] merge(BasicTree[] trees) throws TgdhException{
		// add itself to the list;
		BasicTree[] trees2merge = new BasicTree[trees.length + 1];
		trees2merge[0] = this;		
		System.arraycopy(trees, 0, trees2merge, 1, trees.length);
				
		// get the names of all leafnodes
		String names[] = new String[0];
		for (int i = 0; i < trees2merge.length; i++) {
			String[] nodeNames= trees2merge[i].leafNodeNames();
		    String[] tmp = new String[names.length + nodeNames.length];
		    System.arraycopy(names, 0, tmp, 0, names.length);
		    System.arraycopy(nodeNames, 0, tmp, names.length, nodeNames.length);
		    names = tmp;
		}
		
		if(TgdhUtil.duplicate(names)){
			throw new TgdhException("name duplicate");
		}		
		
		// Order the trees from the greatest T1 to the lowst Tk
		// if multiple trees are of the same height, list the trees 
		// in hexicographic order of the first member in each tree
		// the order of i-te tree in list will be stored in index[i]
		// Bubble sort
		BasicTree swap = null;		
		for(int i = 0; i < trees2merge.length - 1; i++){			
			for(int j = trees2merge.length - 1; j > i; j--){
				BasicTree t1 = trees2merge[j];
				BasicTree t2 = trees2merge[j-1];
				if((t1.height() > t2.height()) ||
				   ( (t1.height() == t2.height()) && 
				      t1.leftMost().getName().compareTo(t2.leftMost().getName()) < 0) ){
					swap = trees2merge[j]; 
					trees2merge[j] = trees2merge[j-1];
					trees2merge[j-1] = swap;
				}
			}
		}			
		
		List sponsors = new ArrayList();		
		// get the greatest tree
		BasicTree tmpT = trees2merge[0];		
		for(int i = 1; i < trees2merge.length; i++){
			// merge the trees in list, store the sponsors
			tmpT = merge(tmpT, trees2merge[i], sponsors);
		}
		
		// get the merged tree
		this.root = tmpT.root;				
		// store the sponsors in an array
		LeafNode[] ln = new LeafNode[sponsors.size()];
		for(int i= 0; i < sponsors.size(); i++)
			ln[i] = (LeafNode) sponsors.get(i);
		
		// return the sponsors in an array
		return ln;
	}
	
	/**
	 * Merges <t>otherTree</t> into this tree. It succeed only if the both trees
	 * haven't nodes with same names.
	 * 
	 * @param otherTree another tree who wants to merge into this tree
	 */
	protected LeafNode merge(BasicTree otherTree) throws TgdhException{				
		// get the names of all leafnodes
		String names1[] = leafNodeNames();
		String names2[] = otherTree.leafNodeNames();
		String names[] = new String[names1.length + names2.length];
		System.arraycopy(names1, 0, names, 0, names1.length);
		System.arraycopy(names2, 0, names, names1.length, names2.length);				
		
		if(TgdhUtil.duplicate(names)){
			throw new TgdhException("name duplicate");
		}		
		
		BasicTree tmpTree = null;
		List tmpList      = new ArrayList();
				
		int height1 = this.height();
		int height2 = otherTree.height();		
		if(height1 > height2 ||(height1 == height2 && 
		      this.leftMost().name.compareTo(otherTree.leftMost().name) > 0))
		{
			tmpTree = merge(this, otherTree, tmpList);
		}
		else{
			tmpTree = merge(otherTree, this, tmpList);
		}
		
		// get the merged tree
		this.root = tmpTree.root;		
		return (LeafNode) tmpList.get(0);
	}
	


	
	public TreeInfo basicTreeInfo() {
	    return basicTreeInfo(TreeInfo.PREORDER);
	}

	/**
	 * Returns the information about this tree. The nodes is listed according the order <t>order</t>
	 * @param order Order of the nodes in the list, the supported order is preorder, inorder and postorder.
	 * @return <t>TreeInfo</t> of this tree
	 */
	public TreeInfo basicTreeInfo(int order) {			
		// set the order
		List list = new ArrayList();
		
		if(order == TreeInfo.PREORDER) {
			// Speichert die Nodes nach Ordnung <i>PREORDER</i> in <i>tin</i>
			preorder(root, list);
		}
		else if(order == TreeInfo.INORDER){
			// Speichert die Nodes nach Ordnung <i>INORDER</i> in <i>tin</i>
			inorder(root, list);
		}
		else if(order == TreeInfo.POSTORDER){
			// Speichert die Nodes nach Ordnung <i>POSTORDER</i> in <i>tin</i>
			postorder(root, list);
		}
		else{
		    throw new IllegalArgumentException("unknown order type " + order);
		}
		
		Node[] nodes = new Node[list.size()];
		for(int i = 0; i < list.size(); i++){
			nodes[i] = (Node) list.get(i);
		}
		
		// return the TreeInfo
		return new TreeInfo(nodes, order, owner.getName());				
	}

	/**
	 * Deposit according <t>PREORDER</t> all nodes in subtree whose root is <t>localRoot</t>. 
	 * @param localRoot root of the subtree
	 * @param list nodes list
	 */
	protected void preorder(
	        Node localRoot, 
	        List list)  
	{
	   	if(localRoot == null) return;
	   
	 	list.add(localRoot);	
	  	preorder(localRoot.getLeft(), list);
	  	preorder(localRoot.getRight(), list);
	}
	
	/**
	 * Deposit according <t>INORDER</t> all nodes in subtree whose root is <t>localRoot</t>. 
	 * @param localRoot root of the subtree
	 * @param list nodes list
	 */
	protected void inorder(
	        Node localRoot, 
	    	List list) 
	{
		if(localRoot == null) return;

		inorder( localRoot.getLeft(), list);
		list.add(localRoot);				
		inorder( localRoot.getRight(), list);
	}

	/**
	 * Deposit according <t>POSTORDER</t> all nodes in subtree whose root is <t>localRoot</t>. 
	 * @param localRoot root of the subtree
	 * @param list nodes list
	 */
	protected void postorder(
	        Node localRoot, 
	        List list) 
	{
		if(localRoot == null) return;
		
		postorder(localRoot.getLeft(),  list);		
		postorder(localRoot.getRight(), list);	
		list.add(localRoot);
	}
	
	/**
	 * returns the node who has this view of the tree
	 * @return the node who has this view of the tree
	 */
	public LeafNode getOwner(){
		return owner;
	}
	
	/**
	 * sets the node who has this view of the tree
	 * @param owner the node who has this view of the tree
	 */
	public void setOwner(LeafNode owner){
		this.owner = owner;
	}

	/**
	 * Returns the leafnode whose name is <t>name</t>
	 * @param name name of the leafnode
	 * @return the leafnode whose name is <t>name</t>
	 */
	public LeafNode leafNode(String name){
		LeafNode[] lnodes = leafNodes();
		
		for(int i = 0; i < lnodes.length; i++){
			if(name.equals(lnodes[i].getName()))
				return lnodes[i];			
		}
		
		return null;			
	}
	

	/**
	 * Returns all leafnodes
	 * @return alle leafnodes
	 */
	public LeafNode[] leafNodes(){
		List list = new ArrayList();
		leafnodes(this.root, list);
		
		LeafNode[] nodes = new LeafNode[list.size()];
		for(int i = 0; i < nodes.length; i++){
			nodes[i] = (LeafNode) list.get(i);
		}
		
		return nodes;
	}
	

	/**
	 * Returns the names of all leafnodes
	 * @return the names of all leafnodes
	 */
	public String[] leafNodeNames(){
		LeafNode[] nodes = leafNodes();
		String[] names = new String[nodes.length];		
		for (int i = 0; i < names.length; i++) {
			names[i] = nodes[i].getName();			
		}
		
		return names;
	}
	

	public String toString(){
		TreeInfo tin =basicTreeInfo(TreeInfo.PREORDER);
		StringBuffer s = new StringBuffer("Preorder: ");
		
		int i = 0;
		for(; i<tin.nodes.length - 1; i++){
			s.append( ((Node)tin.nodes[i]) . toString());
			s.append(", ");
		}		
		s.append( ((Node)tin.nodes[i]) . toString());
		
		return s.toString();
	}
	
}
