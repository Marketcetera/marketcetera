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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRegion;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.ITypeHierarchyChangedListener;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.codeassist.RubyElementRequestor;
import org.rubypeople.rdt.internal.core.LogicalType;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * Manages a type hierarchy, to keep it refreshed, and to allow it to be shared.
 */
public class TypeHierarchyLifeCycle implements ITypeHierarchyChangedListener, IElementChangedListener {
	
	private boolean fHierarchyRefreshNeeded;
	private ITypeHierarchy fHierarchy;
	private IRubyElement fInputElement;
	private boolean fIsSuperTypesOnly;
	
	private List fChangeListeners;
	
	public TypeHierarchyLifeCycle() {
		this(false);
	}	
	
	public TypeHierarchyLifeCycle(boolean isSuperTypesOnly) {
		fHierarchy= null;
		fInputElement= null;
		fIsSuperTypesOnly= isSuperTypesOnly;
		fChangeListeners= new ArrayList(2);
	}
	
	public ITypeHierarchy getHierarchy() {
		return fHierarchy;
	}
	
	public IRubyElement getInputElement() {
		return fInputElement;
	}
	
	
	public void freeHierarchy() {
		if (fHierarchy != null) {
			fHierarchy.removeTypeHierarchyChangedListener(this);
			RubyCore.removeElementChangedListener(this);
			fHierarchy= null;
			fInputElement= null;
		}
	}
	
	public void removeChangedListener(ITypeHierarchyLifeCycleListener listener) {
		fChangeListeners.remove(listener);
	}
	
	public void addChangedListener(ITypeHierarchyLifeCycleListener listener) {
		if (!fChangeListeners.contains(listener)) {
			fChangeListeners.add(listener);
		}
	}
	
	private void fireChange(IType[] changedTypes) {
		for (int i= fChangeListeners.size()-1; i>=0; i--) {
			ITypeHierarchyLifeCycleListener curr= (ITypeHierarchyLifeCycleListener) fChangeListeners.get(i);
			curr.typeHierarchyChanged(this, changedTypes);
		}
	}
			
	public void ensureRefreshedTypeHierarchy(final IRubyElement element, IRunnableContext context) throws InvocationTargetException, InterruptedException {
		if (element == null || !element.exists()) {
			freeHierarchy();
			return;
		}
		boolean hierachyCreationNeeded= (fHierarchy == null || !element.equals(fInputElement));
		
		if (hierachyCreationNeeded || fHierarchyRefreshNeeded) {
			
			IRunnableWithProgress op= new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
					try {
						doHierarchyRefresh(element, pm);
					} catch (RubyModelException e) {
						throw new InvocationTargetException(e);
					} catch (OperationCanceledException e) {
						throw new InterruptedException();
					}
				}
			};
			fHierarchyRefreshNeeded= true;
			context.run(true, true, op);
			fHierarchyRefreshNeeded= false;
		}
	}
	
	private IType getLogicalType(IType type, String name) {
		RubyElementRequestor requestor = new RubyElementRequestor(type.getRubyScript());
		IType[] types = requestor.findType(name);
		if (types == null || types.length == 0) return null;
		return new LogicalType(types);
	}
	
	private ITypeHierarchy createTypeHierarchy(IRubyElement element, IProgressMonitor pm) throws RubyModelException {
		if (element.getElementType() == IRubyElement.TYPE) {
			IType type= (IType) element;
			type = getLogicalType(type, type.getFullyQualifiedName());
			if (fIsSuperTypesOnly) {
				return type.newSupertypeHierarchy(pm);
			} else {
				return type.newTypeHierarchy(pm);
			}
		} else {
			IRegion region= RubyCore.newRegion();
			if (element.getElementType() == IRubyElement.RUBY_PROJECT) {
				// for projects only add the contained source folders
				ISourceFolderRoot[] roots= ((IRubyProject) element).getSourceFolderRoots();
				for (int i= 0; i < roots.length; i++) {
					if (!roots[i].isExternal()) {
						region.add(roots[i]);
					}
				}
			} else if (element.getElementType() == IRubyElement.SOURCE_FOLDER) {
				ISourceFolderRoot[] roots= element.getRubyProject().getSourceFolderRoots();
				String name= element.getElementName();
				for (int i= 0; i < roots.length; i++) {
					ISourceFolder pack= roots[i].getSourceFolder(name);
					if (pack.exists()) {
						region.add(pack);
					}
				}
			} else {
				region.add(element);
			}
			IRubyProject jproject= element.getRubyProject();
			return jproject.newTypeHierarchy(region, pm);
		}
	}
	
	
	public synchronized void doHierarchyRefresh(IRubyElement element, IProgressMonitor pm) throws RubyModelException {
		boolean hierachyCreationNeeded= (fHierarchy == null || !element.equals(fInputElement));
		// to ensure the order of the two listeners always remove / add listeners on operations
		// on type hierarchies
		if (fHierarchy != null) {
			fHierarchy.removeTypeHierarchyChangedListener(this);
			RubyCore.removeElementChangedListener(this);
		}
		if (hierachyCreationNeeded) {
			fHierarchy= createTypeHierarchy(element, pm);
			if (pm != null && pm.isCanceled()) {
				throw new OperationCanceledException();
			}
			fInputElement= element;
		} else {
			fHierarchy.refresh(pm);
		}
		if (fHierarchy != null) {		
			fHierarchy.addTypeHierarchyChangedListener(this);
			RubyCore.addElementChangedListener(this);
			fHierarchyRefreshNeeded= false;
		}
	}		
	
	/*
	 * @see ITypeHierarchyChangedListener#typeHierarchyChanged
	 */
	public void typeHierarchyChanged(ITypeHierarchy typeHierarchy) {
	 	fHierarchyRefreshNeeded= true;
 		fireChange(null);
	}		

	/*
	 * @see IElementChangedListener#elementChanged(ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		if (fChangeListeners.isEmpty()) {
			return;
		}
		
		if (fHierarchyRefreshNeeded) {
			return;
		} else {
			ArrayList changedTypes= new ArrayList();
			processDelta(event.getDelta(), changedTypes);
			if (changedTypes.size() > 0) {
				fireChange((IType[]) changedTypes.toArray(new IType[changedTypes.size()]));
			}
		}
	}
	
	/*
	 * Assume that the hierarchy is intact (no refresh needed)
	 */					
	private void processDelta(IRubyElementDelta delta, ArrayList changedTypes) {
		IRubyElement element= delta.getElement();
		switch (element.getElementType()) {
			case IRubyElement.TYPE:
				processTypeDelta((IType) element, changedTypes);
				processChildrenDelta(delta, changedTypes); // (inner types)
				break;
			case IRubyElement.RUBY_MODEL:
			case IRubyElement.RUBY_PROJECT:
			case IRubyElement.SOURCE_FOLDER_ROOT:
			case IRubyElement.SOURCE_FOLDER:
				processChildrenDelta(delta, changedTypes);
				break;
			case IRubyElement.SCRIPT:
				IRubyScript cu= (IRubyScript)element;
				if (!RubyModelUtil.isPrimary(cu)) {
					return;
				}
				
				if (delta.getKind() == IRubyElementDelta.CHANGED && isPossibleStructuralChange(delta.getFlags())) {
					try {
						if (cu.exists()) {
							IType[] types= cu.getAllTypes();
							for (int i= 0; i < types.length; i++) {
								processTypeDelta(types[i], changedTypes);
							}
						}
					} catch (RubyModelException e) {
						RubyPlugin.log(e);
					}
				} else {
					processChildrenDelta(delta, changedTypes);
				}
				break;
		}
	}
	
	private boolean isPossibleStructuralChange(int flags) {
		return (flags & (IRubyElementDelta.F_CONTENT | IRubyElementDelta.F_FINE_GRAINED)) == IRubyElementDelta.F_CONTENT;
	}
	
	private void processTypeDelta(IType type, ArrayList changedTypes) {
		if (getHierarchy().contains(type)) {
			changedTypes.add(type);
		}
	}
	
	private void processChildrenDelta(IRubyElementDelta delta, ArrayList changedTypes) {
		IRubyElementDelta[] children= delta.getAffectedChildren();
		for (int i= 0; i < children.length; i++) {
			processDelta(children[i], changedTypes); // recursive
		}
	}
	

}
