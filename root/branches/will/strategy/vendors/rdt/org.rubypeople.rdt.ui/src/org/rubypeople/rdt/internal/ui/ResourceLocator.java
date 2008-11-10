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

import org.eclipse.core.resources.IResource;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * This class locates different resources
 * which are related to an object
 */
public class ResourceLocator implements IResourceLocator {
	
	public IResource getUnderlyingResource(Object element) throws RubyModelException {
		if (element instanceof IRubyElement)
			return ((IRubyElement) element).getUnderlyingResource();
		else
			return null;
	}

	public IResource getCorrespondingResource(Object element) throws RubyModelException {
		if (element instanceof IRubyElement)
			return ((IRubyElement) element).getCorrespondingResource();
		else
			return null;
	}

	public IResource getContainingResource(Object element) throws RubyModelException {
		IResource resource= null;
		if (element instanceof IResource)
			resource= (IResource) element;
		if (element instanceof IRubyElement) {
			resource= ((IRubyElement) element).getResource();
			if (resource == null)
				resource= ((IRubyElement) element).getRubyProject().getProject();
		}
		return resource;
	}
}
