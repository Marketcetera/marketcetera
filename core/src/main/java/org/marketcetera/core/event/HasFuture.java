package org.marketcetera.core.event;

import org.marketcetera.core.trade.Future;

/* $License$ */

/**
 * Has a {@link org.marketcetera.core.trade.Future} attribute.
 *
 * @version $Id$
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
