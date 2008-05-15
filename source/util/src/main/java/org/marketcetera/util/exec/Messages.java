package org.marketcetera.util.exec;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessageProvider;

/**
 * The internationalization constants used by this package.
 *
 * @author tlerios
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
        new I18NMessageProvider("util_exec");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage CANNOT_COPY_OUTPUT=
        new I18NMessage(LOGGER,"cannot_copy_output");
    static final I18NMessage CANNOT_EXECUTE=
        new I18NMessage(LOGGER,"cannot_execute");
    static final I18NMessage UNEXPECTED_TERMINATION=
        new I18NMessage(LOGGER,"unexpected_termination");
}
