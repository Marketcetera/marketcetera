package org.marketcetera.core.attributes;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 6/28/12 3:01 PM
 */

public interface Messages {
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

    I18NMessage0P ERROR_FETCHING_VERSION_PROPERTIES = new I18NMessage0P(Messages.LOGGER,
                                                                                     "error_fetching_version_properties");   //$NON-NLS-1$
}
