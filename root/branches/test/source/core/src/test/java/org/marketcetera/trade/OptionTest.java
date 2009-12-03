package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link Option}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
public class OptionTest extends InstrumentTestBase<Option> {

    @Override
    protected Option createFixture() {
        return new Option("ABC", "20091010", BigDecimal.ONE, OptionType.Call);
    }

    @Override
    protected Option createEqualFixture() {
        return new Option("ABC", "20091010", BigDecimal.ONE, OptionType.Call);
    }

    @Override
    protected List<Option> createDifferentFixtures() {
        return ImmutableList.of(

        new Option("ABC", "20091010", new BigDecimal("5"), OptionType.Call),

        new Option("ABC", "20091010", new BigDecimal("6"), OptionType.Call),

        new Option("ABC", "20091011", new BigDecimal("5"), OptionType.Call),

        new Option("ABC", "20091010", new BigDecimal("5"), OptionType.Put),

        new Option("METC", "20091011", new BigDecimal("5"), OptionType.Call));
    }

    @Override
    protected SecurityType getSecurityType() {
        return SecurityType.Option;
    }

	@Test
    public void testStrikePriceTrailingZerosTrimmed() throws Exception {
        verifyStrikePriceTrimTrailingZero("5", "5.00");
        verifyStrikePriceTrimTrailingZero("50", "50.00");
        verifyStrikePriceTrimTrailingZero("5.5", "5.500");
        verifyStrikePriceTrimTrailingZero("5000", "5000.00000");
        verifyStrikePriceTrimTrailingZero("5000.4343", "5000.434300000");
    }

    private void verifyStrikePriceTrimTrailingZero(String inPrice1, String inPrice2) {
        Option option1 = new Option("ABC", "20091010", new BigDecimal(inPrice1),
                OptionType.Call);
        Option option2 = new Option("ABC", "20091010", new BigDecimal(inPrice2),
                OptionType.Call);
        assertThat(option2.getStrikePrice(), is(new BigDecimal(inPrice1)));
        assertThat(option1, is(option2));
        assertThat(option1.hashCode(), is(option2.hashCode()));
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option(null, "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("", "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("   ", "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testNullType() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("ABC", "20091010", BigDecimal.ONE, null);
            }
        };
    }

    @Test
    public void testNullExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("ABC", null, BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testWhitespaceExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("ABC", "", BigDecimal.ONE, OptionType.Call);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("ABC", " ", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testNullStrikePrice() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Option("ABC", "20091010", null, OptionType.Call);
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        assertThat(
                createFixture().toString(),
                is("Option[symbol=ABC,type=Call,expiry=20091010,strikePrice=1]"));
        assertThat(
                new Option("ABC", "200911", BigDecimal.ONE, OptionType.Put).toString(),
                is("Option[symbol=ABC,type=Put,expiry=200911,strikePrice=1,augmentedExpiry=20091121]"));
    }
    
    @Test
    public void testAugmentedExpiryEquality() {
        String symbol = "SYM";
        OptionType type = OptionType.Call;
        BigDecimal strikePrice = BigDecimal.TEN;
        Option [] options = {
                new Option(symbol, "200911", strikePrice, type),
                new Option(symbol, "20091121", strikePrice, type)
        };
        //Test all permutations of equality
        for(Option option1: options) {
            for(Option option2: options) {
                assertEquals(option1, option2);
                assertEquals(option1.hashCode(), option2.hashCode());
            }
        }
        EqualityAssert.assertEquality(options[0], options[1],
                new Option(symbol, "20091120", strikePrice, type),
                new Option(symbol, "200912", strikePrice, type),
                new Option(symbol, "2009", strikePrice, type)
                );
    }

    @Test
    public void testAugmentedExpiryValue() {
        //augmented expiry
        Option option = new Option("s", "200911", BigDecimal.TEN, OptionType.Call);
        assertEquals("200911", option.getExpiry());
        assertEquals("20091121", option.getAugmentedExpiry());

        //no augmentation
        option = new Option("s", "20091111", BigDecimal.TEN, OptionType.Call);
        assertEquals("20091111", option.getExpiry());
        assertEquals(null, option.getAugmentedExpiry());

        //invalid expiry
        option = new Option("s", "2009", BigDecimal.TEN, OptionType.Call);
        assertEquals("2009", option.getExpiry());
        assertEquals(null, option.getAugmentedExpiry());
    }
}
