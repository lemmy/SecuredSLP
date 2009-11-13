package tgdh.tree;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
/**
 * This class implements the leafnode used in basic binary tree that used for TGDH in specification  
 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class LeafNode extends Node {
	/**
	 * PrivateKey to sign the message
	 */
	protected transient PrivateKey signKey;
	
	/**
	 * Public Key to verify the signatue of message
	 */
	protected PublicKey verifyKey;
   
	protected String name;
	
	/**
	 * Returns the name
	 * @return the name
	 */
	public String getName() {
	    return name;
	}

    /**
     * returns the PublicKey for verifying
	 * @return the PublicKey for verifying  
	 */
	public PublicKey getVerifyKey() {
		return verifyKey;
	}

	/**
	 * returns the PrivateKey for signing
	 * @return the PrivateKey for signing
	 */
	public PrivateKey getSignKey() {
		return signKey;
	}
	
	/**
	 * Sets the signKey
	 * @param signKey the signKey
	 */
	public void setSignKey(PrivateKey signKey) 
		throws NotSerializableException 
	{
		if(signKey instanceof Serializable)
			this.signKey = signKey;
		else
			throw new NotSerializableException();		
	}
	
	public void setVerifyKey(PublicKey  verifyKey) 
		throws NotSerializableException 
	{
		if(verifyKey instanceof Serializable)
			this.verifyKey = verifyKey;		
		else
			throw new NotSerializableException();		
	}
	
	
	/**
	 * Default Constructor
	 */
	public LeafNode(){
		super();
	}

	/**
	 * Constructor with parameter <t>name</t>
	 * @param name name of <code>LeafNode</code>
	 */
	public LeafNode(String name){
	    super();
		this.name = name;
	}

	
	public String toString(){
		return "<" + this.getCoordinate().getLevel() + "," + 
			this.getCoordinate().getOrdinal() + ">(" + this.getName() + ")";
	}
	
    
    public byte[] toByteArray(){
        byte[] a = super.toByteArray();
        byte[] b = name.getBytes();
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
       
}
