/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Advanced Systems Concepts - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser.parsers;

import java.util.HashMap;
import java.util.Map;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class ControlDataParser.
 * 
 * @author daveo
 */
public class WidgetDataParser extends NonDisposableDataParser {

	private Map map = new HashMap();

	/**
	 * Method put. Put an ID into the id to control map
	 * 
	 * @param id
	 *            the String ID
	 * @param obj
	 *            The object we're storing
	 */
	public void put(String id, Object obj) {
		map.put(id, obj);
	}

	/**
	 * Gets the object in the WidgetMap.
	 * 
	 * @param id
	 *            the widget's reference ID. may be null.
	 * @return the Widget instance. May be null.
	 */
	public Object get(String id) {
		return map.get(id);
	}

	/**
	 * Method getControlMap. Returns a reference to the control map
	 * 
	 * @return The Map we use to link IDs to objects
	 */
	public Map getWidgetMap() {
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
		Object result = null;
		result = map.get(source);
		if (result == null ) {
			// throw new XSWTException("Undefined ID: " + source);
		} else if (! klass.isInstance(result)) {
			throw new XSWTException("ID object " + result + " is not of " + klass);
		}
		return result;
	}
}

