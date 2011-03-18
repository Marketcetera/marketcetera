package org.marketcetera.systemmodel.persistence;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface PositionDao
{
    /**
     * 
     *
     *
     * @param <T>
     * @param inInstrument
     * @param inDate
     * @return
     */
    public <T extends Instrument> BigDecimal getPositionAsOf(T inInstrument,
                                                             Date inDate);
    /**
     * 
     *
     *
     * @param inDate
     * @return
     */
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate);
}
