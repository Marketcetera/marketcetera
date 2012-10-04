package org.marketcetera.core.quickfix;

import java.lang.reflect.Field;

import junit.framework.Test;
import junit.framework.TestCase;

import org.marketcetera.core.MarketceteraTestSuite;

import quickfix.SessionID;

/**
 * @version $Id: DefaultSessionIDTest.java 16063 2012-01-31 18:21:55Z colin $
 */
public class DefaultSessionIDTest extends TestCase {
    public DefaultSessionIDTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(DefaultSessionIDTest.class);
    }

    public void testDefaultSessionIDTest() throws Exception {
        Class<?> aClass = quickfix.SessionSettings.class;
        Field theField = aClass.getDeclaredField("DEFAULT_SESSION_ID"); //$NON-NLS-1$
        theField.setAccessible(true);
        Object defaultSessionID = theField.get(null);
        assertEquals(SessionID.class, defaultSessionID.getClass());
        assertEquals("DEFAULT:->", defaultSessionID.toString()); //$NON-NLS-1$
    }
}
