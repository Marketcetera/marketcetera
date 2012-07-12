package org.marketcetera.ors.history;

import org.marketcetera.trade.*;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.core.position.PositionKey;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/* $License$ */
/**
 * Tests {@link ReportHistoryServices}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public abstract class ReportHistoryTestBase<I extends Instrument> extends ReportsTestBase {

    /**
     * Runs a very basic test that saves execution reports, and
     * then retrieves them and the position for equity.
     * This test only verifies that the underlying API is being
     * correctly invoked by the service facade. Extensive
     * testing of the underlying API is done in their own
     * individual tests.
     * 
     *
     * @throws Exception if there were errors.
     */
    @Test
    public void instrument() throws Exception {
        final String idPrefix = "ord";
        Date before = new Date();
        sleepForSignificantTime();
        //Save 100 reports to db for kicks
        mExpectedPosition = BigDecimal.TEN;
        String origOrderID = null;
        I instrument = getInstrument();
        for(int i = 0; i < 10; i++) {
            sServices.save(createCancelReject());
            String orderID = idPrefix + i;
            mExpectedPosition = mExpectedPosition.add(BigDecimal.ONE);
            createAndSaveER(orderID, origOrderID,
                            instrument,Side.Buy, mExpectedPosition);
            origOrderID = orderID;
        }
        sServices.save(createCancelReject(sExtraUserID));
        mExpectedExtraPosition = mExpectedPosition.add(BigDecimal.ONE);
        createAndSaveER(idPrefix + 10, null,
                        instrument,Side.Buy, mExpectedExtraPosition, sExtraUserID);
        mExpectedActorPosition = mExpectedExtraPosition.add(mExpectedPosition);
        sleepForSignificantTime();
        Date after = new Date();

        assertEquals(20,sServices.getReportsSince(sViewer,before).length);
        assertEquals(22,sServices.getReportsSince(sActor,before).length);
        assertEquals(2,sServices.getReportsSince(sExtraUser,before).length);

        assertEquals(0,sServices.getReportsSince(sViewer,after).length);
        assertEquals(0,sServices.getReportsSince(sActor,after).length);
        assertEquals(0,sServices.getReportsSince(sExtraUser,after).length);

        assertBigDecimalEquals
            (mExpectedPosition, getInstrumentPosition(after,instrument));
        assertBigDecimalEquals
            (mExpectedActorPosition, getInstrumentPosition(after,instrument,sActor));
        assertBigDecimalEquals
            (mExpectedExtraPosition, getInstrumentPosition(after,instrument,sExtraUser));

        assertBigDecimalEquals
            (BigDecimal.ZERO, getInstrumentPosition(before,instrument));
        assertBigDecimalEquals
            (BigDecimal.ZERO, getInstrumentPosition(before,instrument,sActor));
        assertBigDecimalEquals
            (BigDecimal.ZERO, getInstrumentPosition(before,instrument,sExtraUser));

        assertThat(getInstrumentPositions(after),
                   allOf(isOfSize(1),
                         hasEntry(pos(instrument), mExpectedPosition.setScale(SCALE))));
        assertThat(getInstrumentPositions(after,sActor),
                   allOf(isOfSize(1),
                         hasEntry(pos(instrument), mExpectedActorPosition.setScale(SCALE))));
        assertThat(getInstrumentPositions(after,sExtraUser),
                   allOf(isOfSize(1),
                         hasEntry(pos(instrument), mExpectedExtraPosition.setScale(SCALE))));

        assertThat(getPositions(before),isOfSize(0));
        assertThat(getPositions(before,sActor),isOfSize(0));
        assertThat(getPositions(before,sExtraUser),isOfSize(0));
    }

    /**
     * Returns the instrument that should be used for testing.
     *
     * @return the instrument for testing.
     */
    protected abstract I getInstrument();

    /**
     * Fetches the instrument position.
     *
     * @param inDate the date in UTC.
     * @param inInstrument the instrument
     *
     * @return the position.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected abstract BigDecimal getInstrumentPosition(Date inDate, I inInstrument) throws Exception;

    /**
     * Fetches the instrument position.
     *
     * @param inDate the date in UTC.
     * @param inInstrument the instrument.
     * @param inUser the querying user.
     *
     * @return the position.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected abstract BigDecimal getInstrumentPosition(Date inDate, I inInstrument, SimpleUser inUser) throws Exception;

    /**
     * Fetches all open positions for the specific instrument type.
     *
     * @param inDate the date in UTC.
     * @return the instrument.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected abstract Map<PositionKey<I>,BigDecimal> getInstrumentPositions(Date inDate) throws Exception;

    /**
     * Fetches all open positions for the specific instrument type.
     *
     * @param inDate the date in UTC.
     * @param inUser the querying user.
     *
     * @return all open positions.
     *
     * @throws Exception if there were unexpected errors.
     */
    protected abstract Map<PositionKey<I>,BigDecimal> getInstrumentPositions(Date inDate, SimpleUser inUser) throws Exception;

    /**
     * Expected position for the regular user.
     *
     * @return the expected position value.
     */
    protected BigDecimal getExpectedPosition() {
        return mExpectedPosition;
    }

    /**
     * Expected position for the admin user.
     *
     * @return the expected position value.
     */
    protected BigDecimal getExpectedActorPosition() {
        return mExpectedActorPosition;
    }

    /**
     * Expected position for the extra user.
     *
     * @return the expected position value.
     */
    protected BigDecimal getExpectedExtraPosition() {
        return mExpectedExtraPosition;
    }

    private BigDecimal mExpectedPosition;
    private BigDecimal mExpectedActorPosition;
    private BigDecimal mExpectedExtraPosition;
}
