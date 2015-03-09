package org.marketcetera.options;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.marketcetera.trade.OptionType.Call;
import static org.marketcetera.trade.OptionType.Put;
import static org.marketcetera.trade.OptionType.Unknown;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;

/* $License$ */

/**
 * Tests {@link OptionUtils}.
 *
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class OptionUtilsTest {
    	
    /**
     * Verifies {@link OptionUtils#normalizeEquityOptionExpiry(String)} &
     * {@link OptionUtils#normalizeUSEquityOptionExpiry(String)}.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void testNormalizeOptionExpiry() throws Exception {
	    assertNormalized("200911", "20091121");
	    assertNormalized("201001", "20100116");
	    assertNormalized("201005", "20100522");
        assertNormalized("203712", "20371219");
        assertNormalized("20371212", null);
        assertNormalized("abc", null);
        assertNormalized("", null);
        assertNormalized("  ", null);
        assertNormalized("      ", null);
        assertNormalized("2009xx", null);
        assertNormalized("xxxx05", null);
        assertNormalized("200900", null);
        assertNormalized("200913", null);
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run() throws Exception {
                OptionUtils.normalizeUSEquityOptionExpiry(null);
            }
        };
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run() throws Exception {
                OptionUtils.normalizeEquityOptionExpiry(null);
            }
        };
    }

    private void assertNormalized(String expiry, String expected) {
        assertThat(OptionUtils.normalizeUSEquityOptionExpiry(expiry), is(expected));
        assertThat(OptionUtils.normalizeEquityOptionExpiry(expiry), is(expected));
    }
    
    /**
     * Tests {@link OptionUtils#getOsiOptionFromString(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOsiOptionFromString()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("this-is-not-a-valid-osi-option");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("21-characters-not-osi");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("MSFT  991122p12345123"); // valid except 'Put' indicator is lower-case
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("MSFT  991122c12345123"); // valid except 'Call' indicator is lower-case
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("MSFT  991122 12345123"); // option-type missing
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiOptionFromString("MSFT  991122 12345123"); // option-type missing
            }
        };
        verifyOption(OptionUtils.getOsiOptionFromString("123456301122P12345123"),
                     "123456",
                     OptionType.Put,
                     "20301122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001122P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  000122P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21000122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  000922P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21000922",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001022P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001022",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001222P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001222",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001201P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001201",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  201209P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "20201209",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  201210P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "20201210",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  201219P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "20201219",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001220P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001220",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("123456001220P12345123"),
                     "123456",
                     OptionType.Put,
                     "21001220",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  001229P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "21001229",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  201230P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "20201230",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("MSFT  501231P12345123"),
                     "MSFT",
                     OptionType.Put,
                     "20501231",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("x     301122P12345123"),
                     "x",
                     OptionType.Put,
                     "20301122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("xx    301122P12345123"),
                     "xx",
                     OptionType.Put,
                     "20301122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("ABCDEF301122C12345123"),
                     "ABCDEF",
                     OptionType.Call,
                     "20301122",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("ABCDEF000431C12345123"), // invalid date
                     "ABCDEF",
                     OptionType.Call,
                     "21000431",
                     new BigDecimal("12345.123"));
        verifyOption(OptionUtils.getOsiOptionFromString("ABCDEF301122C00000000"),
                     "ABCDEF",
                     OptionType.Call,
                     "20301122",
                     new BigDecimal("0.0"));
        verifyOption(OptionUtils.getOsiOptionFromString("ABCDEF301122C99999999"),
                     "ABCDEF",
                     OptionType.Call,
                     "20301122",
                     new BigDecimal("99999.999"));
        Locale currentLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRANCE);
            assertEquals(',',
                         DecimalFormatSymbols.getInstance().getDecimalSeparator());
            verifyOption(OptionUtils.getOsiOptionFromString("ABCDEF301122C12345123"),
                         "ABCDEF",
                         OptionType.Call,
                         "20301122",
                         new BigDecimal("12345.123"));
        } finally {
            Locale.setDefault(currentLocale);
        }
    }
    /**
     * Tests {@link OptionUtils#getOsiSymbolFromOption(Option)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testOsiSymbolFromOption()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiSymbolFromOption(null);
            }
        };
        // symbol too long
        verifyGetOsiSymbolFromOptionFails("1234567", 
                "20091010",
                BigDecimal.ONE, 
                OptionType.Call);
        // expiry too long
        verifyGetOsiSymbolFromOptionFails("123456", 
                "200910100",
                BigDecimal.ONE, 
                OptionType.Call);
        // expiry without day
        verifyGetOsiSymbolFromOptionFails("123456", 
                "200910", 
                BigDecimal.ONE,
                OptionType.Call);
        // expiry with week
        verifyGetOsiSymbolFromOptionFails("123456", 
                "200910w1", 
                BigDecimal.ONE,
                OptionType.Call);
        // negative strike
        verifyGetOsiSymbolFromOptionFails("123456", 
                "20091010", 
                new BigDecimal(-1),
                OptionType.Put);
        // strike with too many fractional digits
        verifyGetOsiSymbolFromOptionFails("123456",
                "20091010",
                new BigDecimal("0.1234"),
                OptionType.Put);
        // strike with too many non-fractional digits
        verifyGetOsiSymbolFromOptionFails("123456",
                "20091010",
                new BigDecimal("123456"),
                OptionType.Put);
        // unknown type
        verifyGetOsiSymbolFromOptionFails("123456",
                "20091010",
                BigDecimal.ONE,
                OptionType.Unknown);
        verifyOsiSymbolFromOption("123456301122P12345123",
                "123456",
                OptionType.Put,
                "20301122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001122P12345123",
                "MSFT",
                OptionType.Put,
                "21001122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  000122P12345123",
                "MSFT",
                OptionType.Put,
                "21000122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  000922P12345123",
                "MSFT",
                OptionType.Put,
                "21000922",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001022P12345123",
                "MSFT",
                OptionType.Put,
                "21001022",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001222P12345123",
                "MSFT",
                OptionType.Put,
                "21001222",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001201P12345123",
                "MSFT",
                OptionType.Put,
                "21001201",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  201209P12345123",
                "MSFT",
                OptionType.Put,
                "20201209",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  201210P12345123",
                "MSFT",
                OptionType.Put,
                "20201210",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  201219P12345123",
                "MSFT",
                OptionType.Put,
                "20201219",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001220P12345123",
                "MSFT",
                OptionType.Put,
                "21001220",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("123456001220P12345123",
                "123456",
                OptionType.Put,
                "21001220",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  001229P12345123",
                "MSFT",
                OptionType.Put,
                "21001229",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  201230P12345123",
                "MSFT",
                OptionType.Put,
                "20201230",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("MSFT  501231P12345123",
                "MSFT",
                OptionType.Put,
                "20501231",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("x     301122P12345123",
                "x",
                OptionType.Put,
                "20301122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("xx    301122P12345123",
                "xx",
                OptionType.Put,
                "20301122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("ABCDEF301122C12345123",
                "ABCDEF",
                OptionType.Call,
                "20301122",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("ABCDEF000431C12345123", // invalid date
                "ABCDEF",
                OptionType.Call,
                "21000431",
                new BigDecimal("12345.123"));
        verifyOsiSymbolFromOption("ABCDEF301122C00000000",
                "ABCDEF",
                OptionType.Call,
                "20301122",
                new BigDecimal("0.0"));
        verifyOsiSymbolFromOption("ABCDEF301122C99999999",
                "ABCDEF",
                OptionType.Call,
                "20301122",
                new BigDecimal("99999.999"));
    }
    /**
     * Tests {@link OptionUtils#getOptionTypeForOSICharacter(char)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void instanceForOSIValue()
            throws Exception
    {
        for(char badOptionType : new char[] { ' ', 'x', 'c', 'p' }) {
            assertEquals(Unknown,
                         OptionUtils.getOptionTypeForOSICharacter(badOptionType));
        }
        assertEquals(Put,
                     OptionUtils.getOptionTypeForOSICharacter('P'));
        assertEquals(Call,
                     OptionUtils.getOptionTypeForOSICharacter('C'));
    }
    /**
     * Verifies that the given <code>Option</code> matches the given expected attributes.
     *
     * @param inOption inOption an <code>Option</code> value
     * @param inExpectedSymbol inSymbol a <code>String</code> value
     * @param inExpectedType inOptionType an <code>OptionType</code> value
     * @param inExpectedExpiry inExpiry a <code>String</code> value
     * @param inExpectedStrike inStrike a <code>BigDecimal</code> value
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyOption(Option inOption,
                                     String inExpectedSymbol,
                                     OptionType inExpectedType,
                                     String inExpectedExpiry,
                                     BigDecimal inExpectedStrike)
            throws Exception
    {
        assertNotNull(inOption);
        assertEquals(inExpectedSymbol,
                     inOption.getSymbol());
        assertEquals(inExpectedType,
                     inOption.getType());
        assertEquals(inExpectedExpiry,
                     inOption.getExpiry());
        assertEquals(inExpectedStrike,
                     inOption.getStrikePrice());
    }
    /**
     * Verifies that {@link OptionUtils#getOsiSymbolFromOption(Option)} returns 
     * the expected symbol given an <code>Option</code> with the provided
     * attributes.
     *
     * @param inExpectedSymbol the expected OSI symbol
     * @param inRootSymbol the <code>Option</code> root symbol
     * @param inOptionType the <code>Option</code> type
     * @param inExpiry the <code>Option</code> expiry
     * @param inStrike the <code>Option</code> strike
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyOsiSymbolFromOption(String inExpectedSymbol,
                                     String inRootSymbol,
                                     OptionType inOptionType,
                                     String inExpiry,
                                     BigDecimal inStrike)
            throws Exception
    {
        assertEquals(inExpectedSymbol, OptionUtils
                .getOsiSymbolFromOption(new Option(inRootSymbol, inExpiry,
                        inStrike, inOptionType)));
    }

    /**
     * Verifies that {@link OptionUtils#getOsiSymbolFromOption(Option)} fails
     * with IllegalArgumentException for the given option tuple.
     * 
     * @param inRootSymbol the <code>Option</code> root symbol
     * @param inExpiry the <code>Option</code> expiry
     * @param inStrike the <code>Option</code> strike
     * @param inOptionType the <code>Option</code> type
     * @throws Exception if an unexpected error occurs
     */
    private static void verifyGetOsiSymbolFromOptionFails(final String inRootSymbol,
                                     final String inExpiry,
                                     final BigDecimal inStrike,
                                     final OptionType inOptionType)
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                OptionUtils.getOsiSymbolFromOption(new Option(inRootSymbol,
                        inExpiry, inStrike, inOptionType));
            }
        };
    }
}
