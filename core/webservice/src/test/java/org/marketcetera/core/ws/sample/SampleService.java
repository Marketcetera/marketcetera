package org.marketcetera.core.ws.sample;

import javax.jws.WebService;
import org.marketcetera.api.attributes.ClassVersion;
import org.marketcetera.core.ws.stateful.ClientContext;
import org.marketcetera.core.ws.stateful.ServiceBase;
import org.marketcetera.core.ws.wrappers.RemoteException;

/**
 * A sample stateful service: the interface. It simply returns its
 * argument in the context of a greeting (which also contains session
 * information). Certain argument values result in exceptions/errors
 * being thrown.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: SampleService.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@WebService
@ClassVersion("$Id: SampleService.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface SampleService
    extends ServiceBase
{
    String hello
        (ClientContext context,
         String name)
        throws RemoteException;
}
