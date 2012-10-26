package org.marketcetera.core.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.api.systemmodel.instruments.Equity;
import org.marketcetera.api.systemmodel.instruments.SecurityType;
import org.marketcetera.core.ExpectedFailure;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.trade.EquityImpl}.
 * 
 * @version $Id: EquityTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityTest extends InstrumentTestBase<Equity> {

    @Override
    protected Equity createFixture() {
        return new EquityImpl("METC");
    }

    @Override
    protected Equity createEqualFixture() {
        return new EquityImpl("METC");
    }

    @Override
    protected List<Equity> createDifferentFixtures() {
        return ImmutableList.<Equity> of(new EquityImpl("ABC"), new EquityImpl("IBM"),
                new EquityImpl("MSFT"));
    }
    

    @Override
    protected SecurityType getSecurityType() {
        return SecurityType.CommonStock;
    }

	@Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new EquityImpl(null);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new EquityImpl("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new EquityImpl("   \n");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        assertThat(createFixture().toString(), is("Equity[symbol=METC]"));
    }

}
