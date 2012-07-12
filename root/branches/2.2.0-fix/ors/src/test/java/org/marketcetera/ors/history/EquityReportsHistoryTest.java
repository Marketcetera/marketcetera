package org.marketcetera.ors.history;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.*;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.core.position.PositionKey;
import org.marketcetera.module.ExpectedFailure;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/* $License$ */
/**
 * Tests {@link ReportHistoryServices} behavior for Equity and unhandled
 * instruments.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class EquityReportsHistoryTest extends ReportHistoryTestBase<Equity> {
    /**
     * Verifies the behavior when instrument cannot be extracted from the report.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void unhandledInstrument() throws Exception {
        final ExecutionReport report = createExecReport("ord1", null,
                new Equity("green"), Side.Buy, OrderStatus.Filled,
                BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN, BigDecimal.TEN);
        //Change the security type so that the instrument is not retrievable anymore.
        ((FIXMessageSupport) report).getMessage().setField(
                new quickfix.field.SecurityType(quickfix.field.SecurityType.BANK_NOTES));
        Date before = new Date();
        sleepForSignificantTime();
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run() throws Exception {
                sServices.save(report);
            }
        };
        //verify that no reports were saved.
        assertEquals(0, sServices.getReportsSince(sViewer, before).length);
    }

    @Override
    protected Equity getInstrument() {
        return new Equity("ubm");
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Equity inInstrument) throws Exception {
        return getPosition(inDate, inInstrument);
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Equity inInstrument, SimpleUser inUser) throws Exception {
        return getPosition(inDate, inInstrument, inUser);
    }

    @Override
    protected Map<PositionKey<Equity>, BigDecimal> getInstrumentPositions(Date inDate) throws Exception {
        return getPositions(inDate);
    }

    @Override
    protected Map<PositionKey<Equity>, BigDecimal> getInstrumentPositions(Date inDate, SimpleUser inUser) throws Exception {
        return getPositions(inDate, inUser);
    }
}
