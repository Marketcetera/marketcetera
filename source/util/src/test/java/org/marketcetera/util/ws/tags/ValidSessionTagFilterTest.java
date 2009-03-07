package org.marketcetera.util.ws.tags;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.stateless.StatelessClientContext;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ValidSessionTagFilterTest
    extends TagFilterTestBase
{
    private static final String TEST_USER=
        "metc";
    private static final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();

    @Test
    public void all()
        throws Exception
    {
        SessionId sessionId=new SessionId();
        sessionId.setValue(TEST_TAG.getValue());
        SessionManager<Object> sessionManager=
            new SessionManager<Object>();
        sessionManager.put(sessionId,new SessionHolder<Object>
                           (TEST_USER,TEST_CONTEXT));

        ValidSessionTagFilter<Object> filter=
            new ValidSessionTagFilter<Object>(sessionManager);
        assertEquals(sessionManager,filter.getSessionManager());

        single(filter,sessionId,null,
               Messages.SESSION_REQUIRED);
        single(filter,sessionId,TEST_TAG,
               new I18NBoundMessage1P(Messages.SESSION_EXPIRED,TEST_TAG));
        single(filter,sessionId,TEST_TAG_D,
               new I18NBoundMessage1P(Messages.SESSION_EXPIRED,TEST_TAG_D));

        SessionId sessionIdD=new SessionId();
        sessionIdD.setValue(TEST_TAG_D.getValue());
        single(filter,sessionId,sessionIdD,
               new I18NBoundMessage1P(Messages.SESSION_EXPIRED,sessionIdD));

        filter=new ValidSessionTagFilter<Object>(null);
        assertNull(filter.getSessionManager());

        singlePass(filter,sessionId);
        singlePass(filter,null);
        singlePass(filter,TEST_TAG);
    }
}
