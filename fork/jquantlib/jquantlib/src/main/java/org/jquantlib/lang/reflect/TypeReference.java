/*
 Copyright (C) 2007 Richard Gomes

 This source code is release under the BSD License.

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.lang.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jquantlib.QL;

/**
 * This class provides derived classes the ability to retrieve runtime type information
 * from generic parametric types specified at creation time.
 * <p>
 * A typical use case would be an instance which is interested on a generic parameter which directs it
 * which type of data can be accepted. Below you can see a typical creation use case:
 * <pre>
 * MyClass<String,Data> smap = new MyClass<String,Data>() {};
 * MyClass<Double,Data> dmap = new MyClass<Double,Data>() {};
 * </pre>
 * ... and below you can see a typical retrieval of type information inside the class of our interest:
 * <pre>
 * class Myclass<K,V> extends TypeReference {
 *
 *    Class<?> final keyClass;
 *    Class<?> final valClass;
 *
 *    MyClass() {
 *        // retrieves first actual generic parameter
 *        keyClass = getGenericParameterClass();
 *        // retrieves second actual generic parameter
 *        valClass = getGenericParameterClass(1);
 *    }
 *
 *    public put(K key, V val) {
 *        if (!keyClass.isAssignableFrom(key)) throw new ClassCastException("invalid key");
 *        if (!valClass.isAssignableFrom(val)) throw new ClassCastException("invalid value");
 *    }
 * </pre>
 *
 * @note It's very important to notice that we <b>it is required</b> to create anonymous instances
 *       or at least instances of an extended class of the class you are interested to parameterize.
 *       The reason is that TypeToken and TypeReference uses the tricky Class.getGenericSuperClass(),
 *       which retrieves type information from the super class of the current caller instance.
 *
 * @see TypeToken
 * @see TypeReference
 * @see TypeTokenTree
 *
 * @see <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
 * @see <a href="http://gafter.blogspot.com/2007/05/limitation-of-super-type-tokens.html">A Limitation of Super Type Tokens</a>
 * @see <a href="http://java.sun.com/j2se/1.5/pdf/generics-tutorial.pdf">Generics Tutorial</a>
 * @see <a href="http://www.jquantlib.org/index.php/Using_TypeTokens_to_retrieve_generic_parameters">Using TypeTokens to retrieve generic parameters</a>
 *
 * @author Richard Gomes
 */
// TODO: code review :: license, class comments, comments for access modifiers, comments for @Override
// TODO: review convenience of this class and its methods
public abstract class TypeReference<T> {

    private final Type[] types;
    private volatile Constructor<?> constructor;

    protected TypeReference() {
        final Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new IllegalArgumentException("Class should be anonymous or extended from a generic class");
        }
        this.types = ((ParameterizedType) superclass).getActualTypeArguments();
    }

    /**
     * Gets the referenced type of the first generic parameter
     */
    public Type getGenericType() {
        return getGenericType(0);
    }

    /**
     * Gets the referenced type of the n-th generic parameter
     */
    public Type getGenericType(final int n) {
        return this.types[n];
    }

    /**
     * Gets the referenced Class of the first generic parameter
     */
    public Class<?> getGenericParameterClass() {
        return getGenericParameterClass(0);
    }

    /**
     * Gets the referenced Class of the n-th generic parameter
     */
    public Class<?> getGenericParameterClass(final int n) {
        QL.require(n < types.length , "Missing parameter"); // QA:[RG]::verified // TODO: message
        final Type type = types[n];
        final Class<?> clazz = (type instanceof Class<?>) ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType();
        QL.require(((clazz.getModifiers() & Modifier.ABSTRACT) == 0) , "generic parameter must be a concrete class"); // QA:[RG]::verified // TODO: message
        return clazz;
    }

    /**
     * Instantiates a new instance of {@code T} using the first generic parameter
     */
    @SuppressWarnings("unchecked")
    public T newGenericInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return newGenericInstance(0);
    }

    /**
     * Instantiates a new instance of {@code T} using the n-th generic parameter
     */
    @SuppressWarnings("unchecked")
    public T newGenericInstance(final int n) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (constructor == null) {
            constructor = getGenericParameterClass(n).getConstructor();
        }
        return (T) constructor.newInstance();
    }

    /**
     * Instantiates a new instance of {@code T} using the n-th generic parameter
     */
    @SuppressWarnings("unchecked")
    public T newGenericInstance(final int n, final Object ... objects) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final Type type = types[n];
        final Class<?> rawType = type instanceof Class<?> ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType();

        final Class<?>[] types = new Class[objects.length];
        for (int i=0; i<objects.length; i++) {
            types[i] = objects[i].getClass();
        }
        constructor = rawType.getConstructor(types);

        return (T) constructor.newInstance(objects);
    }

    /**
     * function identifies type of the typeNum -th generics in the paramNum parameter
     * @param paramNum parameter number
     * @param typeNum generics type in the paramNum parameter
     * @return Type of the
     */
    public Type getActualTypeParameters(final int paramNum, final int typeNum)
    {
        return ((ParameterizedType)getGenericType(paramNum)).getActualTypeArguments()[typeNum];
    }


    @Override
    public boolean equals(final Object o) {
        if (o instanceof TypeReference) {
            final int len = ((TypeReference)o).types.length;
            if (len!=types.length) {
                return false;
            }
            for (int i=0; i<types.length; i++) {
                if (! ((TypeReference) o).types[i].equals(this.types[i]) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (final Type type : types) {
            hash = (hash << 1) + type.hashCode();
        }
        return hash;
    }

}
