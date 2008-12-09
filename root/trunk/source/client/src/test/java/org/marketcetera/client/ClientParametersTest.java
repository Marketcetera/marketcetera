package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link ClientParameters}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientParametersTest {
    @Test
    public void all() {
        String user = "you";
        char [] password = "pass".toCharArray();
        String url = "url";
        String hostname = "host";
        int port = Short.MAX_VALUE;
        String idPrefix = "prefix";
        ClientParameters cp = new ClientParameters(user, password, url,
                hostname, port, idPrefix);
        assertCP(cp, user, password, url, hostname, port, idPrefix);
        ClientParameters  cpNull = new ClientParameters(null, null, null, null, -1);
        assertCP(cpNull, null, null, null, null, -1, null);

        EqualityAssert.assertEquality(cp,
                new ClientParameters(user, password, url, hostname, port, idPrefix), cpNull,
                new ClientParameters(user, "nopass".toCharArray(), url, hostname, port, idPrefix),
                new ClientParameters("unuser", password, url, hostname, port, idPrefix),
                new ClientParameters(user, password, "urln", hostname, port, idPrefix),
                new ClientParameters(user, password, url, "hoho", port, idPrefix),
                new ClientParameters(user, password, url, hostname, 90, idPrefix),
                new ClientParameters(user, password, url, hostname, port, "wha"),

                new ClientParameters(null, password, url, hostname, port, idPrefix),
                new ClientParameters(user, null, url, hostname, port, idPrefix),
                new ClientParameters(user, password, null, hostname, port, idPrefix),
                new ClientParameters(user, password, url, null, port, idPrefix),
                new ClientParameters(user, password, url, hostname, -1, idPrefix),
                new ClientParameters(user, password, url, hostname, port, null),

                new ClientParameters(user + " ", password, url, hostname, port, idPrefix),
                new ClientParameters(user, "pass ".toCharArray(), url, hostname, port, idPrefix),
                new ClientParameters(user, password, url + " ", hostname, port, idPrefix),
                new ClientParameters(user, password, url, hostname + " ", port, idPrefix),
                new ClientParameters(user, password, url, hostname, port + 1, idPrefix),
                new ClientParameters(user, password, url, hostname, port , " ")
                );
        //Verify the other constructor
        cp = new ClientParameters(user, password, url, hostname, port);
        assertCP(cp, user, password, url, hostname, port, null);
    }

    private void assertCP(ClientParameters inCp, String inUser,
                          char[] inPassword, String inUrl,
                          String inHostname, int inPort, String inIDPrefix) {
        assertEquals(inUser, inCp.getUsername());
        assertEquals(inPassword, inCp.getPassword());
        assertEquals(inUrl, inCp.getURL());
        assertEquals(inHostname, inCp.getHostname());
        assertEquals(inPort, inCp.getPort());
        assertEquals(inIDPrefix, inCp.getIDPrefix());
    }
}
