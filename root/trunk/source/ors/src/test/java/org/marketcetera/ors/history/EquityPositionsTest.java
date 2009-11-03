package org.marketcetera.ors.history;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.*;
import org.marketcetera.ors.security.SimpleUser;

import java.util.*;
import java.math.BigDecimal;

/* $License$ */
/**
 * Verifies {@link ReportHistoryServices#getPositionAsOf(org.marketcetera.ors.security.SimpleUser, Date, Equity)}
 * & {@link ReportHistoryServices#getPositionsAsOf(org.marketcetera.ors.security.SimpleUser, Date)}. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
public class EquityPositionsTest extends PositionsTestBase<Equity> {

    @Override
    protected Equity getInstrument() {
        return TEST_EQUITY;
    }

    @Override
    protected Equity getInstrumentA() {
        return EQUITY_A;
    }

    @Override
    protected Equity getInstrumentB() {
        return EQUITY_B;
    }

    @Override
    protected BigDecimal getInstrumentPosition(Date inDate, Equity inEquity) throws Exception {
        return getPosition(inDate, inEquity);
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
    protected Map<PositionKey<Equity>, BigDecimal> getInstrumentPositions(Date inAfter, SimpleUser inUser) throws Exception {
        return getPositions(inAfter, inUser);
    }

    private static final Equity EQUITY_A = new Equity("A");
    private static final Equity EQUITY_B = new Equity("B");
    private static final Equity TEST_EQUITY = new Equity("s1");
}
