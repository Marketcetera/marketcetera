/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme (ASC) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt;


import org.xmlpull.v1.XmlPullParser;

import com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder;


/**
 * @author Jan Petersen
 * 
 * Interface for handling custom namespaces
 *
 */
public interface ICustomNSHandler {

	/**
	 * Handle an XML element which has a custom namespace.
	 * @param parser 
	 * @param layoutBuilder
	 */
	public void handleNamespace(XmlPullParser parser, ILayoutBuilder layoutBuilder);

}
