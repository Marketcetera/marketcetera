package com.marketcetera.marketdata.reuters;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Messages for Reuters market data package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Messages.java 82351 2012-05-04 21:46:58Z colin $")
public interface Messages
{
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("reuters",
                                                                        Messages.class.getClassLoader());

    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    public static final I18NMessage0P PROVIDER_DESCRIPTION = new I18NMessage0P(LOGGER,
                                                                               "provider_description"); //$NON-NLS-1$
}
