package org.marketcetera.marketdata.webservices;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import org.marketcetera.marketdata.request.MarketDataRequest;

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
    @POST
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public void request(MarketDataRequest inRequest);
    @GET
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public MarketDataRequest test();
}
