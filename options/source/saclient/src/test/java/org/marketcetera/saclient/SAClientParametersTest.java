package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link SAClientParameters}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class SAClientParametersTest {
    /**
     * Verify constructor and getters.
     */
    @Test
    public void constructAndGet() {
        //test invalid values.
        SAClientParameters scp = new SAClientParameters(null, null, null, null, -1);
        assertParameters(scp, null, null, null, null, -1);
        //test minimal values
        scp = new SAClientParameters("", "".toCharArray(), "", "", 0);
        assertParameters(scp, "", "".toCharArray(), "", "", 0);
        //test valid values
        String user = "you";
        char[] password = "nu".toCharArray();
        String url = "tcp://localghost:9001";
        String host = "localmost";
        int port = 61617;
        scp = new SAClientParameters(user, password, url, host, port);
        assertParameters(scp, user, password, url, host, port);
        //Changing the password array contents after construction doesn't change the password
        char [] expectedPass = scp.getPassword();
        assertArrayEquals(expectedPass, password);
        password[0] = 'l';
        assertArrayEquals(expectedPass, scp.getPassword());
        //Changing password array contents after get doesn't change the password
        password = scp.getPassword();
        password[0] = 'l';
        assertArrayEquals(expectedPass, scp.getPassword());
    }

    /**
     * Verify equals() and hashcode().
     */
    @Test
    public void equality() {
        EqualityAssert.assertEquality(
                new SAClientParameters("you","nu".toCharArray(), "tcp://zoo:91", "ghost", 2001),
                new SAClientParameters("you", "nu".toCharArray(), "tcp://zoo:91", "ghost", 2001),
                new SAClientParameters("your", "nu".toCharArray(), "tcp://zoo:91", "ghost", 2001),
                new SAClientParameters("you", "nul".toCharArray(), "tcp://zoo:91", "ghost", 2001),
                new SAClientParameters("you", "nu".toCharArray(), "tcp://zoo:919", "ghost", 2001),
                new SAClientParameters("you", "nu".toCharArray(), "tcp://zoo:91", "most", 2001),
                new SAClientParameters("you", "nu".toCharArray(), "tcp://zoo:91", "ghost", 2003),
                new SAClientParameters(null, null, null, null, -1)
                );
    }

    /**
     * Verify toString().
     */
    @Test
    public void string() {
        SAClientParameters parm = new SAClientParameters("you", "nu".toCharArray(), "tcp://zoo:91", "ghost", 2001);
        //verify to string contains all values except password
        String string = parm.toString();
        assertTrue(string, string.contains("you"));
        assertFalse(string, string.contains("nu"));
        assertTrue(string, string.contains("tcp://zoo:91"));
        assertTrue(string, string.contains("ghost"));
        assertTrue(string, string.contains("2001"));
    }
    
    private static void assertParameters(SAClientParameters inActual,
                                         String inExpectedUsername,
                                         char[] inExpectedPassword,
                                         String inExpectedURL,
                                         String inExpectedHostName,
                                         int inExpectedPort) {
        assertEquals(inExpectedUsername, inActual.getUsername());
        assertArrayEquals(inExpectedPassword, inActual.getPassword());
        assertEquals(inExpectedURL, inActual.getURL());
        assertEquals(inExpectedHostName, inActual.getHostname());
        assertEquals(inExpectedPort, inActual.getPort());
    }
}
