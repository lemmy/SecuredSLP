/*******************************************************************************
 * Copyright (c) 2009 Markus Alexander Kuppe
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/

package ch.ethz.iks.slp.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import ch.ethz.iks.slp.impl.sec.SecurityGroupSessionKey;


import tgdh.TgdhKeyListener;

/**
 * @author Markus Alexander Kuppe
 */
public class SLPTgdhKeyListener extends TgdhKeyListener {
	
	private MessageDigest md = null;

	public SLPTgdhKeyListener() {
		try {
			//TODO make hash algorithm configurable
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see tgdh.TgdhKeyListener#keyChanged()
	 */
	public void keyChanged(byte[] sessionKey) {
		// AES keys are max 128, thus keyspec is 16 byte and not 64 provided by tgdh
		final byte[] keyBits = new byte[16];
		System.arraycopy(sessionKey, 0, keyBits, 0, 16);
		
		// base64 encoded version of sha-512 key hash is used for key id
		byte[] digest = md.digest(keyBits);
		//TODO use Apache commons instead of javax.xml.* once we move to whiteboard pattern
		String keyName = DatatypeConverter.printBase64Binary(digest);
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBits, SLPCore.CONFIG.getEncryptionAlgorithm());
		String groupName = groupIdentifer.getGroupName();

		SecurityGroupSessionKey sgSessionKey = new SecurityGroupSessionKey(secretKeySpec, keyName, groupName);

		// make the new GSK available to the receiver fully qualified
		SLPCore.sgSessionKeys.put(sgSessionKey.getFQN(), sgSessionKey);
		
		// the sender always just uses "$SG name" 
		SLPCore.sgSessionKeys.put(sgSessionKey.getSecurityGroupIdentifer(), sgSessionKey);
	}
	
}
