package tgdh.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import tgdh.TgdhException;

/**
 * This class implements the messages of the TGDH-protocol. All messages should
 * be signed. Only the successfully verified message will be accepted.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhMessage implements Serializable {
	/** Not special action */
	public static final int NOSPECIAL   = 0;
	/** Join action */	
	public static final int JOIN 		= 1;	
	/** Leave action */	
	public static final int LEAVE 		= 2;
	/** Partition action */	
	public static final int PARTITION 	= 3;
	/** Merge action */	
	public static final int MERGE 		= 4;
	/** Update action */	
	public static final int UPDATE 		= 5;

	/** TGDH groupname */
	protected 			String      group;
	/** Name of the member who signs this message */	
	protected 			String 		signerName;
	/** used key for signing or verifying this message */
	protected transient	Key			key;
	/** signature of this message */
	protected 			byte[] 		signature;
	
	/**
	 * Default Constructor for Serializable
	 */
	protected TgdhMessage(){
	}
	
	/**
	 * Constructor who builds a TgdhMessage 
	 * @param group       TGDH groupname
	 * @param signername  signer's name
	 * @param key         PrivateKey for signing or PublicKey 
	 *                    for verifying the message
	 */
	protected TgdhMessage(
			String 		group,
			String 		signername,
			PrivateKey	key) 
	{
		this.group  	= group;
		this.signerName = signername;
		this.key        = key; 
	}
	
	/**
	 * Initializes this object for verification. If this method is called again with a 
	 * different argument, it negates the effect of this call.
	 * @param key   the public key of the identity whose signature is going to be 
	 *              verified.
	 */
	public void init4verify(PublicKey key) {
	    this.key = key;
	}
	
	/**
	 * Generate the signature of this message using the signer's private key  
	 */
    public void sign() 
    	throws TgdhException 
    {
	    if(!(key instanceof PrivateKey)){
	        throw new TgdhException("The key isn't PrivateKey");
	    }
	    try{
	        Signature signer = Signature.getInstance("SHA1With" + key.getAlgorithm());
	        signer.initSign((PrivateKey) key);
	        
	        byte[] data = toByteArray();
	        int start = 0;
	        
	        for(; start + 1024 < data.length; start += 1024){
	            signer.update(data, start, 1024);
	        }
	    	 
	        signer.update(data, start, data.length - start);
	        signature = signer.sign();
	    } catch (NoSuchAlgorithmException e) {
	        throw new TgdhException(e.getMessage());
	    } catch (SignatureException e) {
	        throw new TgdhException(e.getMessage());
	   } catch (InvalidKeyException e) {           
	       throw new TgdhException(e.getMessage());
        }
    }
    
	/**
	 * Verifies the signature of this message using the signer's public key
	 * @return true if the signature was verified, false if not.
	 */

    public boolean verify() 
    	throws TgdhException
    {
        if(!(key instanceof PublicKey)){
            throw new TgdhException("The key is not PublicKey");
        }
        try {
            Signature verifier = Signature.getInstance("SHA1With" + key.getAlgorithm());
            verifier.initVerify((PublicKey) key);
            byte[] data = toByteArray();
    	    int start = 0;
    	    int block = data.length / 1024;
    	        
    	    for(; start + 1024 < data.length; start += 1024){
    	        verifier.update(data, start, 1024);
    	    }
    	    	 
    	    verifier.update(data, start, data.length - start);
            return verifier.verify(signature);        
         } catch (NoSuchAlgorithmException e) {
             throw new TgdhException(e.getMessage());
         } catch (SignatureException e) {
             throw new TgdhException(e.getMessage());
         } catch (InvalidKeyException e){
             throw new TgdhException(e.getMessage());
         }
         
    }

    
    /**
     * @return Returns the signerName.
     */
    public String getSignerName() {
        return signerName;
    }
    
    /**
     * Checks if the given <t>cause</t> is valid
     */
    public static boolean checkCause(int cause){
        return cause  == JOIN     || cause == LEAVE || 
               cause == PARTITION || cause == MERGE || cause == UPDATE;
    }
      
    /**
     * Returns the key for verifying if the setted key is instance of PublicKey,
     * null if not.
     * @return  the key for verifying if the setted key is instance of PublicKey,
     *          null if not.
     */
    public PublicKey getPublic() {
        if(key instanceof PublicKey)
            return (PublicKey) key;
        else 
            return null;
    }
    
    /**
     * Returns the groupname
     * @return the groupname
     */
    public String getGroup() {
    	return group;	
    }
    
    
    private void writeObject(ObjectOutputStream out)
    	throws IOException
    {
        out.defaultWriteObject();
        if(key instanceof PublicKey ){
            out.writeObject(key);
        }
        
    }
    private void readObject(ObjectInputStream in)
    	throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        try{
            Object o = in.readObject();
            if(o != null && o instanceof PublicKey){
                key = (Key) o;
            }
        } catch(IOException e){
            ;
        }
    }
    
    /**
     * Returns a bytearray of the message, used for the signature. All important 
     * data, here group name, signer name, should be included in order to 
     * prevent the modification of this data.
     * @return  a bytearray representation of the message, used for the signature. 
     */
    protected byte[] toByteArray(){
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	try{
    		bout.write(group.getBytes());
    		bout.write(signerName.getBytes()); 
    	} catch(IOException e){
    		return null;
    	}
    	
    	return bout.toByteArray();   	
    }
}
