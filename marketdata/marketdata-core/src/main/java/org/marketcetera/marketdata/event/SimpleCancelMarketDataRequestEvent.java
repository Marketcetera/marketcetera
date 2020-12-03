package org.marketcetera.marketdata.event;

/* $License$ */

/**
 * Provides a POJO {@link CancelMarketDataRequestEvent} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleCancelMarketDataRequestEvent
        extends AbstractMarketDataRequestCancelEvent
        implements CancelMarketDataRequestEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleCancelMarketDataRequestEvent [marketDataRequestId=").append(getMarketDataRequestId())
                .append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleCancelMarketDataRequestEvent instance.
     *
     * @param inMarketDataRequestId a <code>String</code> value
     */
    public SimpleCancelMarketDataRequestEvent(String inMarketDataRequestId)
    {
        super(inMarketDataRequestId);
    }
}
