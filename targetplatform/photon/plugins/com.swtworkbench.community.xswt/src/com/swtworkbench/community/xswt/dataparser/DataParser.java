/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Advanced Systems Concepts - Initial api and implementation
 *     Yu You                    - Add automatical resource management
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.parsers.ArrayDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.ByteDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.CharacterDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.ClassDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.DeviceDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.FontDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.ImageDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.IntDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.PointDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.RGBDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.RectangleDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.StringBufferDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.StringDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.ValueOfDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.color.CSSColorsDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.color.RGBColorDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.color.SWTColorsDataParser;
import com.swtworkbench.community.xswt.layoutbuilder.ReflectionSupport;
import com.swtworkbench.community.xswt.scripting.Bindings;
import com.swtworkbench.community.xswt.scripting.InterfaceDataParser;

/**
 * Class DataParser. Assists in converting from Strings to arbitrary types.
 * 
 * @author daveo
 */
public final class DataParser implements IDataParserContext {

	private HashMap dataParsers = new HashMap();

	private HashMap disposableData = new HashMap();

	/*
	private static List extensionDataParsers;
	
	public static void addExtensionDataParser(String className, IDataParser parser) {
		if (extensionDataParsers == null) {
			extensionDataParsers = new ArrayList();
		}
		extensionDataParsers.add(className);
		extensionDataParsers.add(parser);
	}
	*/

	public DataParser(boolean addStandardParsers) {
		// ColorDataParser is registered inside setColorManager()
		// ControlDataParser is registered inside the constructor

		if (addStandardParsers) {
			registerDataParser(Character.TYPE, new CharacterDataParser());
			registerDataParser(Integer.TYPE, new IntDataParser());
			registerDataParser(Byte.TYPE, new ByteDataParser());
	
			registerDataParser(String.class, new StringDataParser());
			registerDataParser(StringBuffer.class, new StringBufferDataParser());
			
			registerDataParser(RGB.class, new RGBDataParser(this));
			registerDataParser(Point.class, new PointDataParser(this));
			registerDataParser(Rectangle.class, new RectangleDataParser(this));
			registerDataParser(Image.class, new ImageDataParser());
			registerDataParser(Font.class, new FontDataParser());
			// registerDataParser(Color.class, new ColorDataParser(this));
			addDataParser(Color.class, new CSSColorsDataParser());
			addDataParser(Color.class, new SWTColorsDataParser());
			addDataParser(Color.class, new RGBColorDataParser());
			
			registerDataParser(Device.class, new DeviceDataParser());
		}
		/*
		if (addExtensionPointParsers && extensionDataParsers != null) {
			for (int i = 0; i < extensionDataParsers.size(); i += 2) {
			}
			// Make sure we're inside an Eclipse runtime to begin with. :-)
//			XswtPlugin plugin = XswtPlugin.getDefault();
//			if (plugin != null) {
//				plugin.addDataParsers(this);
//			}
		}
		*/
	}
	public DataParser() {
		this(true);
	}
	
	/**
	 * Tells this object to dispose all its managed resources.
	 */
	public void dispose() {

		Iterator cit = disposableData.values().iterator();
		while (cit.hasNext()){
			Object obj = cit.next();
			if (obj instanceof Color) ((Color)obj).dispose();
			else if (obj instanceof Font) ((Font)obj).dispose();
			else if (obj instanceof Image) ((Image)obj).dispose();
			else obj = null;
		}
		disposableData.clear();
	}

	/**
	 * Method registerDataParser. Register a class that can convert from a
	 * String to Java type represented by Class klass.
	 * 
	 * @param klass
	 *            The Class object representing the type that will be returned
	 * @param dataParser
	 *            The object that can convert a String to that type
	 */
	public void registerDataParser(Class klass, IDataParser dataParser) {
		dataParsers.put(klass, dataParser);
		dataParsers.put(klass.getName(), dataParser);
	}

	/**
	 * Method addDataParser. Register an additional class that can convert from a
	 * String to Java type represented by Class klass. Does not clear an existing one,
	 * but makes them operate together, trying each in order until one returns an object.
	 * 
	 * @param klass
	 *            The Class object representing the type that will be returned
	 * @param dataParser
	 *            The object that can convert a String to that type
	 */
	public void addDataParser(Class klass, IDataParser dataParser) {
		IDataParser oldParser = (IDataParser)dataParsers.get(klass);
		if (oldParser == null) {
			registerDataParser(klass, dataParser);
		} else {
			if (! (oldParser instanceof CompositeDataParser)) {
				oldParser = new CompositeDataParser(oldParser);
				dataParsers.put(klass, oldParser);
			}
			((CompositeDataParser)oldParser).addDataParser(dataParser);
		}
	}

	private boolean trySuperclassParsers = true;
	
	public void setTrySuperclassParsers(boolean trySuperclassParsers) {
		this.trySuperclassParsers = trySuperclassParsers;
	}

	private Class upperSuperclass = null;
	
	public void setUpperSuperclass(Class upperSuperclass) {
		this.upperSuperclass = upperSuperclass;
	}
	
	/**
	 * Method parseValue. Parse a String value into an object of type klass
	 * 
	 * @param value
	 *            The value to convert
	 * @param klass
	 *            The Class into which value should be converted
	 * @return Object the converted value as type klass
	 */
	public Object parse(String value, Class klass) throws XSWTException {

		String stringValue = value;
		while (stringValue != null) {

			Class superClass = klass;
			Class objectClass = ClassDataParser.getObjectClass(klass);
			Object lastResult = null;
			do {
				Object result = null;
				IDataParser parser = getDataParser(superClass);
				if (parser != null) {
					try {
						result = parser.parse(stringValue, klass, this);
					} catch (IllegalArgumentException e) {
					} catch (RuntimeException e) {
						throw new XSWTException("Exception when parsing " + value + " for " + superClass, e);
					}
				} else if (! trySuperclassParsers) {
					throw new XSWTException("Unable to find a parser for type: " + klass.getName());
				}
				if (result != null) {
					if (parser.isResourceDisposeRequired()) {
			    		// FIXME: This will pick up Label, any other SWT control
			    		disposableData.put(klass, result);
			    	}
					if (objectClass.isInstance(result)) {
						return result;
					} else {
						lastResult = result;
					}
				}
				superClass = superClass.getSuperclass();
			} while (trySuperclassParsers && superClass != null && superClass != upperSuperclass);

			Object result = resolvePath(stringValue);
			if (result == null || objectClass.isInstance(result)) {
				return result;
			}
			if (result instanceof String) {
				// and try again with new value
				stringValue = (String)result;
			} else {
				// only throw exception if we got a result that was of the wrong class
				throw new XSWTException(value + " (" + stringValue + ")" + " could not be parsed into " + klass + ", but to " + lastResult);
			}
		}
		return null;
	}
		
	private IDataParser getDataParser(Class klass) {
		IDataParser parser = (IDataParser)dataParsers.get(klass);
		if (parser == null) {
			parser = (IDataParser)dataParsers.get(klass.getName());
		}
		if (parser == null) {
			if (klass.isArray()) {
				parser = new ArrayDataParser();
			} else if (ValueOfDataParser.supportsClass(klass)) {
				parser = new ValueOfDataParser();
			} else if (klass.isInterface() && klass.getName().endsWith("Listener")) {
				parser = new InterfaceDataParser();
			}
			if (parser != null) {
				dataParsers.put(klass, parser);
			} else {
				parser = (IDataParser)dataParsers.get(Object.class); // WidgetDataParser is the fallback parser
			}
		}
		return parser;
	}
	
	private static char pathSeparator = '.';

	private Object resolvePath(String valueSource) throws XSWTException {
		int pos = valueSource.indexOf(pathSeparator);
		if (pos <= 0) {
			return null;
		}
		Object result = parse(valueSource.substring(0, pos), Object.class);
		int start = pos + 1;
		while (result != null && start < valueSource.length()) {
			if (result instanceof Bindings) {
				return ((Bindings)result).get(valueSource.substring(start));
			}
			pos = valueSource.indexOf(pathSeparator, start);
			if (pos < 0) {
				pos = valueSource.length();
			}
			String methodName = "get" + Character.toUpperCase(valueSource.charAt(start)) + valueSource.substring(start + 1, pos);
			start = pos;
			Method getter = ReflectionSupport.resolveAttributeGetMethod(result.getClass(), methodName);
			if (getter != null) {
	        	try {
					result = getter.invoke(result, null);
				} catch (Exception e) {
					throw new XSWTException("Error calling getter " + getter.getName(), e);
				}
			} else {
				result = null;
			}
		}
		return result;
	}
}
