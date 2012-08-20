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
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
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
     * @return
     */
    public List<ReportBase> getReportsSince(Date inDate);
    /**
     * 
     *
     *
     * @param inDate
     * @param inInstrument
     * @return
     */
    public BigDecimal getPositionAsOf(Date inDate,
                                      Instrument inInstrument);
    /**
     * 
     *
     *
     * @param inDate
     * @return
     */
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate);
}
