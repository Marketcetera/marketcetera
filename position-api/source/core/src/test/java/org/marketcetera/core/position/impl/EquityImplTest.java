package org.marketcetera.core.position.impl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.marketcetera.core.position.AbstractInstrumentTestBase;
import org.marketcetera.core.position.Equity;
import org.marketcetera.core.position.Instrument;
import org.marketcetera.core.position.Option;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.log.ActiveLocale;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link EquityImpl}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
public class EquityImplTest extends AbstractInstrumentTestBase<Equity> {

    @Override
    protected Equity createFixture() {
        return new EquityImpl("METC");
    }

    @Override
    protected Equity createEqualFixture() {
        return new EquityImpl("METC");
    }

    @Override
    protected List<? extends Instrument> createDifferentFixtures() {
        return ImmutableList.<Instrument> of(

        new EquityImpl("ABC"), new EquityImpl("IBM"), new EquityImpl("MSFT"),

                new OptionImpl("ABC", Option.Type.CALL, "20091010",
                        new BigDecimal("1")));
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new EquityImpl(null);
            }
        };
    }

    @Test
    public void testEmptySymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(null) {
            @Override
            protected void run() throws Exception {
                new EquityImpl("");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        ActiveLocale.pushLocale(Locale.ROOT);
        try {
            assertThat(createFixture().toString(),
                    is("EquityImpl[symbol=METC]"));
        } finally {
            ActiveLocale.popLocale();
        }
    }

}
