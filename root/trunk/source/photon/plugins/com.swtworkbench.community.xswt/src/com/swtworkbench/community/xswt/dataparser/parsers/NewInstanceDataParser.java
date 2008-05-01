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

public class NewInstanceDataParser extends NonDisposableDataParser {

	private static Map constructors = new HashMap();
	
	private static Class[] argClasses = {String.class};
	private static Object[] args = new Object[1];
	
	public static Constructor getConstructor(Class klass) {
		Constructor cons = (Constructor)constructors.get(klass);
    	if (cons != null) {
    		return cons;
    	}
    	if (cons == null) {
    		try {
				cons = ClassDataParser.getObjectClass(klass).getConstructor(argClasses);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			}
    	}
    	if (cons == null) {
    		Constructor[] conses = klass.getConstructors();
    		for (int i = 0; i < conses.length; i++) {
    			if (conses[i].getParameterTypes().length == 1) {
    				cons = conses[i];
    				break;
    			}
    		}
    	}
    	if (cons != null) {
    		constructors.put(klass, cons);
    	}
    	return cons;
	}
	
	public static boolean hasConstructor(Class klass) {
		return getConstructor(klass) != null;
	}
	
    public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
    	Constructor cons = getConstructor(klass);
    	if (cons != null) {
    		args[0] = context.parse(source, cons.getParameterTypes()[0]);
    		try {
				return cons.newInstance(args);
			} catch (InstantiationException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
    	}
    	return null;
    }
}
