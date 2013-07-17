package org.marketcetera.util.misc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import org.marketcetera.util.except.ExceptUtils;

/**
 * Utilities for reflection.
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class ReflectUtils
{

    // CLASS METHODS.

    /**
     * Auguments the given set with all superclasses and interfaces of
     * the given class, incl. the class itself.
     *
     * @param result The set.
     * @param c The class.
     */

    private static void getAllClasses
        (Set<Class<?>> result,
         Class<?> c)
    {
        if (c==null) {
            return;
        }
        result.add(c);
        getAllClasses(result,c.getSuperclass());
        for (Class<?> i:c.getInterfaces()) {
            getAllClasses(result,i);
        }
    }

    /**
     * Returns all superclasses and interfaces of the given class,
     * incl. the class itself.
     *
     * @param c The class.
     *
     * @return The classes and interfaces.
     */

    public static Class<?>[] getAllClasses
        (Class<?> c)
    {
        Set<Class<?>> result=new HashSet<Class<?>>();
        getAllClasses(result,c);
        return result.toArray(new Class<?>[0]);
    }

    /**
     * Returns all fields of the given class, incl. those in the class
     * itself, its superclasses and interfaces.
     *
     * @param c The class.
     *
     * @return The fields.
     */

    public static Field[] getAllFields
        (Class<?> c)
    {
        Set<Field> result=new HashSet<Field>();
        for (Class<?> s:getAllClasses(c)) {
            for (Field f:s.getDeclaredFields()) {
                result.add(f);
            }
        }
        return result.toArray(new Field[0]);
    }

    /**
     * Returns an instance of the class by the given name, using its
     * constructor with the given parameter types, and invoked using
     * the given parameters. It interrupts the current thread if the
     * constructor throws an interruption exception per {@link
     * ExceptUtils#isInterruptException(Throwable)}.
     *
     * @param cName The class name.
     * @param paramTypes The constructor parameter types.
     * @param paramValues The constructor parameters.
     *
     * @return The instance.
     *
     * @throws ClassNotFoundException Thrown if a reflection failure
     * occurs.
     * @throws IllegalAccessException Thrown if a reflection failure
     * occurs.
     * @throws InstantiationException Thrown if a reflection failure
     * occurs.
     * @throws NoSuchMethodException Thrown if a reflection failure
     * occurs.
     * @throws InvocationTargetException Thrown if a reflection
     * failure occurs.
     */

    public static Object getInstance
        (String cName,
         Class<?>[] paramTypes,
         Object[] paramValues)
        throws ClassNotFoundException,
               IllegalAccessException,
               InstantiationException,
               NoSuchMethodException,
               InvocationTargetException
    {
        try {
            return Class.forName(cName).getConstructor(paramTypes).
                newInstance(paramValues);
        } catch (InvocationTargetException ex) {
            ExceptUtils.interrupt(ex.getCause());
            throw ex;
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private ReflectUtils() {}
}
