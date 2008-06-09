/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Advanced Systems Concepts 	- Initial api and implementation
 *     Yu You (Nokia)				- Add ResouceManager support and exception error constances
 *******************************************************************************/
package com.swtworkbench.community.xswt;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Widget;

/**
 * Class ClassBuilder.  Uses reflection to construct SWT controls and other classes.
 * 
 * <p>
 * ClassBuilder has to take care of two different situations:
 * <dl>
 * <li>SWT Controls: each control must have a parent Composite</li>
 * <li>SWT classes in graphics package: the classes require a Device as an 
 * argument in their constructors, for example, classes like Color and Font.
 * </dl>
 * </p>
 * @author daveo
 */
public class ClassBuilder {

	/*
	 * ERROR_NOT_CONTROL
	 * 
	 * Parent is not a Control.
	 */
	public static final String ERROR_NOT_CONTROL = "parent is not Control";

	/*
	 * ERROR_NOT_WIDGET
	 * 
	 * Parent is not a Widget.
	 */
	public static final String ERROR_NOT_WIDGET = "parent is not Widget";
	/*
	 * ERROR_NOT_COMPOSITE
	 * 
	 * Parent is not a Compisite.
	 */
	public static final String ERROR_NOT_COMPOSITE = "parent is not Composite";
	/*
	 * ERROR_NOT_DECORATION
	 * 
	 * Parent is not a Decoration.
	 */
	public static final String ERROR_NOT_DECORATION = "parent is not Decoration";

    private static ClassBuilder builder = null;
    
    /**
     * Method getDefault.  Return the default ClassBuilder object.
     * @return
     */
    public static ClassBuilder getDefault() {
        if (builder == null)
            builder = new ClassBuilder();
        return builder;
    }
    
    public ClassBuilder() {
    	/*
    	if (importExtensionPointPackages) {
    		if (defaultPackageImports != null) {
	    		for (int i = 0; i < defaultPackageImports.size(); i++) {
	    			importPackage((String)defaultPackageImports.get(i));
	    		}
    		}
    		if (defaultClassImports != null) {
	    		for (int i = 0; i < defaultClassImports.size(); i++) {
	    			importClass((String)defaultClassImports.get(i));
	    		}
    		}
//    		XswtPlugin plugin = XswtPlugin.getDefault();
//    		if (plugin != null) {
//				plugin.importPackages(this);
//	    		plugin.importClasses(this);
//    		}
    	}
    	 */
    }
    
    private ClassLoader classLoader = getClass().getClassLoader();
    
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classloader) {
		this.classLoader = classloader;
		resolvedClasses = new HashMap();
	}

	/*
	private static List defaultPackageImports;
	
	public static void addDefaultPackageImports(String pack) {
		if (defaultPackageImports == null) {
			defaultPackageImports = new ArrayList();
		}
		defaultPackageImports.add(pack);
	}
	
	private static List defaultClassImports;

	public static void addDefaultClassImports(String className) {
		if (defaultClassImports == null) {
			defaultClassImports = new ArrayList();
		}
		defaultClassImports.add(className);
	}
	 */
	
    // Remember the imported packages here
    private List imports = new ArrayList();
    
    /**
     * Method importPackage.  Adds packageName to the list of packages that
     * will be searched at object construction time.
     * 
     * @param packageName The fully-qualified package name as a String
     */
    public void importPackage(String packageName) {
        imports.add(packageName);
    }

    /**
     * Method importClass.  Ensures that the simple class name resolves this fully qualified class name.
     * 
     * @param className The fully-qualified class name as a String
     */
    public void importClass(String className) {
    	int pos = className.lastIndexOf('.');
    	String simpleName = className.substring(pos + 1);
        try {
            Class result = getClass(className);
            importClass(simpleName, result);
        } catch (Exception e) {};
    }
    
    /**
     * Method importClass.  Ensures that the simple class name resolves this fully qualified class name.
     * 
     * @param className The fully-qualified class name as a String
     */
    public void importClass(String simpleName, Class klass) {
		resolvedClasses.put(simpleName, klass);
    }
    
    /**
     * Method imports.  Return the list of imports in this XSWT file.
     * 
     * @return List the imports list.
     */
    public List imports() {
    	return imports;
    }
    
    /*
     * Utility methods for the common case of constructing from the current classpath
     */
    
    // Map class name String --> resolved Class object
    private Map resolvedClasses = new HashMap();
    
    /**
     * Method getClass.  Resolve a simple class name (not necessarily 
     * fully-qualified) to its Class object.
     * 
     * @param className The name of the class to resolve
     * @return The Class object represented by className
     * @throws XSWTException if the Class could not be found
     */
    public Class getClass(String className) throws XSWTException {
        Class result = null;
        
        // See if we've resolved this one before...
        result = (Class) resolvedClasses.get(className);
        if (result != null) return result;
        
        // See if we can construct a FQN for the class and resolve it...
        Iterator i = imports.iterator();
        while (i.hasNext()) {
            StringBuffer packageName = new StringBuffer((String) i.next());
            packageName.append(".");
            packageName.append(className);
            String fullyQualifiedName = packageName.toString();
            
            try {
                result = classLoader.loadClass(fullyQualifiedName);
            } catch (Exception e) { result = null;};
            if (result != null) {
            	resolvedClasses.put(className, result);
                StyleParser.registerClassConstants(result);
                return result;
            } 
        }
        
        // See if it's already a fully-qualified class name
        try {
            result = classLoader.loadClass(className);
        } catch (Throwable t) {}
        
        if (result != null) {
        	resolvedClasses.put(className, result);
            StyleParser.registerClassConstants(result);
            return result;
        } 
        
        throw new XSWTException("Unable to resolve class: " + className + ".  Check the import node for the necessary package name");
    }
    
    /**
     * Method constructControl.  Construct an SWT control using reflection on
     * its class name.<p>
     * 
     * This method constructs a control given either its simple or 
     * fully-qualified class name, its parent, and its style bits.  If a simple
     * class name is passed, the package in which the class is found must 
     * have been previously passed using the importPackage() method.<p>
     * 
     * The ClassBuilder object caches the Class objects of resolved classes,
     * so that a search for the desired Class object is only performed once.
     * 
     * @param className The simple or fully-qualified class name as a String.
     * @param parent The Parent "control"
     * @param style Style bits
     * @return The constructed Widget or ControlEditor.
     * @throws XSWTException If something went wrong.  Wraps the actual exception.
     */
    public Object constructControl(Class klass, Object parent, int style) throws XSWTException {
        try {
            Constructor constructor = null;
            
            // FIXME: It should be possible to rewrite this as 2 loops: one over
            // the 2-arg cases, and the second over the one-arg cases
            
            if (parent != null) {
	            // First we try all the 2-arg constructor possibilities
	            try {
	                if (!(parent instanceof Decorations)) throw new Exception(ERROR_NOT_DECORATION);
	                constructor = klass.getDeclaredConstructor(
	                    new Class[] {Decorations.class, Integer.TYPE});
	            } catch (Exception e) {
	                try {
	                    if (!(parent instanceof Composite)) throw new Exception(ERROR_NOT_COMPOSITE); 
	                    constructor = klass.getDeclaredConstructor(
	                        new Class[] {Composite.class, Integer.TYPE});
	                } catch (Exception e0) {
	                    try {
	                        if (!(parent instanceof Control)) throw new Exception(ERROR_NOT_CONTROL); 
	                        constructor = klass.getDeclaredConstructor(
	                            new Class[] {Control.class, Integer.TYPE});
	                    } catch (Exception e1) {
	                        try {
	                            if (!(parent instanceof Widget)) throw new Exception(ERROR_NOT_WIDGET); 
	                            constructor = klass.getDeclaredConstructor(
	                                new Class[] {Widget.class, Integer.TYPE});
	                        } catch (Exception e2) {
	                            try {
	                                constructor = klass.getDeclaredConstructor(
	                                    new Class[] {parent.getClass(), Integer.TYPE});
	                            } catch (Exception e3) {
	                                
	                                // Now we try the 1-arg constructor possibilities
	                                try {
	                                    if (!(parent instanceof Control)) throw new Exception(ERROR_NOT_CONTROL); 
	                                    constructor = klass.getDeclaredConstructor(
	                                        new Class[] {Control.class});
	                                } catch (Exception e4) {
	                                    try {
	                                        if (!(parent instanceof Widget)) throw new Exception(ERROR_NOT_WIDGET); 
	                                        constructor = klass.getDeclaredConstructor(
	                                            new Class[] {Widget.class});
	                                    } catch (Exception e5) {
	                                        constructor = klass.getDeclaredConstructor(
	                                            new Class[] {parent.getClass()});
	                                    }
	                                }
	                            }
	                        }
	                    }
	                }
	            }
            } else {
            	// If parent is null, use a 0-arg constructor.
            	// This should currently only happen if called from within x:extends
            	constructor = klass.getDeclaredConstructor(
                        new Class[] {});
            }
            
            // Now try to actually construct the object
            if (constructor.getParameterTypes().length == 2) {
                return constructor.newInstance(new Object[]{parent, new Integer(style)});
            }
            if (constructor.getParameterTypes().length == 1) {
            	return constructor.newInstance(new Object[]{parent});
            }
            return constructor.newInstance(new Object[] {});
        } catch (Throwable t) {
            throw new XSWTException(t);
        }
    }

	/**
	 * Clean up resources
	 */
	public void dispose() {
		imports.clear();
		resolvedClasses.clear();
	}

}


