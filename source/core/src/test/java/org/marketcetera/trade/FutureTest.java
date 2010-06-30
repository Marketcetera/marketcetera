package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import com.google.common.collect.ImmutableList;

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
        return new Future("ABC", FutureExpirationMonth.JULY, 18);
    }

    @Override
    protected Future createEqualFixture() {
        return new Future("ABC", FutureExpirationMonth.JULY, 18);
    }

    @Override
    protected List<Future> createDifferentFixtures() {
        return ImmutableList.of(
        new Future("ABC", FutureExpirationMonth.JULY, 19),
        new Future("ABC", FutureExpirationMonth.JUNE, 18),
        new Future("METC", FutureExpirationMonth.JULY, 18));
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
                new Future(null, FutureExpirationMonth.JULY, 18);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("",FutureExpirationMonth.JULY, 18);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("   ",FutureExpirationMonth.JULY, 18);
            }
        };
    }

    @Test
    public void testNullExpirationMonth() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", null, 18);
            }
        };
    }

    @Test
    public void testInvalidExpirationYear() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", FutureExpirationMonth.JULY, -1);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("ABC", FutureExpirationMonth.JULY, 0);
            }
        };
    }
    
    @Test
    public void testToString() throws Exception {
        assertThat(
                createFixture().toString(),
                is("Future [symbol=ABC, expirationMonth=JULY, expirationYear=2018]"));
        assertThat(
                new Future("ABC", FutureExpirationMonth.JULY, 18).toString(),
                is("Future [symbol=ABC, expirationMonth=JULY, expirationYear=2018]"));
    }
}