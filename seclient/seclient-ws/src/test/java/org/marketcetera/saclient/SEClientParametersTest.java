package org.marketcetera.saclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link SEClientParameters}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SEClientParametersTest {
    /**
     * Verify constructor and getters.
     */
    @Test
    public void constructAndGet() {
        //test invalid values.
        SEClientParameters scp = new SEClientParameters(null, null, null, null, -1);
        assertParameters(scp, null, null, null, null, -1);
        //test minimal values
        scp = new SEClientParameters("", "", "", "", 0);
        assertParameters(scp, "", "", "", "", 0);
        //test valid values
        String user = "you";
        String password = "nu";
        String url = "tcp://localghost:9001";
        String host = "localmost";
        int port = 61617;
        scp = new SEClientParameters(user, password, url, host, port);
        assertParameters(scp, user, password, url, host, port);
        //Changing the password array contents after construction doesn't change the password
        String expectedPass = scp.getPassword();
        assertEquals(expectedPass, password);
        password.toCharArray()[0] = 'l';
        assertEquals(expectedPass, scp.getPassword());
        //Changing password array contents after get doesn't change the password
        password = scp.getPassword();
        password.toCharArray()[0] = 'l';
        assertEquals(expectedPass, scp.getPassword());
    }
    private static void assertParameters(SEClientParameters inActual,
                                         String inExpectedUsername,
                                         String inExpectedPassword,
                                         String inExpectedURL,
                                         String inExpectedHostName,
                                         int inExpectedPort) {
        assertEquals(inExpectedUsername, inActual.getUsername());
        assertEquals(inExpectedPassword, inActual.getPassword());
        assertEquals(inExpectedURL, inActual.getURL());
        assertEquals(inExpectedHostName, inActual.getHostname());
        assertEquals(inExpectedPort, inActual.getPort());
    }
}
