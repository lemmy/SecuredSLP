/*
 * Created on 09.07.2004
 *
 */
package tgdh;

import java.net.InetAddress;
import java.util.List;


/**
 * This classes is a helper which contains a few static functions 
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhUtil {
	
	/**
	 * Checks whether the object array contains the detail object  
	 * @param objects  the object array
	 * @param object   the detail object
	 * @return true, if <t>objects</t> contains node; false if not.
	 */
	public static boolean contains(Object[] objects, Object object){
		if(object == null || objects == null) return false;
		for(int i=0; i < objects.length; i++){
			if(objects[i] == object){
				return true;
			}
		}
		return false;
	}
	


	/**
	 * Checks whether the two bytearrays have the same byte values in the same
	 * order. 
	 * @param a    first byte array
	 * @param b    second byte array
	 * @return true if they are equal, false if not.
	 */
	public static boolean bytearrayEquals(byte[] a, byte[] b){
		if(a.length != b.length){
			return false; 
		}
		
		for(int i = 0; i < a.length; i++){
			if(a[i] != b[i])	return false;
		}
		
		return true;		
	}
	
	/**
	 * Checks whether there exists two elements in a string array with the 
	 * same value. 
	 * @param strings   string array
	 * @return true if it exists duplicate, false if not
	 */ 
	public static boolean duplicate(String[] strings){
		for(int i=0; i < strings.length - 1; i++){
			for(int j=i+1; j < strings.length; j++){
				if(strings[i].equals(strings[j])){
					return true;
				}
			}
		}	
		
		return false;
	}
	
	/**
	 * Checks whether there exists two elements in a string list with the 
	 * same value. 
	 * @param strings   string list
	 * @return true if it exists duplicate, false if not
	 */ 
	public static boolean duplicate(List strings){
	    String[] tmp = new String[strings.size()];
	    for (int i = 0; i < tmp.length; i++) {
            tmp[i] = (String) strings.get(i);
        }
	    
	    return duplicate(tmp);
	}
	
	/**
	 * Converts a bytearray to a string which represents this bytearray. Every byte
	 * will be represented with a hexvalue between 0x00 and 0xff, and a space will 
	 * be added between two bytes.
	 * E.g. bytes = new byte[]{0, 1, 2, 3, 4};
	 * The result of {@link #toHex(byte[])} is "00 01 02 03 04".
	 * 
	 * @param bytes   the bytes to be converted
	 * @return a string which represents this bytearray
	 */
	public static String toHex(byte[] bytes) {
	    StringBuffer s = new StringBuffer();
	    int h, l;
	    for(int i = 0; i < bytes.length; i++){
	        int value = bytes[i] & 0xff;
	        h = value >>> 4;
	        l = value & 0x0f;

	        s.append(Integer.toHexString(h));
	        s.append(Integer.toHexString(l));
			s.append(" ");       
	    }
	    
	    return s.substring(0, s.length() - 1);
	}
	
	/**
	 * Returns a string of address:port.
	 * @param address the address
	 * @param port    the port
	 * @return a string of address:port.
	 */
	public static String getName(InetAddress address, int port) {
		return address.getHostAddress() + ":" + port;
	}
	
	/**
	 * only for deriving name from IpAddress using libary of JGroups
	 */
	public static String getName(org.jgroups.stack.IpAddress address) {
		return getName(address.getIpAddress(), address.getPort());
	}	
}
