package org.marketcetera.core.attributes;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/28/12 4:57 PM
 */

public interface Messages {

    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core", //$NON-NLS-1$
            Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);


    static final I18NMessage0P ERROR_FETCHING_VERSION_PROPERTIES = new I18NMessage0P(LOGGER,
            "error_fetching_version_properties");   //$NON-NLS-1$

}
