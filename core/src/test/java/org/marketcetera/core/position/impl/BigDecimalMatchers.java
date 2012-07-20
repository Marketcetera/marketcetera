package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.nullValue;

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
        if (value == null) {
            return nullValue();
        } else {
            return OrderingComparison.comparesEqualTo(new BigDecimal(value));
        }
    }

    public static Matcher<? super BigDecimal> comparesEqualTo(Integer value) {
        if (value == null) {
            return nullValue();
        } else {
            return OrderingComparison.comparesEqualTo(new BigDecimal(value));
        }
    }
}
