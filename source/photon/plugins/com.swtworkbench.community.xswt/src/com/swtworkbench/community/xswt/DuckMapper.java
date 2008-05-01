/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme - Initial implementation
 */
package com.swtworkbench.community.xswt;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Class DuckMapper.  Have you gotten tired of having to typecast the results of
 * reading the Map that XSWT returns?  Wouldn't it be nicer to just have an interface
 * with a getter method for each object in your XSWT map?  This class implements just
 * such a beast!<p>
 * 
 * Given an XSWT-generated Map and a Java interface containing getter methods for each
 * element in the Map, this class generates a dynamic proxy implementing the specified
 * Java interface that returns elements from the Map in a type-safe way. 
 * 
 * @author djo
 */
public class DuckMapper implements InvocationHandler {

    /**
     * Make the specified Map of SWT controls implement the specified interface 
     * containing getters for each control in the Map.
     * 
     * @param interfaceToImplement The Java interface to implement.
     * @param map The XSWT-generated Map.
     * @return An object implementing interfaceToImplement.
     */
    public static Object implement(Class interfaceToImplement, Map map) {
        return Proxy.newProxyInstance(interfaceToImplement.getClassLoader(), 
                new Class[] {interfaceToImplement}, new DuckMapper(map));
    }

    
    private Map map;

    private DuckMapper(Map map) {
        this.map = map;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        
        // Assume interface method is named get<ObjectName>
        Object result = map.get(method.getName().substring(3));
        if (result != null) {
            return result;
        }
        
        // We didn't find it, assume interface method the same as object name
        return map.get(method.getName());
    }

}
