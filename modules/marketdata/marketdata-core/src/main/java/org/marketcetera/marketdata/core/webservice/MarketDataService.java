package org.marketcetera.marketdata.core.webservice;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.ClientContext;
import org.marketcetera.util.ws.stateful.ServiceBase;
import org.marketcetera.util.ws.wrappers.RemoteException;

/* $License$ */

/**
 * Provides market data services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@WebService(targetNamespace="marketdata")
@ClassVersion("$Id$")
public interface MarketDataService
        extends ServiceBase
{
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequest
     * @param inStreamEvents
     * @return
     * @throws RemoteException
     */
    long request(@WebParam(name="context")ClientContext inContext,
                 @WebParam(name="request")MarketDataRequest inRequest,
                 @WebParam(name="stream")boolean inStreamEvents)
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
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequestId
     * @return
     * @throws RemoteException 
     */
    Deque<Event> getEvents(@WebParam(name="context")ClientContext inContext,
                           @WebParam(name="id")long inRequestId)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequestId
     * @return
     * @throws RemoteException
     */
    long getLastUpdate(@WebParam(name="context")ClientContext inContext,
                       @WebParam(name="id")long inRequestId)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequestIds
     * @return
     * @throws RemoteException
     */
    Map<Long,LinkedList<Event>> getAllEvents(@WebParam(name="context")ClientContext inContext,
                                             @WebParam(name="id")List<Long> inRequestIds)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @param inInstrument
     * @param inContent
     * @param inProvider
     * @return
     * @throws RemoteException
     */
    Deque<Event> getSnapshot(@WebParam(name="context")ClientContext inContext,
                             @WebParam(name="instrument")Instrument inInstrument,
                             @WebParam(name="content")Content inContent,
                             @WebParam(name="provider")String inProvider)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @param inRequestId
     * @param inPage
     * @return
     * @throws RemoteException
     */
    Deque<Event> getSnapshotPage(@WebParam(name="context")ClientContext inContext,
                                 @WebParam(name="instrument")Instrument inInstrument,
                                 @WebParam(name="content")Content inContent,
                                 @WebParam(name="provider")String inProvider,
                                 @WebParam(name="page")PageRequest inPage)
            throws RemoteException;
    /**
     * 
     *
     *
     * @param inContext
     * @throws RemoteException
     */
    void heartbeat(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
}
