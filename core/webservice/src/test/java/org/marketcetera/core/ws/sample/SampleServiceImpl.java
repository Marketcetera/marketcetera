package org.marketcetera.core.ws.sample;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.core.util.log.I18NMessage2P;
import org.marketcetera.core.attributes.ClassVersion;
import org.marketcetera.core.ws.stateful.ClientContext;
import org.marketcetera.core.ws.stateful.RemoteCaller;
import org.marketcetera.core.ws.stateful.ServiceBaseImpl;
import org.marketcetera.core.ws.stateful.SessionHolder;
import org.marketcetera.core.ws.stateful.SessionManager;
import org.marketcetera.core.ws.wrappers.RemoteException;

/**
 * A sample stateful service: the implementation.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: SampleServiceImpl.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: SampleServiceImpl.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
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
