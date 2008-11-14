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
package org.rubypeople.rdt.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

/**
 * Represents the outcome of an Ruby model operation. Status objects are
 * used inside <code>RubyModelException</code> objects to indicate what went
 * wrong.
 * <p>
 * Ruby model status object are distinguished by their plug-in id:
 * <code>getPlugin</code> returns <code>"org.eclipse.jdt.core"</code>.
 * <code>getCode</code> returns one of the status codes declared in
 * <code>IRubyModelStatusConstants</code>.
 * </p>
 * <p>
 * A Ruby model status may also carry additional information (that is, in 
 * addition to the information defined in <code>IStatus</code>):
 * <ul>
 *   <li>elements - optional handles to Ruby elements associated with the failure</li>
 *   <li>string - optional string associated with the failure</li>
 * </ul>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 *
 * @see org.eclipse.core.runtime.IStatus
 * @see IRubyModelStatusConstants
 */
public interface IRubyModelStatus extends IStatus {
/**
 * Returns any Ruby elements associated with the failure (see specification
 * of the status code), or an empty array if no elements are related to this
 * particular status code.
 *
 * @return the list of Ruby element culprits
 * @see IRubyModelStatusConstants
 */
IRubyElement[] getElements();
/**
 * Returns the path associated with the failure (see specification
 * of the status code), or <code>null</code> if the failure is not 
 * one of <code>DEVICE_PATH</code>, <code>INVALID_PATH</code>, 
 * <code>PATH_OUTSIDE_PROJECT</code>, or <code>RELATIVE_PATH</code>.
 *
 * @return the path that caused the failure, or <code>null</code> if none
 * @see IRubyModelStatusConstants#DEVICE_PATH
 * @see IRubyModelStatusConstants#INVALID_PATH
 * @see IRubyModelStatusConstants#PATH_OUTSIDE_PROJECT
 * @see IRubyModelStatusConstants#RELATIVE_PATH
 */
IPath getPath();
/**
 * Returns the string associated with the failure (see specification
 * of the status code), or <code>null</code> if no string is related to this
 * particular status code.
 *
 * @return the string culprit, or <code>null</code> if none
 * @see IRubyModelStatusConstants
 * @deprecated Use IStatus#getMessage instead
 */
String getString();
/**
 * Returns whether this status indicates that a Ruby model element does not exist.
 * This convenience method is equivalent to
 * <code>getCode() == IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST</code>.
 *
 * @return <code>true</code> if the status code indicates that a Ruby model
 *   element does not exist
 * @see IRubyModelStatusConstants#ELEMENT_DOES_NOT_EXIST
 */
boolean isDoesNotExist();
}
