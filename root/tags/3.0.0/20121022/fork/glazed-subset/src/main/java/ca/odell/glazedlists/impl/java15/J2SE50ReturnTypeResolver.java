/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.impl.java15;

import java.lang.reflect.Method;

import ca.odell.glazedlists.impl.reflect.ReturnTypeResolver;

/**
 * An implementation of {@link ca.odell.glazedlists.impl.reflect.ReturnTypeResolver} that adapt's Google's
 * {@link ca.odell.glazedlists.impl.java15.TypeLiteral generic type resolver}.
 *
 */
public class J2SE50ReturnTypeResolver implements ReturnTypeResolver {
    public Class<?> getReturnType(Class<?> clazz, Method method) {
        return new TypeLiteral<Object>(clazz).getReturnType(method).getRawType();
    }

    public Class<?> getFirstParameterType(Class<?> clazz, Method method) {
        return new TypeLiteral<Object>(clazz).getParameterTypes(method).get(0).getRawType();
    }
}