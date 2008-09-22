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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.swtworkbench.community.xswt.ClassBuilder;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.codegen.CodeGenerator;



/**
 * Class SWTSourceBuilder.  An XSWT LayoutBuilder that generates Java source code
 * rather than building an actual SWT layout.  When the XSWT file has been parsed,
 * the generated source code is in "source" and can be accessed via the toString()
 * method on SWTSourceBuilder.
 * 
 * @author daveo
 */
public class SWTSourceBuilder extends LayoutBuilder {
	
	public SWTSourceBuilder(XSWT xswt) {
		super(xswt);
	}

    // The generated source code
    private StringBuffer source = new StringBuffer();
    
    /**
     * The indent string.  Defaults to four spaces.
     */
    public static String INDENT = "    ";
    
    protected void indent() {
        source.append(INDENT);
    }
    
    protected void emit(String source) {
        this.source.append(source);
    }
    
    protected void newline() {
        source.append("\n");
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return source.toString();
    }
    
    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#construct(java.lang.Class, java.util.LinkedList)
     */
    public Object construct(Class valueType, LinkedList argList, Object contextElement)
        throws XSWTException 
    {
        ObjectStub stub = new ObjectStub(valueType.getName());
        indent();
        emit(stub.className);
        emit(" ");
        emit(stub.sourceName);
        emit(" = new ");
        emit(stub.className);
        emit("(");
        
        ConstructorInfo constructorInfo = getConstructorInfo(valueType, argList);
        int i=0;
        for (Iterator current = argList.iterator(); current.hasNext();) {
            String xswtSource = (String)current.next();
            Object value = constructorInfo.args[i];
            if (i > 0) emit (", ");
            emit(CodeGenerator.getDefault().getCode(value, xswtSource)); 
            ++i;
        }
        
        emit(");");
        newline();
        return stub;
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#construct(java.lang.String, org.eclipse.swt.widgets.Widget, java.lang.String)
     */
    public Object construct(Class klass, Object parent, int style, String name, Object contextElement)
        throws XSWTException 
    {
        ObjectStub stub = new ObjectStub(klass.getName(), name);
        ObjectStub parentStub;
        if (parent instanceof ObjectStub)
            parentStub = (ObjectStub) parent;
        else
            parentStub = new ObjectStub("Composite", "xswtParent");
            
        indent();
        emit(stub.className);
        emit(" ");
        emit(stub.sourceName);
        emit(" = new ");
        emit(stub.className);
        emit("(");
        emit(parentStub.sourceName);
        emit(", ");
        emit(Integer.toString(style));
        // emit(getStyle(s));
        emit(");");
        newline();
        return stub;
    }

    /**
     * Method getStyle.  
     * @param style
     * @return
     */
    private String getStyle(String style) {
        if (style == "" || style == null) 
            return "SWT.NULL";
            
        StringBuffer result = new StringBuffer();
        StringTokenizer bits = new StringTokenizer(style, " |");
        int i=0;
        while (bits.hasMoreTokens()) {
            if (i > 0)
                result.append(" | ");
            String bit = bits.nextToken();
            if (bit.indexOf(".") == -1) {
                result.append("SWT.");
            }
            result.append(bit);
            ++i;
        }
        
        return result.toString();
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setField(java.lang.reflect.Field, java.lang.Object, java.lang.Object)
     */
    public void setField(Field field, Object receiver, Object value, Object contextElement)
        throws XSWTException 
    {
        ObjectStub stub = (ObjectStub)receiver;
        indent();
        emit(stub.sourceName);
        emit(".");
        emit(field.getName());
        emit(" = ");
        emit(CodeGenerator.getDefault().getCode(value, ""));
        emit(";");
        newline();
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setField(java.lang.String, java.lang.Object, java.lang.String)
     */
    public boolean setField(
        String fieldName,
        Object receiver,
        String valueSource, Object contextElement)
        throws XSWTException 
    {
        try {
            Class receiverClass = getClass(receiver);
            Field field = receiverClass.getField(fieldName);
            Object value = parseData(valueSource, field.getType());

            ObjectStub stub = (ObjectStub)receiver;
            indent();
            emit(stub.sourceName);
            emit(".");
            emit(field.getName());
            emit(" = ");
            emit(CodeGenerator.getDefault().getCode(value, valueSource));
            emit(";");
            newline();

            return true;
        } catch (Exception e) {
            throw new XSWTException("Unable to set field " + fieldName, e, contextElement);
        }
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setProperty(java.lang.reflect.Method, java.lang.Object, java.lang.Object)
     */
    public void setProperty(Method setter, Object receiver, Object value, Object contextElement)
        throws XSWTException 
    {
        ObjectStub stub = (ObjectStub)receiver;
        indent();
        emit(stub.sourceName);
        emit(".");
        emit(setter.getName());
        emit("(");
        emit(CodeGenerator.getDefault().getCode(value, ""));
        emit(");");
        newline();
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#setProperty(java.lang.String, java.lang.Object, java.lang.String)
     */
    public boolean setProperty(
        String propertyName,
        Object receiver,
        String valueSource, Object contextElement)
        throws XSWTException 
    {
        try {
            Method[] setMethods = resolveAttributeSetMethod(receiver, propertyName, null);
            if (setMethods == null)
                return false;
            Method setMethod = setMethods[0];
            Object value =
                parseData(
                    valueSource,
                    setMethod.getParameterTypes()[0]);

            ObjectStub stub = (ObjectStub)receiver;
            indent();
            emit(stub.sourceName);
            emit(".");
            emit(setMethod.getName());
            emit("(");
            emit(CodeGenerator.getDefault().getCode(value, valueSource));
            emit(");");
            newline();

            return true;
        } catch (Exception e) {
            throw new XSWTException("Unable to set property " + propertyName, e, contextElement);
        }
    }
    
    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#getClass(java.lang.Object)
     */
    public Class getClass(Object obj) throws XSWTException {
        ObjectStub stub = (ObjectStub)obj;
        // FIXME: Shouldn't hard-code ClassBuilder reference here...
        //
        // Using a static ClassBuilder means that imports are cached
        // between runs.  There also could be threading issues...
        return ClassBuilder.getDefault().getClass(stub.className);
    }

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#getProperty(java.lang.reflect.Method, java.lang.Object, java.lang.Object)
	 */
	public Object getProperty(Method getter, Object receiver, Object value, Object contextElement) throws XSWTException {
        try {
            ObjectStub stub = (ObjectStub)receiver;
            indent();
            ObjectStub stub2 = new ObjectStub(getter.getReturnType().getName());
            emit(getter.getReturnType().getName());
            emit(" " + stub2.sourceName + " = ");
            emit(stub.sourceName);
            emit(".");
            emit(getter.getName());
            emit(" ( ");
            // TODO: How to present value's code? AT present value is always null
            if (value!=null) emit(value.toString());
            emit(");");
            newline();
            return stub2;
        } catch (Exception e) {
            throw new XSWTException("Unable to get " + getter.getName() + " property: ", e, contextElement);
        }
	}
}
