package org.marketcetera.core;


import junit.framework.TestCase;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author Graham Miller
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class BigDecimalUtilsTest extends TestCase {

    /*
      * Test method for 'org.marketcetera.core.BigDecimalUtils.add(BigDecimal, BigDecimal)'
      */
    public void testAddBigDecimalBigDecimal() {
        assertEquals((double)123.0+87.5, BigDecimalUtils.add(new BigDecimal(123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)-123.0+87.5, BigDecimalUtils.add(new BigDecimal(-123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)8888.0+.0001, BigDecimalUtils.add(new BigDecimal(8888.0), new BigDecimal(.0001)).doubleValue(), .000001);
        assertEquals((double)92384.5+0, BigDecimalUtils.add(new BigDecimal(92384.5), new BigDecimal(0)).doubleValue(), .000001);
    }

    /*
      * Test method for 'org.marketcetera.core.BigDecimalUtils.multiply(double, double)'
      */
    public void testMultiplyDoubleDouble() {
        assertEquals((double)123.0*87.5, BigDecimalUtils.multiply((double)123.0, (double)87.5).doubleValue(), .000001);
        assertEquals((double)-123.0*87.5, BigDecimalUtils.multiply((double)-123.0, (double)87.5).doubleValue(), .000001);
        assertEquals((double)8888.0*.0001, BigDecimalUtils.multiply((double)8888.0, (double).0001).doubleValue(), .000001);
        assertEquals((double)92384.5*0, BigDecimalUtils.multiply((double)92384.5, (double)0).doubleValue(), .000001);
    }

    /*
      * Test method for 'org.marketcetera.core.BigDecimalUtils.multiply(BigDecimal, double)'
      */
    public void testMultiplyBigDecimalDouble() {
        assertEquals((double)123.0*87.5, BigDecimalUtils.multiply(new BigDecimal(123.0), (double)87.5).doubleValue(), .000001);
        assertEquals((double)-123.0*87.5, BigDecimalUtils.multiply(new BigDecimal(-123.0), (double)87.5).doubleValue(), .000001);
        assertEquals((double)8888.0*.0001, BigDecimalUtils.multiply(new BigDecimal(8888.0), (double).0001).doubleValue(), .000001);
        assertEquals((double)92384.5*0, BigDecimalUtils.multiply(new BigDecimal(92384.5), (double)0).doubleValue(), .000001);
    }

    /*
      * Test method for 'org.marketcetera.core.BigDecimalUtils.multiply(BigDecimal, BigDecimal)'
      */
    public void testMultiplyBigDecimalBigDecimal() {
        assertEquals((double)123.0*87.5, BigDecimalUtils.multiply(new BigDecimal(123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)-123.0*87.5, BigDecimalUtils.multiply(new BigDecimal(-123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)8888.0*.0001, BigDecimalUtils.multiply(new BigDecimal(8888.0), new BigDecimal(.0001)).doubleValue(), .000001);
        assertEquals((double)92384.5*0, BigDecimalUtils.multiply(new BigDecimal(92384.5), new BigDecimal(0)).doubleValue(), .000001);
    }

    /*
      * Test method for 'org.marketcetera.core.BigDecimalUtils.multiply(BigDecimal, BigDecimal)'
      */
    public void testDivideBigDecimalBigDecimal() {
        assertEquals((double)123.0/87.5, BigDecimalUtils.divide(new BigDecimal(123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)-123.0/87.5, BigDecimalUtils.divide(new BigDecimal(-123.0), new BigDecimal(87.5)).doubleValue(), .000001);
        assertEquals((double)8888.0/.0001, BigDecimalUtils.divide(new BigDecimal(8888.0), new BigDecimal(0.0001)).doubleValue(), .000001);
        new ExpectedTestFailure(ArithmeticException.class) {
            protected void execute() throws Throwable {
                BigDecimalUtils.divide(new BigDecimal(92384.5), new BigDecimal(0));
            }
        }.run();

    }

    public void testBigDecimalAssumptions() throws Exception {
        // This is what round and scale do:

        // Round is used to set the total number of significant digits.  So even though we
        // use RoundingMode.HALF_UP on 10.5, its rounding to 1 significant digit, so it
        // rounds to 10.
        assertEquals(new BigDecimal("10").toPlainString(), new BigDecimal("10.5").round(new MathContext(1, RoundingMode.HALF_UP)).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$

        // Set scale sets the scale (the number of digits to the right of the
        // decimal place (this is often what we mean by "round").
        assertEquals(new BigDecimal("11").toPlainString(), new BigDecimal("10.5").setScale(0, RoundingMode.HALF_UP).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(new BigDecimal("10").toPlainString(), new BigDecimal("10.5").setScale(0, RoundingMode.HALF_DOWN).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$

        assertEquals("10.3000", new BigDecimal("10.3000").toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("10.3000", new BigDecimal("10.3000").toString()); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testTrim() throws Exception {
		assertEquals("1.5", BigDecimalUtils.trim(new BigDecimal("1.5")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("1", BigDecimalUtils.trim(new BigDecimal("1.0")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("1", BigDecimalUtils.trim(new BigDecimal("1")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("1.5", BigDecimalUtils.trim(new BigDecimal("1.50")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("0", BigDecimalUtils.trim(new BigDecimal("0")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("0", BigDecimalUtils.trim(new BigDecimal("-0")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("0", BigDecimalUtils.trim(new BigDecimal("0.0")).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$

    }
    
    /**
     * Test the assumption that the String constructor for BigDecimal preserves
     * scale, and trailing zeroes.
     *
     */
    public void testToPlainStringAssumptions() {
    	assertEquals("23.45", new BigDecimal("23.45").toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
    	assertEquals("23.40", new BigDecimal("23.40").toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
    	assertEquals("23.450", new BigDecimal("23.45").setScale(3).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
    	assertEquals("23.400", new BigDecimal("23.40").setScale(3).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
    	assertEquals("23.4", new BigDecimal("23.40").setScale(1).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
    	new ExpectedTestFailure(ArithmeticException.class){
			@Override
			protected void execute() throws Throwable {
		    	assertEquals("23.5", new BigDecimal("23.46").setScale(1).toPlainString()); //$NON-NLS-1$ //$NON-NLS-2$
			}
    	}.run();
    	
    }
    
    
    
}
