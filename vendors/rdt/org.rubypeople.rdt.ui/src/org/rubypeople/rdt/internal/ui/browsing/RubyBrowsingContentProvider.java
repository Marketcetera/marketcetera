package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.IBasicPropertyConstants;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.LogicalType;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;

public class RubyBrowsingContentProvider extends
		StandardRubyElementContentProvider implements IElementChangedListener {

	private RubyBrowsingPart fBrowsingPart;
	private StructuredViewer fViewer;
	private int fReadsInDisplayThread;
	private Object fInput;

	public RubyBrowsingContentProvider(boolean provideMembers,
			RubyBrowsingPart browsingPart) {
		super(provideMembers);
		fBrowsingPart = browsingPart;
		fViewer = fBrowsingPart.getViewer();
		RubyCore.addElementChangedListener(this);
	}

	public boolean hasChildren(Object element) {
		startReadInDisplayThread();
		try {
			return super.hasChildren(element);
		} finally {
			finishedReadInDisplayThread();
		}
	}

	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		startReadInDisplayThread();
		try {
			if (element instanceof Collection) {
				Collection elements= (Collection)element;
				if (elements.isEmpty())
					return NO_CHILDREN;
				Object[] result= new Object[0];
				Iterator iter= ((Collection)element).iterator();
				while (iter.hasNext()) {
					Object[] children= getChildren(iter.next());
					if (children != NO_CHILDREN)
						result= concatenate(result, children);
				}
				return result;
			}
			if (element instanceof ISourceFolder)
				return getFolderContents((ISourceFolder)element);
			if (fProvideMembers && element instanceof IType)
				return getChildren((IType)element);
			if (fProvideMembers && element instanceof ISourceReference && element instanceof IParent)
				return removeImportDeclarations(super.getChildren(element));
			if (element instanceof IRubyProject)
				return getSourceFolderRoots((IRubyProject)element);
			return super.getChildren(element);
		} catch (RubyModelException e) {
			return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}
	
	private Object[] removeImportDeclarations(Object[] members) {
		ArrayList tempResult= new ArrayList(members.length);
		for (int i= 0; i < members.length; i++)
			if (!(members[i] instanceof IImportContainer))
				tempResult.add(members[i]);
		return tempResult.toArray();
	}
	
	protected Object[] getFolderContents(ISourceFolder fragment) throws RubyModelException {
		ISourceReference[] sourceRefs= fragment.getRubyScripts();
		Object[] result= new Object[0];
		for (int i= 0; i < sourceRefs.length; i++)
			result= concatenate(result, getChildren(sourceRefs[i]));
		result = includeSubtypes(result);
		result = convertToLogicalTypes(result);
		return result;
	}

	private Object[] includeSubtypes(Object[] result) throws RubyModelException {
		List<Object> list = new ArrayList<Object>();
		for (int j = 0; j < result.length; j++) {
			if (result[j] instanceof IType) {
				IType type = (IType) result[j];
				list.addAll(Arrays.asList(includeSubtypes(type.getTypes())));
			}
			list.add(result[j]);
		}
		return list.toArray(new Object[list.size()]);
	}

	private Object[] convertToLogicalTypes(Object[] result) {
		Map<String, IType> uniques = new HashMap<String, IType>();
		for (int j= 0; j < result.length; j++) {
			if (result[j] instanceof IType) {
				IType type = (IType) result[j];
				String name = type.getFullyQualifiedName();
				if (!uniques.containsKey(name)) {
					uniques.put(name, type);
				} else {
					IType other = uniques.get(name);
					LogicalType logical = new LogicalType(new IType[] {other, type});
					uniques.put(name, logical);
				}
			}
		}
		Collection values = uniques.values();
		result = values.toArray(new Object[values.size()]);
		return result;
	}
	
	protected Object[] getSourceFolderRoots(IRubyProject project) throws RubyModelException {
		if (!project.getProject().isOpen())
			return NO_CHILDREN;

		ISourceFolderRoot[] roots= project.getSourceFolderRoots();
		List list= new ArrayList(roots.length);
		// filter out package fragments that correspond to projects and
		// replace them with the package fragments directly
		for (int i= 0; i < roots.length; i++) {
			ISourceFolderRoot root= roots[i];
			if (!root.isExternal()) {
				Object[] children= root.getChildren();
				for (int k= 0; k < children.length; k++)
					list.add(children[k]);
			}
			else if (hasChildren(root)) {
				list.add(root);
			}
		}
		return concatenate(list.toArray(), project.getNonRubyResources());
	}
	
	private Object[] getChildren(IType type) throws RubyModelException{
		IParent parent= type.getRubyScript();
		
		if (type.getDeclaringType() != null)
			return type.getChildren();

		// Add import declarations
		IRubyElement[] members= parent.getChildren();
		ArrayList tempResult= new ArrayList(members.length);
		for (int i= 0; i < members.length; i++)
			if ((members[i] instanceof IImportContainer))
				tempResult.add(members[i]);
		tempResult.addAll(Arrays.asList(type.getChildren()));
		return tempResult.toArray();
	}

	private boolean isDisplayThread() {
		Control ctrl = fViewer.getControl();
		if (ctrl == null)
			return false;

		Display currentDisplay = Display.getCurrent();
		return currentDisplay != null
				&& currentDisplay.equals(ctrl.getDisplay());
	}

	protected void startReadInDisplayThread() {
		if (isDisplayThread())
			fReadsInDisplayThread++;
	}

	protected void finishedReadInDisplayThread() {
		if (isDisplayThread())
			fReadsInDisplayThread--;
	}

	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);

		if (newInput instanceof Collection) {
			// Get a template object from the collection
			Collection col = (Collection) newInput;
			if (!col.isEmpty())
				newInput = col.iterator().next();
			else
				newInput = null;
		}
		fInput = newInput;
	}

	/* (non-Javadoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
		super.dispose();
		RubyCore.removeElementChangedListener(this);
	}

	/**
	 * Returns the parent for the element.
	 * <p>
	 * Note: This method will return a working copy if the parent is a working
	 * copy. The super class implementation returns the original element
	 * instead.
	 * </p>
	 */
	protected Object internalGetParent(Object element) {
		if (element instanceof IRubyProject) {
			return ((IRubyProject) element).getRubyModel();
		}
		// try to map resources to the containing package fragment
		if (element instanceof IResource) {
			IResource parent = ((IResource) element).getParent();
			Object jParent = RubyCore.create(parent);
			if (jParent != null)
				return jParent;
			return parent;
		}

		if (element instanceof IRubyElement)
			return ((IRubyElement) element).getParent();

		return null;
	}

	/* (non-Javadoc)
	 * Method declared on IElementChangedListener.
	 */
	public void elementChanged(final ElementChangedEvent event) {
		try {
			processDelta(event.getDelta());
		} catch(RubyModelException e) {
			RubyPlugin.log(e.getStatus());
		}
	}
	
	/**
	 * Processes a delta recursively. When more than two children are affected the
	 * tree is fully refreshed starting at this node. The delta is processed in the
	 * current thread but the viewer updates are posted to the UI thread.
	 */
	protected void processDelta(IRubyElementDelta delta) throws RubyModelException {
		int kind= delta.getKind();
		int flags= delta.getFlags();
		final IRubyElement element= delta.getElement();
		final boolean isElementValidForView= fBrowsingPart.isValidElement(element);

		if (!getProvideWorkingCopy() && element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy())
			return;

		if (element != null && element.getElementType() == IRubyElement.SCRIPT && !isOnClassPath((IRubyScript)element))
			return;

		// handle open and closing of a solution or project
		if (((flags & IRubyElementDelta.F_CLOSED) != 0) || ((flags & IRubyElementDelta.F_OPENED) != 0)) {
			postRefresh(null);
			return;
		}

		if (kind == IRubyElementDelta.REMOVED) {
			Object parent= internalGetParent(element);
			if (isElementValidForView) {
				if (element instanceof IRubyScript && !((IRubyScript)element).isWorkingCopy()) {
						postRefresh(null);
				} else if (element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy()) {
					if (getProvideWorkingCopy())
						postRefresh(null);
				} else if (parent instanceof IRubyScript && getProvideWorkingCopy() && !((IRubyScript)parent).isWorkingCopy()) {
					if (element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy()) {
						// working copy removed from system - refresh
						postRefresh(null);
					}
				} else if (element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy() && parent != null && parent.equals(fInput))
					// closed editor - removing working copy
					postRefresh(null);
				else
					postRemove(element);
			}

			if (fBrowsingPart.isAncestorOf(element, fInput)) {
				if (element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy()) {
					postAdjustInputAndSetSelection(RubyModelUtil.toOriginal((IRubyElement) fInput));
				} else
					postAdjustInputAndSetSelection(null);
			}

			if (fInput != null && fInput.equals(element))
				postRefresh(null);


			return;
		}
		if (kind == IRubyElementDelta.ADDED && delta.getMovedFromElement() != null && element instanceof IRubyScript)
			return;

		if (kind == IRubyElementDelta.ADDED) {
			if (isElementValidForView) {
				Object parent= internalGetParent(element);
				if (element instanceof IRubyScript && !((IRubyScript)element).isWorkingCopy()) {
						postAdd(parent, ((IRubyScript)element).getTypes());
				} else if (parent instanceof IRubyScript && getProvideWorkingCopy() && !((IRubyScript)parent).isWorkingCopy()) {
					//	do nothing
				} else if (element instanceof IRubyScript && ((IRubyScript)element).isWorkingCopy()) {
					// new working copy comes to live
					postRefresh(null);
				} else
					postAdd(parent, element);
			} else	if (fInput == null) {
				IRubyElement newInput= fBrowsingPart.findInputForRubyElement(element);
				if (newInput != null)
					postAdjustInputAndSetSelection(element);
			} else if (element instanceof IType && fBrowsingPart.isValidInput(element)) {
				IRubyElement cu1= element.getAncestor(IRubyElement.SCRIPT);
				IRubyElement cu2= ((IRubyElement)fInput).getAncestor(IRubyElement.SCRIPT);
				if  (cu1 != null && cu2 != null && cu1.equals(cu2))
					postAdjustInputAndSetSelection(element);
			}
			return;
		}

		if (kind == IRubyElementDelta.CHANGED) {
			if (fInput != null && fInput.equals(element) && (flags & IRubyElementDelta.F_CHILDREN) != 0 && (flags & IRubyElementDelta.F_FINE_GRAINED) != 0) {
				postRefresh(null, true);
				return;
			}
			if (isElementValidForView && (flags & IRubyElementDelta.F_MODIFIERS) != 0) {
					postUpdateIcon(element);
			}
		}

		if (isClassPathChange(delta))
			 // throw the towel and do a full refresh
			postRefresh(null);



		IRubyElementDelta[] affectedChildren= delta.getAffectedChildren();
		for (int i= 0; i < affectedChildren.length; i++) {
			processDelta(affectedChildren[i]);
		}
	}
	
	private boolean isOnClassPath(IRubyScript element) throws RubyModelException {
		IRubyProject project= element.getRubyProject();
		if (project == null || !project.exists())
			return false;
		return project.isOnLoadpath(element);
	}

	/**
	 * Updates the package icon
	 */
	 private void postUpdateIcon(final IRubyElement element) {
	 	postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed())
					fViewer.update(element, new String[]{IBasicPropertyConstants.P_IMAGE});
			}
		});
	 }

	private void postRefresh(final Object root, final boolean updateLabels) {
		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed())
					fViewer.refresh(root, updateLabels);
			}
		});
	}

	private void postRefresh(final Object root) {
		postRefresh(root, false);
	}

	private void postAdd(final Object parent, final Object element) {
		postAdd(parent, new Object[] {element});
	}

	private void postAdd(final Object parent, final Object[] elements) {
		if (elements == null || elements.length <= 0)
			return;

		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] newElements= getNewElements(elements);
					if (fViewer instanceof AbstractTreeViewer) {
						if (fViewer.testFindItem(parent) == null) {
							Object root= ((AbstractTreeViewer)fViewer).getInput();
							if (root != null)
								((AbstractTreeViewer)fViewer).add(root, newElements);
						}
						else
							((AbstractTreeViewer)fViewer).add(parent, newElements);
					}
					else if (fViewer instanceof ListViewer)
						((ListViewer)fViewer).add(newElements);
					else if (fViewer instanceof TableViewer)
						((TableViewer)fViewer).add(newElements);
					if (fViewer.testFindItem(elements[0]) != null)
						fBrowsingPart.adjustInputAndSetSelection(elements[0]);
				}
			}
		});
	}
	
	private Object[] getNewElements(Object[] elements) {
		int elementsLength= elements.length;
		ArrayList result= new ArrayList(elementsLength);
		for (int i= 0; i < elementsLength; i++) {
			Object element= elements[i];
			if (fViewer.testFindItem(element) == null)
				result.add(element);
		}
		return result.toArray();
	}

	private void postRemove(final Object element) {
		postRemove(new Object[] {element});
	}

	private void postRemove(final Object[] elements) {
		if (elements.length <= 0)
			return;

		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					if (fViewer instanceof AbstractTreeViewer)
						((AbstractTreeViewer)fViewer).remove(elements);
					else if (fViewer instanceof ListViewer)
						((ListViewer)fViewer).remove(elements);
					else if (fViewer instanceof TableViewer)
						((TableViewer)fViewer).remove(elements);
				}
			}
		});
	}

	private void postAdjustInputAndSetSelection(final Object element) {
		postRunnable(new Runnable() {
			public void run() {
				Control ctrl= fViewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					ctrl.setRedraw(false);
					fBrowsingPart.adjustInputAndSetSelection(element);
					ctrl.setRedraw(true);
				}
			}
		});
	}

	private void postRunnable(final Runnable r) {
		Control ctrl= fViewer.getControl();
		if (ctrl != null && !ctrl.isDisposed()) {
			fBrowsingPart.setProcessSelectionEvents(false);
			try {
				if (isDisplayThread() && fReadsInDisplayThread == 0)
					ctrl.getDisplay().syncExec(r);
				else
					ctrl.getDisplay().asyncExec(r);
			} finally {
				fBrowsingPart.setProcessSelectionEvents(true);
			}
		}
	}

}
