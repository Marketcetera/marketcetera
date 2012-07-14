package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.EqualityAssert;
import org.marketcetera.util.test.UnicodeData;
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
        String user = UnicodeData.COMBO;
        String passString = UnicodeData.COMBO;
        char [] password = passString.toCharArray();
        String url = "url";
        String hostname = "host";
        int port = Short.MAX_VALUE;
        String idPrefix = "prefix";
        int heartbeatInterval = ClientParameters.DEFAULT_HEARTBEAT_INTERVAL+1;
        //Use copy of all parameters to ensure that we test object equality,
        //not reference equality
        ClientParameters cp = new ClientParameters(
                new String(user), passString.toCharArray(), new String(url),
                new String(hostname), port, new String(idPrefix),
                heartbeatInterval);
        assertCP(cp, user, password, url, hostname, port, idPrefix, heartbeatInterval);
        ClientParameters  cpNull = new ClientParameters(null, null, null, null, -1, null, -1);
        assertCP(cpNull, null, null, null, null, -1, null, -1);

        EqualityAssert.assertEquality(cp,
                new ClientParameters(user, password, url, hostname, port, idPrefix, heartbeatInterval),
                cpNull,
                new ClientParameters(user, "nopass".toCharArray(), url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters("unuser", password, url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, "urln", hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, "hoho", port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, 90, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, port, "wha", heartbeatInterval),

                new ClientParameters(null, password, url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, null, url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, null, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, null, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, -1, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, port, null, heartbeatInterval),

                new ClientParameters(user + " ", password, url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, "pass ".toCharArray(), url, hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url + " ", hostname, port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname + " ", port, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, port + 1, idPrefix, heartbeatInterval),
                new ClientParameters(user, password, url, hostname, port , " ", heartbeatInterval),
                new ClientParameters(user, password, url, hostname, port, idPrefix, heartbeatInterval+1)
                );
        //Verify the other constructors
        cp = new ClientParameters(user, password, url, hostname, port, idPrefix);
        assertCP(cp, user, password, url, hostname, port, idPrefix, ClientParameters.DEFAULT_HEARTBEAT_INTERVAL);
        cp = new ClientParameters(user, password, url, hostname, port);
        assertCP(cp, user, password, url, hostname, port, null, ClientParameters.DEFAULT_HEARTBEAT_INTERVAL);
    }

    private void assertCP(ClientParameters inCp, String inUser,
                          char[] inPassword, String inUrl,
                          String inHostname, int inPort, String inIDPrefix,
                          int heartbeatInterval) {
        assertEquals(inUser, inCp.getUsername());
        assertArrayEquals(inPassword, inCp.getPassword());
        assertEquals(inUrl, inCp.getURL());
        assertEquals(inHostname, inCp.getHostname());
        assertEquals(inPort, inCp.getPort());
        assertEquals(inIDPrefix, inCp.getIDPrefix());
        assertEquals(heartbeatInterval, inCp.getHeartbeatInterval());
    }
}
