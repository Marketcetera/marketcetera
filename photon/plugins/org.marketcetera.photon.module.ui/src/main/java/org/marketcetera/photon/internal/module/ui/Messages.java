package org.marketcetera.photon.internal.module.ui;

import org.marketcetera.photon.commons.ReflectiveMessages;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.util.log.I18NMessage1P;
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
public class Messages {
	
	static I18NMessage0P SINK_CONSOLE__NAME;
	public static I18NMessage0P NEW_PROPERTY_DIALOG__TITLE;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_KEY__LABEL;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_INVALID_INITIAL_CHARACTER_ERROR;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_CONTAINS_SPACE_ERROR;
	public static I18NMessage1P NEW_PROPERTY_DIALOG_INVALID_CHARACTER_ERROR;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_END_WITH_PERIOD_ERROR;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_VALUE__LABEL;
	public static I18NMessage0P NEW_PROPERTY_DIALOG_INSTANCE_DEFAULTS__LABEL;
	static I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_BUTTON__LABEL;
	static I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_ADD_ACTION__LABEL;
	static I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_DELETE_ACTION__LABEL;
	static I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_RESTART_WARNING;
	static I18NMessage0P MODULE_PROPERTIES_PREFERENCE_PAGE_PASSWORD_MATCH;
	public static I18NMessage0P MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE_SEE_DETAILS;
	public static I18NMessage0P MODULE_ATTRIBUTE_PREFERENCE_PAGE_UPDATE_FAILURE;
	static I18NMessage2P SINK_CONSOLE_CONTROLLER_MESSAGE_FORMAT;
	
	static {
        ReflectiveMessages.init(Messages.class);
    }

    private Messages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
