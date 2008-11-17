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
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;

class ProjectAndSourceFolderContentProvider extends RubyBrowsingContentProvider {

	ProjectAndSourceFolderContentProvider(RubyBrowsingPart browsingPart) {
		super(false, browsingPart);
	}

	/* (non-Rubydoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		try {
			startReadInDisplayThread();
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result= new Object[0];
				Class clazz= null;
				Iterator iter= ((IStructuredSelection)element).iterator();
				while (iter.hasNext()) {
					Object item=  iter.next();
					if (clazz == null)
						clazz= item.getClass();
					if (clazz == item.getClass())
						result= concatenate(result, getChildren(item));
					else
						return NO_CHILDREN;
				}
				return result;
			}
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result= new Object[0];
				Iterator iter= ((IStructuredSelection)element).iterator();
				while (iter.hasNext())
					result= concatenate(result, getChildren(iter.next()));
				return result;
			}
			if (element instanceof IRubyProject)
				return getSourceFolderRoots((IRubyProject)element);
			if (element instanceof ISourceFolderRoot)
				return NO_CHILDREN;

			return super.getChildren(element);

		} catch (RubyModelException e) {
			return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}

	protected Object[] getSourceFolderRoots(IRubyProject project) throws RubyModelException {
		if (!project.getProject().isOpen())
			return NO_CHILDREN;

		ISourceFolderRoot[] roots= project.getSourceFolderRoots();
		List list= new ArrayList(roots.length);
		// filter out package fragments that correspond to projects and
		// replace them with the package fragments directly
		for (int i= 0; i < roots.length; i++) {
			ISourceFolderRoot root= roots[i];
			if (!isProjectSourceFolderRoot(root))
				list.add(root);
		}
		return list.toArray();
	}

	/*
	 *
	 * @see ITreeContentProvider
	 */
	public boolean hasChildren(Object element) {
		return element instanceof IRubyProject && super.hasChildren(element);
	}
}
