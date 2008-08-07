package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for persistence classes
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("core_persist");  //$NON-NLS-1$
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);

    /**
     * The messages.
     */

    static final I18NMessage0P ERS_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER,"ers_not_initialized");  //$NON-NLS-1$
    static final I18NMessage2P ERS_ALREADY_INITIALIZED =
            new I18NMessage2P(LOGGER,"ers_already_initialized");  //$NON-NLS-1$
    static final I18NMessage0P JPA_VENDOR_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER,"jpa_vendor_not_initialized");  //$NON-NLS-1$
    static final I18NMessage0P DB_VENDOR_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER,"db_vendor_not_initialized");  //$NON-NLS-1$
    static final I18NMessage2P JPA_VENDOR_ALREADY_INITIALIZED =
            new I18NMessage2P(LOGGER,"jpa_vendor_already_initialized");  //$NON-NLS-1$
    static final I18NMessage2P DB_VENDOR_ALREADY_INITIALIZED =
            new I18NMessage2P(LOGGER,"db_vendor_already_initialized");  //$NON-NLS-1$
    static final I18NMessage2P INVALID_STRING_FILTER =
            new I18NMessage2P(LOGGER,"invalid_string_filter");  //$NON-NLS-1$
    static final I18NMessage0P EXCEPTION_TRANSLATE_ISSUE =
            new I18NMessage0P(LOGGER,"exception_translate_issue");  //$NON-NLS-1$
    static final I18NMessage0P UNSPECIFIED_NAME_ATTRIBUTE =
            new I18NMessage0P(LOGGER,"unspecified_name_attribute");  //$NON-NLS-1$
    static final I18NMessage1P NAME_ATTRIBUTE_TOO_LONG =
            new I18NMessage1P(LOGGER,"name_attribute_too_long");  //$NON-NLS-1$
    static final I18NMessage2P NAME_ATTRIBUTE_INVALID =
            new I18NMessage2P(LOGGER,"name_attribute_invalid");  //$NON-NLS-1$
    static final I18NMessage0P HIBERNATE_INTEGRATION_ISSUE =
            new I18NMessage0P(LOGGER,"hibernate_integration_issue");  //$NON-NLS-1$
    static final I18NMessage0P UNEXPECTED_SETUP_ISSUE =
            new I18NMessage0P(LOGGER,"unexpected_setup_issue");  //$NON-NLS-1$
    static final I18NMessage0P UNEXPECTED_ERROR =
            new I18NMessage0P(LOGGER,"unexpected_error");  //$NON-NLS-1$
    static final I18NMessage3P UNSUPPORTED_CHARACTER =
            new I18NMessage3P(LOGGER,"unsupported_character");  //$NON-NLS-1$
    static final I18NMessage1P OPTMISTIC_LOCK_ERROR =
            new I18NMessage1P(LOGGER,"optmistic_lock_error");  //$NON-NLS-1$
    static final I18NMessage1P DEFAULT_ENTITY_NAME =
            new I18NMessage1P(LOGGER,"default_entity_name");  //$NON-NLS-1$
    static final I18NMessage0P UNKNOWN_ENTITY_NAME =
            new I18NMessage0P(LOGGER,"unknown_entity_name");  //$NON-NLS-1$
    static final I18NMessage1P ENTITY_EXISTS_INSERT_ERROR =
            new I18NMessage1P(LOGGER,"entity_exists_error");  //$NON-NLS-1$
    static final I18NMessage0P ENTITY_EXISTS_GENERIC_ERROR =
            new I18NMessage0P(LOGGER,"entity_exists_generic_error");  //$NON-NLS-1$
}
