package org.marketcetera.core.trade;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.trade.Equity}.
 * 
 * @version $Id: EquityTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
public class EquityTest extends InstrumentTestBase<Equity> {

    @Override
    protected Equity createFixture() {
        return new Equity("METC");
    }

    @Override
    protected Equity createEqualFixture() {
        return new Equity("METC");
    }

    @Override
    protected List<Equity> createDifferentFixtures() {
        return ImmutableList.<Equity> of(new Equity("ABC"), new Equity("IBM"),
                new Equity("MSFT"));
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
                new Equity(null);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Equity("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Equity("   \n");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        assertThat(createFixture().toString(), is("Equity[symbol=METC]"));
    }

}
