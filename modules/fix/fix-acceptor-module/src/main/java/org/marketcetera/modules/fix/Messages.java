package org.marketcetera.modules.fix;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/* $License$ */

/**
 * Messages for the FIX modules.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("fix_module", Messages.class.getClassLoader());  //$NON-NLS-1$
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P FIX_ACCEPTOR_PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                                     "fix_acceptor_provider_description"); //$NON-NLS-1$
}
