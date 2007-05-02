package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

/**
 * Mostly a collection of constants for looking up values from config files
 * that have to do with connecting to FIX engines
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ConnectionConstants
{
    // JMS connection setup
    public static final String JMS_INCOMING_QUEUE_KEY = "jms.incoming.queue";
    public static final String JMS_OUTGOING_QUEUE_KEY = "jms.outgoing.queue";
    public static final String JMS_OUTGOING_TOPIC_KEY = "jms.outgoing.topic";
    public static final String JMS_INCOMING_TOPIC_KEY = "jms.incoming.topic";
    public static final String JMS_CONNECTION_FACTORY_KEY = "jms.connection.factory";
    public static final String JMS_CONTEXT_FACTORY_KEY = "jms.context.factory";
    public static final String JMS_URL_KEY = "jms.url";
    
    public static final String MARKETDATA_KEY_BASE = "marketdata";
    public static final String MARKETDATA_STARTUP_KEY = MARKETDATA_KEY_BASE+".startup";
    public static final String MARKETDATA_USER_SUFFIX = "user";
    public static final String MARKETDATA_PASSWORD_SUFFIX = "password";
    public static final String MARKETDATA_URL_SUFFIX = "url";
    
    public static final String WEB_APP_HOST_KEY = "web.app.host";
    public static final String WEB_APP_PORT_KEY = "web.app.port";

    // ID factory
    public static final String ID_FACTORY_BASE_KEY = "id.factory.base";

    // Quote feed
    public static final String QUOTE_FEED_SERVER = "quotefeed.server";
    public static final String QUOTE_FEED_PORT = "quotefeed.port";
    public static final String QUOTE_FEED_USER = "quotefeed.user";
    public static final String QUOTE_FEED_PWD = "quotefeed.pwd";


    /** MarketData request - show all known symbols */
    public static final int MARKET_DATA_REQUEST_SHOW_ALL_KNOWN_SYMBOLS = 0;
}
