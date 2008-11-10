/**
 * Copyright (c) 2005-2008 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Aptana Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 *
 * Redistribution, except as permitted by the above license, is prohibited.
 * Any modifications to this file must keep this entire header intact.
 */
package org.rubypeople.rdt.internal.core.parser;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyModelMarker;

class IgnoreMarker {
	private IResource resource;
	private int id;
	private int offset;
	private int endOffset;
	
	IgnoreMarker(IMarker marker) throws CoreException {
		this.id = ((Integer) marker.getAttribute(IRubyModelMarker.ID)).intValue();
		this.offset = ((Integer) marker.getAttribute(IMarker.CHAR_START)).intValue();
		this.endOffset = ((Integer) marker.getAttribute(IMarker.CHAR_END)).intValue();
		this.resource = marker.getResource();
	}
	
	public int getEndOffset() {
		return endOffset;
	}

	public int getOffset() {
		return offset;
	}

	public int getId() {
		return id;
	}

	public IResource getResource() {
		return resource;
	}

	IgnoreMarker(IResource resource, int id, int offset, int endOffset) {
		this.resource = resource;
		this.id = id;
		this.offset = offset;
		this.endOffset = endOffset;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof IgnoreMarker) {
			IgnoreMarker other = (IgnoreMarker) obj;
			return other.getId() == getId() && other.getOffset() == getOffset() && other.getEndOffset() == getEndOffset() && other.getResource().equals(getResource());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "<" + getResource().getLocation().toPortableString() + "> id: " + getId() + ", start: " + getOffset() + ", end: " + getEndOffset();
	}
}