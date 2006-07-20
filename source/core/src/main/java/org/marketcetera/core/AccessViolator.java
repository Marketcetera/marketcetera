package org.marketcetera.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
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
        Class [] types = new Class[args.length];
        for (int i = 0; i < args.length; i++){
            types[i] = args[i].getClass();
        }
        Method theMethod = violatedClass.getDeclaredMethod(methodName, types);
        theMethod.setAccessible(true);
        return theMethod.invoke(reference, args);
    }

}
