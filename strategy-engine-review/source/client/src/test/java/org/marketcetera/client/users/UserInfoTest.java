package org.marketcetera.client.users;

import org.junit.Test;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class UserInfoTest
    extends TestCaseBase
{
    private static final String TEST_NAME=
        "metc";
    private static final UserID TEST_USER_ID=
        new UserID(2);

    @Test
    public void all()
        throws Exception
    {
        UserInfo s=new UserInfo(TEST_NAME,TEST_USER_ID,true,false);
        assertEquals(TEST_NAME,s.getName());
        assertEquals(TEST_USER_ID,s.getId());
        assertTrue(s.getActive());
        assertFalse(s.getSuperuser());
        assertEquals("User: metc(2,true,false)",s.toString());
    }
}
