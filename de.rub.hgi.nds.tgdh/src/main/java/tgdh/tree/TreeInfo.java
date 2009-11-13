package tgdh.tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.interfaces.DSAParams;
import java.util.ArrayList;
import java.util.List;

import tgdh.TgdhException;
import tgdh.TgdhUtil;

 /**
  * This class contains the inforamtion to reconstruct a tree
  * Sample:
  * <pre>
  * Tree
  *                        1
  *                    /       \
  *                   11        12
  *                 /    \    /    \
  *                M1    M2  M3    M4
  * 
  * PreOrder:  {1, 11, M1, M2, 12, M3, M4}
  * Inorder:   {M1, 11, M2, 1, M3, 12, M4}
  * PostOrder: {M1, M2, 11, M3, M4, 12, 1}
  * </pre>

 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
  */

public class TreeInfo implements Serializable{
	/**
	 * Order <i>preorder</i> 
	 */
	public final static int PREORDER  = 1;
	/**
	 * Order <i>inorder</i> 
	 */
	public final static int INORDER   = 2;
	/**
	 * Order <i>postorder</i> 
	 */
	public final static int POSTORDER = 3;

	/**
	 * Name of the node <t>owner</t> in the corresponging tree.
	 */		
	protected String ownername;
	
	/**
	 * Order of the nodes in tree
	 */
	protected int order;
	
	/** 
	 * Nodes in tree
	 */
	protected Node[] nodes;

	/**
	 * key parameter of the group (tree)
	 */	   
	protected DSAParams keyParams;

	/**
	 * Constructor with nodes, order and ownername as parameters. 
	 * @param nodes      nodes according <t>order</t>
	 * @param order      order, the supported value is PREORDER, INORDER and POSTORDER
	 * @param ownername  ownername of the tree
	 * @throws TgdhException
	 */	 
	public TreeInfo(
	    Node[] nodes, 
		int    order,
		String ownername) 
	{		
		if(order == PREORDER || order == INORDER || order == POSTORDER)
			this.order = order;
		else{
			throw new IllegalArgumentException(
				"Order is not PREORDER, INORDER or POSTORDER");
		}

		for(int i = 0; i < nodes.length; i++){
			if (nodes[i] == null)
				throw new IllegalArgumentException("nodes contains null");
		}
		
		if(checkName(nodes)){
			this.nodes = nodes;		    
		}else{
		    throw new IllegalArgumentException("names of leafnodes duplicate");
		}
		
		this.ownername = ownername;
	}
		
	/**
	 * Constructor with nodes, order, ownername and keyParams as parameters. 
	 * @param nodes      nodes according <t>order</t>
	 * @param order      order, the supported value is PREORDER, INORDER and POSTORDER
	 * @param ownername  ownername of the tree
	 * @param keyParams  key parameter of the group (tree)
	 * @throws TgdhException
	 */	 
	public TreeInfo(
	    Node[] nodes, 
		int order, 
		String ownername,
		DSAParams keyParams) 
		throws TgdhException
	{		
	    this(nodes, order, ownername);
	    
		if((keyParams instanceof Serializable)){
		    this.keyParams = keyParams;
		}
		else{
		    throw new IllegalArgumentException(
		            keyParams.getClass().getName() + " isn't serializable");
		}
	}	
	
	
	/**
	 * Returns the order.
	 * @return the order.
	 */
	public int getOrder(){
		return this.order;
	}

    /**
     * @return the ownername.
     */
    public String getOwnername() {
    	return ownername;
    }
    
	/**
	 * Returns the nodes
	 * @return the nodes
	 */
	public Node[] getNodes(){
		return this.nodes;	
	}
	

	
	/**
	 * Returns the LeafNode with name <t>name</t> if it exists, else null. 
	 * @param name name of the leafnode
	 * @return the leafnode with name <t>name</t> if it exists, else null.
	 */
	public LeafNode getLeafNode(String name){
		for (int i = 0; i < nodes.length; i++) {
			if(nodes[i] instanceof LeafNode){
				if(((LeafNode) nodes[i]).getName()  .  equals(name)){
					return (LeafNode) nodes[i];
				}
			}
		}
		
		return null;
	}	
	
	
    /**
     * Returns key parameter
     * @return the keyParams.
     */
    public DSAParams getKeyParams() {
        return keyParams;
    }
   
    /**
     * For signature, writes ownername, order, nodes and keyParams to the
     * returned ByteArray 
     */
	public byte[] toByteArray() {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    try{
            bout.write(ownername.getBytes());
            bout.write(order);
            for(int i = 0; i < nodes.length; i++){
                bout.write(nodes[i].toByteArray());                
            }
            
            bout.write(keyParams.getP().toByteArray());
            bout.write(keyParams.getQ().toByteArray());
            bout.write(keyParams.getG().toByteArray());  
        }catch (IOException e){
	        return null;
	    }
        
        return bout.toByteArray();
	}
	

	/**
	 * order: [node 1], [node 2], ..., [node n]
	 */
	public String toString(){
		StringBuffer sbuffer = new StringBuffer();
		switch (order){
			case PREORDER:
				sbuffer.append("Preorder: ");
				break;				
			case INORDER:
				sbuffer.append("Inorder: ");
				break;				
			case POSTORDER:
				sbuffer.append("Postorder: ");
				break;				
			default:
				return 	"eeek";			
		}
		
		for(int i = 0; i < nodes.length - 1; i++){
			sbuffer.append(nodes[i].toString());
			sbuffer.append(", "); 
		}		
		sbuffer.append(nodes[nodes.length - 1].toString());
		
		return sbuffer.toString();
	}
	
	
	/**
	 * Check the names of <t>nodes</t>
	 * @return false if it exists name duplicate, true else.
	 */
	private static boolean checkName(Node[] nodes) 
	{
		if(nodes == null) return true;
		
		// get the names of the leafnodes
		List names = new ArrayList();
		for(int i=0; i < nodes.length; i++){
			if(nodes[i] instanceof LeafNode)
				names.add(((LeafNode) nodes[i]).getName());
		}

		return !TgdhUtil.duplicate(names);
	}
	
}
