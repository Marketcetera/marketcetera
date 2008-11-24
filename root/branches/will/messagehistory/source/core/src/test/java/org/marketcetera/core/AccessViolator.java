package org.marketcetera.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AccessViolator {
    Class violatedClass;

    public AccessViolator(Class violatedClass) {
        this.violatedClass = violatedClass;
    }

    public Object getField(String fieldName, Object reference) throws NoSuchFieldException, IllegalAccessException {
        Field theField = violatedClass.getDeclaredField(fieldName);
        theField.setAccessible(true);
        return theField.get(reference);
    }

    public Object invokeMethod(String methodName, Object reference, Object ... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class<?> [] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++){
            types[i] = args[i].getClass();
        }
        return invokeMethod(methodName, reference, args, types);
    }

    public Object invokeMethod(String methodName, Object reference, Object [] args, Class<?> [] classes)
    	throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method theMethod = violatedClass.getDeclaredMethod(methodName, classes);
		theMethod.setAccessible(true);
		return theMethod.invoke(reference, args);
	}
    

    /** Sets the speicified field to the passed-in value */
    public void setField(String fieldName, Object reference, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field theField = violatedClass.getDeclaredField(fieldName);
        theField.setAccessible(true);
        theField.set(reference, value);
    }
}
