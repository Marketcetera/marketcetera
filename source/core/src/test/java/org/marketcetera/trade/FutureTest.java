package org.marketcetera.trade;

import com.google.common.collect.ImmutableList;
import org.junit.Ignore;
import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.util.test.EqualityAssert;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/* $License$ */

/**
 * Tests {@link Future}.
 * 
 * @author <a href="mailto:toli@marketcetera.com">Toli Kuznets</a>
 * @version $Id$
 * @since 2.1.0
 */
public class FutureTest extends InstrumentTestBase<Future> {

    @Override
    protected Future createFixture() {
        return new Future("ABC", "200910");
    }

    @Override
    protected Future createEqualFixture() {
        // todo: is 20091010 an 'equal' expiry? is it the same behaviour as options?
        return new Future("ABC", "200910");
    }

    @Override
    protected List<Future> createDifferentFixtures() {
        return ImmutableList.of(
        new Future("ABC", "200911"),
        new Future("ABC", "200912"),
        new Future("METC", "200911"));
    }

    @Override
    protected SecurityType getSecurityType() {
        return SecurityType.Future;
    }

    @Test
    public void testNullSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future(null, "20091010");
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("", "20091010");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("   ", "20091010");
            }
        };
    }

    @Test
    public void testNullExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", null);
            }
        };
    }

    @Test
    public void testWhitespaceExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", "");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", " ");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        assertThat(
                createFixture().toString(),
                is("Future[symbol=ABC,expiry=200910]"));
        assertThat(
                new Future("ABC", "200911").toString(),
                is("Future[symbol=ABC,expiry=200911]"));
    }
    
    @Test @Ignore
    // todo: is it same behaviour as options? is there such thing as augmented expiry for futures? seems like there are dates there: http://www.farrdirect.com/expfut.htm
    public void testAugmentedExpiryEquality() {
/*
        String symbol = "SYM";
        Future [] futures = {
                new Future(symbol, "200911"),
                new Future(symbol, "20091121")
        };
        //Test all permutations of equality
        for(Future future1: futures) {
            for(Future future2: futures) {
                assertEquals(future1, future2);
                assertEquals(future1.hashCode(), future2.hashCode());
            }
        }
        EqualityAssert.assertEquality(futures[0], futures[1],
                new Future(symbol, "20091120"),
                new Future(symbol, "200912"),
                new Future(symbol, "2009")
                );
*/
    }
}