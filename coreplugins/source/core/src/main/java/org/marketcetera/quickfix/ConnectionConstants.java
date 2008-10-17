package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

/**
 * Mostly a collection of constants for looking up values from config files
 * that have to do with connecting to FIX engines
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ConnectionConstants
{
    // JMS connection setup
    public static final String JMS_INCOMING_QUEUE_KEY = "jms.incoming.queue";            //$NON-NLS-1$
    public static final String JMS_OUTGOING_QUEUE_KEY = "jms.outgoing.queue";            //$NON-NLS-1$
    public static final String JMS_OUTGOING_TOPIC_KEY = "jms.outgoing.topic";            //$NON-NLS-1$
    public static final String JMS_INCOMING_TOPIC_KEY = "jms.incoming.topic";            //$NON-NLS-1$
    public static final String JMS_CONNECTION_FACTORY_KEY = "jms.connection.factory";    //$NON-NLS-1$
    public static final String JMS_CONTEXT_FACTORY_KEY = "jms.context.factory";          //$NON-NLS-1$
    public static final String JMS_URL_KEY = "jms.url";                                  //$NON-NLS-1$
    
    public static final String MARKETDATA_KEY_BASE = "marketdata";                       //$NON-NLS-1$
    public static final String MARKETDATA_STARTUP_KEY = MARKETDATA_KEY_BASE+".startup";  //$NON-NLS-1$
    public static final String MARKETDATA_USER_SUFFIX = "user";                          //$NON-NLS-1$
    public static final String MARKETDATA_PASSWORD_SUFFIX = "password";                  //$NON-NLS-1$
    public static final String MARKETDATA_URL_SUFFIX = "url";                            //$NON-NLS-1$
    public static final String MARKETDATA_SERVICE_SUFFIX = "service"; //$NON-NLS-1$
    
    public static final String WEB_APP_HOST_KEY = "web.app.host";                        //$NON-NLS-1$
    public static final String WEB_APP_PORT_KEY = "web.app.port";                        //$NON-NLS-1$

    public static final String ORDER_ID_PREFIX_KEY = "order.id.prefix";                  //$NON-NLS-1$
    public static final String FIX_VERSION_KEY = "fix.version";                          //$NON-NLS-1$

    
    // ID factory
    public static final String ID_FACTORY_BASE_KEY = "id.factory.base";                  //$NON-NLS-1$

    /** MarketData request - show all known symbols */
    public static final int MARKET_DATA_REQUEST_SHOW_ALL_KNOWN_SYMBOLS = 0;
}
