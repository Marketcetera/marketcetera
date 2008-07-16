package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.*;

/* $License$ */
/**
 * Messages for persistence classes
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
interface Messages {
    /**
     * The message provider
     */
    static final I18NMessageProvider PROVIDER =
            new I18NMessageProvider("persist");
    /**
     * The message logger.
     */
    static final I18NLoggerProxy LOGGER =
            new I18NLoggerProxy(PROVIDER);
    static final I18NMessage0P ERS_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER,"ers_not_initialized");
    static final I18NMessage2P ERS_ALREADY_INITIALIZED =
            new I18NMessage2P(LOGGER,"ers_already_initialized");
    static final I18NMessage0P JPA_VENDOR_NOT_INITIALIZED =
            new I18NMessage0P(LOGGER,"jpa_vendor_not_initialized");
    static final I18NMessage2P JPA_VENDOR_ALREADY_INITIALIZED =
            new I18NMessage2P(LOGGER,"jpa_vendor_already_initialized");
    static final I18NMessage2P INVALID_STRING_FILTER =
            new I18NMessage2P(LOGGER,"invalid_string_filter");
    static final I18NMessage0P EXCEPTION_TRANSLATE_ISSUE =
            new I18NMessage0P(LOGGER,"exception_translate_issue");
    static final I18NMessage0P UNSPECIFIED_NAME_ATTRIBUTE =
            new I18NMessage0P(LOGGER,"unspecified_name_attribute");
    static final I18NMessage1P NAME_ATTRIBUTE_TOO_LONG =
            new I18NMessage1P(LOGGER,"name_attribute_too_long");
    static final I18NMessage2P NAME_ATTRIBUTE_INVALID =
            new I18NMessage2P(LOGGER,"name_attribute_invalid");
    static final I18NMessage0P HIBERNATE_INTEGRATION_ISSUE =
            new I18NMessage0P(LOGGER,"hibernate_integration_issue");
    static final I18NMessage0P UNEXPECTED_SETUP_ISSUE =
            new I18NMessage0P(LOGGER,"unexpected_setup_issue");
}
