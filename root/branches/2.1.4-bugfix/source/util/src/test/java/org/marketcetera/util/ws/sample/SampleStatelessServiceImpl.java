package org.marketcetera.util.ws.sample;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessRemoteCaller;
import org.marketcetera.util.ws.stateless.StatelessServiceBaseImpl;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A sample stateless service: the implementation.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$") //$NON-NLS-1$
public class SampleStatelessServiceImpl
    extends StatelessServiceBaseImpl
    implements SampleStatelessService
{

    // INSTANCE DATA.

    private I18NMessage2P mGreeting;


    // CONSTRUCTORS.

    public SampleStatelessServiceImpl
        (I18NMessage2P greeting)
    {
        mGreeting=greeting;
    }


    // SERVICE IMPLEMENTATION.

    public static void checkName
        (String name)
        throws I18NException
    {
        if ("I18NException".equals(name)) {
            throw new I18NException
                (SampleMessages.EXCEPTION_MESSAGE);
        }
        if ("AssertionError".equals(name)) {
            throw new AssertionError
                ("This is the error message");
        }
        if ("IllegalArgumentException".equals(name)) {
            throw new IllegalArgumentException
                ("This is the exception message");
        }
        if (name==null) {
            throw new AssertionError();
        }
    }

    private String helloImpl
        (StatelessClientContext context,
         String name)
        throws I18NException
    {
        checkName(name);
        return mGreeting.getText(name,-1);
    }


    // SampleStatelessServiceIface.

    @Override
    public String hello
        (StatelessClientContext context,
         final String name)
        throws RemoteException
    {
        return (new StatelessRemoteCaller<String>() {
            @Override
            protected String call
                (StatelessClientContext context)
                throws I18NException
            {
                return helloImpl(context,name);
            }}).execute(context);
    }
}
