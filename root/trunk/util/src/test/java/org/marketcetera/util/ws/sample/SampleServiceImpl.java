package org.marketcetera.util.ws.sample;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.RemoteCaller;
import org.marketcetera.util.ws.stateful.ServiceBaseImpl;
import org.marketcetera.util.ws.stateful.SessionHolder;
import org.marketcetera.util.ws.stateful.SessionManager;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A sample stateful service: the implementation.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SampleServiceImpl
    extends ServiceBaseImpl<SampleSession>
    implements SampleService
{

    // INSTANCE DATA.

    private I18NMessage2P mGreeting;


    // CONSTRUCTORS.

    public SampleServiceImpl
        (SessionManager<SampleSession> sessionManager,
         I18NMessage2P greeting)
    {
        super(sessionManager);
        mGreeting=greeting;
    }


    // SERVICE IMPLEMENTATION.

    private String helloImpl
        (SessionHolder<SampleSession> sessionHolder,
         String name)
        throws I18NException
    {
        SampleStatelessServiceImpl.checkName(name);
        SampleSession session=SampleSession.getSession(sessionHolder);
        return mGreeting.getText(name,session.getCallCount());
    }


    // SampleServiceInterface.

    @Override
    public String hello
        (ClientContext context,
         final String name)
        throws RemoteException
    {
        return (new RemoteCaller<SampleSession,String>(getSessionManager()) {
            @Override
            protected String call
                (ClientContext context,
                 SessionHolder<SampleSession> sessionHolder)
                throws I18NException
            {
                return helloImpl(sessionHolder,name);
            }}).execute(context);
    }
}
