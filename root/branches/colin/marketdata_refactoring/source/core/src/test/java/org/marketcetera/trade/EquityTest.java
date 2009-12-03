package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import com.google.common.collect.ImmutableList;

/* $License$ */

/**
 * Tests {@link Equity}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
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
