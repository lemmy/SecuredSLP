/*
 * Created on 2004-6-26
 *
 */
package tgdh.crypto;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;


/**
 *
 * This class specifies the key used with the TGDH-protocol suite, 
 * as specified in <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 * Tree-based Group Key Agreement</a>. 
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhPrivateKey implements DSAPrivateKey{
	/**
	 * DSA Parametes
	 */
    DSAParams params;
	/**
	 * private value, x
	 */
	private BigInteger x;
	
	
	/**
	 * Constructor, build a DSAPrivateKey with params and private value x.
	 * @param x      private value
	 * @param params DHParameterSpec
	 */
	public TgdhPrivateKey(DSAParams params, BigInteger x){
	    if(! (params instanceof Serializable)){
	        throw new IllegalArgumentException(
	                params.getClass().getName() + " isn't serializable");
	    }
	        
		this.params = params;
		this.x 		= x;
	}
	
		
	/**
	 * Returns the private value
	 * @return the private value
	 */
	public BigInteger getX(){
		return x;
	}
	
    /**
     * @param params The params to set.
     */
    public void setParams(DSAParams params) {
        this.params = params;
    }

    
	/**
	 * Sets the private value, x
	 * @param x private value
	 */
	public void setX(BigInteger x){
	    if(x.compareTo(getParams().getP()) >= 0){
	        throw new IllegalArgumentException("x greater than modulo");
	    }
	    
		this.x = x;
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
	        bout.write(           x  . toByteArray());
        } catch (Exception e){
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
	    
	    s.append("x: \n");	    
	    s.append(x.toString(16));
	    s.append("\n");
	    
		return s.toString();
	}


    /**
     * Returns the DSA-specific key parameters.These parameters 
     * are never secret.
     * @return the DSA-specific key parameters.
     */
    public DSAParams getParams() {
        return params;
    }
}
