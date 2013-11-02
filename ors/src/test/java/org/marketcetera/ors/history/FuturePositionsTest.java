package org.marketcetera.ors.history;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.ors.security.SimpleUser;
import org.marketcetera.trade.Future;
import org.marketcetera.trade.FutureExpirationMonth;

/* $License$ */

/**
 * Tests future positions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FuturePositionsTest
        extends PositionsTestBase<Future>
{
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrument()
     */
    @Override
    protected Future getInstrument()
    {
        return future1;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentA()
     */
    @Override
    protected Future getInstrumentA()
    {
        return future2;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentB()
     */
    @Override
    protected Future getInstrumentB()
    {
        return future3;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentPosition(java.util.Date, org.marketcetera.trade.Instrument)
     */
    @Override
    protected BigDecimal getInstrumentPosition(Date inDate,
                                               Future inInstrument)
            throws Exception
    {
        return getPosition(inDate,
                           inInstrument);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentPosition(java.util.Date, org.marketcetera.trade.Instrument, org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    protected BigDecimal getInstrumentPosition(Date inDate,
                                               Future inInstrument,
                                               SimpleUser inUser)
            throws Exception
    {
        return getPosition(inDate,
                           inInstrument,
                           inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentPositions(java.util.Date)
     */
    @Override
    protected Map<PositionKey<Future>,BigDecimal> getInstrumentPositions(Date inDate)
            throws Exception
    {
        return getFuturePositions(inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.history.PositionsTestBase#getInstrumentPositions(java.util.Date, org.marketcetera.ors.security.SimpleUser)
     */
    @Override
    protected Map<PositionKey<Future>,BigDecimal> getInstrumentPositions(Date inDate,
                                                                         SimpleUser inUser)
            throws Exception
    {
        return getFuturePositions(inDate,
                                  inUser);
    }
    private static final Future future1 = new Future("AAPL",
                                                     FutureExpirationMonth.DECEMBER,
                                                     2014);
    private static final Future future2 = new Future("METC",
                                                     FutureExpirationMonth.NOVEMBER,
                                                     2014);
    private static final Future future3 = new Future("METC",
                                                     FutureExpirationMonth.DECEMBER,
                                                     2014);
}
