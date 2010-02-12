/* Copyright (c) 2005-2008 Jan S. Rellermeyer
 * Systems Group,
 * Department of Computer Science, ETH Zurich.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *    - Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *    - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *    - Neither the name of ETH Zurich nor the names of its contributors may be
 *      used to endorse or promote products derived from this software without
 *      specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package ch.ethz.iks.slp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import ch.ethz.iks.slp.ServiceLocationException;
import ch.ethz.iks.slp.impl.attr.AttributeListVisitor;
import ch.ethz.iks.slp.impl.attr.gen.Parser;
import ch.ethz.iks.slp.impl.attr.gen.ParserException;
import ch.ethz.iks.slp.impl.attr.gen.Rule;
import ch.ethz.iks.slp.impl.sec.SecurityGroupSessionKey;

/**
 * base class for all messages that the SLP framework uses.
 * 
 * @author Jan S. Rellermeyer, ETH Zurich
 * @since 0.1
 */
public abstract class SLPMessage {

	/**
	 * the <code>Locale</code> of the message.
	 */
	Locale locale;

	/**
	 * the funcID encodes the message type.
	 */
	byte funcID;

	/**
	 * the transaction ID.
	 */
	short xid;

	/**
	 * the sender or receiver address.
	 */
	InetAddress address;

	/**
	 * the sender or receiver port.
	 */
	int port;

	/**
	 * true if the message was processed or will be sent via TCP
	 */
	boolean tcp;

	/**
	 * true if the message came in or will go out by multicast.
	 */
	boolean multicast;
	
	/**
	 * The Session Key used during message encryption
	 */
	SecurityGroupSessionKey sessionKey;

	/**
	 * Size of payload. Might differ from what subclasses calculate
	 * depending on whether the messages is encryption and with
	 * what algorithm 
	 */
	private int payloadSize;
	
	/**
	 * Header length as defined by RFC
	 */
	public static final int HEADER_PREFIX_LENGTH = 
		1 /* version*/
		+ 1 /* function id */
		+ 3 /* length */
		+ 5 /* flags */
		+ 2 /* xid */
		+ 2 /* Lang length */
		+ 2 /* SPI length */;
	// ===== 16

	/**
	 * the message version according to RFC 2608, Version = 2.
	 */
	public static final byte VERSION = 3;
	
	/**
	 * the message funcID values according to RFC 2608, Service Request = 1.
	 */
	public static final byte SRVRQST = 1;

	/**
	 * the message funcID values according to RFC 2608, Service Reply = 2.
	 */
	public static final byte SRVRPLY = 2;

	/**
	 * the message funcID values according to RFC 2608, Service Registration =
	 * 3.
	 */
	public static final byte SRVREG = 3;

	/**
	 * the message funcID values according to RFC 2608, Service Deregistration =
	 * 4.
	 */
	public static final byte SRVDEREG = 4;

	/**
	 * the message funcID values according to RFC 2608, Service Acknowledgement =
	 * 5.
	 */
	public static final byte SRVACK = 5;

	/**
	 * the message funcID values according to RFC 2608, Attribute Request = 6.
	 */
	public static final byte ATTRRQST = 6;

	/**
	 * the message funcID values according to RFC 2608, Attribute Reply = 7.
	 */
	public static final byte ATTRRPLY = 7;

	/**
	 * the message funcID values according to RFC 2608, DA Advertisement = 8.
	 */
	public static final byte DAADVERT = 8;

	/**
	 * the message funcID values according to RFC 2608, Service Type Request =
	 * 9.
	 */
	public static final byte SRVTYPERQST = 9;

	/**
	 * the message funcID values according to RFC 2608, Service Type Reply = 10.
	 */
	public static final byte SRVTYPERPLY = 10;

	/**
	 * the message funcID values according to RFC 2608, SA Advertisement = 11.
	 */
	public static final byte SAADVERT = 11;

	/**
	 * used for reverse lookup of funcID values to have nicer debug messages.
	 */
	private static final String[] TYPES = { "NULL", "SRVRQST", "SRVPLY",
			"SRVREG", "SRVDEREG", "SRVACK", "ATTRRQST", "ATTRRPLY", "DAADVERT",
			"SRVTYPERQST", "SRVTYPERPLY", "SAADVERT" };

	/**
	 * 
	 */
	public SLPMessage() {
	}

	/**
	 * @param aSecurityGroup
	 */
	public SLPMessage(String aSecurityGroup) {
		setSecurityGroup(aSecurityGroup);
	}

	/**
	 * Sets the security group identifier of this msg to the given string
	 */
	void setSecurityGroup(String aSecurityGroup) {
		sessionKey = (SecurityGroupSessionKey) SLPCore.sgSessionKeys.get(aSecurityGroup);
	}

	/**
	 * get the bytes from a SLPMessage. Processes the header and then calls the
	 * getBody() method of the implementing subclass.
	 * 
	 * @return an array of bytes encoding the SLPMessage.
	 * @throws IOException
	 * @throws ServiceLocationException
	 *             in case of IOExceptions.
	 */
	private void writeHeader(final DataOutputStream out)
			throws IOException {
		byte flags = 0;
		if (funcID == SRVREG) {
			flags |= 0x40;
		}
		if (multicast) {
			flags |= 0x20;
		}
		int msgSize = getHeaderSize() + payloadSize;
		if (!tcp && msgSize > SLPCore.CONFIG.getMTU()) {
				flags |= 0x80;
		}
		if(isEncrypted()) {
			flags |= 0x10;
		}
		out.write(VERSION);
		out.write(funcID);
		out.write((byte) ((msgSize) >> 16));
		out.write((byte) (((msgSize) >> 8) & 0xFF));
		out.write((byte) ((msgSize) & 0xFF));
		out.write(flags);
		out.write(0);
		out.write(0);
		out.write(0);
		out.write(0);
		out.writeShort(xid);
		out.writeUTF(locale.getLanguage());
		if(isEncrypted()) {
			out.writeUTF(sessionKey.getFQN());
		} else {
			out.writeUTF("");
		}
	}

	/**
	 * 
	 */
	protected abstract void writeTo(final DataOutputStream out) throws IOException;

	/**
	 * 
	 */
	byte[] getBytes() throws IOException {
		// write payload into temp buffer to calc size for header
		final ByteArrayOutputStream payloadBytes = new ByteArrayOutputStream();
		final DataOutputStream payloadOut = new DataOutputStream(payloadBytes);
		writeTo(payloadOut);
		byte[] payload = payloadBytes.toByteArray();
		payloadSize = getSize();
		
		// encrypt payload
		if(isEncrypted()) {
			// the current session key
			Key key = sessionKey.getSecKeySpec();
			try {
				SLPCore.cipher.init(Cipher.ENCRYPT_MODE, key);
				payload = SLPCore.cipher.doFinal(payload);
			} catch (InvalidKeyException e) {
				throw new IOException(e);
			} catch (IllegalBlockSizeException e) {
				throw new IOException(e);
			} catch (BadPaddingException e) {
				throw new IOException(e);
			}
			payloadSize = payload.length;
		}
		
		// write header
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bytes);
		writeHeader(out);
		
		// prepend payload after header
		out.write(payload);
		
		// calculate (H)MAC of header _and_ payload
		// MAC includes header _and_ payload much like IPSec ESP does
		if(isEncrypted()) { //TODO message integrity optional -> move into slpconfiguration?
			Key key = sessionKey.getSecKeySpec();
			try {
				SLPCore.mac.init(key);
				byte[] msg = bytes.toByteArray();
				byte[] mac = SLPCore.mac.doFinal(msg);

				// prepend mac after payload (header -> payload -> mac -> next header -> next header 2 -> ...)
				out.write(mac);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
		}
		
		// finally convert to byte[]
		return bytes.toByteArray();
	}

	/**
	 * The SLPv3 message header:
	 * 
	 * <pre>
	 *                   0                   1                   2                   3
	 *                   0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |    Version    |  Function-ID  |            Length             |
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  | Length, contd.|O|F|R|S|     reserved          |Next Ext Offset|
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |  Next Extension Offset, contd.|              XID              |
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |      Language Tag Length      |         Language Tag          |
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  |Security Parameter Index Length|   Security Parameter Index	|
	 *                  +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 *                  
	 *  New flag is:  SECURITY (0x10) is set when a security group is used
	 * 
	 *  Security Parameter Index Length is the length in bytes of the Security Parameter Index field.
     *  Security Parameter Index conforms to [7]. This field must be encoded 1*8ALPHA *("-" 1*8ALPHA).
	 * </pre>
	 * 
	 * This method parses the header and then delegates the creation of the
	 * corresponding SLPMessage to the subclass that matches the funcID.
	 * 
	 * @param senderAddr
	 *            the address of the message sender.
	 * @param senderPort
	 *            the port of the message sender.
	 * @param data
	 *            the raw bytes of the message
	 * @param len
	 *            the length of the byte array.
	 * @param tcp
	 *            true if the message was received via TCP, false otherwise.
	 * @return a SLPMessage of the matching subtype.
	 * @throws ServiceLocationException
	 *             in case of any parsing errors.
	 */
	static SLPMessage parse(final InetAddress senderAddr, final int senderPort,
			DataInputStream inputStream, final boolean tcp)
			throws ServiceLocationException, ProtocolException {
		try {
			inputStream.mark(Integer.MAX_VALUE); // remember all bytes
			
			final int version = inputStream.readByte(); // version
			if (version != VERSION) {
				inputStream.readByte(); // funcID
				final int length = inputStream.readShort();
				byte[] drop = new byte[length - 4];
				inputStream.readFully(drop);
				SLPCore.platform.logWarning("Dropped SLPv" + version + " message from "
							+ senderAddr + ":" + senderPort);
			}
			final byte funcID = inputStream.readByte(); // funcID
			final int length = readInt(inputStream, 3);

			// slpFlags
			final byte flags = (byte) (inputStream.readShort() >> 8);

			if (!tcp && (flags & 0x80) != 0) {
				throw new ProtocolException();
			}

			// we don't process extensions, we simply ignore them
			readInt(inputStream, 3); // extOffset
			final short xid = inputStream.readShort(); // XID
			final Locale locale = new Locale(inputStream.readUTF(), ""); // Locale

			// read security parameter index
			final String securityParameterIndex = inputStream.readUTF();
			final SecurityGroupSessionKey sessionKey = (SecurityGroupSessionKey) SLPCore.sgSessionKeys.get(securityParameterIndex);
			
			final int headerLength = HEADER_PREFIX_LENGTH
				+ locale.getLanguage().length()
				+ securityParameterIndex.length();
			DataInputStream decryptedInputStream = inputStream;
			if (isEncrypted(flags)) {
				byte[] encryptedBytes = new byte[length - headerLength];
				inputStream.read(encryptedBytes);
				Key key = sessionKey.getSecKeySpec();
				SLPCore.cipher.init(Cipher.DECRYPT_MODE, key);
				byte[] decryptedBytes = SLPCore.cipher.doFinal(encryptedBytes);
				decryptedInputStream = new DataInputStream(new ByteArrayInputStream(
						decryptedBytes));
			}

			final SLPMessage msg;
			// decide on the type of the message
			switch (funcID) {
			case DAADVERT:
				msg = new DAAdvertisement(decryptedInputStream);
				break;
			case SRVRQST:
				msg = new ServiceRequest(decryptedInputStream);
				break;
			case SRVRPLY:
				msg = new ServiceReply(decryptedInputStream);
				break;
			case ATTRRQST:
				msg = new AttributeRequest(decryptedInputStream);
				break;
			case ATTRRPLY:
				msg = new AttributeReply(decryptedInputStream);
				break;
			case SRVREG:
				msg = new ServiceRegistration(decryptedInputStream);
 				break;
			case SRVDEREG:
				msg = new ServiceDeregistration(decryptedInputStream);
				break;
			case SRVACK:
				msg = new ServiceAcknowledgement(decryptedInputStream);
				break;
			case SRVTYPERQST:
				msg = new ServiceTypeRequest(decryptedInputStream);
				break;
			case SRVTYPERPLY:
				msg = new ServiceTypeReply(decryptedInputStream);
				break;
			default:
				throw new ServiceLocationException(
						ServiceLocationException.PARSE_ERROR, "Message type "
								+ getType(funcID) + " not supported");
			}

			// set the fields
			msg.address = senderAddr;
			msg.port = senderPort;
			msg.tcp = tcp;
			msg.multicast = ((flags & 0x2000) >> 13) == 1 ? true : false;
			msg.xid = xid;
			msg.funcID = funcID;
			msg.locale = locale;
			msg.sessionKey = sessionKey;
			//for encrypted messages the decrypted lengths don't match
			if (!isEncrypted(flags) && msg.getSize() != (length - headerLength)) {
				SLPCore.platform.logError("Length of " + msg + " should be " + length + ", read "
								+ msg.getSize() + headerLength);
			}
			
			// read MAC and validate msg integrity
			if(isEncrypted(flags)) {
				byte[] mac = new byte[20]; // TODO currently MAC hardcoded to 20 bytes -> 160 bits
				inputStream.read(mac);

				// reset the input stream to 0 to reread the whole (header and payload) msg
				inputStream.reset();
				byte[] headerAndPayload = new byte[length];
				inputStream.read(headerAndPayload);
				
				// validate mac
				SecurityGroupSessionKey sgSessionKey = (SecurityGroupSessionKey) SLPCore.sgSessionKeys.get(securityParameterIndex);
				Key key = sgSessionKey.getSecKeySpec();
				SLPCore.mac.init(key);
				byte[] macCacl = SLPCore.mac.doFinal(headerAndPayload);
				if(!Arrays.equals(mac, macCacl)) {
					SLPCore.platform.logError("Network Error: HMAC don't match!");
					throw new ServiceLocationException(
							ServiceLocationException.PARSE_ERROR, "HMAC mismatch");
				}
			}
			
			return msg;
		} catch (ProtocolException pe) {
			throw pe;
		} catch (IOException ioe) {
			SLPCore.platform.logError("Network Error", ioe);
			throw new ServiceLocationException(
					ServiceLocationException.NETWORK_ERROR, ioe.getMessage());
		} catch (InvalidKeyException ie) {
			SLPCore.platform.logError("Network Error", ie);
			throw new ServiceLocationException(
					ServiceLocationException.PARSE_ERROR, ie.getMessage());
		} catch (IllegalBlockSizeException ibse) {
			SLPCore.platform.logError("Network Error", ibse);
			throw new ServiceLocationException(
					ServiceLocationException.PARSE_ERROR, ibse.getMessage());
		} catch (BadPaddingException bpe) {
			SLPCore.platform.logError("Network Error", bpe);
			throw new ServiceLocationException(
					ServiceLocationException.PARSE_ERROR, bpe.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 */
	protected int getHeaderSize() {
		int length = HEADER_PREFIX_LENGTH + locale.getLanguage().length();
		if(isEncrypted()) {
			length += sessionKey.getFQN().length();
		}
		return length;
	}
	
	private static boolean isEncrypted(byte flags) {
		return (flags & 0x10) != 0;
	}
	
	private boolean isEncrypted() {
		return sessionKey != null;
	}
	
	/**
	 * 
	 * @return
	 */
	protected abstract int getSize();

	/**
	 * Get a string representation of the message. Overridden by message
	 * subtypes.
	 * 
	 * @return a String.
	 */
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(getType(funcID) + " - ");
		buffer.append("tcp=" + tcp);
		buffer.append(", multicast=" + multicast);
		buffer.append(", xid=" + xid);
		buffer.append(", locale=" + locale);
		if(isEncrypted()) {
			buffer.append(", securityGroupSpi=" + sessionKey.getFQN());
		}
		return buffer.toString();
	}

	/**
	 * returns the string value of the message type, catches the case where an
	 * unsupported message has been received.
	 * 
	 * @param type
	 *            the type.
	 * @return the type as String.
	 */
	static String getType(final int type) {
		if (type > -1 && type < 12) {
			return TYPES[type];
		}
		return String.valueOf(type + " - UNSUPPORTED");
	}

	/**
	 * parse a numerical value that can be spanned over multiple bytes.
	 * 
	 * @param input
	 *            the data input stream.
	 * @param len
	 *            the number of bytes to read.
	 * @return the int value.
	 * @throws ServiceLocationException
	 *             in case of IO errors.
	 */
	private static int readInt(final DataInputStream input, final int len)
			throws ServiceLocationException {
		try {
			int value = 0;
			for (int i = 0; i < len; i++) {
				value <<= 8;
				value += input.readByte() & 0xff;
			}
			return value;
		} catch (IOException ioe) {
			throw new ServiceLocationException(
					ServiceLocationException.PARSE_ERROR, ioe.getMessage());
		}
	}

	/**
	 * transforms a Java list to string list.
	 * 
	 * @param list
	 *            the list
	 * @param delim
	 *            the delimiter
	 * @return the String list.
	 */
	static String listToString(final List list, final String delim) {
		if (list == null || list.size() == 0) {
			return "";
		} else if (list.size() == 1) {
			return list.get(0).toString();
		} else {
			final StringBuffer buffer = new StringBuffer();
			final Object[] elements = list.toArray();
			for (int i = 0; i < elements.length - 1; i++) {
				buffer.append(elements[i]);
				buffer.append(delim);
			}
			buffer.append(elements[elements.length - 1]);
			return buffer.toString();
		}
	}

	/**
	 * transforms a string list to Java List.
	 * 
	 * @param str
	 *            the String list
	 * @param delim
	 *            the delimiter
	 * @return the List.
	 */
	static List stringToList(final String str, final String delim) {
		List result = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(str, delim);
		while (tokenizer.hasMoreTokens()) {
			result.add(tokenizer.nextToken());
		}
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws ServiceLocationException
	 * 
	 * @author Markus Alexander Kuppe
	 * @since 1.1
	 */
	protected List attributeStringToList(String input) throws ServiceLocationException {
		if("".equals(input)) {
			return new ArrayList();
		}
		Parser parser = new Parser();
		try {
			Rule parse = parser.parse("attr-list", input);
			AttributeListVisitor visitor = new AttributeListVisitor();
			parse.visit(visitor);
			return visitor.getAttributes();
		} catch (IllegalArgumentException e) {
			throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, e.getMessage());
		} catch (ParserException e) {
			throw new ServiceLocationException(ServiceLocationException.PARSE_ERROR, e.getMessage());
		}
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws ServiceLocationException
	 * 
	 * @author Markus Alexander Kuppe
	 * @since 1.1
	 */
	protected List attributeStringToListLiberal(String input) {
		if("".equals(input)) {
			return new ArrayList();
		}
		Parser parser = new Parser();
		Rule rule = null;
		try {
			rule = parser.parse("attr-list", input);
		} catch (IllegalArgumentException e) {
			SLPCore.platform.logError(e.getMessage(), e);
			return new ArrayList();
			// may never happen!!!
		} catch (ParserException e) {
			SLPCore.platform.logTraceDrop(e.getMessage());
			rule = e.getRule();
		}
		AttributeListVisitor visitor = new AttributeListVisitor();
		rule.visit(visitor);
		return visitor.getAttributes();
	}
}
