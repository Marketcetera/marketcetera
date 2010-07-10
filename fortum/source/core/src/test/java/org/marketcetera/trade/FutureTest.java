package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.marketcetera.module.ExpectedFailure;

import quickfix.field.MaturityMonthYear;

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
    public void testNordieQuarterlies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOQ0-10");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOQ5-10");
            }
        };
        verifyFuture(new Future("ENOQ1-09"),
                     "ENOQ1-09",
                     "200903");
        verifyFuture(new Future("ENOQ2-15"),
                     "ENOQ2-15",
                     "201506");
        verifyFuture(new Future("ENOQ3-22"),
                     "ENOQ3-22",
                     "202209");
        verifyFuture(new Future("ENOQ4-50"),
                     "ENOQ4-50",
                     "205012");
        // push the symbol envelope a little
        verifyFuture(new Future("XQ4-50"),
                     "XQ4-50",
                     "205012");
        verifyFuture(new Future("ABCXYZQ4-50"),
                     "ABCXYZQ4-50",
                     "205012");
    }
    /**
     * Tests Nord Pool symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNordieYearlies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("BRNYR-1");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("BRNYR-101");
            }
        };
        verifyFuture(new Future("BRNYR-09"),
                     "BRNYR-09",
                     "200912");
        verifyFuture(new Future("BRNYR-25"),
                     "BRNYR-25",
                     "202512");
        // push the symbol envelope a little
        verifyFuture(new Future("XYR-50"),
                     "XYR-50",
                     "205012");
        verifyFuture(new Future("ABCXYZYR-50"),
                     "ABCXYZYR-50",
                     "205012");
    }
    /**
     * Tests Nord Pool symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNordieWeeklies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ITWW00-10");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ITWW53-10");
            }
        };
        verifyFuture(new Future("ITWW01-09"),
                     "ITWW01-09",
                     "200901");
        verifyFuture(new Future("ITWW25-25"),
                     "ITWW25-25",
                     "202506");
        // push the symbol envelope a little
        verifyFuture(new Future("XW52-50"),
                     "XW52-50",
                     "205012");
        verifyFuture(new Future("ABCXYZW10-50"),
                     "ABCXYZW10-50",
                     "205003");
    }
    /**
     * Tests Nord Pool symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNordieMonthlies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOMXXX-10");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOMXXXX-10");
            }
        };
        verifyFuture(new Future("ABCMJAN-10"),
                     "ABCMJAN-10",
                     "201001");
        verifyFuture(new Future("ENOMAUG-25"),
                     "ENOMAUG-25",
                     "202508");
        // push the symbol envelope a little
        verifyFuture(new Future("XMOCT-50"),
                     "XMOCT-50",
                     "205010");
        verifyFuture(new Future("ABCXYZMJUL-50"),
                     "ABCXYZMJUL-50",
                     "205007");
    }
    /**
     * Tests Nord Pool symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testNordieDaylies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOD0100-10");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOD0113-10");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ENOMXXXX-10");
            }
        };
        verifyFuture(new Future("ENOD1107-10"),
                     "ENOD1107-10",
                     "201007");
        verifyFuture(new Future("ENOD3208-25"),
                     "ENOD3208-25",
                     "202508");
        // push the symbol envelope a little
        verifyFuture(new Future("XD0101-50"),
                     "XD0101-50",
                     "205001");
        verifyFuture(new Future("ABCXYZD3103-50"),
                     "ABCXYZD3103-50",
                     "205003");
    }
    /**
     * Tests SKE symbol constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSkeMonthlies()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ABC10A");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ABC1U");
            }
        };
        verifyFuture(new Future("ABC15F"),
                     "ABC15F",
                     "201501");
        verifyFuture(new Future("AB25G"),
                     "AB25G",
                     "202502");
        // push the symbol envelope a little
        verifyFuture(new Future("A30H"),
                     "A30H",
                     "203003");
        verifyFuture(new Future("ABCXYZ99J"),
                     "ABCXYZ99J",
                     "209904");
    }
    private static void verifyFuture(Future inActualFuture,
                                     String inExpectedSymbol,
                                     String inExpectedExpiration)
            throws Exception
    {
        assertEquals(inExpectedSymbol,
                     inActualFuture.getSymbol());
        assertEquals(new MaturityMonthYear(inExpectedExpiration),
                     inActualFuture.getExpiryAsMaturityMonthYear());
        assertEquals(SecurityType.Future,
                     inActualFuture.getSecurityType());
        assertNotNull(inActualFuture.toString());
    }
}