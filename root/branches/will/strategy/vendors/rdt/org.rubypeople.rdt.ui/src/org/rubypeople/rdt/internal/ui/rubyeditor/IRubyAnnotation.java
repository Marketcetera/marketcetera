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
package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Iterator;

import org.rubypeople.rdt.core.IRubyScript;


/**
 * Interface of annotations representing markers
 * and problems.
 * 
 * @see org.eclipse.core.resources.IMarker
 * @see org.eclipse.jdt.core.compiler.IProblem
 */
public interface IRubyAnnotation {
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#getType()
	 */
	String getType();
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#isPersistent()
	 */
	boolean isPersistent();
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#isMarkedDeleted()
	 */
	boolean isMarkedDeleted();
	
	/**
	 * @see org.eclipse.jface.text.source.Annotation#getText() 
	 */
	String getText();
	
	/**
	 * Returns whether this annotation is overlaid.
	 * 
	 * @return <code>true</code> if overlaid
	 */
	boolean hasOverlay();
	
	/**
	 * Returns the overlay of this annotation.
	 * 
	 * @return the annotation's overlay
	 * @since 3.0
	 */
	IRubyAnnotation getOverlay();
	
	/**
	 * Returns an iterator for iterating over the
 	 * annotation which are overlaid by this annotation.
	 * 
	 * @return an iterator over the overlaid annotations
	 */
	Iterator getOverlaidIterator();
	
	/**
	 * Adds the given annotation to the list of
	 * annotations which are overlaid by this annotations.
	 *  
	 * @param annotation the problem annotation
	 */
	void addOverlaid(IRubyAnnotation annotation);
	
	/**
	 * Removes the given annotation from the list of
	 * annotations which are overlaid by this annotation.
	 *  
	 * @param annotation the problem annotation
	 */
	void removeOverlaid(IRubyAnnotation annotation);
	
	/**
	 * Tells whether this annotation is a problem
	 * annotation.
	 * 
	 * @return <code>true</code> if it is a problem annotation
	 */
	boolean isProblem();
	
	/**
	 * Returns the compilation unit corresponding to the document on which the annotation is set
	 * or <code>null</code> if no corresponding co0mpilationunit exists.
	 */
	IRubyScript getRubyScript();

	/**
	 * Returns the problem id or <code>-1</code> if no problem id can be evaluated.
	 * 
	 * @return returns the problem id or <code>-1</code>
	 */
	int getId();

	/**
	 * Returns the marker type associated to this problem or <code>null<code> if no marker type
	 * can be evaluated. 
	 * 
	 * @return the type of the marker which would be associated to the problem or
	 * <code>null<code> if no marker type can be evaluated. 
	 */
	String getMarkerType();
	
	/**
	 * Returns the problem arguments or <code>null</code> if no problem arguments can be evaluated.
	 * 
	 * @return returns the problem arguments or <code>null</code> if no problem
	 *  arguments can be evaluated.
	 */
	String[] getArguments();

}
