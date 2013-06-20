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

package org.jquantlib.testsuite.lang;

import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jquantlib.QL;
import org.jquantlib.lang.exceptions.LibraryException;
import org.jquantlib.lang.reflect.TypeReference;
import org.junit.Test;

/**
 * @author Richard Gomes
 */
public class TypeReferenceTest {

    public TypeReferenceTest() {
        QL.info("::::: "+this.getClass().getSimpleName()+" :::::");
    }

    @Test
    public void testTypeReference() {
        final B b = new B();
        check(b);
    }

    /**
     * It's very important to notice that we need to create anonymous classes in order to make these tests pass.
     * <p>
     * The reason is that TypeToken and TypeReference make use of Class.getGenericSuperClass() which will
     * retrieve type information from the current caller instance.
     */
    @Test
    public void testTypeReference2() {
        final C c = new C<ArrayList, TreeMap, HashSet>() {}; // ANONYMOUS INSTANCE!
        check(c);
    }


    private void check(final A a) {
        final Object[] objs = a.getGenericClasses();
        if (objs[0].getClass() != ArrayList.class) {
            fail("Generic parameter should be " + ArrayList.class.getName());
        }
        if (objs[1].getClass() != TreeMap.class) {
            fail("Generic parameter should be " + TreeMap.class.getName());
        }
        if (objs[2].getClass() != HashSet.class) {
            fail("Generic parameter should be " + HashSet.class.getName());
        }

    }



    //
    // inner classes
    //

    private class A<L extends List, M extends Map, S extends Set> extends TypeReference {
        public Object[] getGenericClasses() {
            final Object[] objs = new Object[3];

            try {
                for (int i=0; i<3; i++) {
                    final Constructor<Object> c = getGenericParameterClass(i).getConstructor();
                    objs[i] = c.newInstance();
                }
            } catch (final Exception e) {
                throw new LibraryException(e);
            }
            return objs;
        }
    }

    private class B extends A<ArrayList, TreeMap, HashSet> { /* nothing */ }

    private class C<L extends List, M extends Map, S extends Set> extends A<L, M, S> { /* nothing */ }

}
