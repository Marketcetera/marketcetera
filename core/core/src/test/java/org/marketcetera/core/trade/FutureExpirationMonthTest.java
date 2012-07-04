package org.marketcetera.core.trade;

import org.junit.Test;
import org.marketcetera.core.ExpectedFailure;

import static org.junit.Assert.assertEquals;

/* $License$ */

/**
 * Tests {@link org.marketcetera.core.trade.FutureExpirationMonth}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureExpirationMonthTest.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public class FutureExpirationMonthTest
{
    /**
     * Tests {@link org.marketcetera.core.trade.FutureExpirationMonth#getFutureExpirationMonth(char)} and
     * {@link org.marketcetera.core.trade.FutureExpirationMonth#getFutureExpirationMonth(String)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void getFutureExpirationMonth()
            throws Exception
    {
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth(null);
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth(' ');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth("");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth(" ");
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth('E');
            }
        };
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                FutureExpirationMonth.getFutureExpirationMonth("E");
            }
        };
        int index = 0;
        for(FutureExpirationMonth expirationMonth : FutureExpirationMonth.values()) {
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(expirationMonth.getCode()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(Character.toLowerCase(expirationMonth.getCode())));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(new StringBuffer().append(expirationMonth.getCode()).toString()));
            assertEquals(FutureExpirationMonth.values()[index],
                         FutureExpirationMonth.getFutureExpirationMonth(new StringBuffer().append(expirationMonth.getCode()).toString().toLowerCase()));
            index += 1;
        }
    }
}
