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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Widget;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.parsers.ClassDataParser;

/**
 * Class SWTLayoutBuilder.  The default ILayoutBuilder; constructs an actual
 * SWT UI from an XSWT file.
 * 
 * @author daveo
 */
public class SWTLayoutBuilder extends LayoutBuilder {
	
	public SWTLayoutBuilder(XSWT xswt) {
		super(xswt);
	}

	/* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setProperty(java.lang.String, java.lang.Object, java.lang.String)
     */
    public boolean setProperty(String propertyName, Object receiver, String valueSource, Object contextElement)
        throws XSWTException 
    {
        Method[] setMethods = resolveAttributeSetMethod(receiver, propertyName, null);
        if (setMethods == null)
            return false;

        Exception lastException = null;
        for (Method method : setMethods) {
	        Object value = parseData(valueSource, method.getParameterTypes()[0]);
	        try {
	        	invokeMethod(propertyName, receiver, method, value);
	        	return true;
	        } catch (Exception e) {
	        	lastException = e;
	        }
        }
        throw new XSWTException("Error calling setter " + propertyName, lastException, contextElement);
    }

	private void invokeMethod(String propertyName, Object receiver, Method setter, Object value) throws IllegalAccessException, InvocationTargetException {
		fireSetProperty(receiver, propertyName, value, false);
		setter.invoke(receiver, new Object[] {value});
		fireSetProperty(receiver, propertyName, value, true);
	}
    
    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setProperty(java.lang.reflect.Method, java.lang.Object, java.lang.Object)
     */
    public void setProperty(Method setter, Object receiver, Object value, Object contextElement)
        throws XSWTException 
    {
        try {
        	invokeMethod(setter.getName(), receiver, setter, value);
        } catch (Throwable t) {
            throw new XSWTException("Error calling setter " + setter.getName(), t, contextElement);
        }
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setField(java.lang.String, java.lang.Object, java.lang.String)
     */
    public boolean setField(String fieldName, Object receiver, String valueSource, Object contextElement)
        throws XSWTException 
    {
        Field field = null;
        try {
            field = receiver.getClass().getField(fieldName);
        } catch (Throwable t) {}
        if (field == null) {
        	return false;
        }
        try {
            Object value = parseData(valueSource, field.getType());
        	setFieldValue(fieldName, receiver, field, value);
        } catch (Exception e) {
            throw new XSWTException("Error setting field " + fieldName, e, contextElement);
        }
        return true;
    }

	private void setFieldValue(String fieldName, Object receiver, Field field, Object value) throws IllegalAccessException {
		fireSetProperty(receiver, fieldName, value, false);
		field.set(receiver, value);
		fireSetProperty(receiver, fieldName, value, true);
	}
    
    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setField(java.lang.reflect.Field, java.lang.Object, java.lang.Object)
     */
    public void setField(Field field, Object receiver, Object value, Object contextElement)
        throws XSWTException 
    {
        try {
            field.set(receiver, value);
        } catch (Throwable t) {
            throw new XSWTException("Error setting field " + field.getName(), t, contextElement);
        }
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#construct(java.lang.String, org.eclipse.swt.widgets.Widget, java.lang.String)
     */
    public Object construct(Class klass, Object parent, int style, String name, Object contextElement)
        throws XSWTException 
    {
        Object result = getClassBuilder().constructControl(klass, parent, style);
        
        // If there's a name, this has to be a Widget, and set the name on it
        if (name != null && result instanceof Widget) {
            ((Widget) result).setData("Sweet_id", name); // Added Sweet_ to avoid naming conflicts
        }
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#construct(java.lang.Class, java.util.LinkedList)
     */
    public Object construct(Class valueType, LinkedList argList, Object contextElement) throws XSWTException {
        // If there were no arguments, construct and return the new object
        if (argList.size() < 1) {
            try {
                if (valueType.isArray())
                	throw new XSWTException("Class array must declare an instance, try to add x:p0", contextElement);
                else
                	return valueType.newInstance();
            } catch (Exception e) {
                throw new XSWTException("Unable to create a newInstance() of " + 
                    valueType.getName(), e, contextElement);
            }
        }
        if (valueType.isArray()) {
            Object value = parseData((String)argList.get(0), valueType);
            //Object result = Array.newInstance(valueType, value.length);
            return value;
        }
        // Try to match a constructor to the arguments we've got
        ConstructorInfo constructorInfo = getConstructorInfo(ClassDataParser.getObjectClass(valueType), argList);
        // Make sure we actually found an appropriate constructor
        if (constructorInfo.constructor == null || constructorInfo.args == null)
        	throw new XSWTException("Unable to locate a constructor for type " +
        			valueType.getName() +" with " + argList.size() + " parameters.", contextElement);
        
        // Construct and return the object
        try {
            return constructorInfo.constructor.newInstance(constructorInfo.args); 
        } catch (Exception e) {
            String paramTypeStr = "";
            if (constructorInfo.paramTypes != null) {
                paramTypeStr = "(" + constructorInfo.paramTypes[0].getName();
                for (int i = 1; i < constructorInfo.paramTypes.length; i++) {
                    paramTypeStr += ", " + constructorInfo.paramTypes[i].getName();
                }
                paramTypeStr += ")";
            }
            throw new XSWTException("Unable to invoke constructor with parameters of type: " + 
                paramTypeStr, e, contextElement);
        }
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#getClass(java.lang.Object)
     */
    public Class getClass(Object obj) throws XSWTException {
        // In this case we're constructing real objects, so just delegate to the object's
        // own getClass() method
        return obj.getClass();
    }

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#getProperty(java.lang.reflect.Method, java.lang.Object, java.lang.Object)
	 */
	public Object getProperty(Method getter, Object receiver, Object value, Object contextElement) throws XSWTException {
        try {
        	if (value == null) return getter.invoke(receiver, null);
            else return getter.invoke(receiver, new Object[] {value});
        } catch (Throwable t) {
            throw new XSWTException("Error calling getter " + getter.getName(), t, contextElement);
        }
	}


}
