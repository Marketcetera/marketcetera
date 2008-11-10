/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.core.search;

import org.eclipse.core.resources.IResource;
import org.rubypeople.rdt.core.IRubyElement;

/**
 * A Java search match that represents a field declaration.
 * The element is an <code>IField</code>.
 * <p>
 * This class is intended to be instantiated and subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class FieldDeclarationMatch extends SearchMatch {

	/**
	 * Creates a new field declaration match.
	 * 
	 * @param element the field declaration
	 * @param accuracy one of A_ACCURATE or A_INACCURATE
	 * @param offset the offset the match starts at, or -1 if unknown
	 * @param length the length of the match, or -1 if unknown
	 * @param participant the search participant that created the match
	 * @param resource the resource of the element
	 */
	public FieldDeclarationMatch(IRubyElement element, int accuracy, int offset, int length, SearchParticipant participant, IResource resource) {
		super(element, accuracy, offset, length, participant, resource);
	}
}
