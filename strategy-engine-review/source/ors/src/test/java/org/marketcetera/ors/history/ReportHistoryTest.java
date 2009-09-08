package org.marketcetera.ors.history;

import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Date;


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
        sServices.save(createCancelReject(sExtraUserID));
        BigDecimal extraPosition = position.add(BigDecimal.ONE);
        createAndSaveER(idPrefix + 10, null,
                        symbol,Side.Buy, extraPosition, sExtraUserID);
        BigDecimal actorPosition=extraPosition.add(position);
        sleepForSignificantTime();
        Date after = new Date();

        assertEquals(20,sServices.getReportsSince(sViewer,before).length);
        assertEquals(22,sServices.getReportsSince(sActor,before).length);
        assertEquals(2,sServices.getReportsSince(sExtraUser,before).length);

        assertEquals(0,sServices.getReportsSince(sViewer,after).length);
        assertEquals(0,sServices.getReportsSince(sActor,after).length);
        assertEquals(0,sServices.getReportsSince(sExtraUser,after).length);

        assertBigDecimalEquals
            (position,getPosition(after,symbol));
        assertBigDecimalEquals
            (actorPosition,getPosition(after,symbol,sActor));
        assertBigDecimalEquals
            (extraPosition,getPosition(after,symbol,sExtraUser));

        assertBigDecimalEquals
            (BigDecimal.ZERO,getPosition(before,symbol));
        assertBigDecimalEquals
            (BigDecimal.ZERO,getPosition(before,symbol,sActor));
        assertBigDecimalEquals
            (BigDecimal.ZERO,getPosition(before,symbol,sExtraUser));

        assertThat(getPositions(after),
                   allOf(isOfSize(1),
                         hasEntry(pos(symbol),position.setScale(SCALE))));
        assertThat(getPositions(after,sActor),
                   allOf(isOfSize(1),
                         hasEntry(pos(symbol),actorPosition.setScale(SCALE))));
        assertThat(getPositions(after,sExtraUser),
                   allOf(isOfSize(1),
                         hasEntry(pos(symbol),extraPosition.setScale(SCALE))));

        assertThat(getPositions(before),isOfSize(0));
        assertThat(getPositions(before,sActor),isOfSize(0));
        assertThat(getPositions(before,sExtraUser),isOfSize(0));
    }
}
