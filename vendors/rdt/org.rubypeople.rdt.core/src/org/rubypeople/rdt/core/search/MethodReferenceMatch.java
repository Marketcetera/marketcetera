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

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.rubypeople.rdt.core.IRubyElement;

/**
 * A Java search match that represents a method reference.
 * The element is the inner-most enclosing member that references this method.
 * <p>
 * This class is intended to be instantiated and subclassed by clients.
 * </p>
 * 
 * @since 3.0
 */
public class MethodReferenceMatch extends SearchMatch {
	private boolean constructor;
	private IRubyElement binding;
	private List<String> arguments;

	/**
	 * Creates a new method reference match.
	 * 
	 * @param enclosingElement the inner-most enclosing member that references this method
	 * @param accuracy one of {@link #A_ACCURATE} or {@link #A_INACCURATE}
	 * @param offset the offset the match starts at, or -1 if unknown
	 * @param length the length of the match, or -1 if unknown
	 * @param insideDocComment <code>true</code> if this search match is inside a doc
	 * comment, and <code>false</code> otherwise
	 * @param participant the search participant that created the match
	 * @param resource the resource of the element
	 */
	public MethodReferenceMatch(IRubyElement enclosingElement, int accuracy, int offset, int length, boolean insideDocComment, SearchParticipant participant, IResource resource) {
		super(enclosingElement, accuracy, offset, length, participant, resource);
		setInsideDocComment(insideDocComment);
	}

	/**
	 * Creates a new method reference match.
	 * 
	 * @param enclosingElement the inner-most enclosing member that references this method
	 * @param accuracy one of {@link #A_ACCURATE} or {@link #A_INACCURATE}
	 * @param offset the offset the match starts at, or -1 if unknown
	 * @param length the length of the match, or -1 if unknown
	 * @param constructor <code>true</code> if this search match a constructor
	 * <code>false</code> otherwise
	 * @param insideDocComment <code>true</code> if this search match is inside a doc
	 * comment, and <code>false</code> otherwise
	 * @param participant the search participant that created the match
	 * @param resource the resource of the element
	 * @since 1.0
	 */
	public MethodReferenceMatch(IRubyElement enclosingElement, IRubyElement binding, List<String> args, int accuracy, int offset, int length, boolean constructor, boolean insideDocComment, SearchParticipant participant, IResource resource) {
		this(enclosingElement, accuracy, offset, length, insideDocComment, participant, resource);
		this.arguments = args;
		this.constructor = constructor;
		this.binding = binding;
	}

	/**
	 * Returns whether the reference is on a constructor.
	 *
	 * @return Returns whether the reference is on a constructor or not.
	 * @since 3.1
	 */
	public final boolean isConstructor() {
		return this.constructor;
	}
	
	public IRubyElement getBinding() {
		return this.binding;
	}

	public List<String> getArguments() {
		return this.arguments;
	}
	
}
