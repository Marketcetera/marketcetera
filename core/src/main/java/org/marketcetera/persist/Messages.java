package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for persistence classes
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("core_persist");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    /**
     * The messages.
     */
    static final I18NMessage0P UNSPECIFIED_NAME_ATTRIBUTE = new I18NMessage0P(LOGGER,"unspecified_name_attribute");  //$NON-NLS-1$
    static final I18NMessage1P NAME_ATTRIBUTE_TOO_LONG = new I18NMessage1P(LOGGER,"name_attribute_too_long");  //$NON-NLS-1$
    static final I18NMessage2P NAME_ATTRIBUTE_INVALID = new I18NMessage2P(LOGGER,"name_attribute_invalid");  //$NON-NLS-1$
}
