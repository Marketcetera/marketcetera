package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NMessageProvider;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.Locale;

/* $License$ */
/**
 * Tests that all persist messages are mapped correctly
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class MessagesTest {
    @Test
    public void messagesExist() {
        I18NMessageProvider.setLocale(Locale.US);
        assertEquals("Entity Remote Services is not initialized. Ensure that spring configuration initializes a subclass of org.marketcetera.persist.EntityRemoteServices",Messages.ERS_NOT_INITIALIZED.getText());
        assertEquals("Cannot create Entity Remote Services of class a. An instance of Entity Remote Services of class b already exists",Messages.ERS_ALREADY_INITIALIZED.getText("a","b"));
        assertEquals("JPA Vendor is not initialized. Ensure that spring configuration initializes a subclass of org.marketcetera.persist.JPAVendor",Messages.JPA_VENDOR_NOT_INITIALIZED.getText());
        assertEquals("Cannot create JPA Vendor of class a. An instance of JPA Vendor of class b already exists",Messages.JPA_VENDOR_ALREADY_INITIALIZED.getText("a","b"));
        assertEquals("Unsupported filter specified. Specified filter 'a' doesn't match the pattern 'b'. Specify a filter value that matches the pattern 'b'",Messages.INVALID_STRING_FILTER.getText("a","b"));
        assertEquals("Unexpected issue encountered when translating persistence exceptions",Messages.EXCEPTION_TRANSLATE_ISSUE.getText());
        assertEquals("Name cannot be empty, specify a name for the entity and retry save.",Messages.UNSPECIFIED_NAME_ATTRIBUTE.getText());
        assertEquals("The specified name 'a' cannot be longer than 255 characters. Specify a name that is between 1 and 255 characters long and retry save.",Messages.NAME_ATTRIBUTE_TOO_LONG.getText("a"));
        assertEquals("The specified name 'a' is not valid. A valid name should match the regex pattern 'b'. Specify a valid name for the entity and retry save.",Messages.NAME_ATTRIBUTE_INVALID.getText("a","b"));
        assertEquals("Hibernate is not correctly integrated. Make sure that correct version of hibernate is being used. Contact product support for more help.",Messages.HIBERNATE_INTEGRATION_ISSUE.getText());
        assertEquals("Unexpected setup issue encountered. Contact product support for more help.",Messages.UNEXPECTED_SETUP_ISSUE.getText());
    }
}
