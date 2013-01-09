package org.marketcetera.ors.info;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface Messages
{
    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER= 
        new I18NMessageProvider("ors_info");  //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER= 
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage0P NULL_KEY=
        new I18NMessage0P(LOGGER,"null_key"); //$NON-NLS-1$
    static final I18NMessage1P MISSING_VALUE=
        new I18NMessage1P(LOGGER,"missing_value"); //$NON-NLS-1$
    static final I18NMessage1P NULL_VALUE=
        new I18NMessage1P(LOGGER,"null_value"); //$NON-NLS-1$
    static final I18NMessage3P BAD_CLASS_VALUE=
        new I18NMessage3P(LOGGER,"bad_class_value"); //$NON-NLS-1$
    static final I18NMessage3P VALUE_EXISTS=
        new I18NMessage3P(LOGGER,"value_exists"); //$NON-NLS-1$
}
