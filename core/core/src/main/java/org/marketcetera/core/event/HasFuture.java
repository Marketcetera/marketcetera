package org.marketcetera.core.event;

import org.marketcetera.api.systemmodel.instruments.Future;

/* $License$ */

/**
 * Has a {@link org.marketcetera.core.trade.FutureImpl} attribute.
 *
 * @version $Id: HasFuture.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
public interface HasFuture
        extends HasInstrument
{
    /**
     * Gets the instrument value.
     *
     * @return a <code>Future</code> value
     */
    @Override
    public Future getInstrument();
}
