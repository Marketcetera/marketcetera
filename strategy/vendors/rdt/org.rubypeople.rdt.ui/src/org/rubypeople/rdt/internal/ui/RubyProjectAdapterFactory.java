/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.IRubyProject;

/**
 * An adapter factory for IRubyProjects.
 */
public class RubyProjectAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IProject.class,};

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

	public Object getAdapter(Object element, Class key) {
		if (IProject.class.equals(key)) {
			IRubyProject rubyProject = (IRubyProject) element;
			return rubyProject.getProject();
		}
		return null;
	}
}
