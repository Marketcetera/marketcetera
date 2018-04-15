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
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;

/**
 * @author Benoit Xhenseval
 *
 */
public final class BigDecimalUtil {
    private static final double ROUNDING_UP_FLOAT = 0.5d;
    private static final int MAX_SCALE_FOR_INVERSE = 20;
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private BigDecimalUtil() {
    }

    /**
     * Convenience method to create a BigDecimal with a String, can be statically imported.
     * @param val a string representing the BigDecimal
     * @return the new BigDecimal
     */
    public static BigDecimal bd(final String val) {
        return new BigDecimal(val);
    }

    /**
     * Return the inverse of value if not null or zero, using scale.
     * @param value the nullable BigDecimal
     * @param scale scale for the result if value is not null
     * @return 1 / value (if not null of zero)
     */
    public static BigDecimal inverse(final BigDecimal value, final int scale) {
        if (isNotZero(value)) {
            return BigDecimal.ONE.divide(value, scale, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
     * Return the inverse of value if no tnull of zero
     * @param value the nullable BigDecimal
     * @return 1 / value (if not null of zero)
     */
    public static BigDecimal inverse(final BigDecimal value) {
        if (isNotZero(value)) {
            return BigDecimal.ONE.setScale(MAX_SCALE_FOR_INVERSE).divide(value, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    /**
      * @param value the nullable BigDecimal
    * @return true if value !=null and &lt;gt; 0.
     */
    public static boolean isNotZero(final BigDecimal value) {
        return value != null && value.signum() != 0;
    }

    /**
     * @param value the nullable BigDecimal
     * @return true if value !=null and 0.
     */
    public static boolean isZero(final BigDecimal value) {
        return value != null && value.signum() == 0;
    }

    /**
      * @param value the nullable BigDecimal
    * @return true if value !=null and &lt; 0.
     */
    public static boolean isNegative(final BigDecimal value) {
        return value != null && value.signum() == -1;
    }

    /**
      * @param value the nullable BigDecimal
    * @return true if value !=null and &gt;0.
     */
    public static boolean isStrictlyPositive(final BigDecimal value) {
        return value != null && value.signum() == 1;
    }

    /**
      * @param value the nullable BigDecimal
    * @return true if value ==null OR 0.
     */
    public static boolean isNullOrZero(final BigDecimal value) {
        return value == null || value.signum() == 0;
    }

    /**
     * @param val1 the nullable BigDecimal
     * @param val2 the nullable BigDecimal
     * @return true if val1 == val2 (ignoring scale and null are treated as 0)
     */
    public static boolean isSameValue(final BigDecimal val1, final BigDecimal val2) {
        return val1 == null && val2 == null || val1 != null && val2 != null && val1.compareTo(val2) == 0;
    }

    /**
     * @param val1 the nullable BigDecimal
     * @param val2 the nullable BigDecimal
     * @return true if val1 == val2 (ignoring scale and null are treated as 0)
     */
    public static boolean isSameValueTreatNullAsZero(final BigDecimal val1, final BigDecimal val2) {
        return val1 == null && val2 == null || signum(val1) == 0 && signum(val2) == 0 || val1 != null && val2 != null && val1.compareTo(val2) == 0;
    }

    /**
     * Add 2 BigDecimal safely (i.e. handles nulls as zeros)
     * @param v1 the nullable BigDecimal
     * @param v2 the nullable BigDecimal
     * @return the sum of the 2 BigDecimal
     */
    private static BigDecimal doAdd(final BigDecimal v1, final BigDecimal v2) {
        BigDecimal total = v1;
        if (v1 != null && v2 != null) {
            total = v1.add(v2);
        } else if (v2 != null) {
            total = v2;
        }
        return total;
    }

    /**
     * Add n BigDecimal safely (i.e. handles nulls)
     * @param start initial BigDecimal
     * @param values series of BigDecimals can be null/empty
     * @return the sum of the n non null BigDecimals
     */
    public static BigDecimal add(final BigDecimal start, final BigDecimal... values) {
        BigDecimal total = start != null ? start : BigDecimal.ZERO;
        if (values != null) {
            for (final BigDecimal v : values) {
                total = doAdd(total, v);
            }
        }
        return total;
    }

    /**
     * Subtract n BigDecimal safely from the start value (i.e. handles nulls as zeros), returns 0
     * @param start starting point, if null, use 0
     * @param values series of BigDecimal to subtract from start, can be null / empty
     * @return start - the series of values
     */
    public static BigDecimal subtract(final BigDecimal start, final BigDecimal... values) {
        BigDecimal total = start != null ? start : BigDecimal.ZERO;
        if (values != null) {
            for (final BigDecimal v : values) {
                total = doSubtract(total, v);
            }
        }
        return total;
    }

    /**
     * Subtract 2 BigDecimal safely (i.e. handles nulls) v1 - v2
     */
    private static BigDecimal doSubtract(final BigDecimal v1, final BigDecimal v2) {
        BigDecimal diff = v1;
        if (v1 != null && v2 != null) {
            diff = v1.subtract(v2);
        } else if (v2 != null) {
            diff = v2.negate();
        }
        return diff;
    }

    /**
     * @return numerator / denominator if they are not null and the denominator is not zero, it returns null otherwise.
     */
    public static BigDecimal divide(final BigDecimal numerator, final BigDecimal denominator, final int rounding) {
        BigDecimal diff = null;
        if (numerator != null && isNotZero(denominator)) {
            diff = numerator.divide(denominator, rounding);
        }
        return diff;
    }

    public static BigDecimal calculateWeight(final BigDecimal value, final BigDecimal total) {
        return BigDecimalUtil.setScale(
                BigDecimalUtil.divide(BigDecimalUtil.setScale(value, 9), BigDecimalUtil.setScale(total, 9), BigDecimal.ROUND_HALF_UP), 9);
    }

    /**
     * @return numerator / denominator if they are not null and the denominator is not zero, it returns null otherwise.
     */
    public static BigDecimal divide(final int numeratorScale, final BigDecimal numerator, final BigDecimal denominator, final int rounding) {
        BigDecimal diff = null;
        if (numerator != null && isNotZero(denominator)) {
            diff = numerator.setScale(numeratorScale, rounding).divide(denominator, rounding);
        }
        return diff;
    }

    /**
     * @return numerator / denominator if they are not null and the denominator is not zero, it returns null otherwise.
     */
    public static BigDecimal divide(final BigDecimal numerator, final BigDecimal denominator, final int scale, final int rounding) {
        BigDecimal diff = null;
        if (numerator != null && isNotZero(denominator)) {
            diff = numerator.divide(denominator, rounding);
        }
        return BigDecimalUtil.setScale(diff, scale, rounding);
    }

    public static BigDecimal multiply(final BigDecimal value, final BigDecimal multiplicand) {
        BigDecimal diff = null;
        if (value != null && multiplicand != null) {
            diff = value.multiply(multiplicand);
        }
        return diff;
    }

    public static BigDecimal multiply(final BigDecimal value, final BigDecimal... multiplicand) {
        BigDecimal diff = null;
        if (value != null && multiplicand != null) {
            diff = value;
            for (final BigDecimal bd : multiplicand) {
                if (bd != null) {
                    diff = diff.multiply(bd);
                }
            }
        }
        return diff;
    }

    /**
     * Returns the ABS of the value, handles null.
     * @param value nullable BigDecimal
     * @return abs(value) or null if null
     */
    public static BigDecimal abs(final BigDecimal value) {
        return value != null ? value.abs() : null;
    }

    /**
     * Returns the negate of the value, handles null.
     * @param value nullable BigDecimal
     * @return -value or null if null
     */
    public static BigDecimal negate(final BigDecimal value) {
        return value != null ? value.negate() : null;
    }

    /**
     * Returns the negate of the value if condition is true, handles null.
     * @param condition triggers negate
     * @param value nullable BigDecimal
     * @return -value if condition==true 
     */
    public static BigDecimal negateIfTrue(final boolean condition, final BigDecimal value) {
        return condition ? negate(value) : value;
    }

    /**
     * Check ABS values of v1 and v2.
     * @param v1 nullable BigDecimal
     * @param v2 nullable BigDecimal
     * @return false if the ABS value match!
     */
    public static boolean isNotSameAbsValue(final BigDecimal v1, final BigDecimal v2) {
        return !isSameAbsValue(v1, v2);
    }

    /**
     * Check values of v1 and v2.
     * @param v1 nullable BigDecimal
     * @param v2 nullable BigDecimal
     * @return false if the value match!
     */
    public static boolean isNotSameValue(final BigDecimal v1, final BigDecimal v2) {
        return !isSameValue(v1, v2);
    }

    /**
     * Check ABS values of v1 and v2.
     * @param v1 nullable BigDecimal
     * @param v2 nullable BigDecimal
     * @return true if the ABS value match!
     */
    public static boolean isSameAbsValue(final BigDecimal v1, final BigDecimal v2) {
        return isSameValue(abs(v1), abs(v2));
    }

    /**
     * @return 1 if v1 &gt; v2 or v2==null and v2!=null
     * @return 0 if v1 == v2 or v1==null and v2==null
     * @return -1 if v1 &lt; v2 or v1==null and v2!=null
     */
    public static int compareTo(final BigDecimal v1, final BigDecimal v2) {
        int ret = 1;
        if (v1 != null && v2 != null) {
            ret = v1.compareTo(v2);
        } else if (v1 == null && v2 == null) {
            ret = 0;
        } else if (v1 == null) {
            ret = -1;
        }
        return ret;
    }

    /**
     * @return true if the ABS(v1) &gt; ABS(v2)
     */
    public static int absCompareTo(final BigDecimal v1, final BigDecimal v2) {
        return compareTo(abs(v1), abs(v2));
    }

    /**
     * @return true if the ABS( ABS(v1) - ABS(v2) )
     */
    public static BigDecimal absDiff(final BigDecimal v1, final BigDecimal v2) {
        return abs(doSubtract(abs(v1), abs(v2)));
    }

    /**
     * Safe shift (check for null), shift RIGHT if shift&gt;0.
     */
    public static BigDecimal movePoint(final BigDecimal v1, final int shift) {
        return v1 == null ? null : v1.movePointRight(shift);
    }

    /**
     * returns a new BigDecimal with correct scale after being round to n dec places.
     *
     * @param bd value
     * @param numberOfDecPlaces number of dec place to round to
     * @param finalScale final scale of result (typically numberOfDecPlaces &lt; finalScale);
     * @return new bd or null
     */
    public static BigDecimal roundTo(final BigDecimal bd, final int numberOfDecPlaces, final int finalScale) {
        return setScale(setScale(bd, numberOfDecPlaces, BigDecimal.ROUND_HALF_UP), finalScale);
    }

    /**
     * returns a new BigDecimal with correct scale.
     *
     * @param bd
     * @return new bd or null
     */
    public static BigDecimal setScale(final BigDecimal bd, final int scale) {
        return setScale(bd, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * returns a new BigDecimal with correct Scale.
     *
     * @param bd
     * @return new bd or null
     */
    public static BigDecimal setScale(final BigDecimal bd, final Integer scale) {
        return setScale(bd, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * returns a new BigDecimal with correct Scales.PERCENT_SCALE. This is used
     * by the table renderer.
     *
     * @param bd
     * @return new bd or null
     */
    public static BigDecimal setScale(final BigDecimal bd, final Integer scale, final int rounding) {
        if (bd != null && scale != null) {
            return bd.setScale(scale, rounding);
        }
        return null;
    }

    /**
     * If value is null return 0 otherwise the signum().
     * @param value
     * @return an <code>int</code> value
     */
    public static int signum(final BigDecimal value) {
        return value == null ? 0 : value.signum();
    }

    /**
     * @return true if both v1/v2 are null or same sign.
     */
    public static boolean isSameSignum(final BigDecimal v1, final BigDecimal v2) {
        return signum(v1) == signum(v2);
    }

    /**
     * @return true if both v1.signum() != v2.signum() and NOT zero.
     */
    public static boolean hasSignedFlippedAndNotZero(final BigDecimal v1, final BigDecimal v2) {
        final int v1Sign = signum(v1);
        final int v2Sign = signum(v2);
        return v1Sign != v2Sign && v1Sign != 0 && v2Sign != 0;
    }

    /**
     * @return true if v1.signum() != v2.signum().
     */
    public static boolean hasSignedChanged(final BigDecimal v1, final BigDecimal v2) {
        return signum(v1) != signum(v2);
    }

    /**
     * @param bd
     * @param lowerLimit
     * @param upperLimit
     * @return true if outside the range
     */
    public static boolean isOutsideRange(final BigDecimal bd, final BigDecimal lowerLimit, final BigDecimal upperLimit) {
        return !isInsideInclusiveRange(bd, lowerLimit, upperLimit);
    }

    /**
     * @param bd
     * @param lowerLimit
     * @param upperLimit
     * @return true if inside the inclusive range
     */
    public static boolean isInsideInclusiveRange(final BigDecimal bd, final BigDecimal lowerLimit, final BigDecimal upperLimit) {
        return ObjectUtil.noneNull(bd, lowerLimit, upperLimit) && bd.compareTo(lowerLimit) >= 0 && bd.compareTo(upperLimit) <= 0;
    }

    /**
     * @return o1 if not null, otherwise fallBack
     */
    public static BigDecimal assignNonNull(final BigDecimal o1, final BigDecimal fallBack) {
        return o1 != null ? o1 : fallBack;
    }

    /**
     * Calculate the weight of the constituent and add it to the running weighted value.
     * runningWeightedVal + valueToAdd * weightForValueToAdd / totalWeight
     * @param runningWeightedVal
     * @param valueToAdd
     * @param weightForValueToAdd
     * @param totalWeight
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal addWeightedConstituent(final BigDecimal runningWeightedVal, final BigDecimal valueToAdd,
            final BigDecimal weightForValueToAdd, final BigDecimal totalWeight) {
        return BigDecimalUtil.doAdd(runningWeightedVal, BigDecimalUtil.divide(
                BigDecimalUtil.multiply(valueToAdd, BigDecimalUtil.abs(weightForValueToAdd)), BigDecimalUtil.abs(totalWeight),
                BigDecimal.ROUND_HALF_UP));
    }

    /**
     * @return true if all values are either null or zero
     */
    public static boolean allNullOrZero(final BigDecimal... values) {
        for (final BigDecimal bd : values) {
            if (!isNullOrZero(bd)) {
                return false;
            }
        }
        return true;
    }

    /**
     * return a Number formatted or empty string if null.
     * @param bd
     */
    public static String format(final BigDecimal bd) {
        return bd != null ? NUMBER_FORMAT.format(bd) : "";
    }

    /**
     * return a Number formatted or empty string if null.
     * @param bd
     */
    public static String percentFormat(final BigDecimal bd) {
        return bd != null ? NUMBER_FORMAT.format(bd.movePointRight(2)) : "";
    }

    /**
     * true if ABS((startValue-newValue)/startValue) &lt;= abs(thresholdPercent)
     * @param startValue
     * @param newValue
     * @param thresholdPercent
     * @return a <code>boolean</code> value
     */
    public static boolean movedInsideThresholdPercentage(final BigDecimal startValue, final BigDecimal newValue, final BigDecimal thresholdPercent) {
        return !movedStrictlyOutsideThresholdPercentage(startValue, newValue, thresholdPercent);
    }

    /**
     * true if ABS((startValue-newValue)/startValue) &gt; abs(thresholdPercent)
     * @param startValue
     * @param newValue
     * @param thresholdPercent
     * @return a <code>boolean</code> value
     */
    public static boolean movedStrictlyOutsideThresholdPercentage(final BigDecimal startValue, final BigDecimal newValue,
            final BigDecimal thresholdPercent) {
        final BigDecimal s = BigDecimalUtil.setScale(startValue, 10);
        final BigDecimal n = BigDecimalUtil.setScale(newValue, 10);
        final BigDecimal diff = BigDecimalUtil.divide(BigDecimalUtil.doSubtract(s, n), s, BigDecimal.ROUND_HALF_UP);

        return BigDecimalUtil.absCompareTo(diff, thresholdPercent) > 0;
    }

    private static double roundUp(final double n, final int p) {
        double retval;

        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        } else {
            if (p != 0) {
                final double temp = Math.pow(10, p);
                final double nat = Math.abs(n * temp);

                retval = sign(n) * (nat == (long) nat ? nat / temp : Math.round(nat + ROUNDING_UP_FLOAT) / temp);
            } else {
                final double na = Math.abs(n);
                retval = sign(n) * (na == (long) na ? na : (long) na + 1);
            }
        }

        return retval;
    }

    /**
     * Returns a value rounded to p digits after decimal.
     * If p is negative, then the number is rounded to
     * places to the left of the decimal point. eg.
     * 10.23 rounded to -1 will give: 10. If p is zero,
     * the returned value is rounded to the nearest integral
     * value.
     * <p>If n is negative, the resulting value is obtained
     * as the round-up value of absolute value of n multiplied
     * by the sign value of n (@see MathX.sign(double d)).
     * Thus, -0.8 rounded-down to p=0 will give 0 not -1.
     * <p>If n is NaN, returned value is NaN.
     * @param n
     * @param p
     * @return a <code>double</code> value
     */
    private static double roundDown(final double n, final int p) {
        double retval;

        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        } else {
            if (p != 0) {
                final double temp = Math.pow(10, p);
                retval = sign(n) * Math.round(Math.abs(n) * temp - ROUNDING_UP_FLOAT) / temp;
            } else {
                retval = (long) n;
            }
        }

        return retval;
    }

    private static short sign(final double d) {
        return (short) (d == 0 ? 0 : d < 0 ? -1 : 1);
    }

    /**
     * Returns a value rounded-up to p digits after decimal.
     * If p is negative, then the number is rounded to
     * places to the left of the decimal point. eg.
     * 10.23 rounded to -1 will give: 20. If p is zero,
     * the returned value is rounded to the nearest integral
     * value.
     * <p>If n is negative, the resulting value is obtained
     * as the round-up value of absolute value of n multiplied
     * by the sign value of n (@see MathX.sign(double d)).
     * Thus, -0.2 rounded-up to p=0 will give -1 not 0.
     * <p>If n is NaN, returned value is NaN.
     * @param n
     * @param p
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal roundUp(final BigDecimal n, final int p) {
        if (n == null) {
            return null;
        }
        final int scale = n.scale();
        return BigDecimalUtil.setScale(new BigDecimal(roundUp(n.doubleValue(), p)), scale);
    }

    /**
     * Returns a value rounded to p digits after decimal.
     * If p is negative, then the number is rounded to
     * places to the left of the decimal point. eg.
     * 10.23 rounded to -1 will give: 10. If p is zero,
     * the returned value is rounded to the nearest integral
     * value.
     * <p>If n is negative, the resulting value is obtained
     * as the round-up value of absolute value of n multiplied
     * by the sign value of n (@see MathX.sign(double d)).
     * Thus, -0.8 rounded-down to p=0 will give 0 not -1.
     * <p>If n is NaN, returned value is NaN.
     * @param n
     * @param p
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal roundDown(final BigDecimal n, final int p) {
        if (n == null) {
            return null;
        }
        final int scale = n.scale();
        return BigDecimalUtil.setScale(new BigDecimal(roundDown(n.doubleValue(), p)), scale);
    }

    public static BigDecimal roundUpForIncrement(final BigDecimal n, final BigDecimal increment) {
        if (n == null) {
            return null;
        }
        final int scale = n.scale();
        final int p = (int) (increment != null ? -Math.log10(increment.abs().doubleValue()) : 0);
        return BigDecimalUtil.setScale(new BigDecimal(roundUp(n.doubleValue(), p)), scale);
    }

    public static BigDecimal roundDownForIncrement(final BigDecimal n, final BigDecimal increment) {
        if (n == null) {
            return null;
        }
        final int p = (int) (increment != null ? -Math.log10(increment.abs().doubleValue()) : 0);
        final int scale = n.scale();
        return BigDecimalUtil.setScale(new BigDecimal(roundDown(n.doubleValue(), p)), scale);
    }

    /**
     * Return minimum if the value is &lt; minimum.
     */
    public static BigDecimal ensureMin(final BigDecimal minimum, final BigDecimal value) {
        return BigDecimalUtil.compareTo(minimum, value) == 1 ? minimum : value;
    }

    /**
     * Return a negative amount based on amount.
     */
    public static BigDecimal forceNegative(final BigDecimal amount) {
        return BigDecimalUtil.negate(BigDecimalUtil.abs(amount));
    }

    /**
     * Return a negative amount based on amount if true, otherwise return the ABS.
     */
    public static BigDecimal forceNegativeIfTrue(final boolean condition, final BigDecimal amount) {
        return condition ? BigDecimalUtil.negate(BigDecimalUtil.abs(amount)) : BigDecimalUtil.abs(amount);
    }

    /**
     * @return return the min amount
     */
    public static BigDecimal min(final BigDecimal v1, final BigDecimal v2) {
        if (v1 == null) {
            return v2;
        } else if (v2 == null) {
            return v1;
        }
        return v1.compareTo(v2) <= 0 ? v1 : v2;
    }

    /**
     * @return return the max amount
     */
    public static BigDecimal max(final BigDecimal... v1) {
        if (v1 == null) {
            return null;
        }
        BigDecimal max = null;
        for (final BigDecimal bd : v1) {
            max = BigDecimalUtil.compareTo(max, bd) >= 0 ? max : bd;
        }
        return max;
    }

    /**
     * Move by 2 DEC place to the left and take the long value, this
     * takes care of values like 0.18 in fractions.
     */
    public static long longForFraction(final BigDecimal v) {
        if (v == null) {
            return 0L;
        }
        return BigDecimalUtil.movePoint(v, 2).longValue();
    }

    /**
     * @return true if abs(abs(v1)-abs(v2)) &lt; abs(threshold)
     */
    public static boolean isDiffMoreThanAbsThreshold(final BigDecimal v1, final BigDecimal v2, final BigDecimal threshold) {
        final BigDecimal diff = BigDecimalUtil.absDiff(v1, v2);
        return BigDecimalUtil.compareTo(diff, BigDecimalUtil.abs(threshold)) > 0;
    }

    public static double doubleValue(final BigDecimal val) {
        return val != null ? val.doubleValue() : 0.0;
    }

    /**
     * @return true if value !=null and &lt;=0.
     */
    public static boolean isZeroOrLess(final BigDecimal value) {
        return value != null && value.signum() <= 0;
    }

    /**
     * Return the decimal part of the value.
     * @param val
     */
    public static BigDecimal decimalPart(final BigDecimal val) {
        return BigDecimalUtil.subtract(val, val.setScale(0, BigDecimal.ROUND_DOWN));
    }
    /**
     * Calculate the log of the given value in the given base.
     *
     * @param inBase a <code>BigDecimal</code> value
     * @param inValue an <code>int</code> value
     * @return a <code>BigDecimal</code> value
     */
    public static BigDecimal log(int inBase,
                                 BigDecimal inValue)
    {
        BigDecimal result = BigDecimal.ZERO;

        BigDecimal input = new BigDecimal(inValue.toString());
        int decimalPlaces = 100;
        int scale = input.precision() + decimalPlaces;

        int maxite = 10000;
        int ite = 0;
        BigDecimal maxError_BigDecimal = new BigDecimal(BigInteger.ONE,decimalPlaces + 1);

        RoundingMode a_RoundingMode = RoundingMode.UP;

        BigDecimal two_BigDecimal = new BigDecimal("2");
        BigDecimal base_BigDecimal = new BigDecimal(inBase);

        while (input.compareTo(base_BigDecimal) == 1) {
            result = result.add(BigDecimal.ONE);
            input = input.divide(base_BigDecimal, scale, a_RoundingMode);
        }

        BigDecimal fraction = new BigDecimal("0.5");
        input = input.multiply(input);
        BigDecimal resultplusfraction = result.add(fraction);
        while (((resultplusfraction).compareTo(result) == 1)
                && (input.compareTo(BigDecimal.ONE) == 1)) {
            if (input.compareTo(base_BigDecimal) == 1) {
                input = input.divide(base_BigDecimal, scale, a_RoundingMode);
                result = result.add(fraction);
            }
            input = input.multiply(input);
            fraction = fraction.divide(two_BigDecimal, scale, a_RoundingMode);
            resultplusfraction = result.add(fraction);
            if (fraction.abs().compareTo(maxError_BigDecimal) == -1){
                break;
            }
            if (maxite == ite){
                break;
            }
            ite ++;
        }

        MathContext a_MathContext = new MathContext(((decimalPlaces - 1) + (result.precision() - result.scale())),RoundingMode.HALF_UP);
        BigDecimal roundedResult = result.round(a_MathContext);
        BigDecimal strippedRoundedResult = roundedResult.stripTrailingZeros();
        //return result;
        //return result.round(a_MathContext);
        return strippedRoundedResult;
    }
}
