/*
 * ObjectLab, http://www.objectlab.co.uk/open is sponsoring the ObjectLab Kit.
 *
 * Based in London, we are world leaders in the design and development
 * of bespoke applications for the securities financing markets.
 *
 * <a href="http://www.objectlab.co.uk/open">Click here to learn more</a>
 *           ___  _     _           _   _          _
 *          / _ \| |__ (_) ___  ___| |_| |    __ _| |__
 *         | | | | '_ \| |/ _ \/ __| __| |   / _` | '_ \
 *         | |_| | |_) | |  __/ (__| |_| |__| (_| | |_) |
 *          \___/|_.__// |\___|\___|\__|_____\__,_|_.__/
 *                   |__/
 *
 *                     www.ObjectLab.co.uk
 *
 * $Id: AbstractDateCalculator.java 309 2010-03-23 21:01:49Z marchy $
 *
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.marketcetera.core;

import java.math.BigDecimal;

public final class ObjectUtil {
    private ObjectUtil() {
    }

    /**
     * Return true if i1 equals (according to {@link Integer#equals(Object)}) 12.
     * Also returns true if 11 is null and 12 is null! Is safe on either 11 or 12 being null.
     */
    public static boolean equalsValue(final Integer i1, final Integer i2) {
        if (i1 == null) {
            return i2 == null;
        }
        return i1.equals(i2);
    }

    /**
     * Return true if bd1 has the same value (according to
     * {@link BigDecimalUtil#isSameValue(BigDecimal, BigDecimal)}, which takes care of different scales)
     * as bd2.
     * Also returns true if bd1 is null and bd2 is null! Is safe on either bd1 or bd2 being null.
     */
    public static boolean equalsValue(final BigDecimal bd1, final BigDecimal bd2) {
        if (bd1 == null) {
            return bd2 == null;
        }
        return BigDecimalUtil.isSameValue(bd1, bd2);
    }

    /**
     * Return true if o1 equals (according to {@link Object#equals(Object)}) o2.
     * Also returns true if o1 is null and o2 is null! Is safe on either o1 or o2 being null.
     */
    public static boolean equalsValue(final Object o1, final Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }

    /**
     * Return true if o1 equals (according to {@link Object#equals(Object)} any of the given objects.
     * Also returns true if o1 is null and any of the given objects is null as well!
     */
    public static boolean equalsAny(final Object o1, final Object... o2s) {
        for (final Object o2 : o2s) {
            if (o1 == null) {
                if (o2 == null) {
                    return true;
                }
                continue;
            }
            // o1 != null
            if (o1.equals(o2)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if o1 equals (according to {@link Object#equals(Object)} ALL of the given objects.
     */
    public static boolean equalsAll(final Object o1, final Object... o2s) {
        for (final Object o2 : o2s) {
            if (o1 == null) {
                if (o2 != null) {
                    return false;
                }
                continue;
            }
            // o1 != null
            if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if o1 does NOT equal (according to {@link Object#equals(Object)} any of the given objects.
     * Also returns false if o1 is null and any of the given objects is null as well!
     */
    public static boolean notEqualsAny(final Object o1, final Object... o2s) {
        return !equalsAny(o1, o2s);
    }

    /**
     * Return true if any of the given objects are null.
     */
    public static boolean anyNull(final Object... o1s) {
        for (final Object o1 : o1s) {
            if (o1 == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if ALL of the given objects are null.
     */
    public static boolean allNull(final Object... o1s) {
        for (final Object o1 : o1s) {
            if (o1 != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if at least one of the given objects is not null.
     */
    public static boolean atLeastOneNotNull(final Object... o1s) {
        for (final Object o1 : o1s) {
            if (o1 != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return true if NONE of the given objects are null.
     */
    public static boolean noneNull(final Object... o1s) {
        return !anyNull(o1s);
    }
}
