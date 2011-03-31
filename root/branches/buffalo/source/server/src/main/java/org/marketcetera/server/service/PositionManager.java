package org.marketcetera.server.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to positions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface PositionManager
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
