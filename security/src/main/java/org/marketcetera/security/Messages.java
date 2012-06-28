package org.marketcetera.security;

import org.marketcetera.util.log.*;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Provides messages for the security package.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Messages.java 82318 2012-03-22 22:37:06Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Messages.java 82318 2012-03-22 22:37:06Z colin $")
public interface Messages
{
    /**
     * The message provider.
     */
    static final I18NMessageProvider PROVIDER = new I18NMessageProvider("security", //$NON-NLS-1$
                                                                        Messages.class.getClassLoader());
    /**
     * The logger.
     */
    static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);
    // the messages
    static final I18NMessage0P NULL_SALT = new I18NMessage0P(LOGGER,
                                                             "null_salt"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ENCODER = new I18NMessage0P(LOGGER,
                                                                "null_encoder"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ENCODING = new I18NMessage0P(LOGGER,
                                                                 "null_encoding"); //$NON-NLS-1$
    static final I18NMessage1P INVALID_ENCODING = new I18NMessage1P(LOGGER,
                                                                    "invalid_encoding"); //$NON-NLS-1$
    static final I18NMessage2P ITERATION_COUNT_TOO_LOW = new I18NMessage2P(LOGGER,
                                                                           "iteration_count_too_low"); //$NON-NLS-1$
    static final I18NMessage2P KEY_LENGTH_TOO_SHORT = new I18NMessage2P(LOGGER,
                                                                        "key_length_too_short"); //$NON-NLS-1$
    static final I18NMessage2P SALT_TOO_SHORT = new I18NMessage2P(LOGGER,
                                                                  "salt_too_short"); //$NON-NLS-1$
    static final I18NMessage0P NULL_ALGORITHM = new I18NMessage0P(LOGGER,
                                                                  "null_algorithm"); //$NON-NLS-1$
    static final I18NMessage0P NULL_SOURCE = new I18NMessage0P(LOGGER,
                                                               "null_source"); //$NON-NLS-1$
}
