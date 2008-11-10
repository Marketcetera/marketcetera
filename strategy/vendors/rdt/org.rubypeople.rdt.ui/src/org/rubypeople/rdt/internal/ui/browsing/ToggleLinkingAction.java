/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.browsing;

import org.rubypeople.rdt.internal.ui.actions.AbstractToggleLinkingAction;

/**
 * This action toggles whether this package explorer links its selection to the active
 * editor.
 *
 * @since 2.1
 */
public class ToggleLinkingAction extends AbstractToggleLinkingAction {

	RubyBrowsingPart fRubyBrowsingPart;

	/**
	 * Constructs a new action.
	 */
	public ToggleLinkingAction(RubyBrowsingPart part) {
		setChecked(part.isLinkingEnabled());
		fRubyBrowsingPart= part;
	}

	/**
	 * Runs the action.
	 */
	public void run() {
		fRubyBrowsingPart.setLinkingEnabled(isChecked());
	}

}
