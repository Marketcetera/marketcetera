package org.marketcetera.core.position;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.ReportBase;

/* $License$ */

/**
 *
 *
 * @version $Id$
 * @since $Release$
 */
public interface PositionManager
{
    /**
     * 
     *
     *
     * @param inDate
     * @return a <code>List&lt;ReportBase&gt;</code> value
     */
    public List<ReportBase> getReportsSince(Date inDate);
    /**
     * 
     *
     *
     * @param inDate
     * @param inInstrument
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getPositionAsOf(Date inDate,
                                      Instrument inInstrument);
    /**
     * 
     *
     *
     * @param inDate
     * @return a <code>Map&lt;PositionKey&lt;Instrument&gt;,BigDecimal&gt;</code> value
     */
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate);
}
