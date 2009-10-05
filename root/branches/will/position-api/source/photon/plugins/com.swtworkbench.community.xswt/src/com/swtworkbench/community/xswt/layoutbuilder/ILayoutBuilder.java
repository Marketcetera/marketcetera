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
import java.lang.reflect.Method;
import java.util.LinkedList;

import com.swtworkbench.community.xswt.XSWTException;

/**
 * Class ILayoutBuilder. An interface for an object that can construct a SWT
 * layout given the appropriate information from an XSWT file parser.
 * <p>
 * 
 * @author daveo
 */
public interface ILayoutBuilder {
	/**
	 * Method construct. Construct a SWT object.
	 * 
	 * @param klass
	 *            SWT object's class
	 * @param parent
	 *            The parent object
	 * @param style
	 *            The style bit string in XSWT format or "" for SWT.NULL
	 * @return The result object
	 * 
	 * @throws XSWTException
	 */
	public Object construct(Class klass, Object parent, int style,
			String name, Object contextElement) throws XSWTException;

	/**
	 * Method construct. Construct an instance of valueType, using the strings
	 * in argList as arguments.
	 * 
	 * @param valueType
	 *            The type we need to construct
	 * @param argList
	 *            An array of strings containing the raw XSWT strings
	 *            representing the constructor arguments.
	 * @return the result object
	 * 
	 * @throws XSWTException
	 */
	public Object construct(Class valueType, LinkedList argList, Object contextElement)
			throws XSWTException;

	/**
	 * Method setProperty. Set a property value on an object.
	 * 
	 * @param propertyName
	 *            The name of the property in XSWT syntax
	 * @param receiver
	 *            The object on which the property should be set
	 * @param valueSource
	 *            The XSWT source string for the property value
	 * @return true if the property was successfully set; false otherwise
	 * 
	 * @throws XSWTException
	 */
	public boolean setProperty(String propertyName, Object receiver,
			String valueSource, Object contextElement) throws XSWTException;

	/**
	 * Method setProperty.
	 * 
	 * @param setter
	 * @param receiver
	 * @param value
	 * @throws XSWTException
	 */
	public void setProperty(Method setter, Object receiver, Object value, Object contextElement)
			throws XSWTException;

	/**
	 * Method getProperty.
	 * 
	 * @param getter
	 * @param receiver
	 * @param value
	 * @return Object
	 * @throws XSWTException
	 */
	public Object getProperty(Method getter, Object receiver, Object value, Object contextElement)
			throws XSWTException;

	/**
	 * Method setField. Set a field value on an object.
	 * 
	 * @param fieldName
	 *            The name of the field to set.
	 * @param receiver
	 *            The object on which the field should be set
	 * @param valueSource
	 *            The XSWT source string for the field value
	 * @return true if the field was successfully set; false otherwise
	 * 
	 * @throws XSWTException
	 */
	public boolean setField(String fieldName, Object receiver,
			String valueSource, Object contextElement) throws XSWTException;

	/**
	 * Method setField.
	 * 
	 * @param field
	 * @param receiver
	 * @param value
	 * @throws XSWTException
	 */
	public void setField(Field field, Object receiver, Object value, Object contextElement)
			throws XSWTException;

	/**
	 * Method resolveAttributeSetMethod. Return a Method object representing the
	 * set method referred to by an attribute property on the specified object.
	 * 
	 * @param obj
	 *            The object on which the method should be found
	 * @param methodName
	 *            The method name in XSWT syntax
	 * @param propertyType
	 *            The property's type or null if this isn't known
	 * @return The Method found or null if none found.
	 */
	public Method[] resolveAttributeSetMethod(Object obj, String methodName,
			Class propertyType);

	/**
	 * Returns a Method object representing the method referred to by an
	 * attribute property on the specified object.
	 * 
	 * @param obj
	 *            The object on which the method should be found
	 * @param methodName
	 *            The method name in XSWT syntax
	 * @param returnType
	 *            The return type or null.
	 * @return The Method found or null if none found.
	 */
	public Method resolveAttributeGetMethod(Object obj, String methodName);

	/**
	 * Method getClass. Returns an appropriate Class object for the given
	 * object. We can't just use Object.getClass() because some implementations
	 * of ILayoutBuilder may substitute their own stub objects in place of SWT
	 * and Java objects.
	 * 
	 * @param obj
	 *            The object or object stub whose Class is needed
	 * @return Either obj.getClass() or the Class the obj stub represents, as
	 *         appropriate
	 * 
	 * @throws XSWTException
	 */
	public Class getClass(Object obj) throws XSWTException;
	
	/**
	 * Given an object that has been constructed, return the actual object 
	 * that should be stored in the x:ID map in order for references to work 
	 * correctly.  Normally this is just obj, but in a GUI builder, a control
	 * may be wrapped in another object (for example, an invisible Composite).
	 * 
	 * @param obj The object to translate to the named object
	 * @return The object that should be associated with an ID
	 * @throws XSWTException
	 */
	public Object namedObject(Object obj) throws XSWTException;

}

