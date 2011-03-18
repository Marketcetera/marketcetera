package org.marketcetera.server.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.marketcetera.core.position.PositionKey;
import org.marketcetera.server.service.PositionManager;
import org.marketcetera.systemmodel.persistence.PositionDao;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@Service
class PositionManagerImpl
        implements PositionManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.PositionManager#getPositionAsOf(org.marketcetera.trade.Instrument, java.util.Date)
     */
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    @Override
    public <T extends Instrument> BigDecimal getPositionAsOf(T inInstrument,
                                                             Date inDate)
    {
        return positionDao.getPositionAsOf(inInstrument,
                                           inDate);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.PositionManager#getAllPositionsAsOf(java.util.Date)
     */
    @Transactional(propagation=Propagation.SUPPORTS,readOnly=true)
    @Override
    public Map<PositionKey<Instrument>,BigDecimal> getAllPositionsAsOf(Date inDate)
    {
        return positionDao.getAllPositionsAsOf(inDate);
    }
    /**
     * data access object to use for access to persistent positions
     */
    @Autowired
    private PositionDao positionDao;
}
