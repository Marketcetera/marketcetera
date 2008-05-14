package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.log.I18NMessage;

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
        new I18NMessageProvider("util_except");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage THREAD_INTERRUPTED=
        new I18NMessage("thread_interrupted");
    static final I18NMessage THROWABLE_IGNORED=
        new I18NMessage("throwable_ignored");
}
