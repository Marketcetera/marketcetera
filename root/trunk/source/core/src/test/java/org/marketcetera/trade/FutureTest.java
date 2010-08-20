package org.marketcetera.trade;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
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
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run() throws Exception {
                new Future(null, FutureExpirationMonth.JULY, 18);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run() throws Exception {
                new Future(null, "201010");
            }
        };
    }

    @Test
    public void testWhitespaceSymbol() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("",FutureExpirationMonth.JULY, 18);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("   ",FutureExpirationMonth.JULY, 18);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("   ", "201012");
            }
        };
    }

    @Test
    public void testNullExpirationMonth() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_MONTH.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", null, 18);
            }
        };
    }
    /**
     * Tests invalid expiration years. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testInvalidExpirationYear()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_YEAR.getText(-1)) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", FutureExpirationMonth.JULY, -1);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_YEAR.getText(0)) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", FutureExpirationMonth.JULY, 0);
            }
        };
    }
    /**
     * Tests invalid expiration month values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testInvalidExpirationMonth()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_MONTH.getText("00")) {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ABC",
                           "201000");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_MONTH.getText("13")) {
            @Override
            protected void run()
                    throws Exception
            {
                new Future("ABC",
                           "201013");
            }
        };
    }

    @Test
    public void testInvalidExpiry() throws Exception {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_EXPIRY.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_EXPIRY.getText()) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", "    ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_EXPIRY.getText("YYYYMM")) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", "YYYYMM");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_YEAR.getText("0")) {
            @Override
            protected void run() throws Exception {
                new Future("ABC", "000000");
            }
        };
    }
    /**
     * Tests the ability to parse expiries in {@link Future#Future(String, String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testExpiryParsing()
            throws Exception
    {
        Future future = new Future("ABC",
                                   "000101");
        assertEquals("ABC-200101",
                     future.getSymbol());
        assertEquals(2001,
                     future.getExpirationYear());
        assertEquals(FutureExpirationMonth.JANUARY,
                     future.getExpirationMonth());
        future = new Future("ABC",
                            "010012");
        assertEquals("ABC-010012",
                     future.getSymbol());
        assertEquals(100,
                     future.getExpirationYear());
        assertEquals(FutureExpirationMonth.DECEMBER,
                     future.getExpirationMonth());   
        future = new Future("ABC",
                            "009912");
        assertEquals("ABC-209912",
                     future.getSymbol());
        assertEquals(2099,
                     future.getExpirationYear());
        assertEquals(FutureExpirationMonth.DECEMBER,
                     future.getExpirationMonth());
        for(FutureExpirationMonth month : FutureExpirationMonth.values()) {
            future = new Future("ABC",
                                "2010" + month.getMonthOfYear());
            assertEquals("ABC-2010" + month.getMonthOfYear(),
                         future.getSymbol());
            assertEquals(2010,
                         future.getExpirationYear());
            assertEquals(month,
                         future.getExpirationMonth());
        }
    }
    /**
     * Tests ability to parse future instruments from strings.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testFromString()
            throws Exception
    {
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                Future.fromString(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                Future.fromString("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.NULL_SYMBOL.getText()) {
            @Override
            protected void run()
                    throws Exception
            {
                Future.fromString("    ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_SYMBOL.getText("x")) {
            @Override
            protected void run()
                    throws Exception
            {
                Future.fromString("x");
            }
        };
        new ExpectedFailure<IllegalArgumentException>(Messages.INVALID_MONTH.getText("13")) {
            @Override
            protected void run()
                    throws Exception
            {
                Future.fromString("x-201113");
            }
        };
        Future future = Future.fromString("SYMBOL-WITH-MULTIPLE_PARTS-IN-IT-201010");
        assertEquals("SYMBOL-WITH-MULTIPLE_PARTS-IN-IT-201010",
                     future.getSymbol());
        assertEquals(2010,
                     future.getExpirationYear());
        assertEquals(FutureExpirationMonth.OCTOBER,
                     future.getExpirationMonth());
    }
    @Test
    public void testToString() throws Exception {
        assertThat(
                createFixture().toString(),
                is("Future ABC-201807 [ABC JULY(N) 2018]"));
        assertThat(
                new Future("ABC", FutureExpirationMonth.JULY, 18).toString(),
                is("Future ABC-201807 [ABC JULY(N) 2018]"));
        assertThat(
                new Future("ABC",
                           "201010").toString(),
                is("Future ABC-201010 [ABC OCTOBER(V) 2010]"));
    }
}