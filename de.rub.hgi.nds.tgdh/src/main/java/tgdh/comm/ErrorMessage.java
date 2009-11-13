/*
 * Created on 2004-9-16
 *
 */
package tgdh.comm;

import java.security.PrivateKey;


/**
 * This class implements the error message in tgdh-protocol. It contains 
 * the cause, which is enumeration of JOIN, LEAVE, PARTITION, MERGE and 
 * UPDATE, which defined in {@link tgdh.comm.TgdhMessage}
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class ErrorMessage extends TgdhMessage {
    private int    cause;
    private String errorText;    

	/**
	 * Constructs a new error message with the specified detail cause and message. 
	 * @param group      the name of the TGDH group
	 * @param cause      the cause, it can be JOIN, LEAVE, PARTITION, MERGE and UPDATE
	 * @param errorText  detail message 
	 */
    public ErrorMessage(
    		String  group,
            int 	cause, 
            String 	errorText,
            String 	signerName,
            PrivateKey key)
    {
    	super(group, signerName, key);
    	
        if(!TgdhMessage.checkCause(cause)){
            throw new IllegalArgumentException("unknown cause");
        }
        
        this.errorText     = errorText;
    }
    
    /**
     * Returns the detail cause
     * @return Returns the cause.
     */
    public int getCause() {
        return cause;
    }    

    /**
     * Returns the detail message.
     * @return the detail message.
     */
    public String getErrorText() {
        return errorText;
    }

	/**
	 * Returns a bytearray of the message, will be used for the signature. 
	 * All important data, here group name, signer name, cause and message will be included in 
	 * order to prevent the modification of this data.
	 * @return  a bytearray representation of the message, used for the signature. 
	 */
    protected byte[] toByteArray() {
    	byte[] a = super.toByteArray();
        byte[] b = errorText.getBytes();
        byte[] c = new byte[a.length + b.length + 4];

        b[0] = (byte) (cause >>> 24);
        b[1] = (byte) (cause >>> 16);
        b[2] = (byte) (cause >>> 8);
        b[3] = (byte)  cause;
        
        System.arraycopy(a, 0, b, 4, a.length);
        System.arraycopy(b, 0, c, a.length + 4, b.length);        
        return c;
    }
    
}
