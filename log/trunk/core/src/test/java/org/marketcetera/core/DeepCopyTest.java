package org.marketcetera.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.Util;
import org.marketcetera.event.EventTestBase;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.trade.Equity;

/* $License$ */

/**
 * Tests {@link Util#deepCopy(java.io.Serializable)}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DeepCopyTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /**
     * Tests deep copy of various events.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEvents()
            throws Exception
    {
        TradeEvent trade = EventTestBase.generateTradeEvent(new Equity("METC"));
        TradeEvent tradeCopy = Util.deepCopy(trade);
        assertEquals(trade,
                     tradeCopy);
        assertNotSame(trade,
                      tradeCopy);
    }
}
