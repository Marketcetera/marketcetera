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

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jquantlib.QL;


/**
 * This class provides other classes the ability to retrieve runtime type information
 * from generic parametric types specified at creation time.
 * <p>
 * A typical use case would be an instance which is interested on a generic parameter which directs it
 * which type of data can be accepted. Below you can see a typical creation use case:
 * <pre>
 * // Please read note below regarding usage of anonymous classes and derived classes.
 * Map<String,Data> smap = new MyHashMap<String,Data>() {}; // anonymous instance!
 * Map<Double,Data> dmap = new MyHashMap<Double,Data>() {}; // anonymous instance!
 * </pre>
 * ... and below you can see a typical retrieval of type information inside the class of our interest:
 * <pre>
 * class MyHashMap<K,V> extends HashMap<K,V> {
 *
 *    Class<?> final keyClass;
 *    Class<?> final valClass;
 *
 *    MyHashMap() {
 *        // retrieves first generic parameter
 *        keyClass = TypeToken.getClazz(this.getClass());
 *        // retrieves second generic parameter
 *        valClass = TypeToken.getClazz(this.getClass(), 1);
 *    }
 *
 *    public put(K key, V val) {
 *        if (!keyClass.isAssignableFrom(key)) throw new ClassCastException("invalid key");
 *        if (!valClass.isAssignableFrom(val)) throw new ClassCastException("invalid value");
 *    }
 * </pre>
 *
 * @note It's very important to notice that we it is required to create <b>anonymous instances
 *       or instances of extended class of the class you are interested</b> to parameterise.
 *       The reason is that TypeToken and TypeReference use the tricky Class.getGenericSuperClass(),
 *       which retrieves type information from the super class of the current caller instance.
 *
 * @see TypeReference
 *
 * @see <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
 * @see <a href="http://gafter.blogspot.com/2007/05/limitation-of-super-type-tokens.html">A Limitation of Super Type Tokens</a>
 * @see <a href="http://java.sun.com/j2se/1.5/pdf/generics-tutorial.pdf">Generics Tutorial</a>
 * @see <a href="http://www.jquantlib.org/index.php/Using_TypeTokens_to_retrieve_generic_parameters">Using TypeTokens to retrieve generic parameters</a>
 *
 * @author Richard Gomes
 */
//FIXME :: rename to SuperTypeToken
public class TypeToken {

    static public Type getType(final Class<?> klass) {
        return getType(klass, 0);
    }

    static public Type getType(final Class<?> klass, final int pos) {
        final Type superclass = klass.getGenericSuperclass();
        QL.require(!(superclass instanceof Class) , ReflectConstants.SHOULD_BE_ANONYMOUS_OR_EXTENDED); // QA:[RG]::verified
        final Type[] types = ((ParameterizedType) superclass).getActualTypeArguments();
        QL.require(pos < types.length , ReflectConstants.MISSING_GENERIC_PARAMETER_TYPE); // QA:[RG]::verified
        return types[pos];
    }

    static public Class<?> getClazz(final Class<?> klass) {
        return getClazz(klass, 0);
    }

    static public Class<?> getClazz(final Class<?> klass, final int pos) {
        final Type type = getType(klass, pos);
        final Class<?> clazz = (type instanceof Class<?>) ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType();
        QL.require(((clazz.getModifiers() & Modifier.ABSTRACT) == 0) , ReflectConstants.GENERIC_PARAMETER_MUST_BE_CONCRETE_CLASS); // QA:[RG]::verified
        return clazz;
    }

}
