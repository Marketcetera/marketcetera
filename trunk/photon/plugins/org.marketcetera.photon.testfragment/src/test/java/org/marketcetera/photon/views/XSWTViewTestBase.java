package org.marketcetera.photon.views;

import java.lang.reflect.Method;

import com.swtworkbench.community.xswt.XSWTException;


public abstract class XSWTViewTestBase extends ViewTestBase {

	private final Class<?> interfaceClass;

	public XSWTViewTestBase(Class<?> interfaceClass, String name) {
		super(name);
		this.interfaceClass = interfaceClass;
	}
	
	public void testInterfaceGetters() throws Exception {
		assertTrue(interfaceClass.isInterface());
		Method[] methods = interfaceClass.getDeclaredMethods();
		Object xswt = instantiateXSWT();
		for (Method method : methods) {
			if (method.getName().startsWith("get") && 
					method.getParameterTypes().length == 0){
				Object result = method.invoke(xswt);
				assertNotNull("Method "+method.getName()+" returned null", result);
			}
		}
	}
	
	public abstract Object instantiateXSWT() throws XSWTException;
	
}
