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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTargetList;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.actions.MultiActionGroup;
import org.rubypeople.rdt.internal.ui.actions.SelectAllAction;
import org.rubypeople.rdt.internal.ui.filters.LibraryFilter;
import org.rubypeople.rdt.internal.ui.filters.NonRubyElementFilter;
import org.rubypeople.rdt.internal.ui.packageview.PackageExplorerContentProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.DecoratingRubyLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTableViewer;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTreeViewer;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementSorter;
import org.rubypeople.rdt.ui.RubyUI;


public class PackagesView extends RubyBrowsingPart {

	private static final String TAG_VIEW_STATE= ".viewState"; //$NON-NLS-1$
	private static final int LIST_VIEW_STATE= 0;
	private static final int TREE_VIEW_STATE= 1;

	private SelectAllAction fSelectAllAction;

	private int fCurrViewState;

	private PackageViewerWrapper fWrappedViewer;

	private MultiActionGroup fSwitchActionGroup;
	private boolean fLastInputWasProject;

	/**
	 * Adds filters the viewer of this part.
	 */
	protected void addFilters() {
		super.addFilters();
		getViewer().addFilter(createNonRubyElementFilter());
		getViewer().addFilter(new LibraryFilter());
	}


	/**
	 * Creates new NonRubyElementFilter and overrides method select to allow for
	 * LogicalPackages.
	 * @return NonRubyElementFilter
	 */
	protected NonRubyElementFilter createNonRubyElementFilter() {
		return new NonRubyElementFilter(){
			public boolean select(Viewer viewer, Object parent, Object element){
				return ((element instanceof IRubyElement) || (element instanceof IFolder));
			}
		};
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		//this must be created before all actions and filters
		fWrappedViewer= new PackageViewerWrapper();
		restoreLayoutState(memento);
	}

	private void restoreLayoutState(IMemento memento) {
		if (memento == null) {
			//read state from the preference store
			IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
			fCurrViewState= store.getInt(this.getViewSite().getId() + TAG_VIEW_STATE);
		} else {
			//restore from memento
			Integer integer= memento.getInteger(this.getViewSite().getId() + TAG_VIEW_STATE);
			if ((integer == null) || !isValidState(integer.intValue())) {
				fCurrViewState= LIST_VIEW_STATE;
			} else fCurrViewState= integer.intValue();
		}
	}

	private boolean isValidState(int state) {
		return (state==LIST_VIEW_STATE) || (state==TREE_VIEW_STATE);
	}



	/*
	 * @see org.eclipse.ui.IViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(this.getViewSite().getId()+TAG_VIEW_STATE,fCurrViewState);
	}

	/**
	 * Creates the viewer of this part dependent on the current
	 * layout.
	 *
	 * @param parent the parent for the viewer
	 */
	protected StructuredViewer createViewer(Composite parent) {
		StructuredViewer viewer;
		if(isInListState())
			viewer= createTableViewer(parent);
		else
			viewer= createTreeViewer(parent);

		fWrappedViewer.setViewer(viewer);
		return fWrappedViewer;
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

	protected boolean isInListState() {
		return false;
//		return fCurrViewState== LIST_VIEW_STATE;
	}

	private ProblemTableViewer createTableViewer(Composite parent) {
		return new PackagesViewTableViewer(parent, SWT.MULTI);
	}

	private ProblemTreeViewer createTreeViewer(Composite parent) {
		return new PackagesViewTreeViewer(parent, SWT.MULTI);
	}

	/**
	 * Overrides the createContentProvider from RubyBrowsingPart
	 * Creates the content provider of this part.
	 */
	protected IContentProvider createContentProvider() {
//		if(isInListState())
//			return new PackagesViewFlatContentProvider(fWrappedViewer.getViewer());
//		else return new PackagesViewHierarchicalContentProvider(fWrappedViewer.getViewer());
//		
		return new PackageExplorerContentProvider(false);
	}

//	protected RubyUILabelProvider createLabelProvider() {
////		if(isInListState())
////			return createListLabelProvider();
//		/*else*/ return createTreeLabelProvider();
//	}
//
//	private RubyUILabelProvider createTreeLabelProvider() {
//		return new PackagesViewLabelProvider(PackagesViewLabelProvider.HIERARCHICAL_VIEW_STATE);
//	}

//	private RubyUILabelProvider createListLabelProvider() {
//		return new PackagesViewLabelProvider(PackagesViewLabelProvider.FLAT_VIEW_STATE);
//	}

	/**
	 * Returns the context ID for the Help system
	 *
	 * @return	the string used as ID for the Help context
	 */
	protected String getHelpContextId() {
		return IRubyHelpContextIds.PACKAGES_BROWSING_VIEW;
	}

	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_PACKAGES_TO_EDITOR;
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

	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element instanceof ISourceFolder) {
			IRubyElement parent= ((ISourceFolder)element).getParent();
			if (parent != null)
				return super.isValidElement(parent) || super.isValidElement(parent.getRubyProject());
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
			case IRubyElement.SOURCE_FOLDER:
				return je;
			case IRubyElement.SCRIPT:
				return ((IRubyScript)je).getParent();
			case IRubyElement.TYPE:
				return ((IType)je).getSourceFolder();
			default:
				return findElementToSelect(je.getParent());
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.browsing.RubyBrowsingPart#setInput(java.lang.Object)
	 */
	protected void setInput(Object input) {
		setViewerWrapperInput(input);
		super.updateTitle();
	}

	private void setViewerWrapperInput(Object input) {
		fWrappedViewer.setViewerInput(input);
	}

	/**
	 * @see org.eclipse.jdt.internal.ui.browsing.RubyBrowsingPart#fillActionBars(org.eclipse.ui.IActionBars)
	 */
	protected void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		fSwitchActionGroup.fillActionBars(actionBars);
	}



	private void setUpViewer(StructuredViewer viewer){
		Assert.isTrue(viewer != null);

		RubyUILabelProvider labelProvider= createLabelProvider();
		viewer.setLabelProvider(createDecoratingLabelProvider(labelProvider));

		viewer.setSorter(createRubyElementSorter());
		viewer.setUseHashlookup(true);

		createContextMenu();

		//disapears when control disposed
		addKeyListener();

		//this methods only adds listeners to the viewer,
		//these listenters disapear when the viewer is disposed
		hookViewerListeners();

		// Set content provider
		viewer.setContentProvider(createContentProvider());
		//Disposed when viewer's Control is disposed
		initDragAndDrop();

	}

	protected RubyElementSorter createRubyElementSorter() {
		return new RubyElementSorter();
	}

	protected void setSiteSelectionProvider(){
		getSite().setSelectionProvider(fWrappedViewer);
	}

	//do the same thing as the RubyBrowsingPart but with wrapper
	protected void createActions() {
		super.createActions();

		createSelectAllAction();

		//create the switch action group
		fSwitchActionGroup= createSwitchActionGroup();
	}

	private MultiActionGroup createSwitchActionGroup(){

		LayoutAction switchToFlatViewAction= new LayoutAction(RubyBrowsingMessages.PackagesView_flatLayoutAction_label,LIST_VIEW_STATE);
		LayoutAction switchToHierarchicalViewAction= new LayoutAction(RubyBrowsingMessages.PackagesView_HierarchicalLayoutAction_label, TREE_VIEW_STATE);
		RubyPluginImages.setLocalImageDescriptors(switchToFlatViewAction, "flatLayout.gif"); //$NON-NLS-1$
		RubyPluginImages.setLocalImageDescriptors(switchToHierarchicalViewAction, "hierarchicalLayout.gif"); //$NON-NLS-1$

		return new LayoutActionGroup(new IAction[]{switchToFlatViewAction,switchToHierarchicalViewAction}, fCurrViewState);
	}

	private static class LayoutActionGroup extends MultiActionGroup {

		LayoutActionGroup(IAction[] actions, int index) {
			super(actions, index);
		}

		public void fillActionBars(IActionBars actionBars) {
			//create new layout group
			IMenuManager manager= actionBars.getMenuManager();
			final IContributionItem groupMarker= new GroupMarker("layout"); //$NON-NLS-1$
			manager.add(groupMarker);
			IMenuManager newManager= new MenuManager(RubyBrowsingMessages.PackagesView_LayoutActionGroup_layout_label);
			manager.appendToGroup("layout", newManager); //$NON-NLS-1$
			super.addActions(newManager);
		}
	}


	/**
	 * Switches between flat and hierarchical state.
	 */
	private class LayoutAction extends Action {

		private int fState;

		public LayoutAction(String text, int state) {
			super(text, IAction.AS_RADIO_BUTTON);
			fState= state;
			if (state == PackagesView.LIST_VIEW_STATE)
				PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.LAYOUT_FLAT_ACTION);
			else
				PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.LAYOUT_HIERARCHICAL_ACTION);
		}

		public int getState() {
			return fState;
		}

		public void setRunnable(Runnable runnable) {
			Assert.isNotNull(runnable);
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			switchViewer(fState);
		}
	}

	private void switchViewer(int state) {
		//Indicate which viewer is to be used
		if (fCurrViewState == state)
			return;
		else {
			fCurrViewState= state;
			IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
			store.setValue(getViewSite().getId() + TAG_VIEW_STATE, state);
		}

		//get the information from the existing viewer
		StructuredViewer viewer= fWrappedViewer.getViewer();
		Object object= viewer.getInput();
		ISelection selection= viewer.getSelection();

		// create and set up the new viewer
		Control control= createViewer(fWrappedViewer.getControl().getParent()).getControl();

		setUpViewer(fWrappedViewer);

		createSelectAllAction();

		// add the selection information from old viewer
		fWrappedViewer.setViewerInput(object);
		fWrappedViewer.getControl().setFocus();
		fWrappedViewer.setSelection(selection, true);

		// dispose old viewer
		viewer.getContentProvider().dispose();
		viewer.getControl().dispose();

		// layout the new viewer
		if (control != null && !control.isDisposed()) {
			control.setVisible(true);
			control.getParent().layout(true);
		}
	}

	private void createSelectAllAction() {
		IActionBars actionBars= getViewSite().getActionBars();
		if (isInListState()) {
			fSelectAllAction= new SelectAllAction((TableViewer)fWrappedViewer.getViewer());
			actionBars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL, fSelectAllAction);
		} else {
			actionBars.setGlobalActionHandler(IWorkbenchActionConstants.SELECT_ALL, null);
			fSelectAllAction= null;
		}
		actionBars.updateActionBars();
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
	 * Override the getText and getImage methods for the DecoratingLabelProvider
	 * to handel the decoration of logical packages.
	 *
	 * @see org.eclipse.jdt.internal.ui.browsing.RubyBrowsingPart#createDecoratingLabelProvider(RubyUILabelProvider)
	 */
	protected DecoratingLabelProvider createDecoratingLabelProvider(RubyUILabelProvider provider) {
		return new DecoratingRubyLabelProvider(provider, false);
	}

}
