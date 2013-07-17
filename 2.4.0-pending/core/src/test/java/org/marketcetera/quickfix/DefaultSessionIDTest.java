package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import quickfix.SessionID;

import java.lang.reflect.Field;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class DefaultSessionIDTest extends TestCase {
    public DefaultSessionIDTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DefaultSessionIDTest.class);
    }

    public void testDefaultSessionIDTest() throws Exception {
        Class aClass = quickfix.SessionSettings.class;
        Field theField = aClass.getDeclaredField("DEFAULT_SESSION_ID"); //$NON-NLS-1$
        theField.setAccessible(true);
        Object defaultSessionID = theField.get(null);
        assertEquals(SessionID.class, defaultSessionID.getClass());
        assertEquals("DEFAULT:->", defaultSessionID.toString()); //$NON-NLS-1$
    }
}
