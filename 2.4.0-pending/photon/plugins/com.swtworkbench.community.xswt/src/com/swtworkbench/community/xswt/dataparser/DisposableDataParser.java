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
 * @author Yu
 */
public abstract class DisposableDataParser extends AbstractDataParser {

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#isResourceDisposeRequired()
	 */
	public boolean isResourceDisposeRequired() {
		return true;
	}
	
}
