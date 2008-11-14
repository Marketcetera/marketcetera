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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;


public class RubyBrowsingPerspectiveFactory implements IPerspectiveFactory {

	/*
	 * XXX: This is a workaround for: http://dev.eclipse.org/bugs/show_bug.cgi?id=13070
	 */
	static IRubyElement fgRubyElementFromAction;

	/**
	 * Constructs a new Default layout engine.
	 */
	public RubyBrowsingPerspectiveFactory() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		if (stackBrowsingViewsVertically())
			createVerticalLayout(layout);
		else
			createHorizontalLayout(layout);

		// action sets
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(RubyUI.ID_ACTION_SET);
		layout.addActionSet(RubyUI.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// views - ruby
		//layout.addShowViewShortcut(RubyUI.ID_TYPE_HIERARCHY);
		layout.addShowViewShortcut(RubyUI.ID_PROJECTS_VIEW);
		layout.addShowViewShortcut(RubyUI.ID_TYPES_VIEW);
		layout.addShowViewShortcut(RubyUI.ID_MEMBERS_VIEW);
//		layout.addShowViewShortcut(RubyUI.ID_SOURCE_VIEW);

		// views - search
		layout.addShowViewShortcut(NewSearchUI.SEARCH_VIEW_ID);

		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);

		// new actions - Ruby project creation wizard
		layout.addNewWizardShortcut("org.rubypeople.rdt.ui.wizards.RubyProjectWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.rubypeople.rdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
	}

	private void createVerticalLayout(IPageLayout layout) {
		String relativePartId= IPageLayout.ID_EDITOR_AREA;
		int relativePos= IPageLayout.LEFT;

		IPlaceholderFolderLayout placeHolderLeft= layout.createPlaceholderFolder("left", IPageLayout.LEFT, (float)0.25, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		//placeHolderLeft.addPlaceholder(RubyUI.ID_TYPE_HIERARCHY);
		placeHolderLeft.addPlaceholder(IPageLayout.ID_OUTLINE);
		placeHolderLeft.addPlaceholder(IPageLayout.ID_RES_NAV);

		if (shouldShowProjectsView()) {
			layout.addView(RubyUI.ID_PROJECTS_VIEW, IPageLayout.LEFT, (float)0.25, IPageLayout.ID_EDITOR_AREA);
			relativePartId= RubyUI.ID_PROJECTS_VIEW;
			relativePos= IPageLayout.BOTTOM;
		}
		layout.addView(RubyUI.ID_TYPES_VIEW, relativePos, (float)0.33, relativePartId);
		layout.addView(RubyUI.ID_MEMBERS_VIEW, IPageLayout.BOTTOM, (float)0.50, RubyUI.ID_TYPES_VIEW);

		IPlaceholderFolderLayout placeHolderBottom= layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, (float)0.75, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		placeHolderBottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		placeHolderBottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		placeHolderBottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		placeHolderBottom.addPlaceholder(IPageLayout.ID_BOOKMARKS);
//		placeHolderBottom.addPlaceholder(RubyUI.ID_SOURCE_VIEW);
		placeHolderBottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
	}

	private void createHorizontalLayout(IPageLayout layout) {
		String relativePartId= IPageLayout.ID_EDITOR_AREA;
		int relativePos= IPageLayout.TOP;

		if (shouldShowProjectsView()) {
			layout.addView(RubyUI.ID_PROJECTS_VIEW, IPageLayout.TOP, (float)0.25, IPageLayout.ID_EDITOR_AREA);
			relativePartId= RubyUI.ID_PROJECTS_VIEW;
			relativePos= IPageLayout.RIGHT;
		}
		layout.addView(RubyUI.ID_TYPES_VIEW, relativePos, (float)0.33, relativePartId);
		layout.addView(RubyUI.ID_MEMBERS_VIEW, IPageLayout.RIGHT, (float)0.50, RubyUI.ID_TYPES_VIEW);

		IPlaceholderFolderLayout placeHolderLeft= layout.createPlaceholderFolder("left", IPageLayout.LEFT, (float)0.25, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
//		placeHolderLeft.addPlaceholder(RubyUI.ID_TYPE_HIERARCHY);
		placeHolderLeft.addPlaceholder(IPageLayout.ID_OUTLINE);
		placeHolderLeft.addPlaceholder(IPageLayout.ID_RES_NAV);


		IPlaceholderFolderLayout placeHolderBottom= layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, (float)0.75, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		placeHolderBottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		placeHolderBottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		placeHolderBottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		placeHolderBottom.addPlaceholder(IPageLayout.ID_BOOKMARKS);
//		placeHolderBottom.addPlaceholder(RubyUI.ID_SOURCE_VIEW);
		placeHolderBottom.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);
	}

	private boolean shouldShowProjectsView() {
		return fgRubyElementFromAction == null || fgRubyElementFromAction.getElementType() == IRubyElement.RUBY_MODEL;
	}

	private boolean stackBrowsingViewsVertically() {
		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.BROWSING_STACK_VERTICALLY);
	}

	/*
	 * XXX: This is a workaround for: http://dev.eclipse.org/bugs/show_bug.cgi?id=13070
	 */
	static void setInputFromAction(IAdaptable input) {
		if (input instanceof IRubyElement)
			fgRubyElementFromAction= (IRubyElement)input;
		else
			fgRubyElementFromAction= null;
	}
}
