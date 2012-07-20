package org.marketcetera.modules.requester;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Messages.java 82384 2012-07-20 19:09:59Z colin $")
public interface Messages
{
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("modules_requester",  //$NON-NLS-1$ 
                                                                        Messages.class.getClassLoader());
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                        "provider_description");   //$NON-NLS-1$
}
