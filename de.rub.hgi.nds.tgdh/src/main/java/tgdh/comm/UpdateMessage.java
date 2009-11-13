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
 * This class implements the UpdateMessage in tgdh-protocol.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class UpdateMessage extends TgdhMessage {

	private TreeInfo updatedTree;
	private int      cause;
	private int      treeSequence;

	/**
	 * Constructs a new UpdateMessage with detail group name, cause, sequence
	 * number of the updated tree, the updated tree, the signer's name and key
	 * for sigining the message.
	 * @param group          the group name
	 * @param cause          the cause
	 * @param treeSequence   the sequence number of the updated tree
	 * @param updatedTree    the TreeInfo of the updated tree
	 * @param signerName     the signer's name
	 * @param key            the signer#s key for signing the message
	 */
	public UpdateMessage(
			String 	   group,
	        int 	   cause,
	        int        treeSequence,
	        TreeInfo   updatedTree,
	        String 	   signerName,
	        PrivateKey key)
	{
		super(group, signerName, key);
		this.treeSequence = treeSequence;
				
	    if(TgdhMessage.checkCause(cause)){
		    this.cause 		 = cause;	        
	    }
	    else{
	       throw new IllegalArgumentException("unknown cause"); 
	    }
	    
	    this.updatedTree = updatedTree;
	}
	
    /**
     * Returns the TreeInfo of the updated tree.
     * @return the TreeInfo of the updated tree.
     */
    public TreeInfo getUpdatedTree() {
        return updatedTree;
    }    
    

    /**
     * Returns the cause.
     * @return the cause.
     */
    public int getCause() {
        return cause;
    }

    /**
     * Returns the sequence number of the updated tree
     * @return the sequence number of the updated tree
     */
	public int getTreeSequence() {
		return treeSequence;
	}
	
	/**
	 * Returns a bytearray of the message, will be used for the signature. 
	 * All important data, here group name, signer name, cause, sequence number  
	 * and TreeInfo of the updated tree, will be included in order to prevent 
	 * the modification of this data.
	 * @return  a bytearray representation of the message, will used for the 
	 *          signature. 
	 */
    protected byte[] toByteArray() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();        
        bout.write(cause);
        bout.write(treeSequence);
        
        try{
            bout.write(updatedTree.toByteArray());
        }catch (IOException e){
            return null;
        }
        return bout.toByteArray();
    }


}
