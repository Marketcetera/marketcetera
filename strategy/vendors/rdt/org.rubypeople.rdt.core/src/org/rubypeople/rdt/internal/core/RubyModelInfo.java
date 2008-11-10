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
package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * Implementation of IRubyModel. A Ruby Model is specific to a
 * workspace.
 *
 * @see org.eclipse.jdt.core.IRubyModel
 */
public class RubyModelInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-java projects contained by this model
	 */
	Object[] nonRubyResources;

/**
 * Compute the non-java resources contained in this java project.
 */
private Object[] computeNonRubyResources() {
	IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
	int length = projects.length;
	Object[] resources = null;
	int index = 0;
	for (int i = 0; i < length; i++) {
		IProject project = projects[i];
		if (!RubyProject.hasRubyNature(project)) {
			if (resources == null) {
				resources = new Object[length];
			}
			resources[index++] = project;
		}
	}
	if (index == 0) return NO_NON_RUBY_RESOURCES;
	if (index < length) {
		System.arraycopy(resources, 0, resources = new Object[index], 0, index);
	}
	return resources;
}

/**
 * Returns an array of non-ruby resources contained in the receiver.
 */
Object[] getNonRubyResources() {

	if (this.nonRubyResources == null) {
		this.nonRubyResources = computeNonRubyResources();
	}
	return this.nonRubyResources;
}
}
