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

import org.jquantlib.QL;
import org.jquantlib.lang.reflect.TypeToken;
import org.junit.Test;

/**
 * @author Richard Gomes
 */
public class TypeTokenTest {

    public TypeTokenTest() {
        QL.info("::::: "+this.getClass().getSimpleName()+" :::::");
    }

    @Test
    public void testTypeToken() {
        final C c = new C();
        if (c.getClazz() != Double.class) {
            fail("Object 'c' should be java.lang.Double");
        }

        final D d = new D();
        if (d.getClazz() != Integer.class) {
            fail("Object 'd' should be java.lang.Integer");
        }
    }


    /**
     * It's very important to notice that we need to create anonymous classes in order to make these tests pass.
     * <p>
     * The reason is that TypeToken and TypeReference make use of Class.getGenericSuperClass() which will
     * retrieve type information from the current caller instance.
     */
    @Test
    public void testTypeToken2() {
        final K k1 = new K<java.lang.Double>() {}; // ANONYMOUS INSTANCE!
        if (k1.getClazz() != Double.class) {
            fail("Object 'k1' should be java.lang.Double");
        }

        final K k2 = new K<java.lang.Integer>() {}; // ANONYMOUS INSTANCE!
        if (k2.getClazz() != Integer.class) {
            fail("Object 'k2' should be java.lang.Integer");
        }

    }

    //
    // inner classes
    //

    private class B<T extends Number> {
        public Class<?> getClazz() {
            return TypeToken.getClazz(this.getClass());
        }
    }

    private class C extends B<java.lang.Double> {  }

    private class D extends B<java.lang.Integer> {  }

    private class K<T extends Number> extends B<T> {  }

}
