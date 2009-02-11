package org.marketcetera.util.ws.sample;

import javax.jws.WebService;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/**
 * A sample stateful service: the interface. It simply returns its
 * argument in the context of a greeting (which also contains session
 * information). Certain argument values result in exceptions/errors
 * being thrown.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@WebService
@ClassVersion("$Id$") //$NON-NLS-1$
public interface SampleService
    extends ServiceBase
{
    String hello
        (ClientContext context,
         String name)
        throws RemoteException;
}
