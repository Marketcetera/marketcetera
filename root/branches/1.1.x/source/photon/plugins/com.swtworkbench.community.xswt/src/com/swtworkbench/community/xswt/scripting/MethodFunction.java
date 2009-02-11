package com.swtworkbench.community.xswt.scripting;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodFunction implements Function {

	private Method method;
	private Object object;
	
	public MethodFunction(Method method, Object object) {
		this.method = method;
		this.object = object;
	}
	public MethodFunction(Method method) {
		this(method, null);
	}

	public String getName() {
		return method.getName();
	}
	
	private Class[] argTypes;
	
	private Class[] argTypes() {
		if (argTypes == null) {
			argTypes = method.getParameterTypes();
		}
		return argTypes;
	}
	
	public int arity() {
		return argTypes().length;
	}

	public Class argumentType(int i) {
		return argTypes()[i];
	}

	public Object invoke(Object[] args) throws Exception {
		try {
			return method.invoke(object, args);
		} catch (InvocationTargetException e) {
			throw (Exception)e.getCause();
		}
	}
}
