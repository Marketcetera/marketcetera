package org.marketcetera.webservices.systemmodel;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides access to provisioning services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Path("provisioning")
public interface ProvisioningService
{
    /**
     * Performs the provisioning changes as indicated in the given instructions.
     *
     * @param inData a <code>WebServicesProvisioning</code> value containing encoded object references
     * @return a <code>Response</code> value
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response provision(WebServicesProvisioning inData);
}
