package org.marketcetera.core.ws.sample;

import javax.jws.WebService;
import org.marketcetera.core.ws.stateless.StatelessClientContext;
import org.marketcetera.core.ws.stateless.StatelessServiceBase;
import org.marketcetera.core.ws.wrappers.RemoteException;

/**
 * A sample stateless service: the interface. It simply returns its
 * argument in the context of a greeting. Certain argument values
 * result in exceptions/errors being thrown.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: SampleStatelessService.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@WebService
public interface SampleStatelessService
    extends StatelessServiceBase
{
    String hello
        (StatelessClientContext context,
         String name)
        throws RemoteException;
}
