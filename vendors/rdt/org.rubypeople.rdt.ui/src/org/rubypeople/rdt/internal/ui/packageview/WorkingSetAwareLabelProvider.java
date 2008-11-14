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
package org.rubypeople.rdt.internal.ui.packageview;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.ui.IWorkingSet;

public class WorkingSetAwareLabelProvider extends PackageExplorerLabelProvider {

	private Map fImages= new HashMap();
	
	public WorkingSetAwareLabelProvider(long textFlags, int imageFlags, PackageExplorerContentProvider cp) {
		super(textFlags, imageFlags, cp);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText(Object element) {
		if (element instanceof IWorkingSet) {
			return decorateText(((IWorkingSet)element).getLabel(), element);
		} 
		return super.getText(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Image getImage(Object element) {
		if (element instanceof IWorkingSet) {
			ImageDescriptor image= ((IWorkingSet)element).getImage();
			Image result= (Image)fImages.get(image);
			if (result == null) {
				result= image.createImage();
				fImages.put(image, result);
			}
			return decorateImage(result, element);
		}
		return super.getImage(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
		for (Iterator iter= fImages.values().iterator(); iter.hasNext();) {
			((Image)iter.next()).dispose();
		}
		super.dispose();
	}
}
