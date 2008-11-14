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

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.rubypeople.rdt.internal.core.RubyModelStatus;


/**
 * A checked exception representing a failure in the Ruby model.
 * Ruby model exceptions contain a Ruby-specific status object describing the
 * cause of the exception.
 * <p>
 * This class is not intended to be subclassed by clients. Instances of this
 * class are automatically created by the Ruby model when problems arise, so
 * there is generally no need for clients to create instances.
 * </p>
 *
 * @see IRubyModelStatus
 * @see IRubyModelStatusConstants
 */
public class RubyModelException extends CoreException {

	private static final long serialVersionUID = -760398656505871287L; // backward compatible
	
	CoreException nestedCoreException;
/**
 * Creates a Ruby model exception that wrappers the given <code>Throwable</code>.
 * The exception contains a Ruby-specific status object with severity
 * <code>IStatus.ERROR</code> and the given status code.
 *
 * @param e the <code>Throwable</code>
 * @param code one of the Ruby-specific status codes declared in
 *   <code>IRubyModelStatusConstants</code>
 * @see IRubyModelStatusConstants
 * @see org.eclipse.core.runtime.IStatus#ERROR
 */
public RubyModelException(Throwable e, int code) {
	this(new RubyModelStatus(code, e)); 
}
/**
 * Creates a Ruby model exception for the given <code>CoreException</code>.
 * Equivalent to 
 * <code>RubyModelException(exception,IRubyModelStatusConstants.CORE_EXCEPTION</code>.
 *
 * @param exception the <code>CoreException</code>
 */
public RubyModelException(CoreException exception) {
	super(exception.getStatus());
	this.nestedCoreException = exception;
}
/**
 * Creates a Ruby model exception for the given Ruby-specific status object.
 *
 * @param status the Ruby-specific status object
 */
public RubyModelException(IRubyModelStatus status) {
	super(status);
}
/**
 * Returns the underlying <code>Throwable</code> that caused the failure.
 *
 * @return the wrappered <code>Throwable</code>, or <code>null</code> if the
 *   direct case of the failure was at the Ruby model layer
 */
public Throwable getException() {
	if (this.nestedCoreException == null) {
		return getStatus().getException();
	}
	return this.nestedCoreException;
}
/**
 * Returns the Ruby model status object for this exception.
 * Equivalent to <code>(IRubyModelStatus) getStatus()</code>.
 *
 * @return a status object
 */
public IRubyModelStatus getRubyModelStatus() {
	IStatus status = this.getStatus();
	if (status instanceof IRubyModelStatus) {
		return (IRubyModelStatus)status;
	}
	// A regular IStatus is created only in the case of a CoreException.
	// See bug 13492 Should handle RubyModelExceptions that contains CoreException more gracefully  
	return new RubyModelStatus(this.nestedCoreException);
}
/**
 * Returns whether this exception indicates that a Ruby model element does not
 * exist. Such exceptions have a status with a code of
 * <code>IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST</code> or
 * <code>IRubyModelStatusConstants.ELEMENT_NOT_ON_CLASSPATH</code>.
 * This is a convenience method.
 *
 * @return <code>true</code> if this exception indicates that a Ruby model
 *   element does not exist
 * @see IRubyModelStatus#isDoesNotExist()
 * @see IRubyModelStatusConstants#ELEMENT_DOES_NOT_EXIST
 * @see IRubyModelStatusConstants#ELEMENT_NOT_ON_CLASSPATH
 */
public boolean isDoesNotExist() {
	IRubyModelStatus javaModelStatus = getRubyModelStatus();
	return javaModelStatus != null && javaModelStatus.isDoesNotExist();
}

/**
 * Prints this exception's stack trace to the given print stream.
 * 
 * @param output the print stream
 * @since 3.0
 */
public void printStackTrace(PrintStream output) {
	synchronized(output) {
		super.printStackTrace(output);
		Throwable throwable = getException();
		if (throwable != null) {
			output.print("Caused by: "); //$NON-NLS-1$
			throwable.printStackTrace(output);
		}
	}
}

/**
 * Prints this exception's stack trace to the given print writer.
 * 
 * @param output the print writer
 * @since 3.0
 */
public void printStackTrace(PrintWriter output) {
	synchronized(output) {
		super.printStackTrace(output);
		Throwable throwable = getException();
		if (throwable != null) {
			output.print("Caused by: "); //$NON-NLS-1$
			throwable.printStackTrace(output);
		}
	}
}
/*
 * Returns a printable representation of this exception suitable for debugging
 * purposes only.
 */
public String toString() {
	StringBuffer buffer= new StringBuffer();
	buffer.append("Ruby Model Exception: "); //$NON-NLS-1$
	if (getException() != null) {
		if (getException() instanceof CoreException) {
			CoreException c= (CoreException)getException();
			buffer.append("Core Exception [code "); //$NON-NLS-1$
			buffer.append(c.getStatus().getCode());
			buffer.append("] "); //$NON-NLS-1$
			buffer.append(c.getStatus().getMessage());
		} else {
			buffer.append(getException().toString());
		}
	} else {
		buffer.append(getStatus().toString());
	}
	return buffer.toString();
}
}
