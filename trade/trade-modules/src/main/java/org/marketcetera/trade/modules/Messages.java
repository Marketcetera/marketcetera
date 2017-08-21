package org.marketcetera.trade.modules;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Provides messages for this package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("trade_module",Messages.class.getClassLoader()); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P ORDER_CONVERTER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"order_converter_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P TRADE_MESSAGE_CONVERTER_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"trade_message_converter_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P TRADE_MESSAGE_BROADCAST_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"trade_message_broadcast_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P OUTGOING_MESSAGE_CACHE_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"outgoing_message_cache_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P OUTGOING_MESSAGE_PERSISTENCE_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"outgoing_message_persistence_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P TRADE_MESSAGE_PERSISTENCE_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"trade_message_persistence_provider_description"); //$NON-NLS-1$
    static final I18NMessage1P NO_TRADE_MESSAGE_PUBLISHER = new I18NMessage1P(LOGGER,"no_trade_message_publisher"); //$NON-NLS-1$
    static final I18NMessage1P PERSISTED_REPLY = new I18NMessage1P(LOGGER,"persisted_reply"); //$NON-NLS-1$
}
