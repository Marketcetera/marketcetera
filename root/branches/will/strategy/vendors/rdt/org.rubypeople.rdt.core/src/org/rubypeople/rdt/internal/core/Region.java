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
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;

import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRegion;
import org.rubypeople.rdt.core.IRubyElement;


/**
 * @see IRegion
 */
 
public class Region implements IRegion {

	/**
	 * A collection of the top level elements
	 * that have been added to the region
	 */
	protected ArrayList fRootElements;
/**
 * Creates an empty region.
 *
 * @see IRegion
 */
public Region() {
	fRootElements = new ArrayList(1);
}
/**
 * @see IRegion#add(IRubyElement)
 */
public void add(IRubyElement element) {
	if (!contains(element)) {
		//"new" element added to region
		removeAllChildren(element);
		fRootElements.add(element);
		fRootElements.trimToSize();
	}
}
/**
 * @see IRegion
 */
public boolean contains(IRubyElement element) {
	
	int size = fRootElements.size();
	ArrayList parents = getAncestors(element);
	
	for (int i = 0; i < size; i++) {
		IRubyElement aTop = (IRubyElement) fRootElements.get(i);
		if (aTop.equals(element)) {
			return true;
		}
		for (int j = 0, pSize = parents.size(); j < pSize; j++) {
			if (aTop.equals(parents.get(j))) {
				//an ancestor is already included
				return true;
			}
		}
	}
	return false;
}
/**
 * Returns a collection of all the parents of this element
 * in bottom-up order.
 *
 */
private ArrayList getAncestors(IRubyElement element) {
	ArrayList parents = new ArrayList();
	IRubyElement parent = element.getParent();
	while (parent != null) {
		parents.add(parent);
		parent = parent.getParent();
	}
	parents.trimToSize();
	return parents;
}
/**
 * @see IRegion
 */
public IRubyElement[] getElements() {
	int size= fRootElements.size();
	IRubyElement[] roots= new IRubyElement[size];
	for (int i = 0; i < size; i++) {
		roots[i]= (IRubyElement) fRootElements.get(i);
	}

	return roots;
}
/**
 * @see IRegion#remove(IRubyElement)
 */
public boolean remove(IRubyElement element) {

	removeAllChildren(element);
	return fRootElements.remove(element);
}
/**
 * Removes any children of this element that are contained within this
 * region as this parent is about to be added to the region.
 *
 * <p>Children are all children, not just direct children.
 */
protected void removeAllChildren(IRubyElement element) {
	if (element instanceof IParent) {
		ArrayList newRootElements = new ArrayList();
		for (int i = 0, size = fRootElements.size(); i < size; i++) {
			IRubyElement currentRoot = (IRubyElement)fRootElements.get(i);
			//walk the current root hierarchy
			IRubyElement parent = currentRoot.getParent();
			boolean isChild= false;
			while (parent != null) {
				if (parent.equals(element)) {
					isChild= true;
					break;
				}
				parent = parent.getParent();
			}
			if (!isChild) {
				newRootElements.add(currentRoot);
			}
		}
		fRootElements= newRootElements;
	}
}
/**
 * Returns a printable representation of this region.
 */
public String toString() {
	StringBuffer buffer= new StringBuffer();
	IRubyElement[] roots= getElements();
	buffer.append('[');
	for (int i= 0; i < roots.length; i++) {
		buffer.append(roots[i].getElementName());
		if (i < (roots.length - 1)) {
			buffer.append(", "); //$NON-NLS-1$
		}
	}
	buffer.append(']');
	return buffer.toString();
}
}
