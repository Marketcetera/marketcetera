package com.swtworkbench.community.xswt.layoutbuilder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionSupport {

	public static Method[] resolvePropertySetter(Class klass, String[] names, Class propertyType) {
		List results = new ArrayList();
        try {
            Method[] methods = klass.getMethods();
            for (int method = 0; method < methods.length; method++) {
            	for (int name = 0; name < names.length; name++) {
            		if (names[name].equals(methods[method].getName()) && methods[method].getParameterTypes().length == 1) {
            			// If we don't know the property type, assume we've found it.
            			if (propertyType == null) {
            				results.add(methods[method]);
            				continue;
            			}
            			if (methods[method].getParameterTypes()[0].isAssignableFrom(propertyType)) {
            				return new Method[] { methods[method] };
            			}
            		}
				}
            }
        } catch (Exception e) {
        }
        return results.size() == 0 ? null : (Method[]) results.toArray(new Method[results.size()]);
    }

    /**
     * Method getSetMethodNames.  Get an array of possible set method names
     * given a root name.
     * 
     * @param nodeName The root name
     * @return A String[] of possible set method names
     */

    // these are reused with the same prefix each time
    private static StringBuffer setBuffer = new StringBuffer("set");
    private static StringBuffer addBuffer = new StringBuffer("add");

    public static String[] getSetMethodNames(String nodeName) {
        String[] result = new String[3];
        
        // setXXXX...
        setBuffer.setLength(3);
        setBuffer.append(nodeName.substring(0, 1).toUpperCase());
        //setterBuf.append(nodeName.substring(1, nodeName.length()));
        setBuffer.append(nodeName.substring(1));
        result[0] = setBuffer.toString();
        
        // It's already a literal method name
        result[1] = nodeName;

        // addXXXX...
        addBuffer.setLength(3);
        addBuffer.append(nodeName.substring(0, 1).toUpperCase());
        //setterBuf.append(nodeName.substring(1, nodeName.length()));
        addBuffer.append(nodeName.substring(1));
        result[2] = addBuffer.toString();
        
        return result;
    }

	/* (non-Javadoc)
	 * @see com.swtworkbench.community.xswt.layoutbuilder.ILayoutBuilder#resolveAttributeGetMethod(java.lang.Object, java.lang.String, java.lang.Class)
	 */
	public static Method resolveAttributeGetMethod(Class klass, String methodName) {
        Method[] methods = klass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methodName.equals(methods[i].getName())) {
            	// methods[i].getReturnType().getName();
                return methods[i];
            }
        }
        return null;
	}
	
	/**
	 * Method getWidgetName. Get's the widget's name if the current node
	 * specifies an ID attribute.
	 * 
	 * @param attribute
	 *            The attribute to check
	 * @return The name String or null if not found
	 */
	private static Class[] getParamTypes(Object[] args) {
		Class[] paramTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			paramTypes[i] = args[i].getClass();
		}
		return paramTypes;
	}

	/**
	 * Method invokei. Invoke some named method on some object. Ignore any
	 * errors that occur.
	 * 
	 * @param receiver
	 *            The object receiving the message
	 * @param method
	 *            The method or message name
	 * @param args
	 *            The arguments
	 * @return The result object or null if there was no result or there was an
	 *         error.
	 */
	public static Object invokei(Object receiver, String method, Object[] args) {
		Object result = null;
		try {
			Method methodCaller = receiver.getClass().getMethod(method, getParamTypes(args));
			result = methodCaller.invoke(receiver, args);
		} catch (Exception e) {
		}
		return result;
	}
}
