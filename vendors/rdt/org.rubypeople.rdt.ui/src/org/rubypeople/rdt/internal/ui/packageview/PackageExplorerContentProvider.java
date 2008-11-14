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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkingSet;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.ERBScript;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.workingsets.WorkingSetModel;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;
 
/**
 * Content provider for the PackageExplorer.
 * 
 * <p>
 * Since 2.1 this content provider can provide the children for flat or hierarchical
 * layout. The hierarchical layout is done by delegating to the <code>SourceFolderProvider</code>.
 * </p>
 * 
 * @see org.eclipse.jdt.ui.StandardRubyElementContentProvider
 * @see org.eclipse.jdt.internal.ui.packageview.SourceFolderProvider
 */
public class PackageExplorerContentProvider extends StandardRubyElementContentProvider implements ITreeContentProvider, IElementChangedListener {
	
	protected static final int ORIGINAL= 0;
	protected static final int PARENT= 1 << 0;
	protected static final int GRANT_PARENT= 1 << 1;
	protected static final int PROJECT= 1 << 2;
	
	private TreeViewer fViewer;
	private Object fInput;
	private boolean fIsFlatLayout;
	private SourceFolderProvider fSourceFolderProvider;
	
	private int fPendingChanges;
	
	/**
	 * Creates a new content provider for Ruby elements.
	 */
	public PackageExplorerContentProvider(boolean provideMembers) {
		super(provideMembers);
		fSourceFolderProvider= new SourceFolderProvider();
	}
		
	/* package */ SourceFolderProvider getSourceFolderProvider() {
		return fSourceFolderProvider;
	}
	
	protected Object getViewerInput() {
		return fInput;
	}
	
	/* (non-Rubydoc)
	 * Method declared on IElementChangedListener.
	 */
	public void elementChanged(final ElementChangedEvent event) {
		try {
			// 58952 delete project does not update Package Explorer [package explorer] 
			// if the input to the viewer is deleted then refresh to avoid the display of stale elements
			if (inputDeleted())
				return;
			processDelta(event.getDelta());
		} catch(RubyModelException e) {
			RubyPlugin.log(e);
		}
	}

	private boolean inputDeleted() {
		if (fInput == null)
			return false;
		if ((fInput instanceof IRubyElement) && ((IRubyElement) fInput).exists())
			return false;
		if ((fInput instanceof IResource) && ((IResource) fInput).exists())
			return false;
		if (fInput instanceof WorkingSetModel)
			return false;
		if (fInput instanceof IWorkingSet) // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=156239
			return false;
		postRefresh(fInput, ORIGINAL, fInput);
		return true;
	}

	/* (non-Rubydoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
		super.dispose();
		RubyCore.removeElementChangedListener(this);
		fSourceFolderProvider.dispose();
	}
	
	// ------ Code which delegates to SourceFolderProvider ------

	private boolean needsToDelegateGetChildren(Object element) {
		int type= -1;
		if (element instanceof IFolder) {
			IFolder folder = (IFolder) element;
			if (RubyProject.hasRubyNature(folder.getProject())) return true;
			return false;
		}
		if (element instanceof IRubyElement)
			type= ((IRubyElement)element).getElementType();
		return (!fIsFlatLayout && (type == IRubyElement.SOURCE_FOLDER || type == IRubyElement.SOURCE_FOLDER_ROOT || type == IRubyElement.RUBY_PROJECT));
	}		

	public Object[] getChildren(Object parentElement) {
		Object[] children= NO_CHILDREN;
		try {
			if (parentElement instanceof IRubyModel) 
				return concatenate(getRubyProjects((IRubyModel)parentElement), getNonRubyProjects((IRubyModel)parentElement));

			if (parentElement instanceof LoadPathContainer)
				return getContainerSourceFolderRoots((LoadPathContainer)parentElement);
				
			if (parentElement instanceof IProject) 
				return ((IProject)parentElement).members();
					
			if (needsToDelegateGetChildren(parentElement)) {
				Object[] packageFragments= fSourceFolderProvider.getChildren(parentElement);
				children= getWithParentsResources(packageFragments, parentElement);
			} else {
				children= super.getChildren(parentElement);
			}
	
			if (parentElement instanceof IRubyProject) {
				IRubyProject project= (IRubyProject)parentElement;
				return rootsAndContainers(project, children);
			}
			else
				return children;

		} catch (CoreException e) {
			return NO_CHILDREN;
		}
	}

	private Object[] rootsAndContainers(IRubyProject project, Object[] roots) throws RubyModelException { 
		List result= new ArrayList(roots.length);
		Set containers= new HashSet(roots.length);
		Set containedRoots= new HashSet(roots.length); 
		
		ILoadpathEntry[] entries= project.getRawLoadpath();
		for (int i= 0; i < entries.length; i++) {
			ILoadpathEntry entry= entries[i];
			if (entry != null && entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) { 
				ISourceFolderRoot[] roots1= project.findSourceFolderRoots(entry);
				containedRoots.addAll(Arrays.asList(roots1));
				containers.add(entry);
			}
		}
		for (int i= 0; i < roots.length; i++) {
			if (roots[i] instanceof ISourceFolderRoot) {
				if (!containedRoots.contains(roots[i])) {
					result.add(roots[i]);
				}
			} else {
				result.add(roots[i]);
			}
		}
		for (Iterator each= containers.iterator(); each.hasNext();) {
			ILoadpathEntry element= (ILoadpathEntry) each.next();
			result.add(new LoadPathContainer(project, element));
		}		
		return result.toArray();
	}

	private Object[] getContainerSourceFolderRoots(LoadPathContainer container) {
		return container.getChildren(container);
	}

	private Object[] getNonRubyProjects(IRubyModel model) throws RubyModelException {
		return model.getNonRubyResources();
	}

	public Object getParent(Object child) {
		if (needsToDelegateGetParent(child)) {
			return fSourceFolderProvider.getParent(child);
		} else
			return super.getParent(child);
	}

	protected Object internalGetParent(Object element) {
		// since we insert logical package containers we have to fix
		// up the parent for package fragment roots so that they refer
		// to the container and containers refere to the project
		//
		if (element instanceof ISourceFolderRoot) {
			ISourceFolderRoot root= (ISourceFolderRoot)element;
			IRubyProject project= root.getRubyProject();
			try {
				ILoadpathEntry[] entries= project.getRawLoadpath();
				for (int i= 0; i < entries.length; i++) {
					ILoadpathEntry entry= entries[i];
					if (entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) {
						if (LoadPathContainer.contains(project, entry, root)) 
							return new LoadPathContainer(project, entry);
					}
				}
			} catch (RubyModelException e) {
				// fall through
			}
		}
		if (element instanceof LoadPathContainer) {
			return ((LoadPathContainer)element).getRubyProject();
		}
		return super.internalGetParent(element);
	}
	
	private boolean needsToDelegateGetParent(Object element) {
		int type= -1;
		if (element instanceof IRubyElement)
			type= ((IRubyElement)element).getElementType();
		return (!fIsFlatLayout && type == IRubyElement.SOURCE_FOLDER);
	}		

	/**
	 * Returns the given objects with the resources of the parent.
	 */
	private Object[] getWithParentsResources(Object[] existingObject, Object parent) {
		Object[] objects= super.getChildren(parent);
		List list= new ArrayList();
		
		// Add everything that is not a SourceFolder (Files)
		for (int i= 0; i < objects.length; i++) {
			Object object= objects[i];
			if (!(object instanceof ISourceFolder) && !(object instanceof ERBScript)) {
				list.add(object);
			}
		}		
		if (existingObject != null)
			list.addAll(Arrays.asList(existingObject));	
		
		return list.toArray();
	}

	/* (non-Rubydoc)
	 * Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		fSourceFolderProvider.inputChanged(viewer, oldInput, newInput);
		fViewer= (TreeViewer)viewer;
		if (oldInput == null && newInput != null) {
			RubyCore.addElementChangedListener(this); 
		} else if (oldInput != null && newInput == null) {
			RubyCore.removeElementChangedListener(this); 
		}
		fInput= newInput;
	}

	// ------ delta processing ------

	/**
	 * Processes a delta recursively. When more than two children are affected the
	 * tree is fully refreshed starting at this node. The delta is processed in the
	 * current thread but the viewer updates are posted to the UI thread.
	 */
	private void processDelta(IRubyElementDelta delta) throws RubyModelException {
	
		int kind= delta.getKind();
		int flags= delta.getFlags();
		IRubyElement element= delta.getElement();
		int elementType= element.getElementType();
		
		
		if (elementType != IRubyElement.RUBY_MODEL && elementType != IRubyElement.RUBY_PROJECT) {
			IRubyProject proj= element.getRubyProject();
			if (proj == null || !proj.getProject().isOpen()) // TODO: Not needed if parent already did the 'open' check!
				return;	
		}
		
		if (!fIsFlatLayout && elementType == IRubyElement.SOURCE_FOLDER) {
			fSourceFolderProvider.processDelta(delta);
			if (processResourceDeltas(delta.getResourceDeltas(), element))
			    return;
			handleAffectedChildren(delta, element);
			return;
		}
		
		if (elementType == IRubyElement.SCRIPT) {
			IRubyScript cu= (IRubyScript) element;
			if (!RubyModelUtil.isPrimary(cu)) {
				return;
			}
						
			if (!getProvideMembers() && cu.isWorkingCopy() && kind == IRubyElementDelta.CHANGED) {
				return;
			}
			
			if ((kind == IRubyElementDelta.CHANGED) && !isStructuralCUChange(flags)) {
				return; // test moved ahead
			}
			
			if (!isOnClassPath(cu)) { // TODO: isOnClassPath expensive! Should be put after all cheap tests
				return;
			}
			
		}
		
		if (elementType == IRubyElement.RUBY_PROJECT) {
			// handle open and closing of a project
			if ((flags & (IRubyElementDelta.F_CLOSED | IRubyElementDelta.F_OPENED)) != 0) {			
				postRefresh(element, ORIGINAL, element);
				return;
			}
			// if the raw class path has changed we refresh the entire project
			if ((flags & IRubyElementDelta.F_CLASSPATH_CHANGED) != 0) {
				postRefresh(element, ORIGINAL, element);
				return;				
			}
		}
	
		if (kind == IRubyElementDelta.REMOVED) {
			Object parent= internalGetParent(element);			
			if (element instanceof ISourceFolder) {
				// refresh package fragment root to allow filtering empty (parent) packages: bug 72923
				if (fViewer.testFindItem(parent) != null)
					postRefresh(parent, PARENT, element);
				return;
			}
			
			postRemove(element);
			if (parent instanceof ISourceFolder) 
				postUpdateIcon((ISourceFolder)parent);
			// we are filtering out empty subpackages, so we
			// a package becomes empty we remove it from the viewer. 
			if (isSourceFolderEmpty(element.getParent())) {
				if (fViewer.testFindItem(parent) != null)
					postRefresh(internalGetParent(parent), GRANT_PARENT, element);
			}  
			return;
		}
	
		if (kind == IRubyElementDelta.ADDED) { 
			Object parent= internalGetParent(element);
			// we are filtering out empty subpackages, so we
			// have to handle additions to them specially. 
			if (parent instanceof ISourceFolder) {
				Object grandparent= internalGetParent(parent);
				if (((ISourceFolder)parent).isDefaultPackage()) {
					parent = grandparent;
					grandparent = internalGetParent(parent);
				}
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an unvisible parent
				if (parent.equals(fInput)) {
					postRefresh(parent, PARENT, element);
				} else {
					// refresh from grandparent if parent isn't visible yet
					if (fViewer.testFindItem(parent) == null)
						postRefresh(grandparent, GRANT_PARENT, element);
					else {
						postRefresh(parent, PARENT, element);
					}	
				}
				return;				
			} else {  
				if ((flags & IRubyElementDelta.F_MOVED_FROM) != 0) {					
					postRemove(delta.getMovedFromElement());
				}
				postAdd(parent, element);
			}
		}
	
		if (elementType == IRubyElement.SCRIPT) {
			if (kind == IRubyElementDelta.CHANGED) {
				// isStructuralCUChange already performed above
				postRefresh(element, ORIGINAL, element);
				updateSelection(delta);
			}
			return;
		}		
		
		if (elementType == IRubyElement.SOURCE_FOLDER_ROOT) {
			// the contents of an external JAR has changed
			if ((flags & IRubyElementDelta.F_ARCHIVE_CONTENT_CHANGED) != 0) {
				postRefresh(element, ORIGINAL, element);
				return;
			}
			// the source attachment of a JAR has changed
			if ((flags & (IRubyElementDelta.F_SOURCEATTACHED | IRubyElementDelta.F_SOURCEDETACHED)) != 0)
				postUpdateIcon(element);
			
			if (isClassPathChange(delta)) {
				 // throw the towel and do a full refresh of the affected java project. 
				postRefresh(element.getRubyProject(), PROJECT, element);
				return;
			}
		}
		
		if (processResourceDeltas(delta.getResourceDeltas(), element))
			return;
	
		handleAffectedChildren(delta, element);
	}
	
	private static boolean isStructuralCUChange(int flags) {
		// No refresh on working copy creation (F_PRIMARY_WORKING_COPY)
		return ((flags & IRubyElementDelta.F_CHILDREN) != 0) || ((flags & (IRubyElementDelta.F_CONTENT | IRubyElementDelta.F_FINE_GRAINED)) == IRubyElementDelta.F_CONTENT);
	}
	
	/* package */ void handleAffectedChildren(IRubyElementDelta delta, IRubyElement element) throws RubyModelException {
		IRubyElementDelta[] affectedChildren= delta.getAffectedChildren();
		if (affectedChildren.length > 1) {
			// a package fragment might become non empty refresh from the parent
			if (element instanceof ISourceFolder) {				
				IRubyElement parent= (IRubyElement)internalGetParent(element);
				if (parent instanceof ISourceFolderRoot) {
					parent = (IRubyElement)internalGetParent(parent);
				}
				// 1GE8SI6: ITPJUI:WIN98 - Rename is not shown in Packages View
				// avoid posting a refresh to an unvisible parent
				if (element.equals(fInput)) {
					postRefresh(element, ORIGINAL, element);
				} else {
					postRefresh(parent, PARENT, element);
				}
				return;
			}
			// more than one child changed, refresh from here downwards
			if (element instanceof ISourceFolderRoot) {
				Object toRefresh= skipProjectSourceFolderRoot((ISourceFolderRoot)element);
				postRefresh(toRefresh, ORIGINAL, toRefresh);
			} else {
				postRefresh(element, ORIGINAL, element);
			}
			return;
		}
		processAffectedChildren(affectedChildren);
	}
	
	protected void processAffectedChildren(IRubyElementDelta[] affectedChildren) throws RubyModelException {
		for (int i= 0; i < affectedChildren.length; i++) {
			processDelta(affectedChildren[i]);
		}
	}

	private boolean isOnClassPath(IRubyScript element) {
		IRubyProject project= element.getRubyProject();
		if (project == null || !project.exists())
			return false;
		return project.isOnLoadpath(element);
	}

	/**
	 * Updates the selection. It finds newly added elements
	 * and selects them.
	 */
	private void updateSelection(IRubyElementDelta delta) {
		final IRubyElement addedElement= findAddedElement(delta);
		if (addedElement != null) {
			final StructuredSelection selection= new StructuredSelection(addedElement);
			postRunnable(new Runnable() {
				public void run() {
					Control ctrl= fViewer.getControl();
					if (ctrl != null && !ctrl.isDisposed()) {
						// 19431
						// if the item is already visible then select it
						if (fViewer.testFindItem(addedElement) != null)
							fViewer.setSelection(selection);
					}
				}
			});	
		}	
	}

	private IRubyElement findAddedElement(IRubyElementDelta delta) {
		if (delta.getKind() == IRubyElementDelta.ADDED)  
			return delta.getElement();
		
		IRubyElementDelta[] affectedChildren= delta.getAffectedChildren();
		for (int i= 0; i < affectedChildren.length; i++) 
			return findAddedElement(affectedChildren[i]);
			
		return null;
	}

	/**
	 * Updates the package icon
	 */
	 private void postUpdateIcon(final IRubyElement element) {
	 	postRunnable(new Runnable() {
			public void run() {
				// 1GF87WR: ITPUI:ALL - SWTEx + NPE closing a workbench window.
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) 
					fViewer.update(element, new String[]{IBasicPropertyConstants.P_IMAGE});
			}
		});
	 }

	/**
	 * Process a resource delta.
	 * 
	 * @return true if the parent got refreshed
	 */
	private boolean processResourceDelta(IResourceDelta delta, Object parent) {
		int status= delta.getKind();
		int flags= delta.getFlags();
		
		IResource resource= delta.getResource();
		// filter out changes affecting the output folder
		if (resource == null)
			return false;	
			
		// this could be optimized by handling all the added children in the parent
		if ((status & IResourceDelta.REMOVED) != 0) {
			if (parent instanceof ISourceFolder) {
				Object grandparent= internalGetParent(parent);
				// if grandparent is src folder root that is equal to project, refresh project
				if (grandparent instanceof ISourceFolderRoot && ((ISourceFolderRoot)grandparent).getResource().equals(((ISourceFolderRoot)grandparent).getRubyProject().getProject())) {
					parent = grandparent;
					grandparent = internalGetParent(parent);
				}				
				// refresh one level above to deal with empty package filtering properly
				postRefresh(grandparent, PARENT, parent);
				return true;
			} else 
				postRemove(resource);
		}
		if ((status & IResourceDelta.ADDED) != 0) {
			if (parent instanceof ISourceFolder) {
				Object grandparent= internalGetParent(parent);
				// if grandparent is src folder root that is equal to project, refresh project
				if (grandparent instanceof ISourceFolderRoot && ((ISourceFolderRoot)grandparent).getResource().equals(((ISourceFolderRoot)grandparent).getRubyProject().getProject())) {
					parent = grandparent;
					grandparent = internalGetParent(parent);
				}				
				// refresh one level above to deal with empty package filtering properly
				postRefresh(grandparent, PARENT, parent);	
				return true;
			} else
				postAdd(parent, resource);
		}
		// open/close state change of a project
		if ((flags & IResourceDelta.OPEN) != 0) {
			postProjectStateChanged(internalGetParent(parent));
			return true;		
		}
		processResourceDeltas(delta.getAffectedChildren(), resource);
		return false;
	}
	
	public void setIsFlatLayout(boolean state) {
		fIsFlatLayout= state;
	}
	/**
	 * Process resource deltas.
	 *
	 * @return true if the parent got refreshed
	 */
	private boolean processResourceDeltas(IResourceDelta[] deltas, Object parent) {
		if (deltas == null)
			return false;
		
		if (deltas.length > 1) {
			// more than one child changed, refresh from here downwards
			postRefresh(parent, ORIGINAL, parent);
			return true;
		}

		for (int i= 0; i < deltas.length; i++) {
			if (processResourceDelta(deltas[i], parent))
				return true;
		}

		return false;
	}

	private void postRefresh(Object root, int relation, Object affectedElement) {
		// JFace doesn't refresh when object isn't part of the viewer
		// Therefore move the refresh start down to the viewer's input
		if (isParent(root, fInput)) 
			root= fInput;
		List toRefresh= new ArrayList(1);
		toRefresh.add(root);
		augmentElementToRefresh(toRefresh, relation, affectedElement);
		postRefresh(toRefresh, true);
	}
	
	protected void augmentElementToRefresh(List toRefresh, int relation, Object affectedElement) {
	}

	boolean isParent(Object root, Object child) {
		Object parent= getParent(child);
		if (parent == null)
			return false;
		if (parent.equals(root))
			return true;
		return isParent(root, parent);
	}

	protected void postRefresh(final List toRefresh, final boolean updateLabels) {
		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					for (Iterator iter= toRefresh.iterator(); iter.hasNext();) {
						fViewer.refresh(iter.next(), updateLabels);
					}
				}
			}
		});
	}

	protected void postAdd(final Object parent, final Object element) {
		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()){
					// TODO workaround for 39754 New projects being added to the TreeViewer twice
					if (fViewer.testFindItem(element) == null) 
						fViewer.add(parent, element);
				}
			}
		});
	}

	protected void postRemove(final Object element) {
		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					fViewer.remove(element);
				}
			}
		});
	}

	protected void postProjectStateChanged(final Object root) {
		postRunnable(new Runnable() {
			public void run() {
				//fPart.projectStateChanged(root); 
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					fViewer.refresh(root, true);
					// trigger a syntetic selection change so that action refresh their
					// enable state.
					fViewer.setSelection(fViewer.getSelection());
				}
			}
		});
	}

	/* package */ void postRunnable(final Runnable r) {
		Control ctrl= fViewer.getControl();
		final Runnable trackedRunnable= new Runnable() {
			public void run() {
				try {
					r.run();
				} finally {
					removePendingChange();
				}
			}
		};
		if (ctrl != null && !ctrl.isDisposed()) {
			addPendingChange();
			try {
				ctrl.getDisplay().asyncExec(trackedRunnable); 
			} catch (RuntimeException e) {
				removePendingChange();
				throw e;
			} catch (Error e) {
				removePendingChange();
				throw e; 
			}
		}
	}

	// ------ Pending change management due to the use of asyncExec in postRunnable.
	
	public synchronized boolean hasPendingChanges() {
		return fPendingChanges > 0;  
	}
	
	private synchronized void addPendingChange() {
		fPendingChanges++;
	}

	synchronized void removePendingChange() {
		fPendingChanges--;
		if (fPendingChanges < 0)
			fPendingChanges= 0;
	}
}
