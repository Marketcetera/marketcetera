package org.marketcetera.util.ws.stateful;

import org.junit.Test;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.wrappers.RemoteException;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class AuthServiceImplTest
    extends ServiceImplTestBase<Object>
{
    private static final FixedAuthenticator TEST_AUTHENTICATOR=
        new FixedAuthenticator();
    private static final String TEST_USER=
        "metc";
    private static final String TEST_USER_D=
        "metcD";
    private static final char[] TEST_PASSWORD=
        "metc".toCharArray();
    private static final char[] NUL_VALUE=
        StringUtils.repeat("\0",TEST_PASSWORD.length).toCharArray();


    @Test
    public void all()
        throws Exception
    {
        AuthServiceImpl<Object> impl=
            new AuthServiceImpl<Object>(TEST_AUTHENTICATOR,TEST_MANAGER);
        single(impl,null);

        assertEquals(TEST_AUTHENTICATOR,impl.getAuthenticator());

        // Single login.

        char[] password=TEST_PASSWORD.clone();
        SessionId id=impl.login(TEST_CONTEXT,TEST_USER,password);
        assertArrayEquals(NUL_VALUE,password);

        assertEquals(TEST_USER,TEST_MANAGER.get(id).getUser());

        ClientContext context=getContext(id);
        impl.logout(context);

        // Second logout is no-op.

        impl.logout(context);

        // Dual login.

        password=TEST_PASSWORD.clone();
        SessionId id1=impl.login(TEST_CONTEXT,TEST_USER,password);
        password=TEST_PASSWORD.clone();
        SessionId id2=impl.login(TEST_CONTEXT,TEST_USER,password);
        assertFalse(id1.equals(id2));
        assertEquals(TEST_USER,TEST_MANAGER.get(id1).getUser());
        assertEquals(TEST_USER,TEST_MANAGER.get(id2).getUser());
        impl.logout(getContext(id1));
        assertNull(TEST_MANAGER.get(id1));
        assertEquals(TEST_USER,TEST_MANAGER.get(id2).getUser());
        impl.logout(getContext(id2));
        assertNull(TEST_MANAGER.get(id2));

        // Bad credentials.

        password=TEST_PASSWORD.clone();
        try {
            impl.login(TEST_CONTEXT,TEST_USER_D,password);
            fail();
        } catch (RemoteException ex) {
            assertEquals(Messages.BAD_CREDENTIALS,
                         ((I18NException)ex.getCause()).getI18NBoundMessage());
        }
        assertArrayEquals(NUL_VALUE,password);
    }
}
