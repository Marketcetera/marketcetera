package org.marketcetera.core.position.impl;

import java.math.BigDecimal;

import org.hamcrest.Matcher;

/* $License$ */

/**
 * Utility matchers for BigDecimals.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
public class BigDecimalMatchers {
    public static Matcher<? super BigDecimal> comparesEqualTo(String value) {
        return OrderingComparison.comparesEqualTo(new BigDecimal(value));
    }

    public static Matcher<? super BigDecimal> comparesEqualTo(int value) {
        return OrderingComparison.comparesEqualTo(new BigDecimal(value));
    }
}
