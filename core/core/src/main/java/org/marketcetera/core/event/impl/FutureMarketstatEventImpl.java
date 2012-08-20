package org.marketcetera.core.event.impl;

import javax.annotation.concurrent.ThreadSafe;
import org.marketcetera.core.event.FutureEvent;
import org.marketcetera.core.event.MarketstatEvent;
import org.marketcetera.core.event.beans.FutureBean;
import org.marketcetera.core.event.beans.MarketstatBean;
import org.marketcetera.core.trade.DeliveryType;
import org.marketcetera.core.trade.Future;
import org.marketcetera.core.trade.FutureType;
import org.marketcetera.core.trade.FutureUnderlyingAssetType;
import org.marketcetera.core.trade.StandardType;

/* $License$ */

/**
 * Provides a Future implementation of {@link MarketstatEvent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: FutureMarketstatEventImpl.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ThreadSafe
class FutureMarketstatEventImpl
        extends AbstractMarketstatEventImpl
        implements FutureEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getDeliveryType()
     */
    @Override
    public DeliveryType getDeliveryType()
    {
        return future.getDeliveryType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getProviderSymbol()
     */
    @Override
    public String getProviderSymbol()
    {
        return future.getProviderSymbol();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getStandardType()
     */
    @Override
    public StandardType getStandardType()
    {
        return future.getStandardType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getType()
     */
    @Override
    public FutureType getType()
    {
        return future.getType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getUnderylingAssetType()
     */
    @Override
    public FutureUnderlyingAssetType getUnderylingAssetType()
    {
        return future.getUnderlyingAssetType();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFuture#getInstrument()
     */
    @Override
    public Future getInstrument()
    {
        return (Future)super.getInstrument();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.FutureEvent#getContractSize()
     */
    @Override
    public int getContractSize()
    {
        return future.getContractSize();
    }
    /**
     * Create a new FutureMarketstatEventImpl instance.
     *
     * @param inMarketstatBean a <code>MarketstatBean</code> value
     * @throws IllegalArgumentException if <code>MessageId</code> &lt; 0
     * @throws IllegalArgumentException if <code>Timestamp</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>Instrument</code> is <code>null</code>
     */
    FutureMarketstatEventImpl(MarketstatBean inMarketstat,
                              FutureBean inFuture)
    {
        super(inMarketstat);
        future = inFuture;
        future.validate();
    }
    /**
     * the future attributes 
     */
    private final FutureBean future;
    private static final long serialVersionUID = 1L;
}
