/*
 * Created on 2004-6-26
 *
 */
package tgdh.crypto;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;


/**
 * This class specifies the bkey used with the TGDH-protocol suite, 
 * as specified in <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 * Tree-based Group Key Agreement</a>. 
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhPublicKey implements DSAPublicKey {
	/**
	 * DSA Parameters 
	 */
    private DSAParams params;
	/**
	 * a public value, y
	 */
	private BigInteger y;
	
	/**
	 * Default Constructor, for Serializable
	 */
	public TgdhPublicKey() {
	    super();
	}
		
	/**
	 * Constructor, build a DSAPrivateKey with params and public value y.
	 * @param y      public value y
	 * @param params DSAParameters
	 */
	public TgdhPublicKey(DSAParams params, BigInteger y){
	    if(! (params instanceof Serializable)){
	        throw new IllegalArgumentException(
	                params.getClass().getName() + " isn't serializable");
	    }
	        
		this.params = params;
		this.y 		= y;		
	}
		
	/**
	 * Returns the public value y.
	 * @return the public value y
	 */		
	public BigInteger getY(){
		return y;
	}

    /**
     * @param params The params to set.
     */
    public void setParams(DSAParams params) {
        this.params = params;
    }

	
	/**
	 * Sets the public value y.
	 * @param y public value
	 */			
	public void setY(BigInteger y){
	    
	    if(y.compareTo(getParams().getP()) >= 0){
	        throw new IllegalArgumentException("y greater than p");
	    }
	    
		this.y = y;
	}
	
	/**
	 * Returns the standard algorithm name for this key. For example, 
	 * "DSA" would indicate that this key is a DSA key. 
	 * 
	 * @return the standard algorithm name for this key.
	 */
    public String getAlgorithm() {
        return "DSA";
    }

	/**
	 * @see java.security.Key#getFormat(). Here not supported
	 * @return null
	 */
    public String getFormat() {
        return null;
    }

	/**
	 * Returns the key in its primary encoding format, or null if this key 
	 * does not support encoding. We use this function to get the byteArray 
	 * for Signature.
	 * @return the encoded key, or null if the key does not support encoding.
	 */
    public byte[] getEncoded() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try{
	        bout.write(params.getP() . toByteArray());
	        bout.write(params.getQ() . toByteArray());
	        bout.write(params.getG() . toByteArray());
	        bout.write(           y  . toByteArray());
        }catch(IOException e){
            return null;
        }
        
        return bout.toByteArray();
    }


	public String toString(){
	    StringBuffer s = new StringBuffer();
	    s.append("p: \n"); 
	    s.append(params.getP().toString(16)); 
	    s.append("\n");
	    
	    s.append("q: \n");
	    s.append(params.getQ().toString(16));
	    s.append("\n");
	    
	    s.append("g: \n");	    
	    s.append(params.getG().toString(16));
	    s.append("\n");
	    
	    s.append("y: \n");	    
	    s.append(y.toString(16));
	    s.append("\n");
	    
		return s.toString();	
	}

	/**
	 * Returns the DSA-specific key parameters.These parameters are never secret.
	 * @return the DSA-specific key parameters.
	 */
    public DSAParams getParams() {
        return params;
    }	
    
    
    public boolean equals(Object o){
    	if(o == this) return true;
    	else if(!(o instanceof TgdhPublicKey)) return false;
    	
    	TgdhPublicKey key = (TgdhPublicKey) o;
    	DSAParams tmp = key.getParams();
    	return  y.equals(key.y) && 
    			params.getP().equals(tmp.getP()) &&
				params.getQ().equals(tmp.getQ()) &&
				params.getG().equals(tmp.getG());  	
    }
}
