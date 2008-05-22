package org.marketcetera.util.exec;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessageProvider;

@ClassVersion("$Id$")
public interface Messages
{

    /**
     * The message provider.
     */

    static final I18NMessageProvider PROVIDER=
        new I18NMessageProvider("util_exec");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage1P CANNOT_COPY_OUTPUT=
        new I18NMessage1P(LOGGER,"cannot_copy_output");
    static final I18NMessage1P CANNOT_EXECUTE=
        new I18NMessage1P(LOGGER,"cannot_execute");
    static final I18NMessage1P UNEXPECTED_TERMINATION=
        new I18NMessage1P(LOGGER,"unexpected_termination");
}
