package tgdh.crypto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;


/**
 * This class specifies the set of parameters used with the Diffie-Hellman 
 * algorithm, as specified in PKCS #3: Diffie-Hellman Key-Agreement Standard.
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhKeySpec implements DSAParams, Serializable {
	/**
	 * prime modulus
	 */
    private BigInteger 			p;
    /**
     * sub-prime
     */
    private BigInteger 			q;
    
    /** 
     * generator 
     */
    private BigInteger			g;
    
    /**
     * Default constructor
     */
    public TgdhKeySpec() {
     }
    
	/**
	 * Constructor
	 * @param p prime modulus
	 * @param q sub-prime
	 * @param g generator
	 */
	public TgdhKeySpec(
	        BigInteger p,
	        BigInteger q,
	        BigInteger g)
	{
		this.p 		= p;
		this.q 		= q;
		this.g 		= g;
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
            bout.write(getAlgorithm().getBytes());
            bout.write(p.toByteArray());
            bout.write(q.toByteArray());
            bout.write(g.toByteArray());            
        }catch(IOException e){
            return null;
        }
        
        return bout.toByteArray();
    }


	/**
	 * Returns the DSA-specific key parameters. These parameters 
	 * are never secret.
	 * @return the DSA-specific key parameters.
	 */
    public DSAParams getParams() {
        return new TgdhKeySpec(p, q, g);
    }


	public String toString(){
		StringBuffer s = new StringBuffer();
		
		s.append("p:\n");		
		s.append(p.toString());		
		s.append("\n");

		s.append("q:\n");		
		s.append(q.toString());		
		s.append("\n");

		s.append("g:\n");		
		s.append(g.toString());		
		s.append("\n");
		return s.toString();
	}
	
    /**
     * Returns the prime, p.
     * @return the prime, p.
     */
    public BigInteger getP() {
        return p;
    }

    /**
     * Returns the subprime, q.
     * @return the subprime, q.
     */
    public BigInteger getQ() {
        return q;
    }

    /**
     * Returns the base, g.
     * @return the base, g.
     */
    public BigInteger getG() {
        return g;
    }

	
}
