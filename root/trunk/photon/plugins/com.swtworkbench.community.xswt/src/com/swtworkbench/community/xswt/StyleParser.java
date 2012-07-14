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
 ******************************************************************************/

package com.swtworkbench.community.xswt;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.eclipse.swt.SWT;

import com.swtworkbench.community.xswt.dataparser.DataParser;

/**
 * Class StyleParser. This is Chris's original style bit parser and lookup-table
 * code, but made general to deal with any class, not just SWT. However, as in
 * Chris's initial implementation, the SWT class is still the default class for
 * style bit look-ups with no class qualifier. Therefore, the API still
 * functions as before for all the SWT.xxx cases.
 * 
 * @author daveo
 */
public class StyleParser {

	/*
	 * A fast way to look up SWT style bit values from their names...
	 */
	private static final Map mapStyles = new TreeMap();
	static {
		try {
			// Add the SWT class by default. All other classes will be added
			// automatically by ClassBuilder the first time they are
			// encountered.
			StyleParser.registerClassConstants("org.eclipse.swt.SWT");
		} catch (XSWTException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method registerClassConstants. Register all public final int constants
	 * from className with the StyleParser so that it can look them up quickly.
	 * 
	 * @param className
	 *            The fully-qualified class name to register
	 * @throws XSWTException
	 *             If something really bad happens or if the class is not found
	 */
	public static void registerClassConstants(String className)
			throws XSWTException {
		Class constantsClass;
		try {
			constantsClass = Class.forName(className);
			registerClassConstants(constantsClass);
		} catch (Throwable t) {
			throw new XSWTException(t);
		}
	}

	/**
	 * Method registerClassConstants. Register all public final int constants
	 * from className with the StyleParser so that it can look them up quickly.
	 * 
	 * @param className
	 *            The class to register
	 * @throws XSWTException
	 *             If something really bad happens or if the class is not found
	 */
	public static void registerClassConstants(Class constantsClass)
			throws XSWTException {
		TreeMap classStyles = new TreeMap();
		try {
			Field[] fields = constantsClass.getDeclaredFields();

			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];

				if (int.class.equals(field.getType())) {
					int modifiers = field.getModifiers();

					if (Modifier.isPublic(modifiers)
							&& Modifier.isStatic(modifiers)
							&& Modifier.isFinal(modifiers)) {
						try {
							classStyles.put(field.getName(), field.get(null));
						} catch (IllegalAccessException eIllegalAccess) {
						}
					}
				}
			}
		} catch (Throwable t) {
			throw new XSWTException(t);
		}

		// Register the style bit map under the simple name
		StringTokenizer stringTokenizer = new StringTokenizer(constantsClass.getName(), ".");
		int tokens = stringTokenizer.countTokens();
		String simpleClassName = null;
		for (int i = 0; i < tokens; ++i) {
			simpleClassName = stringTokenizer.nextToken();
		}
		mapStyles.put(simpleClassName, classStyles);
	}

	/**
	 * Method parse. Parse a style bit string into its int value. Style bit
	 * strings must be in the following format:
	 * <p>
	 * 
	 * Class.STYLE - Returns the value of STYLE in Class <br>
	 * STYLE - Returns the value of SWT.STYLE <br>
	 * STYLE1 STYLE2 STYLE3 - Returns SWT.STYLE1 | SWT.STYLE2 | SWT.STYLE3 <br>
	 * Class.STYLE1 Class.STYLE2 - Returns Class.STYLE1 | Class.STYLE2
	 * <p>
	 * 
	 * In all cases "Class" must be a simple (not fully-qualified) class name.
	 * ie: "SWT" or "GridData".
	 * <p>
	 * 
	 * @param source
	 *            The source string
	 * @return The integer value of the (possibly ored) style bit(s)
	 * @throws XSWTException
	 *             If we couldn't resolve a style bit constant
	 */
	public static int parse(String source) throws XSWTException {
		return parse(source, null, null);
	}
	public static int parse(String source, DataParser styleIdParser, List styleIds) throws XSWTException {
		if (source == null || source.length() == 0)
			return 0;
		int style = 0;
		StringTokenizer stringTokenizer = new StringTokenizer(source,
				" |\t\r\n");

		while (stringTokenizer.hasMoreTokens()) {
			String token = stringTokenizer.nextToken();

			// Break up Class.xxx into Class and xxx
			StringTokenizer idScanner = new StringTokenizer(token, ".");
			String identifier = idScanner.nextToken();
			String qualifier = "";
			while (idScanner.hasMoreTokens()) {
				qualifier = identifier;
				identifier = idScanner.nextToken();
			}
			int value = 0;
			if (identifier.startsWith("'")) {
				// convert to a char, e.g. 'N'
				value = identifier.charAt(1);
			} else {
				if (qualifier == "")
					qualifier = "SWT"; // Default to SWT.xxx
				qualifier = XSWT.upperCaseFirstLetter(qualifier);

				// Look up the actual value
				TreeMap classStyles = (TreeMap) mapStyles.get(qualifier);
				Integer classValue = null;
				if (classStyles != null) {
					classValue = (Integer)classStyles.get(identifier);
				}
				if (classValue != null) {
					value = classValue.intValue();
				} else {
					// try parsing it as id reference
					Object styleObject = null;
					if (styleIdParser != null) {
						styleObject = styleIdParser.parse(token, Object.class);
					}
					if (styleObject == null) {
						throw new XSWTException(token + " is not a valid style constant or style ID");
					}
					if (styleIds != null) {
						styleIds.add(styleObject);
					}
				}
			}
			style |= value;
		}

		return style;
	}

}

