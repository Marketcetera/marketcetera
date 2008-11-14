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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IShowInTargetList;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.viewsupport.FilterUpdater;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTreeViewer;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.actions.ProjectActionGroup;

public class ProjectsView extends RubyBrowsingPart {

	private FilterUpdater fFilterUpdater;

	/**
	 * Creates the viewer of this part.
	 *
	 * @param parent	the parent for the viewer
	 */
	protected StructuredViewer createViewer(Composite parent) {
		ProblemTreeViewer result= new ProblemTreeViewer(parent, SWT.MULTI);
		fFilterUpdater= new FilterUpdater(result);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fFilterUpdater);
		return result;
	}

	/* (non-Rubydoc)
	 * @see org.eclipse.jdt.internal.ui.browsing.RubyBrowsingPart#dispose()
	 */
	public void dispose() {
		if (fFilterUpdater != null)
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fFilterUpdater);
		super.dispose();
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
	 * Creates the content provider of this part.
	 */
	protected IContentProvider createContentProvider() {
		return new ProjectAndSourceFolderContentProvider(this);
	}

	/**
	 * Returns the context ID for the Help system.
	 *
	 * @return	the string used as ID for the Help context
	 */
	protected String getHelpContextId() {
		return IRubyHelpContextIds.PROJECTS_VIEW;
	}

	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_PROJECTS_TO_EDITOR;
	}


	/**
	 * Adds additional listeners to this view.
	 */
	protected void hookViewerListeners() {
		super.hookViewerListeners();
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer= (TreeViewer)getViewer();
				Object element= ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (viewer.isExpandable(element))
					viewer.setExpandedState(element, !viewer.getExpandedState(element));
			}
		});
	}

	protected void setInitialInput() {
		IRubyElement root= RubyCore.create(RubyPlugin.getWorkspace().getRoot());
		getViewer().setInput(root);
		updateTitle();
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * input for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		return element instanceof IRubyModel;
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		return element instanceof IRubyProject || element instanceof ISourceFolderRoot;
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
			case IRubyElement.RUBY_MODEL :
				return null;
			case IRubyElement.RUBY_PROJECT:
				return je;
			case IRubyElement.SOURCE_FOLDER_ROOT:
				if (je.getElementName().equals(ISourceFolderRoot.DEFAULT_PACKAGEROOT_PATH))
					return je.getParent();
				else
					return je;
			default :
				return findElementToSelect(je.getParent());
		}
	}

	/*
	 * @see RubyBrowsingPart#setInput(Object)
	 */
	protected void setInput(Object input) {
		// Don't allow to clear input for this view
		if (input != null)
			super.setInput(input);
		else
			getViewer().setSelection(null);
	}

	protected void createActions() {
		super.createActions();
		fActionGroups.addGroup(new ProjectActionGroup(this));
	}

	/**
	 * Handles selection of LogicalPackage in Packages view.
	 *
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 * @since 2.1
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!needsToProcessSelectionChanged(part, selection))
			return;

//		if (selection instanceof IStructuredSelection) {
//			IStructuredSelection sel= (IStructuredSelection)selection;
//			Iterator iter= sel.iterator();
//			while (iter.hasNext()) {
//				Object selectedElement= iter.next();
//				if (selectedElement instanceof LogicalPackage) {
//					selection= new StructuredSelection(((LogicalPackage)selectedElement).getRubyProject());
//					break;
//				}
//			}
//		}
		super.selectionChanged(part, selection);
	}
}
