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
package org.rubypeople.rdt.internal.ui.browsing;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IShowInTargetList;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.actions.SelectAllAction;
import org.rubypeople.rdt.internal.ui.filters.NonRubyElementFilter;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.RubyUI;
public class TypesView extends RubyBrowsingPart {

	private SelectAllAction fSelectAllAction;
	private boolean fLastInputWasProject;

	/**
	 * Creates and returns the label provider for this part.
	 *
	 * @return the label provider
	 * @see org.eclipse.jface.viewers.ILabelProvider
	 */
	protected RubyUILabelProvider createLabelProvider() {
		return new AppearanceAwareLabelProvider(
						AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | RubyElementLabels.T_CATEGORY | RubyElementLabels.T_NAME_FULLY_QUALIFIED,
						AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS);
	}

	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { RubyUI.ID_RUBY_EXPLORER, IPageLayout.ID_RES_NAV  };
				}

			};
		}
		return super.getAdapter(key);
	}

	/**
	 * Adds filters the viewer of this part.
	 */
	protected void addFilters() {
		super.addFilters();
		getViewer().addFilter(new NonRubyElementFilter());
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * input for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		if (element instanceof IRubyProject || (element instanceof ISourceFolderRoot && ((IRubyElement)element).getElementName() != ISourceFolderRoot.DEFAULT_PACKAGEROOT_PATH))
			try {
				IRubyProject jProject= ((IRubyElement)element).getRubyProject();
				if (jProject != null)
					return jProject.getProject().hasNature(RubyCore.NATURE_ID);
			} catch (CoreException ex) {
				return false;
			}
		return false;
	}
	
	@Override
	protected IContentProvider createContentProvider() {
		return new TypesContentProvider(this);
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element instanceof IRubyScript)
			return super.isValidElement(((IRubyScript)element).getParent());
		else if (element instanceof IType) {
			IType type= (IType)element;
			return /*type.getDeclaringType() == null &&*/ isValidElement(type.getRubyScript());
		}
		return false;
	}

	/**
	 * Finds the element which has to be selected in this part.
	 *
	 * @param je	the Ruby element which has the focus
	 */
	protected IRubyElement findElementToSelect(IRubyElement je) {
		if (je == null)
			return null;

		switch (je.getElementType()) {
			case IRubyElement.TYPE:
				IType type= ((IType)je).getDeclaringType();
				if (type == null)
					type= (IType)je;
				return getSuitableRubyElement(type);
			case IRubyElement.SCRIPT:
				return getTypeForRubyScript((IRubyScript)je);
			case IRubyElement.IMPORT_CONTAINER:
			case IRubyElement.IMPORT_DECLARATION:
				return findElementToSelect(je.getParent());
			default:
				if (je instanceof IMember)
					return findElementToSelect(((IMember)je).getDeclaringType());
				return null;

		}
	}

	protected IRubyElement findInputForRubyElement(IRubyElement je) {
		// null check has to take place here as well (not only in
		// findInputForRubyElement(IRubyElement, boolean) since we
		// are accessing the Ruby element
		if (je == null)
			return null;
		if(je.getElementType() == IRubyElement.SOURCE_FOLDER_ROOT || je.getElementType() == IRubyElement.RUBY_PROJECT)
			return findInputForRubyElement(je, true);
		else
			return findInputForRubyElement(je, false);

	}

	protected IRubyElement findInputForRubyElement(IRubyElement je, boolean canChangeInputType) {
		if (je == null || !je.exists())
			return null;

		if (isValidInput(je)) {

			//don't update if input must be project (i.e. project is used as source folder)
			if (canChangeInputType)
				fLastInputWasProject= je.getElementType() == IRubyElement.RUBY_PROJECT;
			return je;
		} else if (fLastInputWasProject) {
			ISourceFolderRoot packageFragmentRoot= (ISourceFolderRoot)je.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
			if (!packageFragmentRoot.isExternal())
				return je.getRubyProject();
		}

		return findInputForRubyElement(je.getParent(), canChangeInputType);
	}
	
	/**
	 * Returns the context ID for the Help system
	 *
	 * @return	the string used as ID for the Help context
	 */
	protected String getHelpContextId() {
		return IRubyHelpContextIds.TYPES_VIEW;
	}

	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_TYPES_TO_EDITOR;
	}

	protected void createActions() {
		super.createActions();
		fSelectAllAction= new SelectAllAction((TableViewer)getViewer());
	}

	protected void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		// Add selectAll action handlers.
		actionBars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL, fSelectAllAction);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.browsing.RubyBrowsingPart#createDecoratingLabelProvider(org.eclipse.jdt.internal.ui.viewsupport.RubyUILabelProvider)
	 */
	protected DecoratingLabelProvider createDecoratingLabelProvider(RubyUILabelProvider provider) {
		DecoratingLabelProvider decoratingLabelProvider= super.createDecoratingLabelProvider(provider);
		provider.addLabelDecorator(new TopLevelTypeProblemsLabelDecorator(null));
		return decoratingLabelProvider;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof MembersView) return;
		super.selectionChanged(part, selection);
	}
}
