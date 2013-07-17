/*******************************************************************************
 * Copyright (c) 2000, 2003 Coconut Palm Software, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Coconut Palm Software - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser.parsers;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.swtworkbench.community.xswt.ClassBuilder;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Parses and resolves java.lang.Class objects
 * 
 * @author djo
 */
public class ClassDataParser extends NonDisposableDataParser {

	private ClassBuilder classBuilder;
	private XSWT xswt;
	
	public ClassDataParser(XSWT xswt) {
		this.xswt = xswt;
	}
	public ClassDataParser(ClassBuilder classBuilder) {
		this.classBuilder = classBuilder;
	}

	private ClassBuilder getClassBuilder() {
		return (xswt != null ? xswt.classBuilder : classBuilder);
	}
	
	private static Map wrapperPrimitiveMap = new HashMap();
	
	private static void addPrimitiveType(Class primitive, Class wrapper) {
		wrapperPrimitiveMap.put(primitive, wrapper);
		wrapperPrimitiveMap.put(wrapper, primitive);
		wrapperPrimitiveMap.put(primitive.getName(), primitive);
	}

	static {
		addPrimitiveType(Integer.TYPE, Integer.class);
		addPrimitiveType(Byte.TYPE, Byte.class);
		addPrimitiveType(Short.TYPE, Short.class);
		addPrimitiveType(Long.TYPE, Long.class);
		addPrimitiveType(Double.TYPE, Double.class);
		addPrimitiveType(Float.TYPE, Float.class);
		addPrimitiveType(Boolean.TYPE, Boolean.class);
		addPrimitiveType(Character.TYPE, Character.class);
	}
	
	public static Class getPrimitiveType(String s) {
		return (Class)wrapperPrimitiveMap.get(s);
	}

	public static Class getPrimitiveType(Class c) {
		if (c.isPrimitive()) {
			return c;
		}
		Class pc = (Class)wrapperPrimitiveMap.get(c);
		return (pc != null && pc.isPrimitive() ? pc : null);
	}
	
	public static Class getObjectClass(Class c) {
		if (! c.isPrimitive()) {
			return c;
		}
		Class wc = (Class)wrapperPrimitiveMap.get(c);
		return (wc != null ? wc : null);
	}
	
	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source) throws XSWTException {
		boolean isArray = source.endsWith("[]");
		if (isArray) {
			source = source.substring(0, source.length() - 2);
		}
		String className = source;
		int pos = source.lastIndexOf('.');
		if (Character.isLowerCase(source.charAt(pos + 1))) {
			// Upcasing is needed so that you could use simple class names in 
			// attribute values without uppercasing them.  ie:  <model valueType="person"/>
			className = XSWT.upperCaseFirstLetter(source.substring(pos + 1));
			if (pos > 0) {
				className = source.substring(0, pos + 1) + className;
			}
		}
		Class klass = null;
		try {
			klass = getClassBuilder().getClass(className);
		} catch (XSWTException e) {
			klass = getPrimitiveType(source);
		}
		if (klass != null && isArray) {
			klass = Array.newInstance(klass, 0).getClass();
		}
		if (klass == null && Character.isLowerCase(source.charAt(0))) {
			klass = (Class)parse(XSWT.upperCaseFirstLetter(source));
		}
		return klass;
	}
}
