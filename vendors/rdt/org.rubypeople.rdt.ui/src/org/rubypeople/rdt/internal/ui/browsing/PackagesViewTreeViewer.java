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
package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTreeViewer;

/**
 * Special problem tree viewer to handle logical packages.
 */
public class PackagesViewTreeViewer extends ProblemTreeViewer implements IPackagesViewViewer {

	public PackagesViewTreeViewer(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	public void unmapElement(Object element) {
		super.unmapElement(element);
	}
	
	@Override
	public void unmapElement(Object element, Widget item) {
		super.unmapElement(element, item);
	}
	
	@Override
	public void mapElement(Object element, Widget item) {
		super.mapElement(element, item);
	}

	/*
	 * @see org.eclipse.jface.viewers.StructuredViewer#getFilteredChildren(java.lang.Object)
	 */
	protected Object[] getFilteredChildren(Object parent) {
		List list= new ArrayList();
		Object[] result= getRawChildren(parent);
		if (result != null)	{
			Object[] toBeFiltered= new Object[1];
			for (int i= 0; i < result.length; i++) {
				Object object= result[i];
				toBeFiltered[0]= object;
				if (isEssential(object) || filter(toBeFiltered).length == 1)
					list.add(object);
			}
		}
		return list.toArray();
	}


	/*
	 * @see org.eclipse.jface.viewers.StructuredViewer#filter(java.lang.Object[])
	 * @since 3.0
	 */
	protected Object[] filter(Object[] elements) {
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

	/*
	 * @see AbstractTreeViewer#isExpandable(java.lang.Object)
	 */
	public boolean isExpandable(Object parent) {
		Object[] children= ((ITreeContentProvider)getContentProvider()).getChildren(parent);
		Object[] toBeFiltered= new Object[1];
		for (int i = 0; i < children.length; i++) {
			Object object= children[i];

			if (isEssential(object))
				return true;

			toBeFiltered[0]= object;
			Object[] filtered= filter(toBeFiltered);
			if (filtered.length > 0)
				return true;
		}
		return false;
	}

	private boolean isEssential(Object object) {
		try {
			if (object instanceof ISourceFolder) {
				ISourceFolder fragment= (ISourceFolder) object;
				return !fragment.isDefaultPackage() && fragment.hasSubfolders();
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		}

		return false;
	}

	// --------- see: IPackagesViewViewer ----------

	public Widget doFindItem(Object element) {
		return super.doFindItem(element);
	}

	public Widget doFindInputItem(Object element) {
		return super.doFindInputItem(element);
	}

	public List getSelectionFromWidget() {
		return super.getSelectionFromWidget();
	}

	public void doUpdateItem(Widget item, Object element, boolean fullMap){
		super.doUpdateItem(item, element, fullMap);
	}

	public void internalRefresh(Object element){
		super.internalRefresh(element);
	}

	public void setSelectionToWidget(List l, boolean reveal){
		super.setSelectionToWidget(l, reveal);
	}
}
