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
package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.browsing.RubyBrowsingMessages;
import org.rubypeople.rdt.internal.ui.viewsupport.SourcePositionSorter;
import org.rubypeople.rdt.ui.RubyElementSorter;

/*
 * XXX: This class should become part of the MemberFilterActionGroup
 *      which should be renamed to MemberActionsGroup
 */
public class LexicalSortingAction extends Action {
	private RubyElementSorter fSorter= new RubyElementSorter();
	private SourcePositionSorter fSourcePositonSorter= new SourcePositionSorter();
	private StructuredViewer fViewer;
	private String fPreferenceKey;

	public LexicalSortingAction(StructuredViewer viewer, String id) {
		super();
		fViewer= viewer;
		fPreferenceKey= "LexicalSortingAction." + id + ".isChecked"; //$NON-NLS-1$ //$NON-NLS-2$
		setText(RubyBrowsingMessages.LexicalSortingAction_label); 
		RubyPluginImages.setLocalImageDescriptors(this, "alphab_sort_co.gif"); //$NON-NLS-1$
		setToolTipText(RubyBrowsingMessages.LexicalSortingAction_tooltip); 
		setDescription(RubyBrowsingMessages.LexicalSortingAction_description); 
		boolean checked= RubyPlugin.getDefault().getPreferenceStore().getBoolean(fPreferenceKey); 
		valueChanged(checked, false);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.LEXICAL_SORTING_BROWSING_ACTION);
	}

	public void run() {
		valueChanged(isChecked(), true);
	}

	private void valueChanged(final boolean on, boolean store) {
		setChecked(on);
		BusyIndicator.showWhile(fViewer.getControl().getDisplay(), new Runnable() {
			public void run() {
				if (on)
					fViewer.setSorter(fSorter);
				else
					fViewer.setSorter(fSourcePositonSorter);
			}
		});
		
		if (store)
			RubyPlugin.getDefault().getPreferenceStore().setValue(fPreferenceKey, on);
	}
}
