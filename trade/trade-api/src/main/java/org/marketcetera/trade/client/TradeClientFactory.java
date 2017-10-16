package org.marketcetera.trade.client;

/* $License$ */

/**
 * Constructs {@link TradeClient} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TradeClientFactory<ParameterClazz>
{
    /**
     * Create a new {@link TradeClient} instance.
     *
     * @param inParameterClazz a <code>ParameterClazz</code> value
     * @return a <code>TradingClient</code> value
     */
    TradeClient create(ParameterClazz inParameterClazz);
}
