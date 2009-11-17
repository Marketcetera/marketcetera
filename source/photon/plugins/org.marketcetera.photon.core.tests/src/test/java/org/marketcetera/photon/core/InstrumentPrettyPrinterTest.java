package org.marketcetera.photon.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.marketcetera.photon.commons.ValidateTest.ExpectedNullArgumentFailure;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.SecurityType;

/* $License$ */

/**
 * Tests {@link InstrumentPrettyPrinter}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class InstrumentPrettyPrinterTest {

    @SuppressWarnings("serial")
    @Test
    public void testPrinting() throws Exception {
        test(new Equity("XYZ"), "XYZ");
        test(new Option("IBM", "20091010", BigDecimal.ONE, OptionType.Call),
                "Oct 10 09 IBM Call 1.00");
        test(new Option("IBM", "200901", BigDecimal.ONE, OptionType.Call),
                "Jan 09 IBM Call 1.00");
        test(new Option("IBM", "200901", new BigDecimal("1234.556"),
                OptionType.Put), "Jan 09 IBM Put 1234.56");
        test(new Instrument() {
            @Override
            public String getSymbol() {
                return null;
            }

            @Override
            public SecurityType getSecurityType() {
                return null;
            }

            @Override
            public String toString() {
                return "asdf";
            }
        }, "asdf");
        new ExpectedNullArgumentFailure("instrument") {
            @Override
            protected void run() throws Exception {
                test(null, "");
            }
        };
    }

    private void test(Instrument instrument, String expected) {
        assertThat(InstrumentPrettyPrinter.print(instrument), is(expected));
    }

    @Test
    public void testPrintExpiry() throws Exception {
        testExpiry(new Option("IBM", "20091010", BigDecimal.ONE,
                OptionType.Call), "Oct 10 09");
        testExpiry(
                new Option("IBM", "200901", BigDecimal.ONE, OptionType.Call),
                "Jan 09");
        testExpiry(new Option("IBM", "200901w3", BigDecimal.ONE,
                OptionType.Call), "200901w3");
        new ExpectedNullArgumentFailure("option") {
            @Override
            protected void run() throws Exception {
                testExpiry(null, "");
            }
        };
        /*
         * These next two are odd cases. The behavior is unspecified, but the
         * pretty printer wraps the dates. The tests are included for
         * completeness.
         */
        testExpiry(
                new Option("IBM", "200913", BigDecimal.ONE, OptionType.Call),
                "Jan 10");
        testExpiry(new Option("IBM", "20090230", BigDecimal.ONE,
                OptionType.Call), "Mar 02 09");

    }

    private void testExpiry(Option instrument, String expected) {
        assertThat(InstrumentPrettyPrinter.printOptionExpiry(instrument),
                is(expected));
    }
}
