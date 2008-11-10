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
package org.rubypeople.rdt.internal.ui.typehierarchy;

import org.rubypeople.rdt.internal.ui.actions.AbstractToggleLinkingAction;


/**
 * This action toggles whether the type hierarchy links its selection to the active
 * editor.
 * 
 * @since 2.1
 */
public class ToggleLinkingAction extends AbstractToggleLinkingAction {
	
	TypeHierarchyViewPart fHierarchyViewPart;
	
	/**
	 * Constructs a new action.
	 */
	public ToggleLinkingAction(TypeHierarchyViewPart part) {
		setChecked(part.isLinkingEnabled());
		fHierarchyViewPart= part;
	}

	/**
	 * Runs the action.
	 */
	public void run() {
		fHierarchyViewPart.setLinkingEnabled(isChecked());
	}

}
