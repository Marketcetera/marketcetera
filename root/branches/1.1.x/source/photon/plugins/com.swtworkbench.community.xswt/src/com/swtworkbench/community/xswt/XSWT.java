/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors/Changelog:
 *     IBM Corporation - initial API and implementation.
 *      This is a rewrite of XSWT as described at
 *          http://xswt.sourceforge.net/cgi-bin/wiki?ProjectDocumentation
 *      It borrows code and ideas from the original work done by: 
 * 		Chris McLaren and Bob Foster 
 * 
 *     Bob Foster 		- The color manager idea; XSWT top-level node idea; some other 
 *                        important stuff
 *     David Orme       - Rewrote: switched to a reflection-based implementation
 *     Jan Petersen 	- JFace handling; other important details
 *     Yu You 			- XML Pull parser port, based on kXML engine
 *     Dave Orme        - Genericized JFace code to anything implementing getControl()
 *     Dave Orme        - Made x:children optional; factored editor out of core library
 *******************************************************************************
 * TODO: Localize error messages
 * TODO: Move to KXML2 since KXML1 is no longer maintained
 ******************************************************************************/
package com.swtworkbench.community.xswt;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.xmlpull.v1.XmlPullParserException;

import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.dataparser.IDataParser;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.parsers.ClassDataParser;
import com.swtworkbench.community.xswt.dataparser.parsers.WidgetDataParser;
import com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder;
import com.swtworkbench.community.xswt.layoutbuilder.ObjectStub;
import com.swtworkbench.community.xswt.layoutbuilder.ReflectionSupport;
import com.swtworkbench.community.xswt.layoutbuilder.SWTLayoutBuilder;
import com.swtworkbench.community.xswt.metalogger.Logger;
import com.swtworkbench.community.xswt.scripting.Bindings;
import com.swtworkbench.community.xswt.scripting.BindingsListener;
import com.swtworkbench.community.xswt.scripting.EvaluationContext;
import com.swtworkbench.community.xswt.scripting.IScriptable;
import com.swtworkbench.community.xswt.scripting.ScriptingEngine;
import com.swtworkbench.community.xswt.xmlhandler.IAttributeHandler;
import com.swtworkbench.community.xswt.xmlhandler.IElementHandler;
import com.swtworkbench.community.xswt.xmlhandler.IHandlerContext;
import com.swtworkbench.community.xswt.xmlparser.IMinimalOM;
import com.swtworkbench.community.xswt.xmlparser.IMinimalParser;
import com.swtworkbench.community.xswt.xmlparser.MinimalPullParser;

/**
 * XSWT Parser
 * 
 * @author Dave Orme <djo@coconut-palm-software.com>
 */
public class XSWT implements Bindings, IHandlerContext {

	private static final String AMBIGUOUS_ERROR_MSG = "Ambiguous error.  If node was parsed as a property node (1) is the correct error; else if node was parsed as an object constructor (2) is the correct error:";

	public static final String XSWT_NS = "http://sweet_swt.sf.net/xswt";

	private static Map customNSRegistry = new HashMap();

	/**
	 * Register a custom namespace handler.
	 * 
	 * @param namespace
	 * @param handler
	 */
	public static void registerNSHandler(String namespace,
			ICustomNSHandler handler) {
		XSWT.customNSRegistry.put(namespace, handler);
	}

	/**
	 * Method create. Create an SWT layout from a file located in a position in
	 * the file system (or Jar file) relative to some Java class.
	 * 
	 * @param file
	 *            The file name
	 * @param relativeTo
	 *            The Class whose location should be used as the starting
	 *            location for finding file
	 * @return The SWT control Map
	 * @throws XSWTException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static XSWT create(String file, Class relativeTo)
			throws XSWTException, IOException {
		return create(relativeTo.getResource(file).openStream());
	}

	public static URIHandler defaultUriHandler = new DefaultURIHandler();

	private URIHandler uriHandler = defaultUriHandler;
	
	public URIHandler getUriHandler() {
		return uriHandler;
	}

	public void setUriHandler(URIHandler uriHandler) {
		this.uriHandler = uriHandler;
	}

	private String uri = null;
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Method createl. Create an SWT layout from a file located in a position in
	 * the file system (or Jar file) relative to some Java class. Logs any
	 * exceptions and returns null on failure.
	 * 
	 * @param file
	 *            The file name
	 * @param relativeTo
	 *            The Class whose location should be used as the starting
	 *            location for finding file
	 * @return The SWT control Map
	 */
	public static XSWT createl(String file, Class relativeTo) {
		try {
			return create(relativeTo.getResource(file).openStream());
		} catch (Exception e) {
			Logger.log().error(e,
					"Unable to create XSWT layout: " + e.getMessage());
		}
		return null;
	}
    
	/**
	 * Create a XML Pull parser and parse the XSWT
	 * 
	 * @param inputStream
	 *            the XSWT's input stream
	 * @return Map the map contains the controls. Null if error happends.
	 * @throws XSWTException
	 * @throws XmlPullParserException
	 */
	public static XSWT create(InputStream inputStream) throws XSWTException {
		IMinimalParser parser = new MinimalPullParser();
		return new XSWT(parser.build(inputStream), parser);
	}

	/**
	 * Create a XML Poll parser and parse the XSWT
	 * 
	 * @param uri
	 *            URI pointing to the XSWT file
	 * @return Map the map contains the controls. Null if error happends.
	 * @throws XSWTException
	 * @throws XmlPullParserException
	 */
	public static XSWT create(Reader reader) throws XSWTException {
		IMinimalParser parser = new MinimalPullParser();
		return new XSWT(parser.build(reader), parser);
	}

	/**
	 * Create a XML Poll parser and parse the XSWT
	 * 
	 * @param uri
	 *            URI pointing to the XSWT file
	 * @return Map the map contains the controls. Null if error happens.
	 * @throws XSWTException
	 */
	public static XSWT create(String uri) throws XSWTException {
		try {
//			return create(new FileReader(uri));
			XSWT xswt = create(defaultUriHandler.getInputStream(uri));
			xswt.setUri(uri);
			return xswt;
		} catch (IOException e) {
			throw new XSWTException(e);
		}
	}
	
	/**
	 * Parse XSWT XML file and return an interface implementing getters for each control
	 * in the XSWT Map.  For example:<p>
	 * 
     * <p>
     * <code>
     *    &lt;text x:id="Name"&gt;
     * </code>
     * <p>
     * could be retrieved using an interface defined as follows:
     * <p>
     * <code>
     *    interface DataEntry {
     *       Text getName();
     *    }
     * </code>
     * <p>

	 * This is the main loop where XSWT is parsed.  This version logs any exceptions that
	 * occur rather than allowing them to propogate.<p>
	 * 
	 * @param parent The parent control; for SWT, typically an org.eclipse.swt.widgets.Composite
	 * @param resultInterface The interface the returned object should implement
	 * @return An object implementing resultInterface or null on failure.
	 */
    public Object parsel(Object parent, Class resultInterface) {
    	try {
	        Map result = parse(parent);
	        return DuckMapper.implement(resultInterface, result);
    	} catch (XSWTException e) {
    		Logger.log().error(e, "Exception occured while parsing XSWT file");
    		return null;
    	}
    }
    
	/**
	 * Parse XSWT XML file and return an interface implementing getters for each control
	 * in the XSWT Map.  For example:
	 * 
     * <p>
     * <code>
     *    &lt;text x:id="Name"&gt;
     * </code>
     * <p>
     * could be retrieved using an interface defined as follows:
     * <p>
     * <code>
     *    interface DataEntry {
     *       Text getName();
     *    }
     * </code>
     * <p>
     * 
	 * This is the main loop where XSWT is parsed.
	 * 
	 * @param parent The parent object; for SWT, typically an org.eclipse.swt.widgets.Composite
	 * @param resultInterface The interface the returned object should implement
	 * @return An object implementing resultInterface
	 * @throws XSWTException on error
	 */
    public Object parse(Object parent, Class resultInterface) throws XSWTException {
        Map result = parse(parent);
        return DuckMapper.implement(resultInterface, result);
    }
    
	/**
	 * Create SWT UI map from XSWT XML file.
	 * 
	 * This is the main loop where XSWT is parsed.  This version logs any exceptions that
	 * occur rather than allowing them to propogate.
	 * 
	 * @param parent The parent object; for SWT, typically an org.eclipse.swt.widgets.Composite
	 * @return The Map containing the SWT controls that were created or null on failure
	 */
    public Map parsel(Object parent) {
    	try {
    		return parse(parent);
    	} catch (XSWTException e) {
    		Logger.log().error(e, "Exception occured while parsing XSWT file");
    		return null;
    	}
    }
    
	/**
	 * Create SWT UI map from XSWT XML file.
	 * 
	 * This is the main loop where XSWT is parsed.
	 * 
	 * @param parent
	 *            The parent object; for SWT, typically an org.eclipse.swt.widgets.Composite
	 * @return The Map containing the SWT controls that were created
	 * @throws XSWTException if Something Bad Happens
	 */
	public synchronized Map parse(Object parent)
			throws XSWTException {
		if (parent != null) {
			if (parent instanceof Composite) {
				((Composite)parent).addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						dataParser.dispose();
					}
				});
			}
		}
		String tagName = parser.getElementName(root);
		if ("xswt".equals(tagName)) {
					Logger.log().debug(
							XSWT.class,
							"Start XSWT:" + parser.getElementNamespace(root) + ":" + tagName);
			fireProcessDocument(tagName, parent, false);
			processTopLevelElement(root, parent);
			if (fixupTable.size() != 0)
				throw new XSWTException(missingIdRefError());
		}
		fireProcessDocument(tagName, getObjectMap(), true);
		if (styleSheetParent == null) {
			dispose();
		}
		return getObjectMap();
	}

	// private HashMap fixupTable = new HashMap();
	private List fixupTable = new ArrayList();
	
	private class FixupTableEntry {
		public Object control;
		public String attributeName;
		public String attributeValue;
		public int rowNumber;
		public int colNumber;

		public String toString() {
			return "Row " + rowNumber + " Col " + colNumber;
		}
	}

	private List listeners = null;

	public void addBindingsListener(BindingsListener listener) {
		if (listeners == null) {
			listeners = new ArrayList();
		}
		listeners.add(listener);
	}

	public void removeBindingsListener(BindingsListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}

	public void addXSWTListener(XSWTListener listener) {
		addBindingsListener(listener);
	}
	
	public void removeXSWTListener(XSWTListener listener) {
		removeBindingsListener(listener);
	}
	
	public void fireProcessElement(String name, Object o, boolean processed) {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			if (! (listeners.get(i) instanceof XSWTListener))
				continue;
			XSWTListener listener = (XSWTListener)listeners.get(i);
			if (processed) {
				listener.elementProcessed(this, name, o);
			} else {
				listener.processElement(this, name, o);
			}
		}
	}
	public void fireProcessAttribute(String name, String value, Object o, boolean processed) {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			if (! (listeners.get(i) instanceof XSWTListener))
				continue;
			XSWTListener listener = (XSWTListener)listeners.get(i);
			if (processed) {
				listener.attributeProcessed(this, name, value, o);
			} else {
				listener.processAttribute(this, name, value, o);
			}
		}
	}
	public static void fireProcessDocument(List listeners, XSWT xswt, String name, Object o, boolean processed) {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			if (! (listeners.get(i) instanceof XSWTListener))
				continue;
			XSWTListener listener = (XSWTListener)listeners.get(i);
			if (processed) {
				listener.documentProcessed(xswt, o);
			} else {
				listener.processDocument(xswt, o);
			}
		}
	}
	public void fireProcessDocument(String name, Object o, boolean processed) {
		fireProcessDocument(listeners, this, name, o, processed);
	}

	public void fireSetProperty(String name, Object o, Object value, boolean processed) {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			if (! (listeners.get(i) instanceof XSWTListener))
				continue;
			XSWTListener listener = (XSWTListener)listeners.get(i);
			if (processed) {
				listener.setProperty(o, name, value);
			} else {
				listener.propertySet(o, name, value);
			}
		}
	}

	// handler management
	
	private Map elementHandlers = new HashMap();
	private Map attributeHandlers = new HashMap();
	
	public void registerElementHandler(String uri, IElementHandler handler) {
		elementHandlers.put(uri, handler);
	}
	public void registerAttributeHandler(String uri, IAttributeHandler handler) {
		attributeHandlers.put(uri, handler);
	}

	public void registerElementHandler(String uri, String[] uris) {
		elementHandlers.put(uri, uris);
	}
	public void registerAttributeHandler(String uri, String[] uris) {
		attributeHandlers.put(uri, uris);
	}

	private IElementHandler[] getElementHandlers(String uri) {
		Object o = elementHandlers.get(uri);
		if (o instanceof IElementHandler[]) {
			return (IElementHandler[])o;
		}
		if (o instanceof String) {
			o = getElementHandlers((String)o);
		}
		if (o instanceof String[]) {
			String[] uris = (String[])o;
			IElementHandler[][] handlersArray = new IElementHandler[uris.length][];
			int count = 0;
			for (int i = 0; i < uris.length; i++) {
				handlersArray[i] = getElementHandlers(uris[i]);
				count += handlersArray[i].length;
			}
			IElementHandler[] handlers = new IElementHandler[count];
			count = 0;
			for (int i = 0; i < uris.length; i++) {
				System.arraycopy(handlersArray[i], 0, handlers, count, handlersArray[i].length);
				count += handlersArray[i].length;
			}
			o = handlers;
		}
		if (o instanceof IElementHandler[]) {
			elementHandlers.put(uri, o);
			return (IElementHandler[])o;
		}
		return null;
	}

	private IAttributeHandler[] getAttributeHandlers(String uri) {
		Object o = elementHandlers.get(uri);
		if (o instanceof IAttributeHandler[]) {
			return (IAttributeHandler[])o;
		}
		if (o instanceof String) {
			o = getElementHandlers((String)o);
		}
		if (o instanceof String[]) {
			String[] uris = (String[])o;
			IAttributeHandler[][] handlersArray = new IAttributeHandler[uris.length][];
			int count = 0;
			for (int i = 0; i < uris.length; i++) {
				handlersArray[i] = getAttributeHandlers(uris[i]);
				count += handlersArray[i].length;
			}
			IAttributeHandler[] handlers = new IAttributeHandler[count];
			count = 0;
			for (int i = 0; i < uris.length; i++) {
				System.arraycopy(handlersArray[i], 0, handlers, count, handlersArray[i].length);
				count += handlersArray[i].length;
			}
			o = handlers;
		}
		if (o instanceof IAttributeHandler[]) {
			elementHandlers.put(uri, o);
			return (IAttributeHandler[])o;
		}
		return null;
	}
	
	public IDataParserContext getDataParserContext() {
		return dataParser;
	}

	public IMinimalOM getMinimalOM() {
		return parser;
	}

	/**
	 * 
	 * Add unsolved reference to Fixup Table
	 * 
	 * @param attributeName
	 * @param obj
	 * @param attributeValue
	 */
	private void addUnresolvedIDRef(String attributeName, Object obj,
			String attributeValue, int row, int col) {
		Logger.log().debug(
				XSWT.class,
				"Found an unsolved refernce:" + attributeValue);
		FixupTableEntry entry = new FixupTableEntry();
		entry.control = obj;
		entry.colNumber = col;
		entry.rowNumber = row;
		entry.attributeName = attributeName;
		entry.attributeValue = attributeValue;
		fixupTable.add(entry);
//		LinkedList id = (LinkedList) fixupTable.get(attributeValue);
//		if (id == null) {
//			LinkedList newList = new LinkedList();
//			newList.add(entry);
//			fixupTable.put(attributeValue, newList);
//		} else
//			id.add(entry); // No need to put it back to the fixupMap
	}

	/**
	 * Try to resolve any unresolved reference objects from Fixup Table
	 * 
	 * @param idRef
	 * @param object
	 * 
	 * @throws XSWTException
	 *  
	 */
	private void resolveIdRefs(String idRef, Object object)
			throws XSWTException {
		if (idRef == null || fixupTable.size() == 0)
			return;
		Iterator entries = fixupTable.iterator();
		while (entries.hasNext()) {
			FixupTableEntry entry = (FixupTableEntry)entries.next();
			if (entry.attributeValue.startsWith(idRef)) {
				if (layoutBuilder.setProperty(entry.attributeName, entry.control, entry.attributeValue, null)) {
					entries.remove();
				}
			}
		}
		// Get the LinkedList
//		LinkedList list = (LinkedList) fixupTable.get(idRef);
//		if (list == null)
//			return; // No unresolved reference
//		for (int i = 0; i < list.size(); i++) {
//			// We process every one alone the LinkedList
//			FixupTableEntry entry = (FixupTableEntry) list.get(i);
//			if (entry == null)
//				continue; // almost impossible
//			Logger.log().debug(
//					XSWT.class,
//					"Process unresolved reference (".concat(idRef).concat(
//							") at Row:")
//							+ entry.rowNumber + " Col:" + entry.colNumber);
//			layoutBuilder
//					.setProperty(entry.attributeName, entry.control, idRef, parser.getColumnNumber(), parser.getLineNumber());
//
//		}
//		// Clear the source
//		fixupTable.remove(idRef);
	}

	/**
	 * Report all unsolved missing references
	 *  
	 */
	private String missingIdRefError() {
		// Iterator ids = fixupTable.keySet().iterator();
		Iterator ids = fixupTable.iterator();
		String msg = "";
		while (ids.hasNext()) {
//			String id = (String) ids.next();
//			List list = (List) fixupTable.get(id);
			//msg +="\n"; // for every reference ID we use a new line
//			for (int i = 0; i < list.size(); i++) {
//				FixupTableEntry entry = (FixupTableEntry) list.get(i);
				FixupTableEntry entry = (FixupTableEntry)ids.next();
				if (entry != null)
					msg += "\nFound unresolved reference: "
							+ entry.attributeName + "=\"" + entry.attributeValue + "\" at " + entry.toString();
//			}

		}
		fixupTable.clear(); // Clean up the resources
		return msg;
	}

    private IMinimalParser parser;
    private Object root;
	private DataParser dataParser;
	
	/**
	 * Returns the underlying data parser;
	 * 
	 * @return the DataParser
	 */
	public DataParser getDataParser() {
		return dataParser;
	}

	public static class Configuration {

		// ClassBuilder imports
		
		private List packageImports;
		private List classImports;

		private List dataParsers;

		private List scriptingEngines;
		
		public Configuration(Configuration config) {
			packageImports = new ArrayList(config != null ? config.packageImports : Collections.EMPTY_LIST);
			classImports = new ArrayList(config != null ? config.classImports : Collections.EMPTY_LIST);
			dataParsers = new ArrayList(config != null ? config.dataParsers : Collections.EMPTY_LIST);
			scriptingEngines = new ArrayList(config != null ? config.scriptingEngines : Collections.EMPTY_LIST);
		}
		public Configuration() {
			this(null);
		}
		
		public void addPackageImports(String pack) {
			packageImports.add(pack);
		}

		public void addClassImports(String className) {
			classImports.add(className);
		}
		
		// IDataParsers
		
		public void addDataParser(String className, IDataParser parser) {
			dataParsers.add(className);
			dataParsers.add(parser);
		}

		// Scripting engines
		
		public void addScriptingEngine(String name, ScriptingEngine engine, boolean isDefault) {
			int pos = (isDefault ? 0 : scriptingEngines.size());
			scriptingEngines.add(pos, engine);
			scriptingEngines.add(pos, name);
		}
	}
	
	/**
	 * XSWT Constructor
	 * 
	 * @throws XSWTException
	 */
	private XSWT(Object root, IMinimalParser parser, Configuration config) {
		// make sure processXswtPluginExtensions is called before supporting objects are created
		if (! xswtPluginExtensionsProcessed) {
			processXswtPluginExtensions();
		}
		if (config == null) {
			config = defaultConfig;
		}
		this.root = root;
		this.parser = parser;
		// initialise dataParser first
		dataParser = new DataParser();
		Iterator dataParsers = config.dataParsers.iterator();
		while (dataParsers.hasNext()) {
			String className = (String)dataParsers.next();
			IDataParser dataParser = (IDataParser)dataParsers.next();
			try {
				this.dataParser.addDataParser(Class.forName(className), dataParser);
			} catch (ClassNotFoundException e) {
			}
		}
		// must initialise layoutBuilder after dataParser
		layoutBuilder = new SWTLayoutBuilder(this);
		// must initialise widgetDataParser after dataParser
		widgetDataParser = new WidgetDataParser();
		dataParser.registerDataParser(Object.class, widgetDataParser);
		classBuilder = ClassBuilder.getDefault();
		Iterator imports = null;
		imports = config.packageImports.iterator();
		while (imports.hasNext()) {
			String packageName = (String)imports.next();
			classBuilder.importPackage(packageName);
		}
		imports = config.classImports.iterator();
		while (imports.hasNext()) {
			String className = (String)imports.next();
			classBuilder.importClass(className);
		}
		dataParser.addDataParser(Class.class, new ClassDataParser(this));
		this.scriptingEngines = new ArrayList(config.scriptingEngines);
	}

	private static boolean xswtPluginExtensionsProcessed = false;
	
	private static void processXswtPluginExtensions() {
		xswtPluginExtensionsProcessed = true;
		try {
			Class c = Class.forName("com.swtworkbench.community.xswt.XswtPlugin");
			Object xswtPlugin = c.getMethod("getDefault", new Class[]{}).invoke(null, null);
			defaultConfig = (Configuration)c.getMethod("getXSWTConfiguration", new Class[]{}).invoke(xswtPlugin, null);
		} catch (Exception e) {
		}
	}

	private static XSWT.Configuration defaultConfig = new Configuration();
	
	private XSWT(Object root, IMinimalParser parser) {
		this(root, parser, null);
	}

	/**
	 * Clean up all created resources
	 *  
	 */
	private void dispose() {
		classBuilder.dispose();
		fixupTable.clear();
		if (styleOwnerMap != null) {
			List stylesheets = new ArrayList(styleOwnerMap.values());
			for (int i = 0; i < stylesheets.size(); i++) {
				XSWT xswt = (XSWT)stylesheets.get(i);
				if (stylesheets.indexOf(xswt) == i) {
					xswt.dispose();
				}
			}
		}
	}

	// Helper object that caches Class objects and constructs SWT objects for us
	// on the fly...
	public ClassBuilder classBuilder;

	private boolean parseAsStyleSheet = false;
	private XSWT styleSheetParent = null;
	
	/*
	 * Now that DataParser is no longer static (it can't be since it manages dispose()ing
	 * SWT controls), layoutBuilder can no longer be static either.
	 */
	//public static ILayoutBuilder layoutBuilder = new SWTLayoutBuilder(dataParser);
	private ILayoutBuilder layoutBuilder;
	
	/**
	 * Strategy Pattern: The ILayoutBuilder is responsible for "instantiating"
	 * objects and "setting property values". This is in quotes, because various
	 * other implementations may do things like generate Java code rather than
	 * generating a layout or other useful things.
	 * 
	 * @param builder
	 */
	public void setLayoutBuilder(ILayoutBuilder builder) {
		this.layoutBuilder = builder;
	}

	protected WidgetDataParser widgetDataParser;

	/**
	 * Method getControlMap. Returns the map of ids to controls that was
	 * constructed as the layout was created.
	 * 
	 * @return Map the id to control map
	 */
	public Map getObjectMap() {
		return widgetDataParser.getWidgetMap();
	}

	/**
	 * Method processImport. Passes imports to the classBuilder object.
	 * 
	 * @param Object
	 *            The parent control
	 * @param parser
	 *            XmlPullParser
	 * @throws XSWTException
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void processImports(Object element)
			throws XSWTException {
		int count = parser.getChildElementCount(element);
		for (int i = 0; i < count; i++) {
			Object child = parser.getChildElement(element, i);
			if ("package".equals(parser.getElementName(child))) {
				String thePackage = parser.getAttributeValue(child, 0); // get the 1st
				// attr value
				Logger.log().debug(XSWT.class, "Process package :" + thePackage);

				if (thePackage != null) {
					classBuilder.importPackage(thePackage);
				} else {
					throw new XSWTException("Unable to get import name", element);
				}
			} else if ("class".equals(parser.getElementName(child))) {
				String theClass= parser.getAttributeValue(child, 0); // get the 1st
				// attr value
				Logger.log().debug(XSWT.class, "Process class :" + theClass);

				if (theClass != null) {
					classBuilder.importClass(theClass);
				} else {
					throw new XSWTException("Unable to get import name", element);
				}
			} else if ("stylesheet".equals(parser.getElementName(child))) {
				processStylesheet(child);
			}
		}
	}
	
	/**
	 * Process the x:define keyword.
	 * 
	 * @param composite the parent control
	 * @param parser the parser
	 */
	private void processDefine(Object element)
		throws XSWTException 
	{
		if (parser.getAttributeCount(element) != 0) {
				throw new XSWTException("Usage: <x:define><prototypeObject  x:id=\"idInMap\"/></x:extends>\n", element);
		}

		// Process the children.
		int count = parser.getChildElementCount(element);
		for (int i = 0; i < count; i++) {
			processChild(parser.getChildElement(element, i), null);
		}
	}

	/**
	 * Method processTopLevelElement. Looks for a &lt;children>tag as a child node of the
	 * passed element and pass it to "processChildControls" for processing.
	 * 
	 * @param composite
	 *            The parent composite
	 * @param parser
	 *            The element to search for the &lt;children> tag
	 * @throws XSWTException
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private void processTopLevelElement(Object root, Object composite) throws XSWTException {
		int count = parser.getChildElementCount(root);
		for (int i = 0; i < count; i++) {
			Object child = parser.getChildElement(root, i);
			String namespace = parser.getElementNamespace(child);
			if (namespace != null && namespace.length() != 0 && !XSWT_NS.equals(namespace)) {
				// Custom namespace handler
				ICustomNSHandler handler = (ICustomNSHandler) customNSRegistry
						.get(namespace);

				if (handler != null) {
					// handler.handleNamespace(parser, layoutBuilder);
				} else
					Logger.log().debug(XSWT.class,
							"No custom namespace handler for " + namespace);
				
			} else if (namespace != null && namespace.length() != 0
					&& namespace.equals(XSWT.XSWT_NS)) {
				processXSWTNode(child, composite);
			} else {
				// Process other nodes as child properties of parent composite
				// or as child nodes of parent composite
				Object parent = composite;
				if (parent == null && styleSheetParent == null) {
					parent = new ObjectStub("Composite");
				}
				XSWTException ex = null;
				try {
					if (parent != null) {
						processNodeProperty(child, parent);
					}
				} catch (XSWTException e) {
					ex = e;
				}
				if (ex != null || parent == null) {
					// If it's not a property, see if it's a new (implicit) child node
					try {
						processChild(child, parent);
					} catch (XSWTException e2) {
						if (ex.isAmbiguous()) throw ex;
						if (e2.isAmbiguous()) throw e2;
						throw new XSWTException(AMBIGUOUS_ERROR_MSG, ex, e2);
					}
				}
			}
		}
	}

	/**
	 * @param composite
	 * @param parser
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws XSWTException
	 */
	private void processXSWTNode(Object element, Object composite)
			throws XSWTException {

		String tagName = parser.getElementName(element);
		if ("import".equals(tagName))
			processImports(element);
		else if ("define".equals(tagName))
			processDefine(element);
		else if ("children".equals(tagName))
			processChildControls(element, composite);
		else
			throw new XSWTException("Unknown XSWT node", element);
	}

	private void processStylesheet(Object element) throws XSWTException {
		String id = getWidgetName(element);
		String path = getAttributeValue(element, null, "path"), absolutePath = null;
		String baseUri = getAttributeValue(element, null, "relativeTo");
		XSWT styleXswt = null;
		if (baseUri != null) {
			Class relativeTo = (Class)dataParser.parse(baseUri, Class.class);
			if (relativeTo != null) {
				try {
					styleXswt = create(path, relativeTo);
				} catch (IOException e) {
				}
			} else {
				absolutePath = getUriHandler().resolve(path, baseUri);
			}
		}
		if (styleXswt == null) {
			if (absolutePath == null) {
				absolutePath = getUriHandler().resolve(path, getUri());
			}
			styleXswt = create(absolutePath);
		}
		if (styleXswt == null) {
			throw new XSWTException("No such file: " + absolutePath + " (" + path + " relative to " + baseUri +")");
		}
		styleXswt.styleSheetParent = this;
		styleXswt.setUriHandler(getUriHandler());
		try {
			styleXswt.parseAsStyleSheet = true;
			styleXswt.parse(null);
		} finally {
			styleXswt.parseAsStyleSheet = false;
		}
		addWidgetId(id, styleXswt);
	}

	/**
	 * Method processChildControls. We found a <children>tag and have to create
	 * all the controls represented by the tags inside it.
	 * 
	 * @param parent
	 *            The parent composite
	 * @param parser
	 *            The <children>we have to process
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws XSWTException
	 */
	private void processChildControls(Object element, Object parent)
			throws XSWTException {

		int count = parser.getChildElementCount(element);
		for (int i = 0; i < count; i++) {
			Object child = parser.getChildElement(element, i);
			processChild(child, parent);
		}
	}

	// Bindings methods
	
	public boolean has(String name) {
		return getObjectMap().containsKey(name); 
	}
	public Object get(String name) {
		return getObjectMap().get(name);
	}
	public void set(String name, Object value) {
		getObjectMap().put(name, value);
		fireBindingAdded(name, value);
	}
	public Iterator symbols() {
		return getObjectMap().keySet().iterator();
	}

	private List evaluationContexts = new ArrayList();

	private List scriptingEngines;
	
	public void addScriptingEngine(String name, ScriptingEngine engine, boolean isDefault) {
		int pos = (isDefault ? 0 : scriptingEngines.size());
		scriptingEngines.add(pos, engine);
		scriptingEngines.add(pos, name);
	}
	
	public ScriptingEngine getScriptingEngine(String name) throws XSWTException {
		int pos = scriptingEngines.indexOf(name);
		if (pos >= 0) {
			return (ScriptingEngine)scriptingEngines.get(pos + 1);
		}
		String className = defaultScriptingEngineClassName(name);
		ScriptingEngine engine = null;
		try {
			engine = (ScriptingEngine)Class.forName(className).newInstance();
		} catch (Exception e) {
		}
		if (engine != null) {
			addScriptingEngine(name, engine, false);
		}
		if (engine == null) {
			throw new XSWTException(name + " scripting engine is not present");
		}
		return engine;
	}

	private String defaultScriptingEngineClassName(String name) {
		return "com.swtworkbench.community.xswt." + name + "." + upperCaseFirstLetter(name + "ScriptingEngine");
	}

	public ScriptingEngine getDefaultScriptingEngine() throws XSWTException {
		if (scriptingEngines == null) {
			throw new XSWTException("No scripting engine is present");
		}
		return (scriptingEngines != null && scriptingEngines.size() > 1 ? (ScriptingEngine)scriptingEngines.get(1) : null);
	}
	
	private EvaluationContext getEvaluationContext(String lang, String name) throws XSWTException {
		String key = lang + ":" + name;
		int pos = evaluationContexts.indexOf(key);
		if (pos >= 0) {
			return (EvaluationContext)evaluationContexts.get(pos + 1);
		}
		ScriptingEngine engine = (lang != null ? getScriptingEngine(lang) : getDefaultScriptingEngine());
		evaluationContexts.add(key);
		EvaluationContext evaluationContext = engine.getEvaluationContext(name, this);
		evaluationContexts.add(evaluationContext);
		return evaluationContext;
	}

	private void addWidgetId(String id, Object widget) throws XSWTException {
		if (id != null) {
			if (widgetDataParser.get(id) != null) {
				String message = "Duplicated widget ID found";
				throw new XSWTException(message);
			}
			Object o = layoutBuilder.namedObject(widget);
			widgetDataParser.put(id, o);
			// process the Fixup Table to see if there're any unsolved references
			resolveIdRefs(id, widget);
			fireBindingAdded(id, o);
		}
	}

	private void fireBindingAdded(String id, Object value) {
		for (int i = 0; listeners != null && i < listeners.size(); i++) {
			((BindingsListener)listeners.get(i)).bindingAdded(this, id, value);
		}
	}

	/**
	 * Method processChild. We've found a tag inside a &lt;children>tag. We now
	 * have to construct it and set its properties.
	 * 
	 * @param parent
	 *            The parent that will own the new child
	 * @param parser
	 *            The parser representing the new child
	 * @return The new child
	 * @throws XmlPullParserException
	 * @throws XSWTException
	 * @throws IOException
	 */
	private Object processChild(Object element, Object parent) throws XSWTException {
		String widgetName = getWidgetName(element);
		if (widgetName != null) {
			Object previousValue = widgetDataParser.get(widgetName);
			if (previousValue != null) {
				if (parent != null)	// If it's an x:define, we'll just keep the existing one else throw an exception
					throw new XSWTException("Duplicated widget ID found", element);
				else
					return previousValue;
			}
		}
		if (parseAsStyleSheet) {
			if (styleSheetParent != null && widgetName == null) {
				throw new XSWTException("A style without widget ID found", element);
			}
			addWidgetId(widgetName, element);
			styleSheetParent.registerStyle(this, element);
			return element;
		}

		Object result = null;

		String tagName = parser.getElementName(element); // Control's class name
		fireProcessElement(tagName, parent, false);
		XSWTException xe = null;

		try {
			Logger.log().debug(XSWT.class, "Constructing (" + parser.getElementNamespace(element) + ") " + tagName);
			Class klass = (Class)dataParser.parse(tagName, Class.class);
			if (klass == null) {
				throw new XSWTException("Could not resolve the '" + tagName + "' class.");
			}
			int style = resolveStyles(element, new ArrayList(0)); // styles if available
			result = layoutBuilder.construct(klass, parent, style, widgetName,element);
		} catch (XSWTException e1) {
			xe = e1;
		}
		if (result == null) {
			try {
				result = dataParser.parse(tagName, Widget.class);
			} catch (XSWTException e) {
			}
		}
		if (result == null) {
			throw (xe != null ? xe : new XSWTException("No ID named " + tagName, element));
		} else if (parser.isElement(result)) {
			return getStyleOwner(result).processChild(result, parent);
		}
		processChildContent(element, result);
		
		if (widgetName != null) {
			addWidgetId(widgetName, result);
		}
		fireProcessElement(tagName, result, true);
		return result;
	}

	private int resolveStyles(Object element, List idStyles) throws XSWTException {
		String style = getAttributeValue(element, XSWT_NS, "style"); // styles if available
		if (style == null) {
			return SWT.NONE;
		}
		int styleValue = StyleParser.parse(style, getDataParser(), idStyles);
		// collect styles from idStyles
		List subStyles = null;
		for (int i = 0; i < idStyles.size(); i++) {
			Object styleElement = idStyles.get(i);
			if (! parser.isElement(styleElement)) {
				throw new XSWTException(styleElement + " is not a valid style element", element);
			}
			/*
			if (! parser.getElementName(element).equals(parser.getElementName(styleElement))) {
				throw new XSWTException(element + " and " + styleElement + " does not have the same name", element);
			}
			*/
			XSWT styleXswt = (styleOwnerMap != null ? (XSWT)styleOwnerMap.get(styleElement) : this);
			if (subStyles == null) {
				subStyles = new ArrayList(0);
			} else {
				subStyles.clear();
			}
			styleValue |= (styleXswt != null ? styleXswt : this).resolveStyles(styleElement, subStyles);
		}
		return styleValue;
	}
	
	public void processChildContent(Object element, Object result) throws XSWTException {
		List idStyles = new ArrayList(0);
		resolveStyles(element, idStyles); // styles if available
        for (int i = 0; i < idStyles.size(); i++) {
			Object idStyle = idStyles.get(i);
			getStyleOwner(idStyle).processChildContent(idStyle, result);
		}
		processChildAttributes(element, result);
		if (result instanceof IScriptable) {
			IScriptable script = (IScriptable)result;
			if (script.getSource() == null) {
				String text = parser.getElementText(element);
				script.setSource(text.trim());
			}
			String lang = script.getLang();
			try {
				EvaluationContext evaluationContext = getEvaluationContext(lang, getWidgetName(element));
				script.evaluateScript(evaluationContext);
			} catch (Exception e) {
				throw new XSWTException("Error when evaluating " + script.getSource(), e, element);
			}
		} else {
			processSubNodes(element, result);
		}
	}

	private Map styleOwnerMap;

	private void registerStyle(XSWT xswt, Object element) {
		if (styleOwnerMap == null) {
			styleOwnerMap = new HashMap();
		}
		styleOwnerMap.put(element, xswt);
	}

	private XSWT getStyleOwner(Object element) {
		XSWT styleOwner = null;
		if (styleOwnerMap != null) {
			styleOwner = (XSWT)styleOwnerMap.get(element);
		}
		return (styleOwner != null ? styleOwner : this);
	}
	
	public String getAttributeValue(Object element, String namespace, String name) {
		return getAttributeValue(element, namespace, name, false);
	}

	public String getAttributeValue(Object element, String namespace, String name, boolean useStyle) {
		int count = parser.getAttributeCount(element);
		for (int i = 0; i < count; i++) {
			String attrNamespace = parser.getAttributeNamespace(element, i);
			if (name.equals(parser.getAttributeName(element, i)) &&
					(namespace == attrNamespace || (namespace != null && namespace.equals(attrNamespace)))) {
				return parser.getAttributeValue(element, i);
			}
		}
		// if not such attr exists, try to find it in the style(s)
		if (useStyle) {
			List idStyles = new ArrayList();
			try {
				resolveStyles(element, idStyles);
			} catch (XSWTException e) {
				return null;
			}
			for (int i = 0; i < idStyles.size(); i++) {
				String attrValue = getAttributeValue(idStyles.get(i), namespace, name);
				if (attrValue != null) {
					return attrValue;
				}
			}
		}
		return null;
	}

	/**
	 * Method getWidgetName. Returns the name of the current widget as specified
	 * by an ID attribute. If an ID attribute is not present, returns null.
	 * 
	 * @param parser
	 *            The element to search
	 * @return The name string or null if not found
	 */
	private String getWidgetName(Object element) {
		return getAttributeValue(element, XSWT_NS, "id");
	}
	
	/**
	 * Method processChildAttributes. We've seen a tag and constructed its
	 * object. Now we have to process any other attributes on it. Attributes are
	 * considered to function in any of the following roles and are considered
	 * for each role in the following order of precedence:
	 * <p>
	 * 
	 * <ol>
	 * <li>Built-in attribute with special meaning to XSWT (reserved word)
	 * <li>Attribute represents a property on the object
	 * <li>Attribute represents a field on the object
	 * </ol>
	 * 
	 * @param obj
	 *            The object on which the attribute should be applied
	 * @param parent
	 *            The XML element representing the attribute
	 * @throws XSWTException
	 *             If something really bad happened
	 */
	private void processChildAttributes(Object element, Object obj)
			throws XSWTException {

		// Generically handle JFace, Essential Data, etc., viewer-like things...
		Object viewer = obj; // Assume we have a JFace Viewer-like thing
		Object viewedObject = ReflectionSupport.invokei(obj, "getControl", new Object[] {});
		if (viewedObject != null) {
			obj = viewedObject;
			((Widget) obj).setData("viewer", viewer);
		}

		int count = parser.getAttributeCount(element);
		for (int i = 0; i < count; ++i) {
			//Node attribute = attributes.item(i);
			boolean recognized = false;

			String attrName = parser.getAttributeName(element, i);
			String attrValue = parser.getAttributeValue(element, i);
			int colNum = -1; // parser.getColumnNumber();
			int lineNum = -1; // parser.getLineNumber();

			recognized = processBuiltInAttr(obj, attrName, parser.getAttributeNamespace(element, i), attrValue);
			if (recognized)
				continue;

			fireProcessAttribute(attrName, attrValue, obj, false);
			// Process Property
			try {
				recognized = layoutBuilder.setProperty(attrName, obj, attrValue, element);
			} catch (XSWTException e) {
				// We found an unsolved reference
				addUnresolvedIDRef(attrName, obj, attrValue, lineNum, colNum);
				recognized = true; // We treat as recognized at this moment
			}
			if (recognized) {
				fireProcessAttribute(attrName, attrValue, obj, true);
				continue;
			}
			
			// Process Field
			try {
				recognized = layoutBuilder.setField(attrName, obj, attrValue, element);
			} catch (XSWTException e) {
				// We found an unsolved reference
				addUnresolvedIDRef(attrName, obj, attrValue, lineNum, colNum);
				recognized = true; // We treat as recognized at this moment
			}
			if (recognized) {
				fireProcessAttribute(attrName, attrValue, obj, true);
				continue;
			}
			if (!recognized)
				throw new XSWTException(attrName + " attribute not found", element);
		}
	}


	/**
	 * Method processBuiltInAttr. Process any attributes that are reserved
	 * words. These currently are "style", "class", and "id".
	 * 
	 * @param obj
	 *            The object to operate on
	 * @param nodeName
	 *            The attribute name to process
	 * @param namespace
	 *            The attribute namespace to process
	 * @param value
	 *            The attribute value to process
	 * @return true if we processed this attribute or there is no need to do so
	 * @throws XSWTException
	 *             If something went horribly wrong
	 */
	private boolean processBuiltInAttr(Object obj, String nodeName,
			String namespace, String value) throws XSWTException {
		// TODO: an ugly hack to get rid of namespace prefix
		int index = nodeName.indexOf(':');
		nodeName = (index == -1) ? nodeName : nodeName.substring(index + 1);

		if (XSWT_NS.equals(namespace)) {
			// The object's "ID" (similar to its "name" property in the builder
			if (nodeName.endsWith("id"))
				return true;

            // x:id.<key> - setData on Widget objects
            if (nodeName.startsWith("id.")) {
                String key = nodeName.substring("id.".length());
                //String value = attribute.getNodeValue();
    			try {
    				obj.getClass().getMethod("setData", new Class[]{String.class, Object.class}).invoke(obj, new Object[] {key, value});
    			} catch (Exception e) {
				}
    			// ReflectionSupport.invokei(obj, "setData", new Object[] {key, value});
                return true;
            }
            
			// Constructor parameters have already been processed
			if ("p".equals(nodeName.substring(0, 1)))
				return true;

			// "style" was already processed at construction time
			if (nodeName.endsWith("style"))
				return true;

			// "class" was already processed (or is a reserved word in XSWT in
			// any case)
			if (nodeName.endsWith("class"))
				return true;

		}

		// Didn't recognize anything here...
		return false;
	}

	/**
	 * Method processSubNodes. Sub-elements of an XML tag denote one of three
	 * things:
	 * <p>
	 * 
	 * <ul>
	 * <li>If the element is a <children>node, the node denotes SWT child
	 * controls in the SWT containership hierarchy.
	 * <li>Otherwise, the element is assumed to be a property set operation or
	 * a method call, if a corresponding property/method exists on parent
	 * <li>Otherwise, the element is assumed to be a new child node
	 * </ul>
	 * 
	 * @param obj
	 *            The current parent object
	 * @param parser
	 *            The element we're processing
	 * @return true if the element was recognized.
	 * @throws XmlPullParserException
	 * @throws XSWTException
	 * @throws IOException
	 */
	private void processSubNodes(Object element, Object obj)
			throws XSWTException {

		Object viewedObject = ReflectionSupport.invokei(obj, "getControl", new Object[] {});
		if (viewedObject != null) {
			obj = viewedObject;
		}

		int count = parser.getChildElementCount(element);
		for (int i = 0; i < count; i++) {
			Object child = parser.getChildElement(element, i);
//			IElementHandler[] handlers = getElementHandlers(parser.getElementNamespace(element));
//			for (int j = 0; j < handlers.length; j++) {
//				IElementHandler elementHandler = handlers[j];
//				Object result = null;
//				try {
//					result = elementHandler.handleElement(element, obj, this);
//				} catch (XSWTException xe) {
//				}
//				if (result != null) {
//				}
//			}
			if ("children".equals(parser.getElementName(child))) {
				processChildControls(child, obj);
			} else {
				// Otherwise, first try to treat it as a property setter.
				// Failing that, treat it as a new object
				try {
					processNodeProperty(child, obj);
				} catch (XSWTException e) {
					try {
						processChild(child, obj);
					} catch (XSWTException e2) {
						if (e.isAmbiguous()) throw e;
						if (e2.isAmbiguous()) throw e2;
						throw new XSWTException(AMBIGUOUS_ERROR_MSG, e, e2);
					}
				}
			}
		}
	}

	public void processAttributes(Object element, Object context) throws XSWTException {
		Object viewer = context; // Assume we have a JFace Viewer-like thing
		Object viewedObject = ReflectionSupport.invokei(context, "getControl", new Object[] {});
		if (viewedObject != null) {
			context = viewedObject;
			((Widget)context).setData("viewer", viewer);
		}
		int count = parser.getAttributeCount(element);
		for (int i = 0; i < count; ++i) {
			//Node attribute = attributes.item(i);
			boolean recognized = false;

			String attrName = parser.getAttributeName(element, i);
			String attrValue = parser.getAttributeValue(element, i);
			String attributeUri = parser.getAttributeNamespace(element, i);
			int colNum = -1; // parser.getColumnNumber();
			int lineNum = -1; // parser.getLineNumber();

			IAttributeHandler[] handlers = getAttributeHandlers(attributeUri);
			for (int j = 0; j < handlers.length; j++) {
				IAttributeHandler attributeHandler = handlers[j];
				Object result = null;
				try {
					fireProcessAttribute(attrName, attrValue, context, false);
					recognized = attributeHandler.handleAttribute(attrName, attrValue, attributeUri, context, this);
				} catch (XSWTException xe) {
				}
				fireProcessAttribute(attrName, attrValue, context, true);
				if (recognized) {
					break;
				}
			}
			if (! recognized) {
				throw new XSWTException(attrName + " attribute not found", element);
			}
		}
	}

	public void processChildren(Object element, Object context, String uri) throws XSWTException {
		Object viewedObject = ReflectionSupport.invokei(context, "getControl", new Object[] {});
		if (viewedObject != null) {
			context = viewedObject;
		}
		int count = parser.getChildElementCount(element);
		for (int i = 0; i < count; i++) {
			Object child = parser.getChildElement(element, i);
			if (uri == null) {
				uri = parser.getElementNamespace(child);
			}
			IElementHandler[] handlers = getElementHandlers(uri);
			for (int j = 0; j < handlers.length; j++) {
				IElementHandler elementHandler = handlers[j];
				Object result = null;
				try {
					result = elementHandler.handleElement(child, context, this);
				} catch (XSWTException xe) {
				}
				if (result != null) {
					processAttributes(child, result);
					if (! elementHandler.handlesChildElements(child, this)) {
						processChildren(child, result, null);
					}
					return;
				}
			}
		}
	}

	/**
	 * Method processNodeProperty. Deal with the case where a node represents a
	 * property/field set operation or a method call.
	 * 
	 * @param obj
	 *            The object that is the subject of the verb
	 * @param nodeChild
	 *            The child node representing the value to be set
	 * @throws XmlPullParserException
	 * @throws XSWTException
	 * @throws IOException
	 */
	private void processNodeProperty(Object element, Object obj) throws XSWTException {
		// The type of the value to construct
		Class valueType = null;

		// Either "setter" or "field" will be set but not both
		Method[] setters = null;
		Method setter = null;
		Field field = null;

		// The value to pass to the method or field
		Object value = null;

		// Check for a "class=" attribute
		String classAttrib = getAttributeValue(element, XSWT_NS, "class", true);
		if (classAttrib != null) {
//			String className = upperCaseFirstLetter(classAttrib);
//			valueType = classBuilder.getClass(className);
			valueType = (Class)dataParser.parse(classAttrib, Class.class);
		}

		// Find the method or field
		String nodeName = parser.getElementName(element);
		setters = layoutBuilder.resolveAttributeSetMethod(obj, nodeName,
				valueType);

		// If we didn't find a setter,
		// New: look for a zero-argument full qualified method name with a
		// return class
		// and then processSubNodes with the new parent object
		// Get the return class name
		if (setters == null) {
			String parentReferenceId = getAttributeValue(element, XSWT_NS, "id");

			Method getParentReferenceMethod = layoutBuilder.resolveAttributeGetMethod(obj, nodeName);
			if (getParentReferenceMethod != null) {
				Object temp_parent = layoutBuilder.getProperty(getParentReferenceMethod, obj, null, element);
				if (temp_parent != null) {
					if (parentReferenceId != null) {
						addWidgetId(parentReferenceId, temp_parent);
					}
					processChildContent(element, temp_parent);
				}
				return;
			}
		}
		if (setters == null) {
			try {
				field = layoutBuilder.getClass(obj).getField(nodeName);
			} catch (Throwable t) {
//			    t.printStackTrace();
			}

			// If we didn't find a field either, give up
			if (obj!= null && field == null)
				throw new XSWTException("Property/method/field not found on " + obj.getClass().getName(), element);

			// Make sure we have a ValueType
			if (valueType == null) {
				valueType = field.getType();
			}
		}

		// if the valueType isn't set and we have a setter, set valueType
		// from the method's type.
		if (valueType == null && setters != null) {
			XSWTException lastException = new XSWTException("Assert failure");
			for (int i = 0; i < setters.length; i++) {
				setter = setters[i];
				Class[] paramTypes = setter.getParameterTypes();
				valueType = paramTypes[0];
				try {
					value = constructValueObject(valueType, element);
				} catch (XSWTException e) {
					lastException = e;
				}
				break;
			}
			if (value == null) {
				throw lastException;
			}
		} else if (valueType != null) {
			value = constructValueObject(valueType, element);
		} else {
			throw new XSWTException("Cannot determine the valueType for " + nodeName);
		}
		Logger.log().debug(XSWT.class, "Field type: " + valueType.getName());
		

		// Set its properties
		processChildContent(element, value);
		
		// Call the setter or assign the field...
		if (setters != null) {
			if (setter != null) {
				layoutBuilder.setProperty(setter, obj, value, element);
			} else {
				for (int i = 0; i < setters.length; i++) {
					layoutBuilder.setProperty(setters[i], obj, value, element);
				}
			}
		} else if (obj != null) {
			layoutBuilder.setField(field, obj, value, element);
		}
	}

	/**
	 * Method constructValueObject. When processing a node property, it is
	 * possible to pass arguments to the object's constructor. This method
	 * resolves the correct constructor and constructs the value that will be
	 * set into the node property.
	 * 
	 * @param valueType
	 *            The class of the object to construct
	 * @param node
	 *            The node representing the class
	 * @return The constructed object
	 * @throws XSWTException
	 *             If a constructor could not be found or if something bad
	 *             happened
	 */
	private Object constructValueObject(Class valueType, Object element)
			throws XSWTException {
		// Get the constructor argument strings (if any) out of the node
		LinkedList argList = new LinkedList();

		//NamedNodeMap nodeMap = node.getAttributes();
		int count = parser.getAttributeCount(element);
		StringBuffer arg = new StringBuffer("p");
		for (int i = 0; i < count; ++i) {
			arg.setLength(1);
			arg.append(i);
			String argName = arg.toString();
			String value = getAttributeValue(element, XSWT_NS, argName, true);
			// TODO: "break" may not be good
			// TODO: how to process typo like "x:p0="2" x:p0="3"?
			// TODO: how to process missing argument? like x:p0="1" x:p2="2"?
			if (value == null)
				break;
			argList.addLast(value);
		}
		return layoutBuilder.construct(valueType, argList, element);
	}

	/**
	 * Method upperCaseFirstLetter. Returns source with the first letter
	 * guaranteed to be upper-case.
	 * 
	 * @param source
	 * @return
	 */
	public static String upperCaseFirstLetter(String source) {
		StringBuffer buf = new StringBuffer(source.substring(0, 1).toUpperCase());
		buf.append(source.substring(1, source.length()));
		return buf.toString();
	}

	/*
	 * Only needed if I'm going to support setData(key, value) private void
	 * processData(Widget widget, Element element) { NodeList nodeListData =
	 * element.getElementsByTagName("data");
	 * 
	 * for (int i = 0; i < nodeListData.getLength(); i++) { Element elementData =
	 * (Element) nodeListData.item(i);
	 * 
	 * if (elementData.getParentNode().equals(element)) { String attributeKey =
	 * elementData.getAttribute("key"); String attributeValue =
	 * elementData.getAttribute("value");
	 * 
	 * if (attributeKey == null) widget.setData(attributeValue); else
	 * widget.setData(attributeKey, attributeValue); } } }
	 */
}
