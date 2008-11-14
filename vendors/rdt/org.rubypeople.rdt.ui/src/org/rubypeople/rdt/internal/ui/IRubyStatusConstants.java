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
package org.rubypeople.rdt.internal.ui;

/**
 * Defines status codes relevant to the Ruby UI plug-in. When a 
 * Core exception is thrown, it contain a status object describing
 * the cause of the exception. The status objects originating from the
 * Ruby UI plug-in use the codes defined in this interface.
  */
public interface IRubyStatusConstants {

	// Ruby UI status constants start at 10000 to make sure that we don't
	// collide with resource and ruby model constants.
	
	public static final int INTERNAL_ERROR= 10001;
	
	/**
	 * Status constant indicating that an validateEdit call has changed the
	 * content of a file on disk.
	 */
	public static final int VALIDATE_EDIT_CHANGED_CONTENT= 10003;
 }
