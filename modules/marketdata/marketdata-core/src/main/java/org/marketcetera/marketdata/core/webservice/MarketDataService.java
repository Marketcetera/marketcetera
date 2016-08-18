package org.marketcetera.marketdata.core.webservice;

import java.util.*;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.marketcetera.event.Event;
import org.marketcetera.marketdata.Capability;
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
 * @since 2.4.0
 */
@WebService(targetNamespace="marketdata")
@ClassVersion("$Id$")
public interface MarketDataService
        extends ServiceBase
{
    /**
     * Request market data.
     * 
     * <p>Begins a market data subscription. The returned id can be
     * used to retrieve events via {@link #getEvents(ClientContext, long)}.
     * If the <code>inStreamEvents</code> value is true, events will be queued
     * for retrieval. If false, events will not be retrieved, but you can
     * determine if the market data contents have changed via {@link #getLastUpdate(ClientContext, long)}.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inRequest a <code>MarketDataRequest</code> value
     * @param inStreamEvents a <code>boolean</code> value
     * @return a <code>long</code> value
     * @throws RemoteException if an error occurs requesting market data
     */
    long request(@WebParam(name="context")ClientContext inContext,
                 @WebParam(name="request")MarketDataRequest inRequest,
                 @WebParam(name="stream")boolean inStreamEvents)
            throws RemoteException;
    /**
     * Cancels a market data request.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inRequestId a <code>long</code> value
     * @throws RemoteException if an error occurs canceling market data
     */
    void cancel(@WebParam(name="context")ClientContext inContext,
                @WebParam(name="id")long inRequestId)
            throws RemoteException;
    /**
     * Gets the queued events generate for a market data request.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inRequestId a <code>long</code> value
     * @return a <code>Deque&lt;Event&gt;</code> value
     * @throws RemoteException if an error occurs retrieving market data
     */
    Deque<Event> getEvents(@WebParam(name="context")ClientContext inContext,
                           @WebParam(name="id")long inRequestId)
            throws RemoteException;
    /**
     * Gets the timestamp of the last update for the given request.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inRequestId a <code>long</code> value
     * @return a <code>long</code> value
     * @throws RemoteException if an error occurs retrieving the last update
     */
    long getLastUpdate(@WebParam(name="context")ClientContext inContext,
                       @WebParam(name="id")long inRequestId)
            throws RemoteException;
    /**
     * Gets the events from multiple market data requests at the same time.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inRequestIds a <code>List&lt;Long&gt;</code> value
     * @return a <code>Map&lt;Long,LinkedList&lt;Event&gt;&gt;</code> value
     * @throws RemoteException
     */
    Map<Long,LinkedList<Event>> getAllEvents(@WebParam(name="context")ClientContext inContext,
                                             @WebParam(name="id")List<Long> inRequestIds)
            throws RemoteException;
    /**
     * Gets the most recent snapshot of the given market data.
     * 
     * <p>Market data must be pre-requested via {@link #request(ClientContext, MarketDataRequest, boolean)}.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @return a <code>Deque&lt;Event&gt;</code>
     * @throws RemoteException if an error occurs retrieving the snapshot
     */
    Deque<Event> getSnapshot(@WebParam(name="context")ClientContext inContext,
                             @WebParam(name="instrument")Instrument inInstrument,
                             @WebParam(name="content")Content inContent,
                             @WebParam(name="provider")String inProvider)
            throws RemoteException;
    /**
     * Gets a subset of the most recent snapshot available of the given market data.
     *
     * <p>Market data must be pre-requested via {@link #request(ClientContext, MarketDataRequest, boolean)}.
     *
     * @param inContext a <code>ClientContext</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inContent a <code>Content</code> value
     * @param inProvider a <code>String</code> value or <code>null</code>
     * @param inPage a <code>PageRequest</code> value indicating what subset to return
     * @return a <code>Deque&lt;Event&gt;</code>
     * @throws RemoteException if an error occurs retrieving the snapshot
     */
    Deque<Event> getSnapshotPage(@WebParam(name="context")ClientContext inContext,
                                 @WebParam(name="instrument")Instrument inInstrument,
                                 @WebParam(name="content")Content inContent,
                                 @WebParam(name="provider")String inProvider,
                                 @WebParam(name="page")PageRequest inPage)
            throws RemoteException;
    /**
     * Executes a heartbeat with the server.
     *
     * @param inContext a <code>ClientContext</code> value
     * @throws RemoteException if an error occurs executing the heartbeat
     */
    void heartbeat(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
    /**
     * Returns the current set of available capability.
     *
     * @param inContext a <code>ClientContext</code> value
     * @return a <code>Set&lt;Capability&gt;</code> value
     * @throws RemoteException if an error occurs returning the available capability
     */
    Set<Capability> getAvailableCapability(@WebParam(name="context")ClientContext inContext)
            throws RemoteException;
}
