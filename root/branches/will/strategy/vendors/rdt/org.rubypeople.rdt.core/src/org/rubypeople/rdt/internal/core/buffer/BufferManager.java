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
package org.rubypeople.rdt.internal.core.buffer;

import java.text.NumberFormat;
import java.util.Enumeration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.core.Openable;

/**
 * The buffer manager manages the set of open buffers.
 * It implements an LRU cache of buffers.
 */
public class BufferManager {

	protected static BufferManager DEFAULT_BUFFER_MANAGER;
	public static boolean VERBOSE;

	/**
	 * LRU cache of buffers. The key and value for an entry
	 * in the table is the identical buffer.
	 */
	protected OverflowingLRUCache openBuffers = new BufferCache(60);
	

/**
 * Adds a buffer to the table of open buffers.
 */
public void addBuffer(IBuffer buffer) {
	if (VERBOSE) {
		String owner = ((Openable)buffer.getOwner()).toString();
		System.out.println("Adding buffer for " + owner); //$NON-NLS-1$
	}
	this.openBuffers.put(buffer.getOwner(), buffer);
	if (VERBOSE) {
		System.out.println("-> Buffer cache filling ratio = " + NumberFormat.getInstance().format(this.openBuffers.fillingRatio()) + "%"); //$NON-NLS-1$//$NON-NLS-2$
	}
}
public IBuffer createBuffer(IOpenable owner) {
	IRubyElement element = (IRubyElement)owner;
	IResource resource = element.getResource();
	return 
		new Buffer(
			resource instanceof IFile ? (IFile)resource : null, 
			owner, 
			element.isReadOnly());
}
/**
 * Returns the open buffer associated with the given owner,
 * or <code>null</code> if the owner does not have an open
 * buffer associated with it.
 */
public IBuffer getBuffer(IOpenable owner) {
	return (IBuffer)this.openBuffers.get(owner);
}
/**
 * Returns the default buffer manager.
 */
public synchronized static BufferManager getDefaultBufferManager() {
	if (DEFAULT_BUFFER_MANAGER == null) {
		DEFAULT_BUFFER_MANAGER = new BufferManager();
	}
	return DEFAULT_BUFFER_MANAGER;
}
/**
 * Returns an enumeration of all open buffers.
 * <p> 
 * The <code>Enumeration</code> answered is thread safe.
 *
 * @see OverflowingLRUCache
 * @return Enumeration of IBuffer
 */
public Enumeration getOpenBuffers() {
	synchronized (this.openBuffers) {
		this.openBuffers.shrink();
		return this.openBuffers.elements();
	}
}

/**
 * Removes a buffer from the table of open buffers.
 */
public void removeBuffer(IBuffer buffer) {
	this.openBuffers.remove(buffer.getOwner());
}
}
