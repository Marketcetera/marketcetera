/**
 * Copyright (c) 2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 * This file is based on a JDT equivalent:
 *******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards.buildpaths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;

public class CPUserLibraryElement {
	
	private  class UpdatedLoadpathContainer implements ILoadpathContainer {
				
		/* (non-Javadoc)
		 * @see org.eclipse.jdt.core.ILoadpathContainer#getLoadpathEntries()
		 */
		public ILoadpathEntry[] getLoadpathEntries() {
			CPListElement[] children= getChildren();
			ILoadpathEntry[] entries= new ILoadpathEntry[children.length];
			for (int i= 0; i < entries.length; i++) {
				entries[i]= children[i].getLoadpathEntry();
			}
			return entries;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.core.ILoadpathContainer#getDescription()
		 */
		public String getDescription() {
			return getName();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.core.ILoadpathContainer#getKind()
		 */
		public int getKind() {
			return isSystemLibrary() ? ILoadpathContainer.K_SYSTEM : K_APPLICATION;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jdt.core.ILoadpathContainer#getPath()
		 */
		public IPath getPath() {
			return CPUserLibraryElement.this.getPath();
		}
	}
	
	
	private String fName;
	private List fChildren;
	private boolean fIsSystemLibrary;

	public CPUserLibraryElement(String name, ILoadpathContainer container, IRubyProject project) {
		fName= name;
		fChildren= new ArrayList();
		if (container != null) {
			ILoadpathEntry[] entries= container.getLoadpathEntries();
			CPListElement[] res= new CPListElement[entries.length];
			for (int i= 0; i < res.length; i++) {
				ILoadpathEntry curr= entries[i];
				CPListElement elem= CPListElement.createFromExisting(this, curr, project);
				//elem.setAttribute(CPListElement.SOURCEATTACHMENT, curr.getSourceAttachmentPath());
				//elem.setAttribute(CPListElement.JAVADOC, JavaUI.getLibraryJavadocLocation(curr.getPath()));
				fChildren.add(elem);
			}
			fIsSystemLibrary= container.getKind() == ILoadpathContainer.K_SYSTEM;
		} else {
			fIsSystemLibrary= false;
		}
	}
	
	public CPUserLibraryElement(String name, boolean isSystemLibrary, CPListElement[] children) {
		fName= name;
		fChildren= new ArrayList();
		if (children != null) {
			for (int i= 0; i < children.length; i++) {
				fChildren.add(children[i]);
			}
		}
		fIsSystemLibrary= isSystemLibrary;
	}
	
	public CPListElement[] getChildren() {
		return (CPListElement[]) fChildren.toArray(new CPListElement[fChildren.size()]);
	}

	public String getName() {
		return fName;
	}
	
	public IPath getPath() {
		return new Path(RubyCore.USER_LIBRARY_CONTAINER_ID).append(fName);
	}

	public boolean isSystemLibrary() {
		return fIsSystemLibrary;
	}
	
	public void add(CPListElement element) {
		if (!fChildren.contains(element)) {
			fChildren.add(element);
		}
	}
		
	private List moveUp(List elements, List move) {
		int nElements= elements.size();
		List res= new ArrayList(nElements);
		Object floating= null;
		for (int i= 0; i < nElements; i++) {
			Object curr= elements.get(i);
			if (move.contains(curr)) {
				res.add(curr);
			} else {
				if (floating != null) {
					res.add(floating);
				}
				floating= curr;
			}
		}
		if (floating != null) {
			res.add(floating);
		}
		return res;
	}
	
	public void moveUp(List toMoveUp) {
		if (toMoveUp.size() > 0) {
			fChildren= moveUp(fChildren, toMoveUp);
		}
	}
	
	public void moveDown(List toMoveDown) {
		if (toMoveDown.size() > 0) {
			Collections.reverse(fChildren);
			fChildren= moveUp(fChildren, toMoveDown);
			Collections.reverse(fChildren);
		}
	}
	
	
	public void remove(CPListElement element) {
		fChildren.remove(element);
	}
	
	public void replace(CPListElement existingElement, CPListElement element) {
		if (fChildren.contains(element)) {
			fChildren.remove(existingElement);
		} else {
			int index= fChildren.indexOf(existingElement);
			if (index != -1) {
				fChildren.set(index, element);
			} else {
				fChildren.add(element);
			}
		}
	}
	
	private void copyAttribute(CPListElement source, CPListElement target, String attributeName) {
		Object value= source.getAttribute(attributeName);
		if (value != null) {
			target.setAttribute(attributeName, value);
		}
	}

	public ILoadpathContainer getUpdatedContainer() {
		return new UpdatedLoadpathContainer();
	}
		
	public boolean hasChanges(ILoadpathContainer oldContainer) {
		if (oldContainer == null || (oldContainer.getKind() == ILoadpathContainer.K_SYSTEM) != fIsSystemLibrary) {
			return true;
		}
		ILoadpathEntry[] oldEntries= oldContainer.getLoadpathEntries();
		if (fChildren.size() != oldEntries.length) {
			return true;
		}
		for (int i= 0; i < oldEntries.length; i++) {
			CPListElement child= (CPListElement) fChildren.get(i);
			if (!child.getLoadpathEntry().equals(oldEntries[i])) {
				return true;
			}
		}
		return false;
	}
	
	
}
