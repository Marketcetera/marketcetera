/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.packageview;

import org.rubypeople.rdt.internal.ui.actions.AbstractToggleLinkingAction;
import org.rubypeople.rdt.ui.IPackagesViewPart;


/**
 * This action toggles whether this package explorer links its selection to the active
 * editor.
 * 
 * @since 2.1
 */
public class ToggleLinkingAction extends AbstractToggleLinkingAction {
	
	private IPackagesViewPart fPackageExplorerPart;
	
	/**
	 * Constructs a new action.
	 */
	public ToggleLinkingAction(IPackagesViewPart explorer) {
		setChecked(explorer.isLinkingEnabled());
		fPackageExplorerPart= explorer;
	}

	/**
	 * Runs the action.
	 */
	public void run() {
		fPackageExplorerPart.setLinkingEnabled(isChecked());
	}

}
