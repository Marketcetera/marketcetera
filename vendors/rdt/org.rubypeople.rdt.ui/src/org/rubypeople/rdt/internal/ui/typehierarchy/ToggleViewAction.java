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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.util.Assert;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

/**
 * Action to switch between the different hierarchy views.
 */
public class ToggleViewAction extends Action {
	
	private TypeHierarchyViewPart fViewPart;
	private int fViewerIndex;
		
	public ToggleViewAction(TypeHierarchyViewPart v, int viewerIndex) {
		super("", AS_RADIO_BUTTON); //$NON-NLS-1$
		String contextHelpId= null;
		if (viewerIndex == TypeHierarchyViewPart.VIEW_ID_SUPER) {
			setText(TypeHierarchyMessages.ToggleViewAction_supertypes_label); 
			contextHelpId= IRubyHelpContextIds.SHOW_SUPERTYPES;
			setDescription(TypeHierarchyMessages.ToggleViewAction_supertypes_description); 
			setToolTipText(TypeHierarchyMessages.ToggleViewAction_supertypes_tooltip); 
			RubyPluginImages.setLocalImageDescriptors(this, "super_co.gif"); //$NON-NLS-1$
		} else if (viewerIndex == TypeHierarchyViewPart.VIEW_ID_SUB) {
			setText(TypeHierarchyMessages.ToggleViewAction_subtypes_label); 
			contextHelpId= IRubyHelpContextIds.SHOW_SUBTYPES;
			setDescription(TypeHierarchyMessages.ToggleViewAction_subtypes_description); 
			setToolTipText(TypeHierarchyMessages.ToggleViewAction_subtypes_tooltip); 
			RubyPluginImages.setLocalImageDescriptors(this, "sub_co.gif"); //$NON-NLS-1$
		} else if (viewerIndex == TypeHierarchyViewPart.VIEW_ID_TYPE) {
			setText(TypeHierarchyMessages.ToggleViewAction_vajhierarchy_label); 
			contextHelpId= IRubyHelpContextIds.SHOW_HIERARCHY;
			setDescription(TypeHierarchyMessages.ToggleViewAction_vajhierarchy_description); 
			setToolTipText(TypeHierarchyMessages.ToggleViewAction_vajhierarchy_tooltip); 
			RubyPluginImages.setLocalImageDescriptors(this, "hierarchy_co.gif"); //$NON-NLS-1$
		} else {
			Assert.isTrue(false);
		}		
		
		fViewPart= v;
		fViewerIndex= viewerIndex;
		
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, contextHelpId);
	}
				
	public int getViewerIndex() {
		return fViewerIndex;
	}

	/*
	 * @see Action#actionPerformed
	 */	
	public void run() {
		fViewPart.setView(fViewerIndex);
	}		
}
