package org.marketcetera.core.util.exec;

import org.marketcetera.core.util.log.I18NLoggerProxy;
import org.marketcetera.core.util.log.I18NMessage1P;
import org.marketcetera.core.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: Messages.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_exec"); //$NON-NLS-1$

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P CANNOT_COPY_OUTPUT=
        new I18NMessage1P(LOGGER,"cannot_copy_output"); //$NON-NLS-1$
    static final I18NMessage1P CANNOT_EXECUTE=
        new I18NMessage1P(LOGGER,"cannot_execute"); //$NON-NLS-1$
    static final I18NMessage1P UNEXPECTED_TERMINATION=
        new I18NMessage1P(LOGGER,"unexpected_termination"); //$NON-NLS-1$
}
