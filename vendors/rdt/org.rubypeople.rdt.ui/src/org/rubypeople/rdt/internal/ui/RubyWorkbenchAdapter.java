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
package org.rubypeople.rdt.internal.ui;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.ui.RubyElementLabels;

/**
 * An imlementation of the IWorkbenchAdapter for IRubyElements.
 */
public class RubyWorkbenchAdapter implements IWorkbenchAdapter {
	
	protected static final Object[] NO_CHILDREN= new Object[0];
	
	private RubyElementImageProvider fImageProvider;
	
	public RubyWorkbenchAdapter() {
		fImageProvider= new RubyElementImageProvider();
	}

	public Object[] getChildren(Object element) {
		IRubyElement je= getRubyElement(element);
		if (je instanceof IParent) {
			try {
				return ((IParent)je).getChildren();
			} catch(RubyModelException e) {
				RubyPlugin.log(e); 
			}
		}
		return NO_CHILDREN;
	}

	public ImageDescriptor getImageDescriptor(Object element) {
		IRubyElement je= getRubyElement(element);
		if (je != null)
			return fImageProvider.getRubyImageDescriptor(je, RubyElementImageProvider.OVERLAY_ICONS | RubyElementImageProvider.SMALL_ICONS);
		
		return null;
		
	}

	public String getLabel(Object element) {
		return RubyElementLabels.getTextLabel(getRubyElement(element), RubyElementLabels.ALL_DEFAULT);
	}

	public Object getParent(Object element) {
		IRubyElement je= getRubyElement(element);
		return je != null ? je.getParent() :  null;
	}
	
	private IRubyElement getRubyElement(Object element) {
		if (element instanceof IRubyElement)
			return (IRubyElement)element;
		if (element instanceof IRubyScriptEditorInput)
			return ((IRubyScriptEditorInput)element).getRubyScript().getPrimaryElement();

		return null;
	}
}
