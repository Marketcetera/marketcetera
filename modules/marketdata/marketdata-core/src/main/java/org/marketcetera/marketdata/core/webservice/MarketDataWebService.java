package org.marketcetera.marketdata.core.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides market data web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@WebService(targetNamespace="marketdata")
@ClassVersion("$Id$")
public interface MarketDataWebService
        extends ServiceBase
{
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequest
     * @return
     * @throws RemoteException 
     */
    long request(@WebParam(name="context")ClientContext inContext,
                 @WebParam(name="request")MarketDataRequest inRequest)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequestId
     * @throws RemoteException 
     */
    void cancel(@WebParam(name="context")ClientContext inContext,
                @WebParam(name="id")long inRequestId)
            throws RemoteException;
//    /**
//     * 
//     *
//     *
//     * @param inContext
//     * @param inRequestId
//     * @return
//     */
//    List<Event> getEvents(@WebParam(name="context")ClientContext inContext,
//                          @WebParam(name="id")long inRequestId);
}
