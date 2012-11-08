package org.marketcetera.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link Currency}.
 * 
 */
public class CurrencyTest
        extends InstrumentTestBase<Currency>
{
    @Override
    protected Currency createFixture() {
        return new Currency("USD", "INR", "1D","2W");
    }

    @Override
    protected Currency createEqualFixture() {
    	return new Currency("USD", "INR", "1D","2W");
    }

    @Override
    protected List<Currency> createDifferentFixtures() {
        return ImmutableList.of(
        	new Currency("GBP", "INR", "1D","2W"),
        	new Currency("GBP", "USD", "3D","2W"),
        	new Currency("GBP", "AUD", "1D","3W"));
    }

    @Override
    protected SecurityType getSecurityType() {
        return SecurityType.Currency;
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_LEFT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency(null, "INR", "1D","2W");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_RIGHT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency("INR", null, "1D","2W");
            }
        };
    }
    /**
     * Tests the ability to parse symbols with white space.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testWhitespaceSymbol()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_RIGHT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency("INR", "", "1D","2W");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_RIGHT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency("INR", " ", "1D","2W");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_LEFT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency("", "INR", "1D","2W");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.MISSING_LEFT_CURRENCY.getText()) {
            @Override
            protected void run() throws Exception {
            	new Currency("  ", "INR", "1D","2W");
            }
        };
    }
    /**
     * Verifies that the given actual <code>Currency</code> contains the given expected attributes.
     *
     * @param inActualInstrument a <code>Currency</code> value
     * @param inExpectedSymbol a <code>String</code> value
     * @param inNearTenor a <code>String</code> value
     * @param inFarTenor a <code>String</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyCurrency(Currency inActualInstrument,
                              String inExpectedSymbol,
                              String inNearTenor,
                              String inFarTenor)
            throws Exception
    {
        assertNotNull(inActualInstrument.toString());
        assertEquals(inNearTenor,inActualInstrument.getNearTenor());
        assertEquals(inFarTenor,inActualInstrument.getFarTenor()); 
        assertEquals(SecurityType.Currency,
                     inActualInstrument.getSecurityType());
        assertEquals(inExpectedSymbol,
                     inActualInstrument.getSymbol());
    }
}