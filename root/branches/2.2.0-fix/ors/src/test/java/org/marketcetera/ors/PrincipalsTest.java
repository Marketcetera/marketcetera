package org.marketcetera.ors;

import org.junit.Test;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class PrincipalsTest
    extends TestCaseBase
{
    protected static final UserID TEST_ID=
        new UserID(0);
    protected static final UserID TEST_ID_D=
        new UserID(1);


    @Test
    public void basics()
    {
        Principals p=new Principals(TEST_ID,TEST_ID_D);
        Principals copy=new Principals(TEST_ID,TEST_ID_D);

        assertEquality(p,copy,Principals.UNKNOWN,
                       new Principals(TEST_ID_D,TEST_ID));

        assertEquals(TEST_ID,p.getActorID());
        assertEquals(TEST_ID_D,p.getViewerID());

        assertNull(Principals.UNKNOWN.getActorID());
        assertNull(Principals.UNKNOWN.getViewerID());

        assertEquality(Principals.UNKNOWN,new Principals(null,null),p);
    }
}
