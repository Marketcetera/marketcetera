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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.framelist.Frame;
import org.eclipse.ui.views.framelist.FrameAction;
import org.eclipse.ui.views.framelist.FrameList;
import org.eclipse.ui.views.framelist.IFrameSource;
import org.eclipse.ui.views.framelist.TreeFrame;
import org.eclipse.ui.views.navigator.LocalSelectionTransfer;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dnd.DelegatingDropAdapter;
import org.rubypeople.rdt.internal.ui.dnd.RdtViewerDragAdapter;
import org.rubypeople.rdt.internal.ui.preferences.MembersOrderPreferenceCache;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.rubyeditor.ExternalRubyFileEditorInput;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.DecoratingRubyLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.FilterUpdater;
import org.rubypeople.rdt.internal.ui.viewsupport.IViewPartInputProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTreeViewer;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.StatusBarUpdater;
import org.rubypeople.rdt.internal.ui.workingsets.ConfigureWorkingSetAction;
import org.rubypeople.rdt.internal.ui.workingsets.ViewActionGroup;
import org.rubypeople.rdt.internal.ui.workingsets.WorkingSetFilterActionGroup;
import org.rubypeople.rdt.internal.ui.workingsets.WorkingSetModel;
import org.rubypeople.rdt.ui.IPackagesViewPart;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabels;
import org.rubypeople.rdt.ui.RubyElementSorter;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;
import org.rubypeople.rdt.ui.actions.CustomFiltersActionGroup;
 
/**
 * The ViewPart for the ProjectExplorer. It listens to part activation events.
 * When selection linking with the editor is enabled the view selection tracks
 * the active editor page. Similarly when a resource is selected in the packages
 * view the corresponding editor is activated. 
 */
 
public class PackageExplorerPart extends ViewPart  
	implements ISetSelectionTarget, IMenuListener,
		IShowInTarget,
		IPackagesViewPart,  IPropertyChangeListener, 
		IViewPartInputProvider {
	
	private static final String PERF_CREATE_PART_CONTROL= "org.rubypeople.rdt.ui/perf/explorer/createPartControl"; //$NON-NLS-1$
	private static final String PERF_MAKE_ACTIONS= "org.rubypeople.rdt.ui/perf/explorer/makeActions"; //$NON-NLS-1$
	
	private boolean fIsCurrentLayoutFlat; // true means flat, false means hierachical

	private static final int HIERARCHICAL_LAYOUT= 0x1;
	private static final int FLAT_LAYOUT= 0x2;
	
	public final static String VIEW_ID= RubyUI.ID_RUBY_EXPLORER;
				
	// Persistance tags.
	static final String TAG_SELECTION= "selection"; //$NON-NLS-1$
	static final String TAG_EXPANDED= "expanded"; //$NON-NLS-1$
	static final String TAG_ELEMENT= "element"; //$NON-NLS-1$
	static final String TAG_PATH= "path"; //$NON-NLS-1$
	static final String TAG_VERTICAL_POSITION= "verticalPosition"; //$NON-NLS-1$
	static final String TAG_HORIZONTAL_POSITION= "horizontalPosition"; //$NON-NLS-1$
	static final String TAG_FILTERS = "filters"; //$NON-NLS-1$
	static final String TAG_FILTER = "filter"; //$NON-NLS-1$
	static final String TAG_LAYOUT= "layout"; //$NON-NLS-1$
	static final String TAG_CURRENT_FRAME= "currentFramge"; //$NON-NLS-1$
	static final String TAG_ROOT_MODE= "rootMode"; //$NON-NLS-1$
	static final String SETTING_MEMENTO= "memento"; //$NON-NLS-1$
	
	private int fRootMode;
	private WorkingSetModel fWorkingSetModel;
	
	private PackageExplorerLabelProvider fLabelProvider;	
	private PackageExplorerContentProvider fContentProvider;
	private FilterUpdater fFilterUpdater;
	
	private RubyExplorerActionGroup fActionSet;
	private ProblemTreeViewer fViewer; 
	private Menu fContextMenu;		
	
	private IMemento fMemento;
	
	private ISelection fLastOpenSelection;
	private ISelectionChangedListener fPostSelectionListener;
	
	private String fWorkingSetLabel;
	
	private IPartListener fPartListener= new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			if (part instanceof IEditorPart)
				editorActivated((IEditorPart) part);
		}
		public void partBroughtToTop(IWorkbenchPart part) {
		}
		public void partClosed(IWorkbenchPart part) {
		}
		public void partDeactivated(IWorkbenchPart part) {
		}
		public void partOpened(IWorkbenchPart part) {
		}
	};
	
	private ITreeViewerListener fExpansionListener= new ITreeViewerListener() {
		public void treeCollapsed(TreeExpansionEvent event) {
		}
		
		public void treeExpanded(TreeExpansionEvent event) {
			Object element= event.getElement();
			if (element instanceof IRubyScript)
				expandMainType(element);
		}
	};

	private class PackageExplorerProblemTreeViewer extends ProblemTreeViewer {
		// fix for 64372  Projects showing up in Package Explorer twice [package explorer] 
		private List fPendingRefreshes;
		
		public PackageExplorerProblemTreeViewer(Composite parent, int style) {
			super(parent, style);
			fPendingRefreshes= Collections.synchronizedList(new ArrayList());
		}
		public void add(Object parentElement, Object[] childElements) {
			if (fPendingRefreshes.contains(parentElement)) {
				return;
			}
			super.add(parentElement, childElements);
		}
						
		/* (non-Rubydoc)
		 * @see org.eclipse.jface.viewers.AbstractTreeViewer#internalRefresh(java.lang.Object, boolean)
		 */
	    protected void internalRefresh(Object element, boolean updateLabels) {
			try {
				fPendingRefreshes.add(element);
				super.internalRefresh(element, updateLabels);
			} finally {
				fPendingRefreshes.remove(element);
			}
		}
		
		/*
		 * @see org.eclipse.jface.viewers.StructuredViewer#filter(java.lang.Object)
		 */
		protected Object[] getFilteredChildren(Object parent) {
			Object[] children = getRawChildren(parent);
			if (!hasFilters()) {
				return children;
			}
			List list = new ArrayList();
			ViewerFilter[] filters = getFilters();

			for (int i = 0; i < children.length; i++) {
				Object object = children[i];
				if (!isFiltered(object, parent, filters)) {
					list.add(object);
				}
			}
			return list.toArray();
		}
		
		protected boolean evaluateExpandableWithFilters(Object parent) {
			if (parent instanceof IRubyProject
					|| parent instanceof IRubyScript
					|| parent instanceof LoadPathContainer) {
				return false;
			}
			if (parent instanceof ISourceFolderRoot && ((ISourceFolderRoot) parent).isArchive()) {
				return false;
			}
			return true;
		}

		protected boolean isFiltered(Object object, Object parent, ViewerFilter[] filters) {
			boolean res= super.isFiltered(object, parent, filters);
			if (res && isEssential(object)) {
				return false;
			}
			return res;
		}
		
		/*
		 * @see org.eclipse.jface.viewers.StructuredViewer#filter(java.lang.Object[])
		 * @since 3.0
		 */
		protected Object[] filter(Object[] elements) {
			if (isFlatLayout())
				return super.filter(elements);

			ViewerFilter[] filters= getFilters();
			if (filters == null || filters.length == 0)
				return elements;
			
			ArrayList filtered= new ArrayList(elements.length);
			Object root= getRoot();
			for (int i= 0; i < elements.length; i++) {
				boolean add= true;
				if (!isEssential(elements[i])) {
					for (int j = 0; j < filters.length; j++) {
						add= filters[j].select(this, root,
							elements[i]);
						if (!add)
							break;
					}
				}
				if (add)
					filtered.add(elements[i]);
			}
			return filtered.toArray();
		}
		
		/* Checks if a filtered object is essential (ie. is a parent that
		 * should not be removed).
		 */ 
		private boolean isEssential(Object object) {
			// just hide folders if they match filter, don't do children check
//			try {
//				if (!isFlatLayout() && object instanceof ISourceFolder) {
//					ISourceFolder fragment = (ISourceFolder) object;
//					if (!fragment.isDefaultPackage() && fragment.hasSubfolders()) {
//						return getFilteredChildren(fragment).length != 0;
//					}
//				}
//			} catch (RubyModelException e) {
//				RubyPlugin.log(e);
//			}
			return false;
		}
		
		protected void handleInvalidSelection(ISelection invalidSelection, ISelection newSelection) {
			IStructuredSelection is= (IStructuredSelection)invalidSelection;
			List ns= null;
			if (newSelection instanceof IStructuredSelection) {
				ns= new ArrayList(((IStructuredSelection)newSelection).toList());
			} else {
				ns= new ArrayList();
			}
			boolean changed= false;
			for (Iterator iter= is.iterator(); iter.hasNext();) {
				Object element= iter.next();
				if (element instanceof IRubyProject) {
					IProject project= ((IRubyProject)element).getProject();
					if (!project.isOpen() && project.exists()) {
						ns.add(project);
						changed= true;
					}
				} else if (element instanceof IProject) {
					IProject project= (IProject)element;
					if (project.isOpen()) {
						IRubyProject jProject= RubyCore.create(project);
						if (jProject != null && jProject.exists())
							ns.add(jProject);
							changed= true;
					}
				}
			}
			if (changed) {
				newSelection= new StructuredSelection(ns);
				setSelection(newSelection);
			}
			super.handleInvalidSelection(invalidSelection, newSelection);
		}
		
		/**
		 * {@inheritDoc}
		 */
		protected Object[] addAditionalProblemParents(Object[] elements) {
			if (showWorkingSets() && elements != null) {
				return fWorkingSetModel.addWorkingSets(elements);
			}
			return elements;
		}
		
	    //---- special handling to preserve the selection correctly
	    private boolean fInPreserveSelection;
		protected void preservingSelection(Runnable updateCode) {
			try {
				fInPreserveSelection= true;
				super.preservingSelection(updateCode);
			} finally {
				fInPreserveSelection= false;
			}
		}
		protected void setSelectionToWidget(ISelection selection, boolean reveal) {
			if (true) {
				super.setSelectionToWidget(selection, reveal);
				return;
			}
			if (!fInPreserveSelection || !(selection instanceof ITreeSelection)) {
				super.setSelectionToWidget(selection, reveal);
				return;
			}
			IContentProvider cp= getContentProvider();
			if (!(cp instanceof IMultiElementTreeContentProvider)) {
				super.setSelectionToWidget(selection, reveal);
				return;
			}
			IMultiElementTreeContentProvider contentProvider= (IMultiElementTreeContentProvider)cp;
			ITreeSelection toRestore= (ITreeSelection)selection;
			List pathsToSelect= new ArrayList();
			for (Iterator iter= toRestore.iterator(); iter.hasNext();) {
				Object element= iter.next();
				TreePath[] pathsToRestore= toRestore.getPathsFor(element);
				CustomHashtable currentParents= createRootAccessedMap(contentProvider.getTreePaths(element));
				for (int i= 0; i < pathsToRestore.length; i++) {
					TreePath path= pathsToRestore[i];
					Object root= path.getFirstSegment();
					if (root != null && path.equals((TreePath)currentParents.get(root), getComparer())) {
						pathsToSelect.add(path);
					}
				}
			}
			List toSelect= new ArrayList();
			for (Iterator iter= pathsToSelect.iterator(); iter.hasNext();) {
				TreePath path= (TreePath)iter.next();
				int size= path.getSegmentCount();
				if (size == 0)
					continue;
				Widget current= getTree();
				int last= size - 1;
				Object segment;
				for (int i= 0; i < size && current != null && (segment= path.getSegment(i)) != null; i++) {
					internalExpandToLevel(current, 1);
					current= internalFindChild(current, segment);
					if (i == last && current != null)
						toSelect.add(current);
				}
			}
			getTree().setSelection((TreeItem[])toSelect.toArray(new TreeItem[toSelect.size()]));
		}
	    private Widget internalFindChild(Widget parent, Object element) {
	        Item[] items = getChildren(parent);
	        for (int i = 0; i < items.length; i++) {
	            Item item = items[i];
	            Object data = item.getData();
	            if (data != null && equals(data, element))
	                return item;
	        }
	        return null;
	    }
		private CustomHashtable createRootAccessedMap(TreePath[] paths) {
			CustomHashtable result= new CustomHashtable(getComparer());
			for (int i= 0; i < paths.length; i++) {
				TreePath path= paths[i];
				Object root= path.getFirstSegment();
				if (root != null) {
					result.put(root, path);
				}
			}
			return result;
		}
	}
 
	/* (non-Rubydoc)
	 * Method declared on IViewPart.
	 */
	private boolean fLinkingEnabled;

    public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		fMemento= memento;
		if (fMemento == null) {
			IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().getSection(getSectionName());
			if (section != null) {
				String settings= section.get(SETTING_MEMENTO);
				if (settings != null) {
					try {
						fMemento= XMLMemento.createReadRoot(new StringReader(settings));
					} catch (WorkbenchException e) {
						// don't restore the memento when the settings can't be read.
					}
				}
			}
		}
		restoreRootMode(fMemento);
		if (showWorkingSets()) {
			createWorkingSetModel();
		}
		restoreLayoutState(memento);
	}
    
    private String getSectionName() {
    	return "org.eclipse.jdt.ui.internal.packageExplorer"; //$NON-NLS-1$
    }

	private void restoreRootMode(IMemento memento) {
		if (memento != null) {
			Integer value= fMemento.getInteger(TAG_ROOT_MODE);
			fRootMode= value == null ? ViewActionGroup.SHOW_PROJECTS : value.intValue();
			if (fRootMode != ViewActionGroup.SHOW_PROJECTS && fRootMode != ViewActionGroup.SHOW_WORKING_SETS)
				fRootMode= ViewActionGroup.SHOW_PROJECTS;
		} else {
			fRootMode= ViewActionGroup.SHOW_PROJECTS;
		}
	}

	private void restoreLayoutState(IMemento memento) {
		Integer state= null;
		if (memento != null)
			state= memento.getInteger(TAG_LAYOUT);

		// If no memento try an restore from preference store
		if(state == null) {
			IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
			state= new Integer(store.getInt(TAG_LAYOUT));
		}

		if (state.intValue() == FLAT_LAYOUT)
			fIsCurrentLayoutFlat= true;
		else if (state.intValue() == HIERARCHICAL_LAYOUT)
			fIsCurrentLayoutFlat= false;
//		else
//			fIsCurrentLayoutFlat= true;
	}
	
	/**
	 * Returns the package explorer part of the active perspective. If 
	 * there isn't any package explorer part <code>null</code> is returned.
	 */
	public static PackageExplorerPart getFromActivePerspective() {
		IWorkbenchPage activePage= RubyPlugin.getActivePage();
		if (activePage == null)
			return null;
		IViewPart view= activePage.findView(VIEW_ID);
		if (view instanceof PackageExplorerPart)
			return (PackageExplorerPart)view;
		return null;	
	}
	
	/**
	 * Makes the package explorer part visible in the active perspective. If there
	 * isn't a package explorer part registered <code>null</code> is returned.
	 * Otherwise the opened view part is returned.
	 */
	public static PackageExplorerPart openInActivePerspective() {
		try {
			return (PackageExplorerPart)RubyPlugin.getActivePage().showView(VIEW_ID);
		} catch(PartInitException pe) {
			return null;
		}
	} 
		
	 public void dispose() {
		if (fContextMenu != null && !fContextMenu.isDisposed())
			fContextMenu.dispose();
		getSite().getPage().removePartListener(fPartListener);
		RubyPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		if (fViewer != null) {
			fViewer.removeTreeListener(fExpansionListener);
			XMLMemento memento= XMLMemento.createWriteRoot("packageexplorer"); //$NON-NLS-1$
			saveState(memento);
			StringWriter writer= new StringWriter();
			try {
				memento.save(writer);
				String sectionName= getSectionName();
				IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().getSection(sectionName);
				if (section == null) {
					section= RubyPlugin.getDefault().getDialogSettings().addNewSection(sectionName);
				}
				section.put(SETTING_MEMENTO, writer.getBuffer().toString());
			} catch (IOException e) {
				// don't do anythiung. Simply don't store the settings
			}
		}
		
		if (fActionSet != null)	
			fActionSet.dispose();
		if (fFilterUpdater != null)
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(fFilterUpdater);
		if (fWorkingSetModel != null)
			fWorkingSetModel.dispose();
		super.dispose();	
	}

	/**
	 * Implementation of IWorkbenchPart.createPartControl(Composite)
	 */
	public void createPartControl(Composite parent) {

		final PerformanceStats stats= PerformanceStats.getStats(PERF_CREATE_PART_CONTROL, this);
		stats.startRun();

		fViewer= createViewer(parent);
		fViewer.setUseHashlookup(true);
		
		initDragAndDrop();
		
		setProviders();
		
		RubyPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	
		
		MenuManager menuMgr= new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);
		fContextMenu= menuMgr.createContextMenu(fViewer.getTree());
		fViewer.getTree().setMenu(fContextMenu);
		
		// Register viewer with site. This must be done before making the actions.
		IWorkbenchPartSite site= getSite();
		site.registerContextMenu(menuMgr, fViewer);
		site.setSelectionProvider(fViewer);
		site.getPage().addPartListener(fPartListener);
		
		if (fMemento != null) {
			restoreLinkingEnabled(fMemento);
		}
		
		makeActions(); // call before registering for selection changes
		
		// Set input after filter and sorter has been set. This avoids resorting and refiltering.
		restoreFilterAndSorter();
		fViewer.setInput(findInputElement());
		initFrameActions();
		initKeyListener();
			

		fViewer.addPostSelectionChangedListener(fPostSelectionListener);
		
		fViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				fActionSet.handleDoubleClick(event);
			}
		});
		
		fViewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				fActionSet.handleOpen(event);
				fLastOpenSelection= event.getSelection();
			}
		});

		IStatusLineManager slManager= getViewSite().getActionBars().getStatusLineManager();
		fViewer.addSelectionChangedListener(new StatusBarUpdater(slManager));
		fViewer.addTreeListener(fExpansionListener);
	
		if (fMemento != null)
			restoreUIState(fMemento);
		fMemento= null;
	
		// Set help for the view 
//		RubyUIHelp.setHelp(fViewer, IRubyHelpContextIds.PACKAGES_VIEW); // FIXME Uncomment When we have RubyUIHelp
		
		fillActionBars();

		updateTitle();
		
		fFilterUpdater= new FilterUpdater(fViewer);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fFilterUpdater);
		
		// Syncing the package explorer has to be done here. It can't be done
		// when restoring the link state since the package explorers input isn't
		// set yet.
		if (isLinkingEnabled()) {
			IEditorPart editor= getViewSite().getPage().getActiveEditor();
			if (editor != null) {
				editorActivated(editor);
			}
		}
		
		stats.endRun();
	}

	private void initFrameActions() {
		fActionSet.getUpAction().update();
		fActionSet.getBackAction().update();
		fActionSet.getForwardAction().update();
	}

	/**
	 * This viewer ensures that non-leaves in the hierarchical
	 * layout are not removed by any filters.
	 * 
	 * @since 2.1
	 */
	private ProblemTreeViewer createViewer(Composite composite) {
		return  new PackageExplorerProblemTreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
	}

	/**
	 * Answers whether this part shows the packages flat or hierarchical.
	 * 
	 * @since 2.1
	 */
	public boolean isFlatLayout() {
		return fIsCurrentLayoutFlat;
	}
	
	private void setProviders() {
		//content provider must be set before the label provider
		fContentProvider= createContentProvider();
		fContentProvider.setIsFlatLayout(fIsCurrentLayoutFlat);
		fViewer.setComparer(createElementComparer());
		fViewer.setContentProvider(fContentProvider);
	
		fLabelProvider= createLabelProvider();
		fLabelProvider.setIsFlatLayout(fIsCurrentLayoutFlat);
		fViewer.setLabelProvider(new DecoratingRubyLabelProvider(fLabelProvider, false/*, fIsCurrentLayoutFlat*/));
		// problem decoration provided by PackageLabelProvider
	}
	
	void toggleLayout() {

		// Update current state and inform content and label providers
		fIsCurrentLayoutFlat= !fIsCurrentLayoutFlat;
		saveLayoutState(null);
		
		fContentProvider.setIsFlatLayout(isFlatLayout());
		fLabelProvider.setIsFlatLayout(isFlatLayout());
		// FIXME Work out the hierarchical/flat stuff!
//		((DecoratingRubyLabelProvider) fViewer.getLabelProvider()).setFlatPackageMode(isFlatLayout());
		
		fViewer.getControl().setRedraw(false);
		fViewer.refresh();
		fViewer.getControl().setRedraw(true);
	}
	
	/**
	 * This method should only be called inside this class
	 * and from test cases.
	 */
	public PackageExplorerContentProvider createContentProvider() {
		IPreferenceStore store= PreferenceConstants.getPreferenceStore();
		boolean showCUChildren= store.getBoolean(PreferenceConstants.SHOW_CU_CHILDREN);
		if (showProjects()) 
			return new PackageExplorerContentProvider(showCUChildren);
		else
			return new WorkingSetAwareContentProvider(showCUChildren, fWorkingSetModel);
	}
	
	private PackageExplorerLabelProvider createLabelProvider() {
		if (showProjects()) 
			return new PackageExplorerLabelProvider(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | RubyElementLabels.P_COMPRESSED | RubyElementLabels.ALL_CATEGORY,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | RubyElementImageProvider.SMALL_ICONS,
				fContentProvider);
		else
			return new WorkingSetAwareLabelProvider(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | RubyElementLabels.P_COMPRESSED,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | RubyElementImageProvider.SMALL_ICONS,
				fContentProvider);			
	}
	
	private IElementComparer createElementComparer() {
		if (showProjects()) 
			return null;
		else
			return WorkingSetModel.COMPARER;
	}
	
	private void fillActionBars() {
		IActionBars actionBars= getViewSite().getActionBars();
		fActionSet.fillActionBars(actionBars);
	}
	
	private Object findInputElement() {
		if (showWorkingSets()) {
			return fWorkingSetModel;
		} else {
			Object input= getSite().getPage().getInput();
			if (input instanceof IWorkspace) { 
				return RubyCore.create(((IWorkspace)input).getRoot());
			} else if (input instanceof IContainer) {
				IRubyElement element= RubyCore.create((IContainer)input);
				if (element != null && element.exists())
					return element;
				return input;
			}
			//1GERPRT: ITPJUI:ALL - Packages View is empty when shown in Type Hierarchy Perspective
			// we can't handle the input
			// fall back to show the workspace
			return RubyCore.create(RubyPlugin.getWorkspace().getRoot());
		}
	}
	
	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key.equals(ISelectionProvider.class))
			return fViewer;
		if (key == IShowInSource.class) {
			return getShowInSource();
		}
		if (key == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { IPageLayout.ID_RES_NAV };
				}

			};
		}
//		 FIXME Uncomment When we have RubyUIHelp
//		if (key == IContextProvider.class) {
//			return RubyUIHelp.getHelpContextProvider(this, IRubyHelpContextIds.PACKAGES_VIEW);
//		}
		return super.getAdapter(key);
	}

	/**
	 * Returns the tool tip text for the given element.
	 */
	String getToolTipText(Object element) {
		String result;
		if (!(element instanceof IResource)) {
			if (element instanceof IRubyModel) {
				result= PackagesMessages.PackageExplorerPart_workspace; 
			} else if (element instanceof IRubyElement){
				result= RubyElementLabels.getTextLabel(element, RubyElementLabels.ALL_FULLY_QUALIFIED);
			} else if (element instanceof IWorkingSet) {
				result= ((IWorkingSet)element).getLabel();
			} else if (element instanceof WorkingSetModel) {
				result= PackagesMessages.PackageExplorerPart_workingSetModel; 
			} else {
				result= fLabelProvider.getText(element);
			}
		} else {
			IPath path= ((IResource) element).getFullPath();
			if (path.isRoot()) {
				result= PackagesMessages.PackageExplorer_title; 
			} else {
				result= path.makeRelative().toString();
			}
		}

		if (fRootMode == ViewActionGroup.SHOW_PROJECTS) {
			if (fWorkingSetLabel == null)
				return result;
			if (result.length() == 0)
				return Messages.format(PackagesMessages.PackageExplorer_toolTip, new String[] { fWorkingSetLabel });
			return Messages.format(PackagesMessages.PackageExplorer_toolTip2, new String[] { result, fWorkingSetLabel });
		} else { // Working set mode. During initialization element and action set can be null.
			if (element != null && !(element instanceof IWorkingSet) && !(element instanceof WorkingSetModel) && fActionSet != null) {
				FrameList frameList= fActionSet.getFrameList();
				int index= frameList.getCurrentIndex();
				IWorkingSet ws= null;
				while(index >= 0) {
					Frame frame= frameList.getFrame(index);
					if (frame instanceof TreeFrame) {
						Object input= ((TreeFrame)frame).getInput();
						if (input instanceof IWorkingSet) {
							ws= (IWorkingSet) input;
							break;
						}
					}
					index--;
				}
				if (ws != null) {
					return Messages.format(PackagesMessages.PackageExplorer_toolTip3, new String[] {ws.getLabel() , result}); 
				} else {
					return result;
				}
			} else {
				return result;
			}
		}
	}
	
	public String getTitleToolTip() {
		if (fViewer == null)
			return super.getTitleToolTip();
		return getToolTipText(fViewer.getInput());
	}
	
	/**
	 * @see IWorkbenchPart#setFocus()
	 */
	public void setFocus() {
		fViewer.getTree().setFocus();
	}

	/**
	 * Returns the current selection.
	 */
	private ISelection getSelection() {
		return fViewer.getSelection();
	}
	  
	//---- Action handling ----------------------------------------------------------
	
	/**
	 * Called when the context menu is about to open. Override
	 * to add your own context dependent menu contributions.
	 */
	public void menuAboutToShow(IMenuManager menu) {
		RubyPlugin.createStandardGroups(menu);
		
		fActionSet.setContext(new ActionContext(getSelection()));
		fActionSet.fillContextMenu(menu);
		fActionSet.setContext(null);
	}

	private void makeActions() {

		final PerformanceStats stats= PerformanceStats.getStats(PERF_MAKE_ACTIONS, this);
		stats.startRun();

		fActionSet= new RubyExplorerActionGroup(this);
		if (fWorkingSetModel != null)
			fActionSet.getWorkingSetActionGroup().setWorkingSetModel(fWorkingSetModel);

		stats.endRun();
	}
	
	// ---- Event handling ----------------------------------------------------------
	
	private void initDragAndDrop() {
		initDrag();
		initDrop();
	}

	private void initDrag() {
		int ops= DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK;
		Transfer[] transfers= new Transfer[] {
			LocalSelectionTransfer.getInstance(), 
			ResourceTransfer.getInstance(),
			FileTransfer.getInstance()};
		TransferDragSourceListener[] dragListeners= new TransferDragSourceListener[] {
			new SelectionTransferDragAdapter(fViewer),
			new ResourceTransferDragAdapter(fViewer),
			new FileTransferDragAdapter(fViewer)
		};
		fViewer.addDragSupport(ops, transfers, new RdtViewerDragAdapter(fViewer, dragListeners));
	}

	private void initDrop() {
		int ops= DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_LINK | DND.DROP_DEFAULT;
		Transfer[] transfers= new Transfer[] {
			LocalSelectionTransfer.getInstance(), 
			FileTransfer.getInstance()};
		TransferDropTargetListener[] dropListeners= new TransferDropTargetListener[] {
			new SelectionTransferDropAdapter(fViewer),
			new FileTransferDropAdapter(fViewer),
//			new WorkingSetDropAdapter(this)
		};
		fViewer.addDropSupport(ops, transfers, new DelegatingDropAdapter(dropListeners));
	}

	/**
	 * Handles post selection changed in viewer.
	 * 
	 * Links to editor (if option enabled).
	 */
	private void handlePostSelectionChanged(SelectionChangedEvent event) {
		ISelection selection= event.getSelection();
		// If the selection is the same as the one that triggered the last
		// open event then do nothing. The editor already got revealed.
		if (isLinkingEnabled() && !selection.equals(fLastOpenSelection)) {
			linkToEditor((IStructuredSelection)selection);
		}
		fLastOpenSelection= null;
	}

	public void selectReveal(ISelection selection) {
		selectReveal(selection, 0);
	}
	
	private void selectReveal(final ISelection selection, final int count) {
		Control ctrl= getViewer().getControl();
		if (ctrl == null || ctrl.isDisposed())
			return;
		ISelection javaSelection= convertSelection(selection);
		fViewer.setSelection(javaSelection, true);
		PackageExplorerContentProvider provider= (PackageExplorerContentProvider)getViewer().getContentProvider();
		ISelection cs= fViewer.getSelection();
		// If we have Pending changes and the element could not be selected then
		// we try it again on more time by posting the select and reveal asynchronously
		// to the event queue. See PR http://bugs.eclipse.org/bugs/show_bug.cgi?id=30700
		// for a discussion of the underlying problem.
		if (count == 0 && provider.hasPendingChanges() && !javaSelection.equals(cs)) {
			ctrl.getDisplay().asyncExec(new Runnable() {
				public void run() {
					selectReveal(selection, count + 1);
				}
			});
		}
	}

	public ISelection convertSelection(ISelection s) {
		if (!(s instanceof IStructuredSelection))
			return s;
			
		Object[] elements= ((IStructuredSelection)s).toArray();
		
		boolean changed= false;
		for (int i= 0; i < elements.length; i++) {
			Object convertedElement= convertElement(elements[i]);
			changed= changed || convertedElement != elements[i];
			elements[i]= convertedElement;
		}
		if (changed)
			return new StructuredSelection(elements);
		else
			return s;
	}

	private Object convertElement(Object original) {
		if (original instanceof IRubyElement) {
			return original;
		
		} else if (original instanceof IResource) {
			IRubyElement je= RubyCore.create((IResource)original);
			if (je != null && je.exists()) 
				return je;
		
		} else if (original instanceof IAdaptable) {
			IAdaptable adaptable= (IAdaptable)original;
			IRubyElement je= (IRubyElement) adaptable.getAdapter(IRubyElement.class);
			if (je != null && je.exists())
				return je;
			
			IResource r= (IResource) adaptable.getAdapter(IResource.class);
			if (r != null) {
				je= RubyCore.create(r);
				if (je != null && je.exists()) 
					return je;
				else
					return r;
			}
		}
		return original;
	}
	
	public void selectAndReveal(Object element) {
		selectReveal(new StructuredSelection(element));
	}
	
	public boolean isLinkingEnabled() {
		return fLinkingEnabled;
	}
	
	/**
	 * Initializes the linking enabled setting from the preference store.
	 */
	private void initLinkingEnabled() {
		fLinkingEnabled= PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
	}


	/**
	 * Links to editor (if option enabled)
	 */
	private void linkToEditor(IStructuredSelection selection) {
		// ignore selection changes if the package explorer is not the active part.
		// In this case the selection change isn't triggered by a user.
		if (!isActivePart())
			return;
		Object obj= selection.getFirstElement();

		if (selection.size() == 1) {
			IEditorPart part= EditorUtility.isOpenInEditor(obj);
			if (part != null) {
				IWorkbenchPage page= getSite().getPage();
				page.bringToTop(part);
				if (obj instanceof IRubyElement)
					EditorUtility.revealInEditor(part, (IRubyElement)obj);
			}
		}
	}

	private boolean isActivePart() {
		return this == getSite().getPage().getActivePart();
	}


	
	public void saveState(IMemento memento) {
		if (fViewer == null) {
			// part has not been created
			if (fMemento != null) //Keep the old state;
				memento.putMemento(fMemento);
			return;
		}
		
		memento.putInteger(TAG_ROOT_MODE, fRootMode);
		if (fWorkingSetModel != null)
			fWorkingSetModel.saveState(memento);
		
		// disable the persisting of state which can trigger expensive operations as
		// a side effect: see bug 52474 and 53958
		// saveCurrentFrame(memento);
		// saveExpansionState(memento);
		// saveSelectionState(memento);
		saveLayoutState(memento);
		saveLinkingEnabled(memento);
		// commented out because of http://bugs.eclipse.org/bugs/show_bug.cgi?id=4676
		// saveScrollState(memento, fViewer.getTree());
		fActionSet.saveFilterAndSorterState(memento);
	}
	
	/*
	private void saveCurrentFrame(IMemento memento) {
        FrameAction action = fActionSet.getUpAction();
        FrameList frameList= action.getFrameList();

		if (frameList.getCurrentIndex() > 0) {
			TreeFrame currentFrame = (TreeFrame) frameList.getCurrentFrame();
			// don't persist the working set model as the current frame
			if (currentFrame.getInput() instanceof WorkingSetModel)
				return;
			IMemento frameMemento = memento.createChild(TAG_CURRENT_FRAME);
			currentFrame.saveState(frameMemento);
		}
	}
	*/

	private void saveLinkingEnabled(IMemento memento) {
		memento.putInteger(PreferenceConstants.LINK_PACKAGES_TO_EDITOR, fLinkingEnabled ? 1 : 0);
	}

	private void saveLayoutState(IMemento memento) {
		if (memento != null) {	
			memento.putInteger(TAG_LAYOUT, getLayoutAsInt());
		} else {
		//if memento is null save in preference store
			IPreferenceStore store= RubyPlugin.getDefault().getPreferenceStore();
			store.setValue(TAG_LAYOUT, getLayoutAsInt());
		}
	}

	private int getLayoutAsInt() {
		if (fIsCurrentLayoutFlat)
			return FLAT_LAYOUT;
		else
			return HIERARCHICAL_LAYOUT;
	}

	protected void saveScrollState(IMemento memento, Tree tree) {
		ScrollBar bar= tree.getVerticalBar();
		int position= bar != null ? bar.getSelection() : 0;
		memento.putString(TAG_VERTICAL_POSITION, String.valueOf(position));
		//save horizontal position
		bar= tree.getHorizontalBar();
		position= bar != null ? bar.getSelection() : 0;
		memento.putString(TAG_HORIZONTAL_POSITION, String.valueOf(position));
	}

	protected void saveSelectionState(IMemento memento) {
		Object elements[]= ((IStructuredSelection) fViewer.getSelection()).toArray();
		if (elements.length > 0) {
			IMemento selectionMem= memento.createChild(TAG_SELECTION);
			for (int i= 0; i < elements.length; i++) {
				IMemento elementMem= selectionMem.createChild(TAG_ELEMENT);
				// we can only persist RubyElements for now
				Object o= elements[i];
				if (o instanceof IRubyElement)
					elementMem.putString(TAG_PATH, ((IRubyElement) elements[i]).getHandleIdentifier());
			}
		}
	}

	protected void saveExpansionState(IMemento memento) {
		Object expandedElements[]= fViewer.getVisibleExpandedElements();
		if (expandedElements.length > 0) {
			IMemento expandedMem= memento.createChild(TAG_EXPANDED);
			for (int i= 0; i < expandedElements.length; i++) {
				IMemento elementMem= expandedMem.createChild(TAG_ELEMENT);
				// we can only persist RubyElements for now
				Object o= expandedElements[i];
				if (o instanceof IRubyElement)
					elementMem.putString(TAG_PATH, ((IRubyElement) expandedElements[i]).getHandleIdentifier());
			}
		}
	}

	private void restoreFilterAndSorter() {
		setSorter();
		if (fMemento != null)	
			fActionSet.restoreFilterAndSorterState(fMemento);
	}

	private void restoreUIState(IMemento memento) {
		// see comment in save state
		// restoreCurrentFrame(memento);
		// restoreExpansionState(memento);
		// restoreSelectionState(memento);
		// commented out because of http://bugs.eclipse.org/bugs/show_bug.cgi?id=4676
		// restoreScrollState(memento, fViewer.getTree());
	}

	/*
	private void restoreCurrentFrame(IMemento memento) {
		IMemento frameMemento = memento.getChild(TAG_CURRENT_FRAME);
		
		if (frameMemento != null) {
	        FrameAction action = fActionSet.getUpAction();
	        FrameList frameList= action.getFrameList();
			TreeFrame frame = new TreeFrame(fViewer);
			frame.restoreState(frameMemento);
			frame.setName(getFrameName(frame.getInput()));
			frame.setToolTipText(getToolTipText(frame.getInput()));
			frameList.gotoFrame(frame);
		}
	}
	*/

	private void restoreLinkingEnabled(IMemento memento) {
		Integer val= memento.getInteger(PreferenceConstants.LINK_PACKAGES_TO_EDITOR);
		if (val != null) {
			fLinkingEnabled= val.intValue() != 0;
		}
	}

	protected void restoreScrollState(IMemento memento, Tree tree) {
		ScrollBar bar= tree.getVerticalBar();
		if (bar != null) {
			try {
				String posStr= memento.getString(TAG_VERTICAL_POSITION);
				int position;
				position= new Integer(posStr).intValue();
				bar.setSelection(position);
			} catch (NumberFormatException e) {
				// ignore, don't set scrollposition
			}
		}
		bar= tree.getHorizontalBar();
		if (bar != null) {
			try {
				String posStr= memento.getString(TAG_HORIZONTAL_POSITION);
				int position;
				position= new Integer(posStr).intValue();
				bar.setSelection(position);
			} catch (NumberFormatException e) {
				// ignore don't set scroll position
			}
		}
	}

	protected void restoreSelectionState(IMemento memento) {
		IMemento childMem;
		childMem= memento.getChild(TAG_SELECTION);
		if (childMem != null) {
			ArrayList list= new ArrayList();
			IMemento[] elementMem= childMem.getChildren(TAG_ELEMENT);
			for (int i= 0; i < elementMem.length; i++) {
				Object element= RubyCore.create(elementMem[i].getString(TAG_PATH));
				if (element != null)
					list.add(element);
			}
			fViewer.setSelection(new StructuredSelection(list));
		}
	}

	protected void restoreExpansionState(IMemento memento) {
		IMemento childMem= memento.getChild(TAG_EXPANDED);
		if (childMem != null) {
			ArrayList elements= new ArrayList();
			IMemento[] elementMem= childMem.getChildren(TAG_ELEMENT);
			for (int i= 0; i < elementMem.length; i++) {
				Object element= RubyCore.create(elementMem[i].getString(TAG_PATH));
				if (element != null)
					elements.add(element);
			}
			fViewer.setExpandedElements(elements.toArray());
		}
	}
	
	/**
	 * Create the KeyListener for doing the refresh on the viewer.
	 */
	private void initKeyListener() {
		fViewer.getControl().addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent event) {
				fActionSet.handleKeyEvent(event);
			}
		});
	}

	/**
	 * An editor has been activated.  Set the selection in this Packages Viewer
	 * to be the editor's input, if linking is enabled.
	 */
	void editorActivated(IEditorPart editor) {
		if (!isLinkingEnabled())  
			return;
		Object input= getElementOfInput(editor.getEditorInput());
		if (input == null) 
			return;
		if (!inputIsSelected(editor.getEditorInput()))
			showInput(input);
		else
			getTreeViewer().getTree().showSelection();
	}

	private boolean inputIsSelected(IEditorInput input) {
		IStructuredSelection selection= (IStructuredSelection)fViewer.getSelection();
		if (selection.size() != 1) 
			return false;
		IEditorInput selectionAsInput= null;
		try {
			selectionAsInput= EditorUtility.getEditorInput(selection.getFirstElement());
		} catch (RubyModelException e1) {
			return false;
		}
		return input.equals(selectionAsInput);
	}

	boolean showInput(Object input) {
		Object element= null;
			
		if (input instanceof IFile && isOnClassPath((IFile)input)) {
			element= RubyCore.create((IFile)input);
		}
				
		if (element == null) // try a non Ruby resource
			element= input;
				
		if (element != null) {
			ISelection newSelection= new StructuredSelection(element);
			if (fViewer.getSelection().equals(newSelection)) {
				fViewer.reveal(element);
			} else {
				try {
					fViewer.removePostSelectionChangedListener(fPostSelectionListener);						
					fViewer.setSelection(newSelection, true);
	
					while (element != null && fViewer.getSelection().isEmpty()) {
						// Try to select parent in case element is filtered
						element= getParent(element);
						if (element != null) {
							newSelection= new StructuredSelection(element);
							fViewer.setSelection(newSelection, true);
						}
					}
				} finally {
					fViewer.addPostSelectionChangedListener(fPostSelectionListener);
				}
			}
			return true;
		}
		return false;
	}
	
	private boolean isOnClassPath(IFile file) {
		IRubyProject jproject= RubyCore.create(file.getProject());
		return jproject.isOnLoadpath(file);
	}

	/**
	 * Returns the element's parent.
	 * 
	 * @return the parent or <code>null</code> if there's no parent
	 */
	private Object getParent(Object element) {
		if (element instanceof IRubyElement)
			return ((IRubyElement)element).getParent();
		else if (element instanceof IResource)
			return ((IResource)element).getParent();
//		else if (element instanceof IStorage) {
			// can't get parent - see bug 22376
//		}
		return null;
	}
	
	/**
	 * A compilation unit or class was expanded, expand
	 * the main type.  
	 */
	void expandMainType(Object element) {
		try {
			IType type= null;
			if (element instanceof IRubyScript) {
				IRubyScript cu= (IRubyScript)element;
				IType[] types= cu.getTypes();
				if (types.length > 0)
					type= types[0];
			}	
			if (type != null) {
				final IType type2= type;
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ctrl.getDisplay().asyncExec(new Runnable() {
						public void run() {
							Control ctrl2= fViewer.getControl();
							if (ctrl2 != null && !ctrl2.isDisposed()) 
								fViewer.expandToLevel(type2, 1);
						}
					}); 
				}
			}
		} catch(RubyModelException e) {
			// no reveal
		}
	}
	
	/**
	 * Returns the element contained in the EditorInput
	 */
	Object getElementOfInput(IEditorInput input) {
		if (input instanceof IRubyScriptEditorInput)
			return ((IRubyScriptEditorInput)input).getRubyScript();
		else if (input instanceof IFileEditorInput)
			return ((IFileEditorInput)input).getFile();
		else if (input instanceof ExternalRubyFileEditorInput)
			return ((ExternalRubyFileEditorInput)input).getStorage();
		return null;
	}
	
	/**
 	 * Returns the Viewer.
 	 */
	TreeViewer getViewer() {
		return fViewer;
	}
	
	/**
 	 * Returns the TreeViewer.
 	 */
	public TreeViewer getTreeViewer() {
		return fViewer;
	}
	
	boolean isExpandable(Object element) {
		if (fViewer == null)
			return false;
		return fViewer.isExpandable(element);
	}

	void setWorkingSetLabel(String workingSetName) {
		fWorkingSetLabel= workingSetName;
		setTitleToolTip(getTitleToolTip());
	}
	
	/**
	 * Updates the title text and title tool tip.
	 * Called whenever the input of the viewer changes.
	 */ 
	void updateTitle() {		
		Object input= fViewer.getInput();
		if (input == null
			|| (input instanceof IRubyModel)) {
			setContentDescription(""); //$NON-NLS-1$
			setTitleToolTip(""); //$NON-NLS-1$
		} else {
			String inputText= RubyElementLabels.getTextLabel(input, AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS);
			setContentDescription(inputText);
			setTitleToolTip(getToolTipText(input));
		} 
	}
	
	/**
	 * Sets the decorator for the package explorer.
	 *
	 * @param decorator a label decorator or <code>null</code> for no decorations.
	 * @deprecated To be removed
	 */
	public void setLabelDecorator(ILabelDecorator decorator) {
	}
	
	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (fViewer == null)
			return;
		
		boolean refreshViewer= false;
	
		if (PreferenceConstants.SHOW_CU_CHILDREN.equals(event.getProperty())) {
			fActionSet.updateActionBars(getViewSite().getActionBars());
			
			boolean showCUChildren= PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.SHOW_CU_CHILDREN);
			((StandardRubyElementContentProvider)fViewer.getContentProvider()).setProvideMembers(showCUChildren);
			
			refreshViewer= true;
		} else if (MembersOrderPreferenceCache.isMemberOrderProperty(event.getProperty())) {
			refreshViewer= true;
		}

		if (refreshViewer)
			fViewer.refresh();
	}
	
	/* (non-Rubydoc)
	 * @see IViewPartInputProvider#getViewPartInput()
	 */
	public Object getViewPartInput() {
		if (fViewer != null) {
			return fViewer.getInput();
		}
		return null;
	}

	public void collapseAll() {
		try {
			fViewer.getControl().setRedraw(false);		
			fViewer.collapseToLevel(getViewPartInput(), AbstractTreeViewer.ALL_LEVELS);
		} finally {
			fViewer.getControl().setRedraw(true);
		}
	}
	
	public PackageExplorerPart() { 
		initLinkingEnabled();
		fPostSelectionListener= new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handlePostSelectionChanged(event);
			}
		};
	}

	public boolean show(ShowInContext context) {

		ISelection selection= context.getSelection();
		if (selection instanceof IStructuredSelection) {
			// fix for 64634 Navigate/Show in/Package Explorer doesn't work 
			IStructuredSelection structuredSelection= ((IStructuredSelection) selection);
			if (structuredSelection.size() == 1 && tryToReveal(structuredSelection.getFirstElement()))
				return true;
		}
		
		Object input= context.getInput();
		if (input instanceof IEditorInput) {
			Object elementOfInput= getElementOfInput((IEditorInput)context.getInput());
			return elementOfInput != null && tryToReveal(elementOfInput);
		}

		return false;
	}

	/**
	 * Returns the <code>IShowInSource</code> for this view.
	 */
	protected IShowInSource getShowInSource() {
		return new IShowInSource() {
			public ShowInContext getShowInContext() {
				return new ShowInContext(
					getViewer().getInput(),
					getViewer().getSelection());
			}
		};
	}

	/*
	 * @see org.eclipse.ui.views.navigator.IResourceNavigator#setLinkingEnabled(boolean)
	 * @since 2.1
	 */
	public void setLinkingEnabled(boolean enabled) {
		fLinkingEnabled= enabled;
		PreferenceConstants.getPreferenceStore().setValue(PreferenceConstants.LINK_PACKAGES_TO_EDITOR, enabled);

		if (enabled) {
			IEditorPart editor = getSite().getPage().getActiveEditor();
			if (editor != null) {
				editorActivated(editor);
			}
		}
	}

	/**
	 * Returns the name for the given element.
	 * Used as the name for the current frame. 
	 */
	String getFrameName(Object element) {
		if (element instanceof IRubyElement) {
			return ((IRubyElement) element).getElementName();
		} else if (element instanceof WorkingSetModel) {
			return ""; //$NON-NLS-1$
		} else {
			return fLabelProvider.getText(element);
		}
	}
	
	void projectStateChanged(Object root) {
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			fViewer.refresh(root, true);
			// trigger a syntetic selection change so that action refresh their
			// enable state.
			fViewer.setSelection(fViewer.getSelection());
		}
	}

    public boolean tryToReveal(Object element) {
		if (revealElementOrParent(element))
            return true;
        
        WorkingSetFilterActionGroup workingSetGroup= fActionSet.getWorkingSetActionGroup().getFilterGroup();
        if (workingSetGroup != null) {
		    IWorkingSet workingSet= workingSetGroup.getWorkingSet();  	    
		    if (workingSetGroup.isFiltered(getVisibleParent(element), element)) {
		        String message= Messages.format(PackagesMessages.PackageExplorer_notFound, workingSet.getLabel());  
		        if (MessageDialog.openQuestion(getSite().getShell(), PackagesMessages.PackageExplorer_filteredDialog_title, message)) { 
		            workingSetGroup.setWorkingSet(null, true);		
		            if (revealElementOrParent(element))
		                return true;
		        }
		    }
        }
        // try to remove filters
        CustomFiltersActionGroup filterGroup= fActionSet.getCustomFilterActionGroup();
        String[] currentFilters= filterGroup.internalGetEnabledFilterIds(); 
        String[] newFilters= filterGroup.removeFiltersFor(getVisibleParent(element), element, getTreeViewer().getContentProvider()); 
        if (currentFilters.length > newFilters.length) {
            String message= PackagesMessages.PackageExplorer_removeFilters; 
            if (MessageDialog.openQuestion(getSite().getShell(), PackagesMessages.PackageExplorer_filteredDialog_title, message)) { 
                filterGroup.setFilters(newFilters);		
                if (revealElementOrParent(element))
                    return true;
            }
        }
        FrameAction action= fActionSet.getUpAction();
        while (action.getFrameList().getCurrentIndex() > 0) {
        	// only try to go up if there is a parent frame
        	// fix for bug# 63769 Endless loop after Show in Package Explorer 
        	if (action.getFrameList().getSource().getFrame(IFrameSource.PARENT_FRAME, 0) == null)
        		break; 
            action.run();
            if (revealElementOrParent(element))
                return true;
        }
        return false;
    }
    
    private boolean revealElementOrParent(Object element) {
        if (revealAndVerify(element))
		    return true;
		element= getVisibleParent(element);
		if (element != null) {
		    if (revealAndVerify(element))
		        return true;
		    if (element instanceof IRubyElement) {
		        IResource resource= ((IRubyElement)element).getResource();
		        if (resource != null) {
		            if (revealAndVerify(resource))
		                return true;
		        }
		    }
		}
        return false;
    }

    private Object getVisibleParent(Object object) {
    	// Fix for http://dev.eclipse.org/bugs/show_bug.cgi?id=19104
    	if (object == null)
    		return null;
    	if (!(object instanceof IRubyElement))
    	    return object;
    	IRubyElement element2= (IRubyElement) object;
    	switch (element2.getElementType()) {
    		case IRubyElement.IMPORT_DECLARATION:
    		case IRubyElement.IMPORT_CONTAINER:
    		case IRubyElement.TYPE:
    		case IRubyElement.METHOD:
    		case IRubyElement.FIELD:
    			// select parent script
    			element2= (IRubyElement)element2.getOpenable();
    			break;
    		case IRubyElement.RUBY_MODEL:
    			element2= null;
    			break;
    	}
    	return element2;
    }

    private boolean revealAndVerify(Object element) {
    	if (element == null)
    		return false;
    	selectReveal(new StructuredSelection(element));
    	return ! getSite().getSelectionProvider().getSelection().isEmpty();
    }

	public void rootModeChanged(int newMode) {
		fRootMode= newMode;
		if (showWorkingSets() && fWorkingSetModel == null) {
			createWorkingSetModel();
			if (fActionSet != null) {
				fActionSet.getWorkingSetActionGroup().setWorkingSetModel(fWorkingSetModel);
			}
		}
		IStructuredSelection selection= new StructuredSelection(((IStructuredSelection) fViewer.getSelection()).toArray());
		Object input= fViewer.getInput();
		boolean isRootInputChange= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot()).equals(input) 
			|| (fWorkingSetModel != null && fWorkingSetModel.equals(input))
			|| input instanceof IWorkingSet;
		try {
			fViewer.getControl().setRedraw(false);
			if (isRootInputChange) {
				fViewer.setInput(null);
			}
			setProviders();
			setSorter();
			fActionSet.getWorkingSetActionGroup().fillFilters(fViewer);
			if (isRootInputChange) {
				fViewer.setInput(findInputElement());
			}
			fViewer.setSelection(selection, true);
		} finally {
			fViewer.getControl().setRedraw(true);
		}
		if (isRootInputChange && showWorkingSets() && fWorkingSetModel.needsConfiguration()) {
			ConfigureWorkingSetAction action= new ConfigureWorkingSetAction(getSite());
			action.setWorkingSetModel(fWorkingSetModel);
			action.run();
			fWorkingSetModel.configured();
		}
		setTitleToolTip(getTitleToolTip());
	}

	private void createWorkingSetModel() {
		SafeRunner.run(new ISafeRunnable() {
			public void run() throws Exception {
				fWorkingSetModel= fMemento != null 
				? new WorkingSetModel(fMemento) 
				: new WorkingSetModel();
			}
			public void handleException(Throwable exception) {
				fWorkingSetModel= new WorkingSetModel();
			}
		});
	}

	public WorkingSetModel getWorkingSetModel() {
		return fWorkingSetModel;
	}
	
	public int getRootMode() {
		return fRootMode;
	}
	
	/* package */ boolean showProjects() {
		return fRootMode == ViewActionGroup.SHOW_PROJECTS;
	}
	
	/* package */ boolean showWorkingSets() {
		return fRootMode == ViewActionGroup.SHOW_WORKING_SETS;
	}
	
	private void setSorter() {
		if (showWorkingSets()) {
			fViewer.setSorter(new WorkingSetAwareRubyElementSorter());
		} else {
			fViewer.setSorter(new RubyElementSorter());
		}
	}
	
	//---- test methods for working set mode -------------------------------
	
	public void internalTestShowWorkingSets(IWorkingSet[] workingSets) {
		if (fWorkingSetModel == null)
			createWorkingSetModel();
		fWorkingSetModel.setActiveWorkingSets(workingSets);
		fWorkingSetModel.configured();
		rootModeChanged(ViewActionGroup.SHOW_WORKING_SETS);
	}
}
