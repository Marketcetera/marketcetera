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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Content provider which provides package fragments for hierarchical
 * Package Explorer layout.
 * 
 * @since 2.1
 */
public class SourceFolderProvider implements IPropertyChangeListener {

	private TreeViewer fViewer;
	private boolean fFoldPackages;
	
	public SourceFolderProvider() {
		fFoldPackages= arePackagesFoldedInHierarchicalLayout();
		RubyPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}
	
	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof IFolder) {
				IResource[] resources= ((IFolder) parentElement).members();
				return filter(getFolders(resources)).toArray();
			} else if (parentElement instanceof IRubyElement) {
				IRubyElement iRubyElement= (IRubyElement) parentElement;
				int type= iRubyElement.getElementType();
	
				switch (type) {
					case IRubyElement.RUBY_PROJECT: {
						IRubyProject project= (IRubyProject) iRubyElement;
						
						ISourceFolderRoot root= project.findSourceFolderRoot(project.getPath());
						if (root != null) {
							List children= getTopLevelChildren(root);
							return filter(children).toArray();
						} 
						break;
					}
					case IRubyElement.SOURCE_FOLDER_ROOT: {
						ISourceFolderRoot root= (ISourceFolderRoot) parentElement;
						if (root.exists()) {
							return filter(getTopLevelChildren(root)).toArray();
						}
						break;
					}
					case IRubyElement.SOURCE_FOLDER: {
						ISourceFolder packageFragment = (ISourceFolder) parentElement;
						if (!packageFragment.isDefaultPackage()) {
							ISourceFolderRoot root= (ISourceFolderRoot) packageFragment.getParent();
							List children = getPackageChildren(root, packageFragment);
							return filter(children).toArray();
						}
						break;
					}
					default :
						// do nothing
				}
			}
		} catch (CoreException e) {
			RubyPlugin.log(e);
		}
		return new Object[0];
	}
	
	private List filter(List children) throws RubyModelException {
		if (fFoldPackages) {
			int size= children.size();
			for (int i = 0; i < size; i++) {
				Object curr= children.get(i);
				if (curr instanceof ISourceFolder) {
					ISourceFolder fragment = (ISourceFolder) curr;
					if (!fragment.isDefaultPackage() && isEmpty(fragment)) {
						ISourceFolder collapsed= getCollapsed(fragment);
						if (collapsed != null) {
							children.set(i, collapsed); // replace with collapsed
						}
					}
				}
			}
		}
		return children;
	}
	
	private ISourceFolder getCollapsed(ISourceFolder pack) throws RubyModelException {
		IRubyElement[] children= ((ISourceFolderRoot) pack.getParent()).getChildren();
		ISourceFolder child= getSinglePackageChild(pack, children);
		while (child != null && isEmpty(child)) {
			ISourceFolder collapsed= getSinglePackageChild(child, children);
			if (collapsed == null) {
				return child;
			}
			child= collapsed;
		}
		return child;
	}
		
	private boolean isEmpty(ISourceFolder fragment) throws RubyModelException {
		return !fragment.containsRubyResources() && fragment.getNonRubyResources().length == 0;
	}
	
	private static ISourceFolder getSinglePackageChild(ISourceFolder fragment, IRubyElement[] children) {
		String prefix= fragment.getElementName() + '.';
		int prefixLen= prefix.length();
		ISourceFolder found= null;
		for (int i= 0; i < children.length; i++) {
			IRubyElement element= children[i];
			String name= element.getElementName();
			if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf('.', prefixLen) == -1) {
				if (found == null) {
					found= (ISourceFolder) element;
				} else {
					return null;
				}
			}
		}
		return found;
	}
	
	
	private static List getPackageChildren(ISourceFolderRoot parent, ISourceFolder fragment) throws RubyModelException {
		IRubyElement[] children= parent.getChildren();
		ArrayList list= new ArrayList(children.length);
		String prefix= fragment.getElementName() + File.separatorChar;
		int prefixLen= prefix.length();
		for (int i= 0; i < children.length; i++) {
			IRubyElement element= children[i];
			if (element instanceof ISourceFolder) { // see bug 134256
				String name= element.getElementName();
				if (name.startsWith(prefix) && name.length() > prefixLen && name.indexOf(File.separatorChar, prefixLen) == -1) {
					list.add(element);
				}
			}
		}
		return list;
	}
	
	private static List getTopLevelChildren(ISourceFolderRoot root) throws RubyModelException {
		IRubyElement[] elements= root.getChildren();
		ArrayList topLevelElements= new ArrayList(elements.length);
		for (int i= 0; i < elements.length; i++) {
			IRubyElement iRubyElement= elements[i];
			// for default src folder, grab it's scripts (unless it's part of project as src folder root)			
			if (iRubyElement instanceof ISourceFolder && (iRubyElement.getElementName().indexOf(File.separatorChar)==-1) && !(((ISourceFolder)iRubyElement).isDefaultPackage()) ) {
				topLevelElements.add(iRubyElement);
			}
		}	
		return topLevelElements;
	}

	private List getFolders(IResource[] resources) throws RubyModelException {
		List list= new ArrayList(resources.length);
		for (int i= 0; i < resources.length; i++) {
			IResource resource= resources[i];
			if (resource instanceof IFolder) {
				IFolder folder= (IFolder) resource;
				IRubyElement element= RubyCore.create(folder);
				if (element instanceof ISourceFolder) {
					list.add(element);	
				} 
			}	
		}
		return list;
	}


	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) {

		if (element instanceof ISourceFolder) {
			ISourceFolder frag = (ISourceFolder) element;
			//@Changed: a fix, before: if(frag.exists() && isEmpty(frag))
		
			return filterParent(getActualParent(frag));
		}
		return null;
	}

	private Object getActualParent(ISourceFolder fragment) {
		try {

			if (fragment.exists()) {
				IRubyElement parent = fragment.getParent();

				if ((parent instanceof ISourceFolderRoot) && parent.exists()) {
					ISourceFolderRoot root = (ISourceFolderRoot) parent;
					if (root.isExternal()) {
						return findNextLevelParentByElementName(fragment);
					} else {

						IResource resource = fragment.getUnderlyingResource();
						if ((resource != null) && (resource instanceof IFolder)) {
							IFolder folder = (IFolder) resource;
							IResource res = folder.getParent();

							IRubyElement el = RubyCore.create(res);
							if (el != null) {
								return el;
							} else {
								return res;
							}
						}
					}
					return parent;
				}
			}

		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}
		return null;
	}
	
	private Object filterParent(Object parent) {
		if (fFoldPackages && (parent!=null)) {
			try {
				if (parent instanceof ISourceFolder) {
					ISourceFolder fragment = (ISourceFolder) parent;
					if (isEmpty(fragment) && hasSingleChild(fragment)) {
						return filterParent(getActualParent(fragment));
					}
				}
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
		return parent;
	}

	private boolean hasSingleChild(ISourceFolder fragment) {
		return getChildren(fragment).length==1;
	}


	private Object findNextLevelParentByElementName(ISourceFolder child) {
		String name= child.getElementName();
		
		int index= name.lastIndexOf(File.separatorChar);
		if (index != -1) {
			String realParentName= name.substring(0, index);
			ISourceFolder element= ((ISourceFolderRoot) child.getParent()).getSourceFolder(realParentName);
			if (element.exists()) {
				return element;
			}
		}
		return child.getParent();
	}


	/*
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element) {
		
		if (element instanceof ISourceFolder) {
			ISourceFolder fragment= (ISourceFolder) element;
			if(fragment.isDefaultPackage())
				return false;
		}
		return getChildren(element).length > 0;
	}

	/*
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		RubyPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
	}

	/**
	 * Called when the view is closed and opened.
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		fViewer= (TreeViewer)viewer;
	}
	
	/*
	 * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
	}
	
	public void processDelta(IRubyElementDelta delta) {

		int kind = delta.getKind();
		final IRubyElement element = delta.getElement();

		if (element instanceof ISourceFolder) {

			if (kind == IRubyElementDelta.REMOVED) {

				postRunnable(new Runnable() {
					public void run() {
						Control ctrl = fViewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							if (!fFoldPackages)
								 fViewer.remove(element);
							else
								refreshGrandParent(element);
						}
					}
				});
				return;

			} else if (kind == IRubyElementDelta.ADDED) {

				final Object parent = getParent(element);
				if (parent != null) {
					postRunnable(new Runnable() {
						public void run() {
							Control ctrl = fViewer.getControl();
							if (ctrl != null && !ctrl.isDisposed()) {
								if (!fFoldPackages)
									 fViewer.add(parent, element);
								else
									refreshGrandParent(element);
							}
						}
					});
				}
				return;
			} 
		}
	}

	// XXX: needs to be revisited - might be a performance issue
	private void refreshGrandParent(final IRubyElement element) {
		if (element instanceof ISourceFolder) {
			Object gp= getGrandParent((ISourceFolder)element);
			if (gp instanceof IRubyElement) {
				IRubyElement el = (IRubyElement) gp;
				if(el.exists())
					fViewer.refresh(gp);
			} else if (gp instanceof IFolder) {
				IFolder folder= (IFolder)gp;
				if (folder.exists())
					fViewer.refresh(folder);
			}
		}
	}

	private Object getGrandParent(ISourceFolder element) {

		Object parent= findNextLevelParentByElementName(element);
		if (parent instanceof ISourceFolderRoot) {
			ISourceFolderRoot root= (ISourceFolderRoot) parent;
			if(isRootProject(root))
				return root.getRubyProject();
			else return root;
		}

		Object grandParent= getParent(parent);
		if(grandParent==null){
			return parent;
		}
		return grandParent;
	}

	private boolean isRootProject(ISourceFolderRoot root) {
		if (ISourceFolderRoot.DEFAULT_PACKAGEROOT_PATH.equals(root.getElementName()))
			return true;
		return false;
	}
	
	private void postRunnable(final Runnable r) {
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {

			Display currentDisplay= Display.getCurrent();
			if (currentDisplay != null && currentDisplay.equals(ctrl.getDisplay()))
				ctrl.getDisplay().syncExec(r);
			else
				ctrl.getDisplay().asyncExec(r);
		}
	}

	/*
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (arePackagesFoldedInHierarchicalLayout() != fFoldPackages){
			fFoldPackages= arePackagesFoldedInHierarchicalLayout();
			if (fViewer != null && !fViewer.getControl().isDisposed()) {
				fViewer.getControl().setRedraw(false);
				Object[] expandedObjects= fViewer.getExpandedElements();
				fViewer.refresh();	
				fViewer.setExpandedElements(expandedObjects);
				fViewer.getControl().setRedraw(true);
			}
		}
	}

	private boolean arePackagesFoldedInHierarchicalLayout(){
		// TODO Uncomment and allow folding packages preference setting?
//		return PreferenceConstants.getPreferenceStore().getBoolean(PreferenceConstants.APPEARANCE_FOLD_PACKAGES_IN_PACKAGE_EXPLORER);
		return false;
	}
}
