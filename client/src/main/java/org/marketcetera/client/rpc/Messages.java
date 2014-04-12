package org.marketcetera.client.rpc;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Internationalized messages used by this package.
 *
 * @author anshul@marketcetera.com
 * @version $Id: Messages.java 16154 2012-07-14 16:34:05Z colin $
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("client_rpc",Messages.class.getClassLoader());  //$NON-NLS-1$ 
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage2P SERVER_STARTING = new I18NMessage2P(LOGGER,"server_starting");   //$NON-NLS-1$
    static final I18NMessage0P SERVER_STOPPING = new I18NMessage0P(LOGGER,"server_stopping");   //$NON-NLS-1$
}
