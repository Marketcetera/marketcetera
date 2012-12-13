package org.marketcetera.marketdata.webservices;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.marketcetera.core.event.Event;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 * Provides access to market data via web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataService
{
    @GET
    @Path("test")
    public List<WebServicesEvent> test();
    /**
     * Request market data.
     * 
     * <p>The returned value can be used to retrieve queued events
     * via {@link #getEvents(long)}.
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @return a <code>long</code> value
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public long request(WebServicesMarketDataRequest inRequest);
    /**
     * Retrieve the events queued from a market data request.
     * 
     * <p>Events must first be requested via {@link #request(MarketDataRequest)}.
     * The returned value is then passed to this method to retrieve
     * the events. If the event queue is not cleared within a certain
     * interval, the queue is cleared and the request is canceled.
     * 
     * <p>If the given request id does not match an active request, an
     * exception will be thrown. TODO
     *
     * @param inRequestId a <code>log</code> value
     * @return a <code>List&lt;WebServicesEvent&gt;</code> value
     */
    @GET
    @Path("events/{id}")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public List<WebServicesEvent> getEvents(@PathParam("id")long inRequestId);
    /**
     * Cancels an active market data request.
     * 
     * <p>If the given request id does not match an active request,
     * nothing happens.
     *
     * @param inRequestId a <code>long</code> value
     */
    @DELETE
    @Path("{id}")
    public Response cancel(@PathParam("id")long inRequestId);
    /**
     * Gets the most recent snapshot matching the given symbol and content.
     *
     * <p>The returned market data will reflect the result of the most recent
     * subscription request for that symbol. If no requests have been made,
     * no market data will be returned: this method will not initiate a new
     * market data request.
     * 
     * @param inSymbol a <code>String</code> value
     * @param inContent a <code>String</code> value
     * @return an <code>Event</code> object representing the most recent snapshot
     */
    @GET
    @Path("snapshot/{symbol}/{content}")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public Event getSnapshot(@PathParam("symbol")String inSymbol,
                             @PathParam("content")String inContent);
}
