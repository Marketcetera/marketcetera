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

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;

public class RubyMarkerAnnotation extends MarkerAnnotation implements IRubyAnnotation {

	public static final String RUBY_MARKER_TYPE_PREFIX= "org.rubypeople.rdt"; //$NON-NLS-1$
	public static final String ERROR_ANNOTATION_TYPE= "org.rubypeople.rdt.ui.error"; //$NON-NLS-1$
	public static final String WARNING_ANNOTATION_TYPE= "org.rubypeople.rdt.ui.warning"; //$NON-NLS-1$
	public static final String INFO_ANNOTATION_TYPE= "org.rubypeople.rdt.ui.info"; //$NON-NLS-1$
	public static final String TASK_ANNOTATION_TYPE= "org.eclipse.ui.workbench.texteditor.task"; //$NON-NLS-1$

	private IRubyAnnotation fOverlay;
		
	public RubyMarkerAnnotation(IMarker marker) {
		super(marker);
	}
		
	/*
	 * @see IRubyAnnotation#getArguments()
	 */
	public String[] getArguments() {
		// FIXME Uncomment when we can do corrections!
//		IMarker marker= getMarker();
//		if (marker != null && marker.exists() && isProblem())
//			return CorrectionEngine.getProblemArguments(marker);
		return null;
	}

	/*
	 * @see IRubyAnnotation#getId()
	 */
	public int getId() {
		IMarker marker= getMarker();
		if (marker == null  || !marker.exists())
			return -1;
		
		if (isProblem())
			return marker.getAttribute(IRubyModelMarker.ID, -1);
		
		return -1;
	}
	
	/*
	 * @see IRubyAnnotation#isProblem()
	 */
	public boolean isProblem() {
		String type= getType();
		return WARNING_ANNOTATION_TYPE.equals(type) || ERROR_ANNOTATION_TYPE.equals(type);
	}

	/**
	 * Overlays this annotation with the given RubyAnnotation.
	 * 
	 * @param RubyAnnotation annotation that is overlaid by this annotation
	 */
	public void setOverlay(IRubyAnnotation RubyAnnotation) {
		if (fOverlay != null)
			fOverlay.removeOverlaid(this);
			
		fOverlay= RubyAnnotation;
		if (!isMarkedDeleted())
			markDeleted(fOverlay != null);
		
		if (fOverlay != null)
			fOverlay.addOverlaid(this);
	}
	
	/*
	 * @see IRubyAnnotation#hasOverlay()
	 */
	public boolean hasOverlay() {
		return fOverlay != null;
	}
	
	/*
	 * @see org.eclipse.jdt.internal.ui.Rubyeditor.IRubyAnnotation#getOverlay()
	 */
	public IRubyAnnotation getOverlay() {
		return fOverlay;
	}
	
	/*
	 * @see IRubyAnnotation#addOverlaid(IRubyAnnotation)
	 */
	public void addOverlaid(IRubyAnnotation annotation) {
		// not supported
	}

	/*
	 * @see IRubyAnnotation#removeOverlaid(IRubyAnnotation)
	 */
	public void removeOverlaid(IRubyAnnotation annotation) {
		// not supported
	}
	
	/*
	 * @see IRubyAnnotation#getOverlaidIterator()
	 */
	public Iterator getOverlaidIterator() {
		// not supported
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.rubyeditor.IRubyAnnotation#getCompilationUnit()
	 */
	public IRubyScript getRubyScript() {
		IRubyElement element= RubyCore.create(getMarker().getResource());
		if (element instanceof IRubyScript) {
			return (IRubyScript)element;
		}
		return null;
	}

	public String getMarkerType() {
		IMarker marker= getMarker();
		if (marker == null  || !marker.exists())
			return null;
		
		return  MarkerUtilities.getMarkerType(getMarker());
	}
}
