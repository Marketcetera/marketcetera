/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bob Foster - The color manager idea; XSWT top-level node idea; some other 
 *                        important stuff
 *     David Orme (ASC) - Rewrote: switched to a reflection-based implementation
 *     Yu You           - Rewrite: switch to DataParser to manage the resource dispose
 ******************************************************************************/

package com.swtworkbench.community.xswt.dataparser.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class ColorDataParser. This class converts between string color
 * representations and SWT Color objects.
 * 
 * @author daveo
 */
public abstract class StaticFieldsParser extends NonDisposableDataParser {

	private String prefix;
	private Map valueMap = null;
	private Class fieldsClass;
	private Class valueClass;
	
	protected StaticFieldsParser(Class fieldsClass, Class valueClass, String prefix, boolean cache) {
		super();
		this.fieldsClass = fieldsClass;
		this.valueClass = valueClass;
		this.prefix = prefix;
		if (cache) {
			valueMap = new HashMap();
			fillValueMap(valueMap);
		}
	}

	private int modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL; 

	protected void fillValueMap(Map map) {
		Field[] fields = fieldsClass.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (testField(field)) {
				String name = field.getName();
				if (prefix == null || name.startsWith(prefix)) {
					try {
						Object o = field.get(null);
						map.put(name, o);
						if (prefix != null) {
							map.put(name.substring(prefix.length()), o);
						}
					} catch (IllegalAccessException eIllegalAccess) {
					}
				}
			}
		}
	}

	private boolean testField(Field field) {
		return field != null && valueClass.equals(field.getType()) && (field.getModifiers() & modifiers) == modifiers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source) {
		if (valueMap != null) {
			return valueMap.get(source);
		}
		Field field = null;
		try {
			field = fieldsClass.getField(source);
		} catch (SecurityException e2) {
		} catch (NoSuchFieldException e2) {
		}
		if (field == null && prefix != null && source.startsWith(prefix)) {
			try {
				field = fieldsClass.getField(source.substring(prefix.length()));
			} catch (SecurityException e2) {
			} catch (NoSuchFieldException e2) {
			}
		}
		if (testField(field)) {
			try {
				return field.get(null);
			} catch (IllegalAccessException eIllegalAccess) {
			}
		}
		return null;
	}
}
