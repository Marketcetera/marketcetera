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
 ********************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Chapman, mpchapman@gmail.com - 89977 Make JDT .java agnostic
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.wizards;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
  */
public class LoadPathDetector implements IResourceProxyVisitor {
		
	private HashMap fSourceFolders;		
	private IProject fProject;		
	private ILoadpathEntry[] fResultLoadpath;
	private IProgressMonitor fMonitor;
	
	private static class LPSorter implements Comparator {
		private Collator fCollator= Collator.getInstance();
		public int compare(Object o1, Object o2) {
			ILoadpathEntry e1= (ILoadpathEntry) o1;
			ILoadpathEntry e2= (ILoadpathEntry) o2;
			return fCollator.compare(e1.getPath().toString(), e2.getPath().toString());
		}
	}
	
	
	public LoadPathDetector(IProject project, IProgressMonitor monitor) throws CoreException {
		fSourceFolders= new HashMap();
		fProject= project;
			
		fResultLoadpath= null;
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
			
		detectLoadpath(monitor);
	}
	
	/**
	 * Method detectLoadpath.
	 * @param monitor The progress monitor (not null)
	 * @throws CoreException 
	 */
	private void detectLoadpath(IProgressMonitor monitor) throws CoreException {
		try {
			monitor.beginTask(NewWizardMessages.LoadPathDetector_operation_description, 2); 
			
			fMonitor= monitor;
			fProject.accept(this, IResource.NONE);
			monitor.worked(1);
			
			ArrayList cpEntries= new ArrayList();

			detectSourceFolders(cpEntries);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			monitor.worked(1);
			
			if (cpEntries.isEmpty()) {
				return;
			}
			ILoadpathEntry[] jreEntries= PreferenceConstants.getDefaultRubyVMLibrary();
			for (int i= 0; i < jreEntries.length; i++) {
				cpEntries.add(jreEntries[i]);
			}

			ILoadpathEntry[] entries= (ILoadpathEntry[]) cpEntries.toArray(new ILoadpathEntry[cpEntries.size()]);
			if (!RubyConventions.validateLoadpath(RubyCore.create(fProject), entries, null).isOK()) {
				return;
			}

			fResultLoadpath= entries;
		} finally {
			monitor.done();
		}
	}
	
	private void detectSourceFolders(ArrayList resEntries) {
		ArrayList res= new ArrayList();
		Set sourceFolderSet= fSourceFolders.keySet();
		for (Iterator iter= sourceFolderSet.iterator(); iter.hasNext();) {
			IPath path= (IPath) iter.next();
			ArrayList excluded= new ArrayList();
			for (Iterator inner= sourceFolderSet.iterator(); inner.hasNext();) {
				IPath other= (IPath) inner.next();
				if (!path.equals(other) && path.isPrefixOf(other)) {
					IPath pathToExclude= other.removeFirstSegments(path.segmentCount()).addTrailingSeparator();
					excluded.add(pathToExclude);
				}
			}
			IPath[] excludedPaths= (IPath[]) excluded.toArray(new IPath[excluded.size()]);
			ILoadpathEntry entry= RubyCore.newSourceEntry(path, excludedPaths);
			res.add(entry);
		}
		Collections.sort(res, new LPSorter());
		resEntries.addAll(res);
	}

	private void visitRubyScript(IFile file) {
		IRubyScript cu= RubyCore.createRubyScriptFrom(file);
		if (cu != null) {
			IRubyScript workingCopy= null;
			try {
				workingCopy= cu.getWorkingCopy(null);				
				IPath packPath= file.getParent().getFullPath();
				String cuName= file.getName();
				addToMap(fSourceFolders, packPath, new Path(cuName));				
			} catch (RubyModelException e) {
				// ignore
			} finally {
				if (workingCopy != null) {
					try {
						workingCopy.discardWorkingCopy();
					} catch (RubyModelException ignore) {
					}
				}
			}
		}
	}
		
	private void addToMap(HashMap map, IPath folderPath, IPath relPath) {
		List list= (List) map.get(folderPath);
		if (list == null) {
			list= new ArrayList(50);
			map.put(folderPath, list);
		}		
		list.add(relPath);
	}

	private boolean isValidScriptName(String name) {
		return !RubyConventions.validateRubyScriptName(name).matches(IStatus.ERROR);
	}	

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceProxyVisitor#visit(org.eclipse.core.resources.IResourceProxy)
	 */
	public boolean visit(IResourceProxy proxy) {
		if (fMonitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		
		if (proxy.getType() == IResource.FILE) {
			String name= proxy.getName();
			if (isValidScriptName(name)) {
				visitRubyScript((IFile) proxy.requestResource());
			}
			return false;
		}
		return true;
	}
		
	public ILoadpathEntry[] getLoadpath() {
		return fResultLoadpath;
	}
}
