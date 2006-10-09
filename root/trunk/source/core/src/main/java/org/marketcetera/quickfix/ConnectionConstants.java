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
    // FIX related
    public static final String FIX_VERSION_KEY = "fix.version";
    public final static String FIX_SENDER_COMP_ID = "fix.sendercompid";
    public final static String FIX_TARGET_COMP_ID = "fix.targetcompid";
    public final static String FIX_SERVER_PORT = "fix.server.port";
    public final static String FIX_SERVER_ADDRESS = "fix.server.address";

    // JMS connection setup
    public static final String JMS_INCOMING_QUEUE_KEY = "jms.incoming.queue";
    public static final String JMS_OUTGOING_QUEUE_KEY = "jms.outgoing.queue";
    public static final String JMS_OUTGOING_TOPIC_KEY = "jms.outgoing.topic";
    public static final String JMS_INCOMING_TOPIC_KEY = "jms.incoming.topic";
    public static final String JMS_CONNECTION_FACTORY_KEY = "jms.connection.factory";
    public static final String JMS_CONTEXT_FACTORY_KEY = "jms.context.factory";
    public static final String JMS_URL_KEY = "jms.url";

    public static final String WEB_APP_HOST_KEY = "web.app.host";

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
