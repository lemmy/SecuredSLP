/*
 * Created on 2004-9-16
 *
 */
package tgdh.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import tgdh.tree.LeafNode;

/**
 * This class implements the JoinMessage in tgdh-protocol.
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class JoinMessage extends TgdhMessage {
    private int      position;
    private LeafNode newNode;

    public JoinMessage(
    		String group, 
    		LeafNode newNode)
    {
        this(group, newNode, 1);
    }
    
	/**
	 * Constucts a new JoinMessage with detail new node who wants join in the group
	 * and the insert position. 
	 * @param group      the name of the new node's group, a new node can only join to
	 *                   the group with the same group name as its.
	 * @param newNode    the new node
	 * @param position   the insertposition
	 */    
    public JoinMessage(
    		String group, 
    		LeafNode newNode, 
    		int position)
    {
    	super(group, newNode.getName(), newNode.getSignKey());
    	
        if(position < 1){
            throw new IllegalArgumentException("invalid position");
        }    	
    	    
        this.position    = position;
        this.newNode 	 = newNode;
    }
    
    
    /**
     * Returns the new node.
     * @return the new node.
     */
    public LeafNode getNewNode() {
        return newNode;
    }
    /**
     * Returns the insert position.
     * @return the insert position.
     */
    public int getPosition() {
        return position;
    }
    
	/**
	 * Returns a bytearray of the message, will be used for the signature. 
	 * All important data, here group name, signer name, new node and the insertposition 
	 * will be included in order to prevent the modification of this data.
	 * @return  a bytearray representation of the message, will used for the 
	 *          signature. 
	 */
    protected byte[] toByteArray() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
			bout.write(super.toByteArray());
			bout.write(position);        
            bout.write(newNode.toByteArray());
        } catch (IOException e) {
            return null;
        }

        return bout.toByteArray();
    }

  
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("Position: ");
        s.append(position);
        s.append("\n");
        s.append("New Node: ");
        s.append(newNode.getName());
        return s.toString();
    }


}
