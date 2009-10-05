package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.marketcetera.core.position.AbstractInstrumentTestBase;
import org.marketcetera.core.position.Instrument;
import org.marketcetera.core.position.Option;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.ActiveLocale;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link OptionImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class OptionImplTest extends AbstractInstrumentTestBase<Option> {

    @Override
    protected Option createFixture() {
        return new OptionImpl("ABC", Option.Type.CALL, "20091010",
                BigDecimal.ONE);
    }

    @Override
    protected Option createEqualFixture() {
        return new OptionImpl("ABC", Option.Type.CALL, "20091010",
                BigDecimal.ONE);
    }

    @Override
    protected List<? extends Instrument> createDifferentFixtures() {
        return ImmutableList.<Instrument> of(

        new EquityImpl("ABC"),

                new OptionImpl("ABC", Option.Type.CALL, "20091010",
                        new BigDecimal("5")),

                new OptionImpl("ABC", Option.Type.CALL, "20091010",
                        new BigDecimal("6")),

                new OptionImpl("ABC", Option.Type.CALL, "20091011",
                        new BigDecimal("5")),

                new OptionImpl("ABC", Option.Type.PUT, "20091010",
                        new BigDecimal("5")),

                new OptionImpl("METC", Option.Type.CALL, "20091011",
                        new BigDecimal("5")));
    }

    @Test
    public void testStrikePrecisionIgnoredForEqualsAndHashCode()
            throws Exception {
        Option option1 = new OptionImpl("ABC", Option.Type.CALL, "20091010",
                new BigDecimal("5"));
        Option option2 = new OptionImpl("ABC", Option.Type.CALL, "20091010",
                new BigDecimal("5.00"));
        assertThat(option1, is(option2));
        assertThat(option1.hashCode(), is(option2.hashCode()));
        assertThat(option1.compareTo(option2), is(0));
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl(null, Option.Type.CALL, "20091010",
                        BigDecimal.ONE);
            }
        };
    }

    @Test
    public void testEmptySymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl("", Option.Type.CALL, "20091010", BigDecimal.ONE);
            }
        };
    }

    @Test
    public void testNullType() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl("ABC", null, "20091010", BigDecimal.ONE);
            }
        };
    }

    @Test
    public void testNullExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl("ABC", Option.Type.CALL, null, BigDecimal.ONE);
            }
        };
    }

    @Test
    public void testEmptyExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl("ABC", Option.Type.CALL, "", BigDecimal.ONE);
            }
        };
    }

    @Test
    public void testNullStrikePrice() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new OptionImpl("ABC", Option.Type.CALL, "20091010", null);
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        ActiveLocale.pushLocale(Locale.ROOT);
        try {
            assertThat(
                    createFixture().toString(),
                    is("OptionImpl[symbol=ABC,type=CALL,expiry=20091010,strikePrice=1]"));
        } finally {
            ActiveLocale.popLocale();
        }
    }
}
