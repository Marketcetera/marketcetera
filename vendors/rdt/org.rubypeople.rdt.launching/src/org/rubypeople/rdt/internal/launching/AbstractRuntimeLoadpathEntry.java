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
package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry;
import org.rubypeople.rdt.launching.IRuntimeLoadpathEntry2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Common function for runtime classpath entries.
 * <p>
 * Clients implementing runtime classpath entries must subclass this
 * class.
 * </p>
 * @since 3.0
 */
public abstract class AbstractRuntimeLoadpathEntry extends PlatformObject implements IRuntimeLoadpathEntry2 {
	
	private IPath sourceAttachmentPath = null;
	private IPath rootSourcePath = null;
	private int classpathProperty = IRuntimeLoadpathEntry.USER_CLASSES;
	/**
	 * Associated ruby project, or <code>null</code>
	 */
	private IRubyProject fRubyProject;
	
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>false</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.internal.launching.IRuntimeLoadpathEntry2#isComposite()
	 */
	public boolean isComposite() {
		return false;
	}
	
	/* (non-rubydoc)
	 * 
	 * Default implementation returns an empty collection.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.internal.launching.IRuntimeLoadpathEntry2#getRuntimeLoadpathEntries()
	 */
	public IRuntimeLoadpathEntry[] getRuntimeLoadpathEntries() throws CoreException {
		return new IRuntimeLoadpathEntry[0];
	}
	
	/**
	 * Throws an exception with the given message and underlying exception.
	 * 
	 * @param message error message
	 * @param exception underlying exception or <code>null</code> if none
	 * @throws CoreException
	 */
	protected void abort(String message, Throwable exception) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, message, exception);
		throw new CoreException(status);
	}

	/* (non-rubydoc)
	 * 
	 * Default implementation generates a string containing an XML
	 * document. Subclasses should override <code>buildMemento</code>
	 * to specify the contents of the required <code>memento</code>
	 * node.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getMemento()
	 */
	public String getMemento() throws CoreException {
		Document doc= DebugPlugin.newDocument();
		Element root = doc.createElement("runtimeLoadpathEntry"); //$NON-NLS-1$
		doc.appendChild(root);
		root.setAttribute("id", getTypeId()); //$NON-NLS-1$
		Element memento = doc.createElement("memento"); //$NON-NLS-1$
		root.appendChild(memento);
		buildMemento(doc, memento);
		return DebugPlugin.serializeDocument(doc);
	}
	
	/**
	 * Constructs a memento for this classpath entry in the given 
	 * document and element. The memento element has already been
	 * appended to the document.
	 * 
	 * @param document XML document
	 * @param memento element node for client specific attributes
	 * @throws CoreException if unable to create a memento 
	 */
	protected abstract void buildMemento(Document document, Element memento) throws CoreException;
	
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getPath()
	 */
	public IPath getPath() {
		return null;
	}
	
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getResource()
	 */
	public IResource getResource() {
		return null;
	}
	
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getSourceAttachmentPath()
	 */
	public IPath getSourceAttachmentPath() {
		return sourceAttachmentPath;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#setSourceAttachmentPath(org.eclipse.core.runtime.IPath)
	 */
	public void setSourceAttachmentPath(IPath path) {
		sourceAttachmentPath = path;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getSourceAttachmentRootPath()
	 */
	public IPath getSourceAttachmentRootPath() {
		return rootSourcePath;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#setSourceAttachmentRootPath(org.eclipse.core.runtime.IPath)
	 */
	public void setSourceAttachmentRootPath(IPath path) {
		rootSourcePath = path;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getLoadpathProperty()
	 */
	public int getLoadpathProperty() {
		return classpathProperty;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#setLoadpathProperty(int)
	 */
	public void setLoadpathProperty(int property) {
		classpathProperty = property;
	}
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getLocation()
	 */
	public String getLocation() {
		return null;
	}
	
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getSourceAttachmentLocation()
	 */
	public String getSourceAttachmentLocation() {
		return null;
	}
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getSourceAttachmentRootLocation()
	 */
	public String getSourceAttachmentRootLocation() {
		return null;
	}
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getVariableName()
	 */
	public String getVariableName() {
		return null;
	}
	/* (non-rubydoc)
	 * 
	 * Default implementation returns <code>null</code>.
	 * Subclasses should override if required.
	 * 
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getLoadpathEntry()
	 */
	public ILoadpathEntry getLoadpathEntry() {
		return null;
	}
	/* (non-rubydoc)
	 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry#getrubyProject()
	 */
	public IRubyProject getRubyProject() {
		return fRubyProject;
	}
	
	/**
	 * Sets the ruby project associated with this entry.
	 * 
	 * @param javaProject
	 */
	protected void setRubyProject(IRubyProject javaProject) {
		fRubyProject = javaProject;
	}
}
