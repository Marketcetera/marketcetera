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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.wizards.NewElementWizard;

public abstract class BuildPathWizard extends NewElementWizard {
	
	private boolean fDoFlushChange;
	private final CPListElement fEntryToEdit;
	private ISourceFolderRoot fSourceFolderRoot;
	private final ArrayList fExistingEntries;

	public BuildPathWizard(CPListElement[] existingEntries, CPListElement newEntry, String titel, ImageDescriptor image) {
		if (image != null)
			setDefaultPageImageDescriptor(image);
		
		setDialogSettings(RubyPlugin.getDefault().getDialogSettings());
		setWindowTitle(titel);

		fEntryToEdit= newEntry;
		fExistingEntries= new ArrayList(Arrays.asList(existingEntries));
		fDoFlushChange= true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		if (fDoFlushChange) {
			IRubyProject rubyProject= getEntryToEdit().getRubyProject();
			
			BuildPathsBlock.flush(getExistingEntries(), rubyProject, monitor);
			
			IProject project= rubyProject.getProject();
			IPath projPath= project.getFullPath();
			IPath path= getEntryToEdit().getPath();
			
			if (!projPath.equals(path) && projPath.isPrefixOf(path)) {
				path= path.removeFirstSegments(projPath.segmentCount());
			}
			
			IFolder folder= project.getFolder(path);
			fSourceFolderRoot= rubyProject.getSourceFolderRoot(folder);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public IRubyElement getCreatedElement() {
		return fSourceFolderRoot;
	}
	
	public void setDoFlushChange(boolean b) {
		fDoFlushChange= b;
	}
	
	public ArrayList getExistingEntries() {
		return fExistingEntries;
	}

	protected CPListElement getEntryToEdit() {
		return fEntryToEdit;
	}

	public List/*<CPListElement>*/ getInsertedElements() {
		return new ArrayList();
	}

	public List/*<CPListElement>*/ getRemovedElements() {
		return new ArrayList();
	}

	public List/*<CPListElement>*/ getModifiedElements() {
		ArrayList result= new ArrayList(1);
		result.add(fEntryToEdit);
		return result;
	}
	
	public abstract void cancel();

}
