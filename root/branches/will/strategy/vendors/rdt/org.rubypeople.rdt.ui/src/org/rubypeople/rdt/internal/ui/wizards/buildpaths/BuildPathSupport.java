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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.LoadpathContainerInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.ui.RubyUI;

/**
 *
 */
public class BuildPathSupport {
	
	public static final String JRE_PREF_PAGE_ID= "org.rubypeople.rdt.debug.ui.preferences.PreferencePageRubyInterpreter"; //$NON-NLS-1$

	
	private BuildPathSupport() {
		super();
	}
	
	private static class UpdatedLoadpathContainer implements ILoadpathContainer {

		private ILoadpathEntry[] fNewEntries;
		private ILoadpathContainer fOriginal;

		public UpdatedLoadpathContainer(ILoadpathContainer original, ILoadpathEntry[] newEntries) {
			fNewEntries= newEntries;
			fOriginal= original;
		}

		public ILoadpathEntry[] getLoadpathEntries() {
			return fNewEntries;
		}

		public String getDescription() {
			return fOriginal.getDescription();
		}

		public int getKind() {
			return fOriginal.getKind();
		}

		public IPath getPath() {
			return fOriginal.getPath();
		}
	}

	/**
	 * Apply a modified classpath entry to the classpath. The classpath entry can also be from a classpath container.
	 * @param shell If not null and the entry could not be found on the projects classpath, a dialog will ask to put the entry on the classpath
	 * @param newEntry The modified entry. The entry's kind or path must be unchanged.
	 * @param changedAttributes The attibutes that have changed. See {@link CPListElement} for constants values.
	 * @param jproject Project where the entry belongs to
	 * @param containerPath The path of the entry's parent container or <code>null</code> if the entry is not in a container
	 * @param monitor The progress monitor to use
	 * @throws CoreException
	 */
	public static void modifyLoadpathEntry(Shell shell, ILoadpathEntry newEntry, String[] changedAttributes, IRubyProject jproject, IPath containerPath, IProgressMonitor monitor) throws CoreException {
		if (containerPath != null) {
			updateContainerLoadpath(jproject, containerPath, newEntry, changedAttributes, monitor);
		} else {
			updateProjectLoadpath(shell, jproject, newEntry, changedAttributes, monitor);
		}
	}
	
	
	/**
	 * Apply a modified classpath entry to the classpath. The classpath entry can also be from a classpath container.
	 * @param shell If not null and the entry could not be found on the projects classpath, a dialog will ask to put the entry on the classpath
	 * @param newEntry The modified entry. The entry's kind or path must be unchanged.
	 * @param jproject Project where the entry belongs to
	 * @param containerPath The path of the entry's parent container or <code>null</code> if the entry is not in a container
	 * @param monitor The progress monitor to use
	 * @throws CoreException
	 */
	public static void modifyLoadpathEntry(Shell shell, ILoadpathEntry newEntry, IRubyProject jproject, IPath containerPath, IProgressMonitor monitor) throws CoreException {
		modifyLoadpathEntry(shell, newEntry, null, jproject, containerPath, monitor);
	}

	private static void updateContainerLoadpath(IRubyProject jproject, IPath containerPath, ILoadpathEntry newEntry, String[] changedAttributes, IProgressMonitor monitor) throws CoreException {
		ILoadpathContainer container= RubyCore.getLoadpathContainer(containerPath, jproject);
		if (container == null) {
			throw new CoreException(new Status(IStatus.ERROR, RubyUI.ID_PLUGIN, IStatus.ERROR, "Container " + containerPath + " cannot be resolved", null));  //$NON-NLS-1$//$NON-NLS-2$
		}
		ILoadpathEntry[] entries= container.getLoadpathEntries();
		ILoadpathEntry[] newEntries= new ILoadpathEntry[entries.length];
		for (int i= 0; i < entries.length; i++) {
			ILoadpathEntry curr= entries[i];
			if (curr.getEntryKind() == newEntry.getEntryKind() && curr.getPath().equals(newEntry.getPath())) {
				newEntries[i]= getUpdatedEntry(curr, newEntry, changedAttributes, jproject);
			} else {
				newEntries[i]= curr;
			}
		}
		requestContainerUpdate(jproject, container, newEntries);
		monitor.worked(1);
	}

	private static ILoadpathEntry getUpdatedEntry(ILoadpathEntry currEntry, ILoadpathEntry updatedEntry, String[] updatedAttributes, IRubyProject jproject) {
		if (updatedAttributes == null) {
			return updatedEntry; // used updated entry 'as is'
		}
		CPListElement currElem= CPListElement.createFromExisting(currEntry, jproject);
		CPListElement newElem= CPListElement.createFromExisting(updatedEntry, jproject);
		for (int i= 0; i < updatedAttributes.length; i++) {
			String attrib= updatedAttributes[i];
			currElem.setAttribute(attrib, newElem.getAttribute(attrib));
		}
		return currElem.getLoadpathEntry();
	}

	/**
	 * Request a container update.
	 * @param jproject The project of the container
	 * @param container The container to requesta  change to
	 * @param newEntries The updated entries
	 * @throws CoreException
	 */
	public static void requestContainerUpdate(IRubyProject jproject, ILoadpathContainer container, ILoadpathEntry[] newEntries) throws CoreException {
		IPath containerPath= container.getPath();
		ILoadpathContainer updatedContainer= new UpdatedLoadpathContainer(container, newEntries);
		LoadpathContainerInitializer initializer= RubyCore.getLoadpathContainerInitializer(containerPath.segment(0));
		if (initializer != null) {
			initializer.requestLoadpathContainerUpdate(containerPath, jproject, updatedContainer);
		}
	}

	private static void updateProjectLoadpath(Shell shell, IRubyProject jproject, ILoadpathEntry newEntry, String[] changedAttributes, IProgressMonitor monitor) throws RubyModelException {
		ILoadpathEntry[] oldLoadpath= jproject.getRawLoadpath();
		int nEntries= oldLoadpath.length;
		ArrayList newEntries= new ArrayList(nEntries + 1);
		int entryKind= newEntry.getEntryKind();
		IPath jarPath= newEntry.getPath();
		boolean found= false;
		for (int i= 0; i < nEntries; i++) {
			ILoadpathEntry curr= oldLoadpath[i];
			if (curr.getEntryKind() == entryKind && curr.getPath().equals(jarPath)) {
				// add modified entry
				newEntries.add(getUpdatedEntry(curr, newEntry, changedAttributes, jproject));
				found= true;
			} else {
				newEntries.add(curr);
			}
		}
		if (!found) {
			if (!putJarOnLoadpathDialog(shell)) {
				return;
			}
			// add new
			newEntries.add(newEntry);			
		}
		ILoadpathEntry[] newLoadpath= (ILoadpathEntry[]) newEntries.toArray(new ILoadpathEntry[newEntries.size()]);
		jproject.setRawLoadpath(newLoadpath, monitor);
	}
	
	private static boolean putJarOnLoadpathDialog(final Shell shell) {
		if (shell == null) {
			return false;
		}
		
		final boolean[] result= new boolean[1];
		shell.getDisplay().syncExec(new Runnable() {
			public void run() {
				String title= NewWizardMessages.BuildPathSupport_putoncpdialog_title; 
				String message= NewWizardMessages.BuildPathSupport_putoncpdialog_message; 
				result[0]= MessageDialog.openQuestion(shell, title, message);
			}
		});
		return result[0];
	}
}
