package com.swtworkbench.community.xswt.dataparser.parsers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.swtworkbench.community.xswt.XSWT;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class ValueOfDataParser extends NonDisposableDataParser {

	private static Map parseMethods = new HashMap();
	
	private static Class[] argClasses = {String.class};
	private static Object[] args = new Object[1];
	
	public static Method getParseMethod(Class klass) {
		Object o = parseMethods.get(klass);
		if (o instanceof Method) {
			return (Method)o;
		} else if (o != null) {
			return null;
		}
    	Method m = null;
		try {
			m = ClassDataParser.getObjectClass(klass).getMethod("valueOf", argClasses);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
    	if (m == null) {
    		try {
    			String name = klass.getName();
    			int pos = name.lastIndexOf('.');
    			if (pos >= 0) {
    				name = name.substring(pos + 1);
    			}
    			m = ClassDataParser.getObjectClass(klass).getMethod("parse" + XSWT.upperCaseFirstLetter(name), argClasses);
    		} catch (SecurityException e) {
    		} catch (NoSuchMethodException e) {
    		}
    	}
    	if (m != null) {
    		parseMethods.put(klass, m);
    	}
    	return m;
	}
	
	private static boolean hasParseMethod(Class klass) {
		return getParseMethod(klass) != null;
	}

	public static Constructor getConstructor(Class klass) {
		Object o = parseMethods.get(klass);
		if (o instanceof Constructor) {
			return (Constructor)o;
		} else if (o != null) {
			return null;
		}
		Constructor cons = null;
		try {
			cons = ClassDataParser.getObjectClass(klass).getConstructor(argClasses);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
    	if (cons != null) {
    		parseMethods.put(klass, cons);
    	}
    	return cons;
	}

	private static boolean hasConstructor(Class klass) {
		return getConstructor(klass) != null;
	}

	public static boolean supportsClass(Class klass) {
		return hasParseMethod(klass) || hasConstructor(klass);
	}
	
    public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
    	args[0] = source;
    	try {
    		Method m = getParseMethod(klass);
    		if (m != null) {
				return m.invoke(null, args);
			} else if (klass.getName().startsWith("java.")) {
	    		Constructor cons = getConstructor(klass);
	    		if (cons != null) {
	    			return cons.newInstance(args);
	    		}
			}
    	}
		catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (InstantiationException e) {
		}
    	return null;
    }
}
