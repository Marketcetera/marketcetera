package org.marketcetera.trade.client;

/* $License$ */

/**
 * Constructs {@link TradingClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradingClientFactory<ParameterClazz>
{
    /**
     * Create a new {@link TradingClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>TradingClient</code> value
     */
    TradingClient create(ParameterClazz inParameterClazz);
}
