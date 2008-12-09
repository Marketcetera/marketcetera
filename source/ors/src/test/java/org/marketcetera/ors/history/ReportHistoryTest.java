package org.marketcetera.ors.history;

import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.persist.PersistTestBase;
import org.marketcetera.trade.*;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.core.MSymbol;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import quickfix.Message;
import quickfix.field.OrigClOrdID;
import quickfix.field.ClOrdID;
import quickfix.field.SendingTime;

/* $License$ */
/**
 * ReportHistoryTest
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class ReportHistoryTest extends ReportsTestBase {

    /**
     * Runs a very basic test that saves execution reports, and
     * then retrieves them and the position.
     * This test only verifies that the underlying API is being
     * correctly invoked by the service facade. Extensive
     * testing of the underlying API is done in their own
     * individual tests.
     * 
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void basic() throws Exception {
        final String idPrefix = "ord";
        Date before = new Date();
        sleepForSignificantTime();
        //Save 100 reports to db for kicks
        BigDecimal position = BigDecimal.TEN;
        String origOrderID = null;
        String symbol = "ubm";
        for(int i = 0; i < 10; i++) {
            sServices.save(createCancelReject());
            String orderID = idPrefix + i;
            position = position.add(BigDecimal.ONE);
            createAndSaveER(orderID, origOrderID,
                    symbol,Side.Buy, position);
            origOrderID = orderID;
        }
        sleepForSignificantTime();
        Date after = new Date();
        //Now retrieve them
        assertEquals(20, sServices.getReportsSince(before).length);
        assertEquals(0, sServices.getReportsSince(after).length);
        //Now select position
        assertBigDecimalEquals(position, getPosition(after, symbol));
        assertBigDecimalEquals(BigDecimal.ZERO, getPosition(before, symbol));
    }
}
