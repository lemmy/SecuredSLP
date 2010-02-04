/*******************************************************************************
 * Copyright (c) 2010 Markus Alexander Kuppe
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Alexander Kuppe (ecf-dev <at> lemmster <dot> de) - initial API and implementation
 ******************************************************************************/
package ch.ethz.iks.slp.impl.sec;

import javax.crypto.spec.SecretKeySpec;

public class SecurityGroupSessionKey {
	
	public static String getFQN(String securityGroupIdentifier, String securityGroupSessionKeyIdentifier) {
		return securityGroupIdentifier + ":" + securityGroupSessionKeyIdentifier;
	}
	
	private SecretKeySpec secKeySpec;
	private String securityGroupSessionKeyIdentifier;
	private String securityGroupIdentifer;
	
	public SecurityGroupSessionKey(SecretKeySpec secKeySpec,
			String securityGroupSessionKeyIdentifier,
			String securityGroupIdentifer) {
		super();
		this.secKeySpec = secKeySpec;
		this.securityGroupSessionKeyIdentifier = securityGroupSessionKeyIdentifier;
		this.securityGroupIdentifer = securityGroupIdentifer;
	}

	/**
	 * @return the secKeySpec
	 */
	public SecretKeySpec getSecKeySpec() {
		return secKeySpec;
	}

	/**
	 * @return the securityGroupSessionKeyIdentifier
	 */
	public String getSecurityGroupSessionKeyIdentifier() {
		return securityGroupSessionKeyIdentifier;
	}

	/**
	 * @return the securityGroupIdentifer
	 */
	public String getSecurityGroupIdentifer() {
		return securityGroupIdentifer;
	}

	public String getFQN() {
		return getFQN(getSecurityGroupIdentifer(), getSecurityGroupSessionKeyIdentifier());
	}
}
