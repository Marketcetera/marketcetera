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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.rubypeople.rdt.internal.ui.viewsupport.ProblemTableViewer;

/**
 * Special problem table viewer to handle logical packages.
 */
class PackagesViewTableViewer extends ProblemTableViewer implements IPackagesViewViewer {

	public PackagesViewTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	public void mapElement(Object element, Widget item) {
		super.mapElement(element, item);
	}

	public void unmapElement(Object element, Widget item) {
		super.unmapElement(element, item);
	}

	/*
	 * @see org.eclipse.jface.viewers.StructuredViewer#getFilteredChildren(java.
	 * lang.Object)
	 */
	protected Object[] getFilteredChildren(Object parent) {

		Object[] result= getRawChildren(parent);
		List list= new ArrayList();
		if (result != null) {
			Object[] toBeFiltered= new Object[1];
			for (int i= 0; i < result.length; i++) {
				Object object= result[i];
				toBeFiltered[0]= object;
				if (filter(toBeFiltered).length == 1)
					list.add(object);
			}
		}
		return list.toArray();
	}

	// --------- see: IPackagesViewViewer ----------

	public Widget doFindItem(Object element){
		return super.doFindItem(element);
	}

	public Widget doFindInputItem(Object element){
		return super.doFindInputItem(element);
	}

	public List getSelectionFromWidget(){
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
