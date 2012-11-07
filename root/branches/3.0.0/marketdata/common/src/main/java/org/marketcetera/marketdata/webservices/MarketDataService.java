package org.marketcetera.marketdata.webservices;

import java.util.Collection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.marketcetera.marketdata.events.Event;
import org.marketcetera.marketdata.request.MarketDataRequest;
import org.marketcetera.marketdata.request.MarketDataRequestAtom;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataService
{
    /**
     * 
     *
     *
     * @param inRequest
     * @return a <code>long</code> value
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public long request(MarketDataRequest inRequest);
    /**
     * 
     *
     *
     * @param inRequestId
     * @return a <code>Collection&lt;Event&gt;</code> value
     */
    @GET
    @Path("events")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public Collection<Event> getEvents(long inRequestId);
    /**
     * 
     *
     *
     * @param inRequestId
     */
    @DELETE
    @Path("{id}")
    public Response cancel(@PathParam("id")long inRequestId);
    /**
     * 
     *
     *
     * @param inRequest
     * @return
     */
    @GET
    @Path("snapshot")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public Event getSnapshot(MarketDataRequestAtom inRequest);
}
