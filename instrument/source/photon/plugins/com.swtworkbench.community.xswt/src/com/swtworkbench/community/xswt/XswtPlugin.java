package com.swtworkbench.community.xswt;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;

import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.dataparser.IDataParser;
import com.swtworkbench.community.xswt.metalogger.EclipseLogger;
import com.swtworkbench.community.xswt.metalogger.Logger;
import com.swtworkbench.community.xswt.scripting.ScriptingEngine;

import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class XswtPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static XswtPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public XswtPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("com.swtworkbench.community.xswt.XswtPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
        Logger.setLogger(new EclipseLogger(this));
        Logger.log().setDebug(false);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static XswtPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = XswtPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	private Iterator getConfigurationElements(String extensionPoint, String elementName) {
		IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(getBundle().getSymbolicName(), extensionPoint);
		if (ep == null) {
			return new LinkedList().iterator();
		}
		IExtension[] extensions = ep.getExtensions();
		List elements = new ArrayList();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] ces = extensions[i].getConfigurationElements();
			for (int j = 0; j < ces.length; j++) {
				if (elementName.equals(ces[j].getName())) {
					elements.add(ces[j]);
				}
			}
		}
		return elements.iterator();
	}

	private XSWT.Configuration xswtConfig;
	
	/*
	private List dataParsers;
	
	public void addDataParsers(DataParser parsers) {
		if (dataParsers == null) {
			dataParsers = new ArrayList();
			processDataParserExtensions();
		}
		for (int i = 0; i < dataParsers.size(); i += 2) {
			try {
				Class klass = Class.forName((String)dataParsers.get(i));
				parsers.addDataParser(klass, (IDataParser)dataParsers.get(i + 1));
			} catch (ClassNotFoundException e) {
			}
		}
	}
	*/

//	private boolean dataParserExtensionsProcessed = false;
	
	private void processDataParserExtensions() {
//		if (dataParserExtensionsProcessed) {
//			return;
//		}
//		dataParserExtensionsProcessed = true;
		Iterator it = getConfigurationElements("dataParser", "data-parser");
		while (it.hasNext()) {
			IConfigurationElement element = (IConfigurationElement)it.next();
			IDataParser parser = null;
			try {
				parser = (IDataParser)element.createExecutableExtension("data-parser-class");
			} catch (CoreException ce) {
			}
			if (parser != null) {
				xswtConfig.addDataParser(element.getAttribute("data-class"), parser);
//				DataParser.addExtensionDataParser(element.getAttribute("data-class"), parser);
//				dataParsers.add(element.getAttribute("data-class"));
//				dataParsers.add(parser);
			}
		}
	}
	
	/*
	private List classBuilderPackages;
	private List classBuilderClasses;
	
	public void importPackages(ClassBuilder builder) {
		if (classBuilderPackages == null) {
			classBuilderPackages = new ArrayList();
			classBuilderClasses = new ArrayList();
			processClassBuilderExtensions();
		}
		for (int i = 0; i < classBuilderPackages.size(); i++) {
			builder.importPackage((String)classBuilderPackages.get(i));
		}
	}
	public void importClasses(ClassBuilder builder) {
		if (classBuilderClasses == null) {
			classBuilderPackages = new ArrayList();
			classBuilderClasses = new ArrayList();
			processClassBuilderExtensions();
		}
		for (int i = 0; i < classBuilderClasses.size(); i++) {
			builder.importClass((String)classBuilderClasses.get(i));
		}
	}
    */
	
//	private boolean classBuilderExtensionsProcessed = false;
	
	private void processClassBuilderExtensions() {
//		if (classBuilderExtensionsProcessed) {
//			return;
//		}
//		classBuilderExtensionsProcessed = true;
		Iterator it = getConfigurationElements("classBuilder", "import");
		while (it.hasNext()) {
			IConfigurationElement element = (IConfigurationElement)it.next();
			String packages = element.getAttribute("packages");
			if (packages != null) {
				StringTokenizer tokens = new StringTokenizer(packages, " ,;");
				while (tokens.hasMoreTokens()) {
					xswtConfig.addPackageImports(tokens.nextToken());
//					ClassBuilder.addDefaultPackageImports(tokens.nextToken());
//					classBuilderPackages.add(tokens.nextToken());
				}
			}
			String classes = element.getAttribute("classes");
			if (classes != null) {
				StringTokenizer tokens = new StringTokenizer(classes, " ,;");
				while (tokens.hasMoreTokens()) {
					xswtConfig.addClassImports(tokens.nextToken());
//					ClassBuilder.addDefaultClassImports(tokens.nextToken());
//					classBuilderClasses.add(tokens.nextToken());
				}
			}
		}
	}

	/*
	private List scriptingEngines;
	
	public ScriptingEngine getScriptingEngine(String name) {
		if (scriptingEngines == null) {
			scriptingEngines = new ArrayList();
			processScriptingEngineExtensions();
		}
		int pos = scriptingEngines.indexOf(name);
		return (pos >= 0 ? (ScriptingEngine)scriptingEngines.get(pos + 1) : null);
	}

	public ScriptingEngine getDefaultScriptingEngine() {
		if (scriptingEngines == null) {
			scriptingEngines = new ArrayList();
			processScriptingEngineExtensions();
		}
		if (scriptingEngines.size() > 1) {
			return (ScriptingEngine)scriptingEngines.get(1);
		}
		return null;
	}
	*/
	
//	private boolean scriptingEngineExtensionsProcessed = false;
	
	private void processScriptingEngineExtensions() {
//		if (scriptingEngineExtensionsProcessed) {
//			return;
//		}
//		scriptingEngineExtensionsProcessed = true;
		Iterator it = getConfigurationElements("scriptingEngine", "scripting-engine");
		while (it.hasNext()) {
			IConfigurationElement element = (IConfigurationElement)it.next();
			String name = element.getAttribute("name");
			boolean isDefault = "true".equals(element.getAttribute("is-default"));
			try {
				ScriptingEngine engine = (ScriptingEngine)element.createExecutableExtension("engine-class");
				xswtConfig.addScriptingEngine(name, engine, isDefault);
//				XSWT.addScriptingEngine(name, engine, isDefault);
//				int pos = (isDefault ? 0 : scriptingEngines.size());
//				scriptingEngines.add(pos, engine);
//				scriptingEngines.add(pos, name);
			} catch (CoreException e) {
				System.err.println(e);
			}
		}
	}
	
	public XSWT.Configuration getXSWTConfiguration() {
		if (xswtConfig == null) {
			xswtConfig = new XSWT.Configuration();
			processClassBuilderExtensions();
			processDataParserExtensions();
			processScriptingEngineExtensions();
		}
		return xswtConfig;
	}
	
	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}