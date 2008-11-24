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
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ClientParametersTest {
    @Test
    public void all() {
        String user = "you";
        char [] password = "pass".toCharArray();
        String url = "url";
        ClientParameters cp = new ClientParameters(user, password, url);
        assertCP(cp, user, password, url);
        ClientParameters  cpNull = new ClientParameters(null, null, null);
        assertCP(cpNull, null, null, null);

        EqualityAssert.assertEquality(cp,
                new ClientParameters(user, password, url), cpNull,
                new ClientParameters(user, "nopass".toCharArray(), url),
                new ClientParameters("unuser", password, url),
                new ClientParameters(user, password, "urln"),
                new ClientParameters(null, password, url),
                new ClientParameters(user, null, url),
                new ClientParameters(user, password, null),
                new ClientParameters(user + " ", password, url),
                new ClientParameters(user, "pass ".toCharArray(), url),
                new ClientParameters(user, password, url + " ")
                );
    }

    private void assertCP(ClientParameters inCp, String inUser,
                          char[] inPassword, String inUrl) {
        assertEquals(inUser, inCp.getUsername());
        assertEquals(inPassword, inCp.getPassword());
        assertEquals(inUrl, inCp.getURL());
    }
}
