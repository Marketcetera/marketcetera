package org.marketcetera.util.ws.sample;

import javax.jws.WebService;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.stateless.StatelessServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A sample stateless service: the interface. It simply returns its
 * argument in the context of a greeting. Certain argument values
 * result in exceptions/errors being thrown.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SampleStatelessService
    extends StatelessServiceBase
{
    String hello
        (StatelessClientContext context,
         String name)
        throws RemoteException;
}
