package com.swtworkbench.community.xswt.dom;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

import com.swtworkbench.community.xswt.ClassBuilder;
import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.layoutbuilder.LayoutBuilder;
import com.swtworkbench.community.xswt.layoutbuilder.ObjectStub;

public class DOMLayoutBuilder extends LayoutBuilder {

	public DOMLayoutBuilder(XSWT xswt) {
		super(xswt);
	}

	public Class getClass(Object obj) throws XSWTException {
        ObjectStub stub = (ObjectStub)obj;
        return ClassBuilder.getDefault().getClass(stub.className);
	}

	public Object construct(Class klass, Object parent, int style,
			String name, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object construct(Class valueType, LinkedList argList, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setProperty(String propertyName, Object receiver,
			String valueSource, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setProperty(Method setter, Object receiver, Object value, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub

	}

	public Object getProperty(Method getter, Object receiver, Object value, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean setField(String fieldName, Object receiver,
			String valueSource, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setField(Field field, Object receiver, Object value, Object contextElement) throws XSWTException {
		// TODO Auto-generated method stub
	}

}
