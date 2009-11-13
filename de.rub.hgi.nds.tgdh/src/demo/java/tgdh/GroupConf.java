package tgdh;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.DSAParams;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.DSAPublicKey;
import tgdh.crypto.TgdhKeySpec;

/**
 * This class implements which works with the configuration of the 
 * tgdh group. It can generate a configuration which contains the 
 * tgdh-group name, the key params, the multicast address and port,
 * can write this configuration in a file, and can read the 
 * configuration from a configuration file.
 *  
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class GroupConf {
	private DSAParams params;
	private String mcast_address;
	private int mcast_port;
	private String groupname;

	/**
	 * Constructs a new GroupConf from a configuration file. 
	 * A example of the configuration is:
	 *<pre>
	 * configuration for tgdh
	 *
	 * -- groupname --
	 * nds.rub.de
	 * 
	 * -- mcast_address --
	 * 228.222.11.8
	 *  
	 * -- mcast_port --
	 * 21108
	 * 
	 * -- dsa_keyparams --
	 * 
	 * -- prime modulus p --
	 * AP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7
	 * gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXz
	 * rith1yrv8iIDGZ3RSAHH
	 * 
	 * -- sub-prime q --
	 * AJdgUI8VIwvMspK5gqLrhAvwWBz1
	 * 
	 * -- generator g --
	 * APfhoIXWmz3ey7yrXDa4V7l5lK+7+jrqgvlXTAs9B4JnUVlXjrrUWU/mcQcQgYC0SRZxI+hMKBYT
	 * t88JMozIpuE8FnqLVHyNKOCjrh4rs6Z1kW6jfwv6ITVi8ftiegEkO8yk8b6oUZCJqIPf4VrlnwaS
	 * i2ZegHtVJWQBTDv+z0kq 
	 * </pre>
	 * 
	 * @param  confFile   the configuration file
	 * @throws IOException
	 */		
	public GroupConf(String confFile) throws IOException {
		FileReader filereader = new FileReader(confFile);
		BufferedReader reader = new BufferedReader(filereader);
		BASE64Decoder decoder = new BASE64Decoder();
		
        String linetext = null;
        BigInteger p = null, q = null, g = null;     

		while(groupname == null || mcast_address == null || mcast_port == 0 ||
		      p == null || q == null || g == null)
		{
			linetext = reader.readLine();
			if(linetext == null){
				continue;
			}
			
			if("-- groupname --".equals(linetext)){
				groupname = reader.readLine();
				reader.readLine(); // skip an empty line	
			}
			else if("-- mcast_address --".equals(linetext)){
				mcast_address = reader.readLine();
				reader.readLine(); // skip an empty line	
			}
			else if("-- mcast_port --".equals(linetext)){
				mcast_port = Integer.parseInt(reader.readLine());
				reader.readLine(); // skip an empty line
			}
			else if("-- prime modulus p --".equals(linetext)){
				StringBuffer s = new StringBuffer();
				linetext = reader.readLine();
				while(linetext != null && linetext.length() != 0){					
					s.append(linetext);
					linetext = reader.readLine();
				}
				p = new BigInteger(1, decoder.decodeBuffer(s.toString()));					
			}
			else if("-- sub-prime q --".equals(linetext)){
				StringBuffer s = new StringBuffer();
				linetext = reader.readLine();
				while(linetext != null && linetext.length() != 0){
					s.append(linetext);
					linetext = reader.readLine();
				}
				q = new BigInteger(1, decoder.decodeBuffer(s.toString()));					
			}
			else if("-- generator g --".equals(linetext)){
				StringBuffer s = new StringBuffer();
				linetext = reader.readLine();
				while(linetext != null && linetext.length() != 0){
					s.append(linetext);
					linetext = reader.readLine();
				}
				g = new BigInteger(1, decoder.decodeBuffer(s.toString()));					
			}
		}
			
		params = new TgdhKeySpec(p, q, g);		

		reader.close();      			
	}
	
	
	/**
	 * Constructs a new GroupConf with detail multicast address and port, group 
	 * name and key bits. It generates a dsa key params of the given key bits.
	 * 
	 * @param mcast_address   multicast address
	 * @param mcast_port      multicast port
	 * @param groupname       group name
	 * @param keyBits         key bits
	 * @throws Exception
	 */
	public GroupConf(
			String mcast_address, 
			int    mcast_port,	
			String groupname, 
			int    keyBits) 
			throws Exception
	{
		if(keyBits < 512) {
			throw new IllegalArgumentException("keybits must be not less than 512");	
		}
		if(mcast_port < 1024 || mcast_port > 65535){
			throw new IllegalArgumentException("mcast_port not between 1024 and 65535");
		}
		
		int ip1, ip2, ip3, ip4, index1, index2;
		index1 = mcast_address.indexOf('.');
		ip1 = Integer.parseInt( mcast_address.substring(0, index1));
		index2 = mcast_address.indexOf('.', index1+1);
		ip2 = Integer.parseInt( mcast_address.substring(index1+1, index2));
		index1 = mcast_address.indexOf('.', index2 + 1);
		ip3 = Integer.parseInt( mcast_address.substring(index2+1, index1));
		ip4 = Integer.parseInt( mcast_address.substring(index1+1));
		
		if(ip1 > 255 || ip2 > 255 || ip3 > 255 || ip4 > 255){
			throw new IllegalArgumentException("invalid ipv4 address");
		}

		if(ip1 < 224 || ip1 > 239 ){
			throw new IllegalArgumentException("invalid ipv4 multicast address");			 		
		}
		
		this.groupname = groupname;
		this.mcast_address   = mcast_address;
		this.mcast_port = mcast_port;
		
		KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");
		keygen.initialize(keyBits, new SecureRandom());		
		KeyPair keypair = keygen.generateKeyPair();
		DSAPublicKey  pubkey  = (DSAPublicKey)  keypair.getPublic();
		params = pubkey.getParams();
		params = new TgdhKeySpec(params.getP(), params.getQ(), params.getG());
	}

	/**
	 * Write the configuration in detail file.
	 * @param confFile   the file name
	 * @throws IOException
	 * @see #GroupConf(String)
	 */
	public void write(String confFile) throws IOException {
		BigInteger p = params.getP();
		BigInteger q = params.getQ();
		BigInteger g = params.getG();
		BASE64Encoder encoder = new BASE64Encoder();
		FileWriter writer = new FileWriter(confFile);
		writer.write("configuration for tgdh\n\n");
		writer.write("-- groupname --\n");
		writer.write(groupname + "\n\n");
		writer.write("-- mcast_address --\n");
		writer.write(mcast_address + "\n\n");
		writer.write("-- mcast_port --\n");
		writer.write(Integer.toString(mcast_port) + "\n\n");
		writer.write("-- dsa_keyparams --\n\n");
		writer.write("-- prime modulus p --\n");
		writer.write(encoder.encode(p.toByteArray()) + "\n\n");		
		writer.write("-- sub-prime q --\n");
		writer.write(encoder.encode(q.toByteArray()) + "\n\n");
		writer.write("-- generator g --\n");
		writer.write(encoder.encode(g.toByteArray()) + "\n\n");

		writer.close();
		System.out.println("Pls. reference to file " + confFile);					
	}

	/**
	 * Returns the group name.
	 * @return the group name.
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * Returns the multicast address.
	 * @return the multicast address.
	 */
	public String getMcast_address() {
		return mcast_address;
	}

	/**
	 * Returns the multicast port.
	 * @return the multicast port.
	 */	
	public int getMcast_port() {
		return mcast_port;
	}

	/**
	 * Returns the key parameters.
	 * @return the key parameters.
	 */
	public DSAParams getParams() {
		return params;
	}
	
	private static void printUsage() {
		StringBuffer usage = new StringBuffer();
		usage.append("Usage:\n");
		usage.append("java GenGroupConf ");
		usage.append("-groupname <groupname> ");
		usage.append("-mcastip <mcast address> ");
		usage.append("-mcastport <mcast port> ");
		usage.append("-keybits <keybit> ");
		usage.append("-conffile <configuration file>");
		System.out.println(usage.toString());
	}

	/**
	 * Generate a new GroupConf, and save it as a file, from command line.
	 * usage:-groupname &lt;groupname&gt; -mcastip &lt;mcast address&gt; 
	 *	-mcastport &lt;mcast port&gt; -keybits &lt;keybit&gt; -conffile &lt;configuration file&gt;
	 */
	public static void main(String[] args) {
		if(args.length !=10){
			printUsage();
			return;
		}
		
		if( args[0].charAt(0) != '-' || args[2].charAt(0) != '-' ||
			args[4].charAt(0) != '-' || args[6].charAt(0) != '-' || 
			args[8].charAt(0) != '-')
		{
			printUsage();
			return;
		}
		
		String groupname = null;
		String mcastip   = null;
		String conffile  = null;
		int mcastport    = -1; 
		int keyBits      = -1;
		
		for(int i = 0; i < 10; i+=2) {
			if(args[i].toLowerCase().equals("-groupname")){
				groupname = args[i+1];
			}
			else if(args[i].toLowerCase().equals("-mcastip")){
				mcastip = args[i+1];
			}
			else if(args[i].toLowerCase().equals("-mcastport")){
				mcastport = Integer.parseInt(args[i+1]);
			}
			else if(args[i].toLowerCase().equals("-keybits")){
				keyBits = Integer.parseInt(args[i+1]);
			}
			else if(args[i].toLowerCase().equals("-conffile")){
				conffile = args[i+1];
			}
			else{
				printUsage();
				return;
			}
		}
		
		if(groupname == null || mcastip == null || conffile == null || 
			mcastport == -1 || keyBits == -1)
		{
			printUsage();
			return;
		}
		
		Security.addProvider(new BouncyCastleProvider());
		try {
			GroupConf aGroupConf = new GroupConf(mcastip, mcastport, groupname, keyBits);
			aGroupConf.write(conffile);
			Security.removeProvider("BC");
		} catch (Exception e) {
			e.printStackTrace();
			Security.removeProvider("BC");
			System.exit(-2);
		} 				
	}
	
}
