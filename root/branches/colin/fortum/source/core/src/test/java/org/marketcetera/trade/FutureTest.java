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
        return new Future("ENOQ1-12");
    }

    @Override
    protected Future createEqualFixture() {
        return new Future("ENOQ1-12");
    }

    @Override
    protected List<Future> createDifferentFixtures() {
        return ImmutableList.of(
        new Future("ENOQ2-12"),
        new Future("ENOW01-13"));
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
                new Future(null);
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                new Future("   ");
            }
        };
    }

    @Test
    public void testToString() throws Exception {
        assertThat(
                createFixture().toString(),
                is("Future [symbol=ENOQ1-12, expiration=201203]"));
    }
    /**
     * Tests Nord Pool symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNordies()
            throws Exception
    {
        
    }
}