/*
 * Created on 2004-9-16
 *
 */
package tgdh.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;

import tgdh.tree.TreeInfo;

/**
 * This class implements the MergeMessage in tgdh-protocol.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class MergeMessage extends TgdhMessage {
    private TreeInfo toMerge;
    private int      treeSequence;

    /**
     * Constructs a new MergeMessage with detail group, treeSequence, signer's name 
     * and key for signing the messsage. 
     * 
     * @param group         group name
     * @param treeSequence  the sequence number of the tree
     * @param toMerge       the TreeInfo about the key tree of the group who will be 
     * 						merged to another key tree of the group with the same name.
     * @param signerName    signer's name
     * @param key           signer's key for signing of the message
     */
    public MergeMessage(
    		String   	group,
    		int         treeSequence,		
            TreeInfo 	toMerge, 
            String   	signerName,
            PrivateKey 	key)
    {
    	super(group, signerName, key);
    	
    	this.treeSequence = treeSequence;
        this.toMerge      = toMerge;        
    }    
    
    /**
     * Returns the treeinfo of the key tree.
     * @return the treeinfo of the key tree.
     */
    public TreeInfo getToMerge() {
        return toMerge;
    }

	/**
	 * Returns the sequence number of the key tree.
	 * @return the sequence number of the key tree.
	 */
    public int getTreeSequence() {
    	return treeSequence;	
    }
    
    
	/**
	 * Returns a bytearray of the message, will be used for the signature. 
	 * All important data, here group name, signer name, sequence number of 
	 * the key tree and the treeinfo of the key tree, will be included in order to prevent the modification of this data.
	 * @return  a bytearray representation of the message, will used for the 
	 *          signature. 
	 */
    protected byte[] toByteArray() {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	try{
    		bout.write(super.toByteArray());
    		bout.write(treeSequence);
    		bout.write(toMerge.toByteArray());
			return bout.toByteArray();
    	} catch(IOException e) {
    		return null;
    	}    	
    }

}
