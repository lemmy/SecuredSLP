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

import java.security.interfaces.DSAKey;

import tgdh.TgdhKeyListener;

/**
 * @author Markus Alexander Kuppe
 */
public class SLPTgdhKeyListener extends TgdhKeyListener {

	/* (non-Javadoc)
	 * @see tgdh.TgdhKeyListener#keyChanged(java.security.interfaces.DSAKey)
	 */
	public void keyChanged(DSAKey dsaKey) {
		String groupName = this.getGroupIdentifer().getGroupName();
		SLPCore.sgSessionKeys.put(groupName, dsaKey);
	}
}
