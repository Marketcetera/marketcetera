package org.marketcetera.marketdata.core;

import org.marketcetera.util.log.*;


/* $License$ */

/**
 * Provides messages for the <code>marketdata</code> packages.
 *
 * @version $Id: Messages.java 82324 2012-04-09 20:56:08Z colin $
 * @since $Release$
 */
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("marketdata_core", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /*
     * The messages.
     */
    static final I18NMessage1P UNKNOWN_MARKETDATA_CONTENT = new I18NMessage1P(LOGGER,"unknown_marketdata_content"); //$NON-NLS-1$
    static final I18NMessage2P UNSUPPORTED_MARKETDATA_CONTENT = new I18NMessage2P(LOGGER,"unsupported_marketdata_content"); //$NON-NLS-1$
    static final I18NMessage0P MARKETDATA_REQUEST_FAILED = new I18NMessage0P(LOGGER,"marketdata_request_failed"); //$NON-NLS-1$
    static final I18NMessage2P EVENT_NOTIFICATION_FAILED = new I18NMessage2P(LOGGER,"event_notification_failed"); //$NON-NLS-1$
    static final I18NMessage0P UNABLE_TO_ACQUIRE_LOCK = new I18NMessage0P(LOGGER,"unable_to_acquire_lock"); //$NON-NLS-1$
    static final I18NMessage0P NO_SYMBOLS_OR_UNDERLYING_SYMBOLS = new I18NMessage0P(LOGGER,"no_symbols_or_underlying_symbols"); //$NON-NLS-1$
    static final I18NMessage0P NO_CONTENT = new I18NMessage0P(LOGGER,"no_content"); //$NON-NLS-1$
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"provider_description"); //$NON-NLS-1$
    static final I18NMessage2P CONTENT_REQUIRES_QUOTE_EVENTS = new I18NMessage2P(LOGGER,"content_requires_quote_events"); //$NON-NLS-1$
}
