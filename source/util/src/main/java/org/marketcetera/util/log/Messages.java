package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

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
        new I18NMessageProvider("util_log");

    /**
     * The logger.
     */

    static final I18NLoggerProxy LOGGER=
        new I18NLoggerProxy(PROVIDER);

    /*
     * The messages.
     */

    static final I18NMessage MESSAGE_NOT_FOUND=
        new I18NMessage("message_not_found");
    static final I18NMessage UNEXPECTED_EXCEPTION=
        new I18NMessage("unexpected_exception");
}
