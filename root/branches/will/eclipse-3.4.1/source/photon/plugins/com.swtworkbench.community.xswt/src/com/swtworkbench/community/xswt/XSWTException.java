/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme (ASC) - Initial implementation
 * 	   Yu You (Nokia)	- Add XSWT Row and Column info
 ******************************************************************************/
package com.swtworkbench.community.xswt;

/**
 * Class XSWTException.  A wrapper exception class for all exceptions that
 * could be thrown by XSWT.
 * 
 * @author daveo
 */
public class XSWTException extends Exception {
	
	private static final long serialVersionUID = 5635963384518522081L;
	private Object faultyObject;
	private boolean ambiguous = false;
	
    public XSWTException() {
    }
    
    public XSWTException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public XSWTException(String message) {
        super(message);
    }
 
	public XSWTException(String string, Throwable cause, Object faultyObject) {
		this(string, cause);
		this.faultyObject = faultyObject;
	}
	public XSWTException(String string, Object faultyObject) {
		this(string, null, faultyObject);
	}
	
    public XSWTException(Throwable cause) {
    	super(cause.getMessage());
    }

	public XSWTException(String message, XSWTException e, XSWTException e2) {
		super(message + "\n 1) " + e.getMessage() + "\n 2) " + e2.getMessage());
		ambiguous  = true;
	}

	public String toString() {
		String s = super.toString();
		if (faultyObject != null) {
			s = s + ": " + faultyObject;
		}
		return s;
	}

	public boolean isAmbiguous() {
		return ambiguous;
	}
}
