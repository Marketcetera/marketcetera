package org.marketcetera.server.service.impl;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.server.service.IdFactory;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
class IdFactoryImpl
        implements IdFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.IdFactory#getId()
     */
    @Override
    public long getId()
    {
        return counter.getAndIncrement();
    }
    /**
     * Create a new IdFactoryImpl instance.
     */
    public IdFactoryImpl()
    {
        counter = new AtomicLong(0);
    }
    /**
     * Create a new IdFactoryImpl instance.
     *
     * @param inSeed
     */
    public IdFactoryImpl(long inSeed)
    {
        counter = new AtomicLong(inSeed);
    }
    /**
     * 
     */
    private final AtomicLong counter;
}
