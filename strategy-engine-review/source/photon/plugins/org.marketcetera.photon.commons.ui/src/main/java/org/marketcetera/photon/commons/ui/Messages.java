package org.marketcetera.photon.commons.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.misc.ClassVersion;


/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
final class Messages {

    static I18NMessage0P JFACE_UTILS_GENERIC_EXCEPTION_OCCURRED;
    static I18NMessage0P JFACE_UTILS_OPERATION_FAILED__DIALOG_TITLE;
    static I18NMessage1P COLOR_MANAGER_PROVIDER_NULL_DESCRIPTOR;
    static I18NMessage1P COLOR_MANAGER_UNKNOWN_DESCRIPTOR;
    static I18NMessage0P COLOR_MANAGER_ALREADY_INITIALIZED;
    static I18NMessage0P SWT_UTILS_INVALID_THREAD;
    static I18NMessage1P LOCALIZED_LABEL__FORMAT_PATTERN;

    static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
