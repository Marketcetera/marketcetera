package org.marketcetera.modules.fix;

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
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("fix_module",Messages.class.getClassLoader()); //$NON-NLS-1$
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P INITIATOR_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"initiator_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P ACCEPTOR_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"acceptor_provider_description"); //$NON-NLS-1$
    static final I18NMessage0P FIX_MESSAGE_BROADCAST_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,"fix_message_broadcast_provider_description"); //$NON-NLS-1$
    static final I18NMessage1P NO_FIX_MESSAGE_PUBLISHER = new I18NMessage1P(LOGGER,"no_fix_message_publisher"); //$NON-NLS-1$
}
