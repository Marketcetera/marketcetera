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
package com.swtworkbench.community.xswt.layoutbuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.swtworkbench.community.xswt.ClassBuilder;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.metalogger.Logger;


/**
 * Class LayoutBuilder.    A place to put methods that are common to all
 * ILayoutBuilder implementations.
 * 
 * @author daveo
 */
public abstract class LayoutBuilder implements ILayoutBuilder {
	
	protected DataParser dataParser = null;
	protected XSWT xswt = null;
	
	public LayoutBuilder(XSWT xswt) {
		this.dataParser = xswt.getDataParser();
		this.xswt = xswt;
		xswt.setLayoutBuilder(this);
	}

	protected void fireSetProperty(Object o, String name, Object value, boolean processed) {
		xswt.fireSetProperty(name, value, o, processed);
	}

    protected class ConstructorInfo {
        public Constructor constructor = null;
        public Class[] paramTypes = null;
        public Object[] args = null;
    }
    
    /**
     * Method getConstructorInfo.  Finds a constructor that can construct the given class
     * using the given arguments.
     * 
     * Note that class array does not contain public constructor. 
     * 
     * @param valueType The Class to construct
     * @param argList A LinkedList of Strings where each String is in XSWT parameter format.
     * @return A ConstructorInfo describing the selected constructor and the parsed arguments
     * @throws XSWTException
     */
    protected ConstructorInfo getConstructorInfo(Class valueType, List argList) throws XSWTException {
        ConstructorInfo result = new ConstructorInfo();
        Constructor[] constructors = valueType.getConstructors();
        for (int i = 0; i < constructors.length; i++) {
            result.constructor = constructors[i];
            
            result.paramTypes = result.constructor.getParameterTypes();
            if (argList.size() != result.paramTypes.length)
            	// TODO: We cannot just simply continue, this is the most common mistakes when people write XSWT.
            	// TODO: At least some feedback to inform the mismatch
                // DJO: You *have* to 'continue' becuase you don't know 
                // that the next constructor you see won't be the right one.
                // Maybe set a flag or keep track of near misses in order
                // to generate more helpful error messages...
                continue;
                
            result.args = new Object[argList.size()];
            try {
                // Try to convert all the arguments to appropriate types
                Iterator argIter = argList.iterator();
                for (int arg=0; arg<result.args.length; ++arg) {
                    String argStr = (String)argIter.next();
                    result.args[arg] = dataParser.parse(argStr, result.paramTypes[arg]);
                }
                // If we succeeded, theOne is the one
                break;
            //}catch (XSWTException e1) {
            //	throw e1;
            } catch (Exception e) {
            	// TODO: message() or debug()?
            	Logger.log().debug(LayoutBuilder.class, "Error in parsing "+ valueType + ": "+e);
                // If we didn't succeed, try the next constructor...
                result.constructor = null;
                result.args = null;
                continue;
            }
        }
        return result;
    }

    /**
     * Method resolveAttributeSetMethod.  Return a Method object representing 
     * the set method referred to by an attribute property on the specified object.
     *   
     * @param obj The object on which the method should be found
     * @param methodName The method name in XSWT syntax
     * @param propertyType The property's type or null if this isn't known
     * @return The Method[] found or null if none found.
     */
    public Method[] resolveAttributeSetMethod(Object obj, String methodName, Class propertyType) {
    	Class klass = null;
    	try {
	    	klass = getClass(obj);
        } catch (XSWTException e) {
        	return null;
        }
        String[] setters = ReflectionSupport.getSetMethodNames(methodName);
        Method[] methods = ReflectionSupport.resolvePropertySetter(klass, setters, propertyType);

        return methods;
    }

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#resolveAttributeGetMethod(java.lang.Object, java.lang.String, java.lang.Class)
	 */
	public Method resolveAttributeGetMethod(Object obj, String methodName) {
		try {
			Class klass = getClass(obj);
			return ReflectionSupport.resolveAttributeGetMethod(klass, methodName);
        } catch (XSWTException e) {
        }
        return null;
	}

	protected ClassBuilder getClassBuilder() {
		return xswt.classBuilder;
	}
	
	protected Object parseData(String valueSource, Class klass) throws XSWTException {
		XSWTException xe = null;
    	Object result = null;
    	try {
			result = dataParser.parse(valueSource, klass);
		} catch (XSWTException e) {
			xe = e;
		}
    	if (result == null && xe != null) {
    		throw xe;
    	}
    	return result;
	}

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.layoutbuilder.LayoutBuilder#namedObject(java.lang.Object)
	 */
	public Object namedObject(Object obj) throws XSWTException {
		return obj;
	}

    public abstract Class getClass(Object obj) throws XSWTException;
}
