/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Yu You (Nokia) - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser.parsers;

import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class DeviceParser.
 * 
 * Use Display.getDefault() as the return
 * 
 * @author daveo
 */
public class DeviceDataParser extends NonDisposableDataParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source) throws XSWTException {
		if (source.equalsIgnoreCase("null"))
			return Display.getCurrent();
		else
			// TODO: consider how to parase custom device string x:p0="printer"
			return Display.getCurrent();
	}
}