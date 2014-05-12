package org.marketcetera.photon.internal.module;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * The internationalization constants used by this package.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
final class Messages {
	
	static I18NMessage0P ACTIVATOR_FAILED_TO_STOP_MODULE_MANAGER;
	static I18NMessage0P ACTIVATOR_FAILED_TO_SAVE_PREFERENCES;
	static I18NMessage0P PREFERENCE_ATTRIBUTE_DEFAULTS_FAILED_TO_SAVE_PREFERENCES;
	static I18NMessage2P MODULE_ATTRIBUTE_SUPPORT_FAILED_TO_GET_ATTRIBUTE;
	static I18NMessage2P MODULE_ATTRIBUTE_SUPPORT_FAILED_TO_SET_ATTRIBUTE;
	static I18NMessage2P NOTIFICATION_HANDLER_ENHANCED_SUBJECT_FORMAT;
	
	static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
