package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link Option}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
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
        return ImmutableList.<Option> of(

        new Option("ABC", "20091010", new BigDecimal("5"), OptionType.Call),

        new Option("ABC", "20091010", new BigDecimal("6"), OptionType.Call),

        new Option("ABC", "20091011", new BigDecimal("5"), OptionType.Call),

        new Option("ABC", "20091010", new BigDecimal("5"), OptionType.Put),

        new Option("METC", "20091011", new BigDecimal("5"), OptionType.Call));
    }

    @Test
    public void testStrikePriceTrailingZerosTrimmed() throws Exception {
        Option option1 = new Option("ABC", "20091010", new BigDecimal("5"),
                OptionType.Call);
        Option option2 = new Option("ABC", "20091010", new BigDecimal("5.00"),
                OptionType.Call);
        assertThat(option2.getStrikePrice(), is(new BigDecimal("5")));
        assertThat(option1, is(option2));
        assertThat(option1.hashCode(), is(option2.hashCode()));
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option(null, "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("", "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("   ", "20091010", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testNullType() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("ABC", "20091010", BigDecimal.ONE, null);
            }
        };
    }

    @Test
    public void testNullExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("ABC", null, BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testWhitespaceExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("ABC", "", BigDecimal.ONE, OptionType.Call);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new Option("ABC", " ", BigDecimal.ONE, OptionType.Call);
            }
        };
    }

    @Test
    public void testNullStrikePrice() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
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
    }
}
