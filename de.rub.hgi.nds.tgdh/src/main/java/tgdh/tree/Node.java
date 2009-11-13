/*
 * Created on 24.05.2004
 *
 */
package tgdh.tree;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;

import tgdh.crypto.TgdhPrivateKey;
import tgdh.crypto.TgdhPublicKey;

/**
 * This class implements the inner node used in basic binary tree that used for TGDH in specification  
 * <a href="http://citeseer.ist.psu.edu/cache/papers/cs/25681/http:zSzzSzeprint.iacr.orgzSz2002zSz009.pdf/kim02treebased.pdf">
 *
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */  
public class Node implements Serializable {
	/**
	 * the bkey
	 */
	protected DSAPublicKey publicKey;

	/**
	 * the key
	 */	
	protected transient DSAPrivateKey privateKey;	
	
	/**
	 * the position of this node in a tree
	 */
	protected int position;
	
	/**
	 * left child, null for leaf 
	 */
	protected transient Node left;
	
	/**
	 * right child, null for leaf
	 */
	protected transient Node right;
	
	/**
	 * parent, null for root of a tree, but not null for root of a subtree
	 */
	protected transient Node parent;	

	/**
	 * Default constructor without no parameter
	 */	
	public Node(){
	}
	
	/**
	 * Returns the bkey.
	 * @return the bkey.
	 */
	public DSAPublicKey getPublic() {
		return publicKey;
	}

	
	
    /**
     * Returns the left child
     * @return the left child
     */
    public Node getLeft() {
        return left;
    }
    /**
     * Returns the parent. 
     * @return the parent.
     */
    public Node getParent() {
        return parent;
    }
    
    /**
     * returns the right child.
     * @return the right child.
     */
    public Node getRight() {
        return right;
    }
    
	/**
	 * Returns the key
	 * @return the key
	 */
	public DSAPrivateKey getPrivate(){
		return privateKey;
	}
	
	/**
	 * compute keys and bkeys use a key of one child and a bkey of the other child
	 * @throws TgdhException if no private key of one child and public key
	 * 		   of another kind exist.
	 */
	public boolean computeKeys() {
	    if(left == null || right == null){
	        return false;
	    }
	    DSAPrivateKey privKey = left.privateKey;
	    DSAPublicKey  pubKey  = right.publicKey;
	    if( privKey == null || pubKey == null ){
		    privKey = right.privateKey;
		    pubKey  = left .publicKey;	        
	    }
	    
	    if( privKey == null || pubKey == null ){
	        return false;
	    }
	    
	    privateKey = new TgdhPrivateKey(
	            privKey.getParams(), 
	            pubKey.getY().modPow(
	                privKey.getX(), privKey.getParams().getP()));
	    
	    publicKey  = new TgdhPublicKey (
	            privateKey.getParams(),
	            privateKey.getParams().getG() . modPow
	            	(privateKey.getX(), privateKey.getParams().getP()));
	    
	    return true;
	}

	
	public void setKeys(DSAPrivateKey privKey, DSAPublicKey pubKey)
		throws NotSerializableException 
	{	
		if(privKey instanceof Serializable && pubKey instanceof Serializable){
			this.privateKey = privKey;
			this.publicKey  = pubKey;
		}else{
			throw new NotSerializableException();
		}
	 
	}
	
	public void setPublic(DSAPublicKey publicKey)
		throws NotSerializableException 
	{
		if(publicKey instanceof Serializable){
		    this.publicKey = publicKey;
		}else{
			throw new NotSerializableException();
		}
	}

	/** clear privateKey and publicKey */
	public void removeKeys(){
	    privateKey = null;
	    publicKey  = null;
	}
	
	public byte[] toByteArray() {
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    try {
            bout.write(position);
            if(publicKey != null) {
            	bout.write(publicKey.getEncoded());
            }
        } catch (IOException e) {
            return null;
        }
        
        return bout.toByteArray();	    
	}
	
	/**
	 * Returns the coordinate of the node in a tree &lt; l, v &gt; which corresponds 
	 * to position <i>2<sup>l</sup> + v</i>. 
	 * @return Coordinate of the node in a tree.
	 */
	public Coordinate getCoordinate(){
		double log2 = Math.log(2);
		int l = (int) (Math.log(this.position) / log2);
		return new Coordinate(l, position - (1 << l)) ;
	}
	
	/**
	 * Sets the parent
	 * @param node parent
	 */
	public void setParent(Node node){
		this.parent = node;
	}		
	
	/**
	 * Sets the left child
	 * @param node left child
	 */
	public void setLeft(Node node){
		this.left = node;
	}	

	/**
	 * Sets the right child
	 * @param node right child
	 */
	public void setRight(Node node){
		this.right = node;
	}
	
	/**
	 * Sets the parent, left child, right child to null.
	 */
	public void clearRelation() {
	    left = null; 	right = null; 	parent = null;
	}
	
	
	/** 
	 * Sets the <code>position</code> in a tree
	 * @param position position in a tree, a positiv number
	 */
	public void setPosition(int position){
		if(position > 0)
			this.position = position;
		else {
			throw new IllegalArgumentException("position isn't positiv");
		}
	}
	
	/** 
	 * Returns position in a tree
	 * @return position in a tree
	 */
	public int getPosition() {
		return position;
	}


	public String toString(){
	    StringBuffer s = new StringBuffer();
	    s.append("<");
	    s.append(getCoordinate().getLevel());
	    s.append(",");
	    s.append(getCoordinate().getOrdinal());
	    s.append(">");
	    
	    return s.toString();
	}	
}
