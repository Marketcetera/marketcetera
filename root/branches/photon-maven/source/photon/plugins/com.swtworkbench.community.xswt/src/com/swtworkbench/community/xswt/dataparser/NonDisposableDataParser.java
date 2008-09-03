/*******************************************************************************
 * Copyright (c) 2000, 2003 Yu You.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Yu You - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser;



/**
 * Class IDataParser.  A function object interface for objects that can parse 
 * a source string into a particular type.
 * 
 * @author daveo
 */
public abstract class NonDisposableDataParser extends AbstractDataParser {

	/**
	 * the value to determine whether the object dispose is done 
	 * by the XSWT dispose manager automatically.
	 * 
	 * The users can override this value in their own IDataParsers if the 
	 * object dispose is processed by their application.
	 * 
	 */
	public boolean isDisposable = false;
	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#isResourceDisposeRequired()
	 */
	public boolean isResourceDisposeRequired() {
		return isDisposable;
	}
}
