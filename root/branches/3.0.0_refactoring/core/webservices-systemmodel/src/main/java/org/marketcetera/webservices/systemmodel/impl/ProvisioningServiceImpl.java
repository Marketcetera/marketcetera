package org.marketcetera.webservices.systemmodel.impl;

import javax.ws.rs.core.Response;

import org.marketcetera.api.security.ProvisioningManager;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.ProvisioningService;
import org.marketcetera.webservices.systemmodel.WebServicesProvisioning;

/* $License$ */

/**
 * Provides provisioning web services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ProvisioningServiceImpl
        implements ProvisioningService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.ProvisioningService#provision(org.marketcetera.webservices.systemmodel.WebServicesProvisioning)
     */
    @Override
    public Response provision(WebServicesProvisioning inData)
    {
        Response response;
        try {
            provisioningManager.provision(inData);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError().build();
        }
        return response;
    }
    /**
     * Sets the provisioningManager value.
     *
     * @param inProvisioningManager a <code>ProvisioningManager</code> value
     */
    public void setProvisioningManager(ProvisioningManager inProvisioningManager)
    {
        provisioningManager = inProvisioningManager;
    }
    /**
     * provides provisioning services
     */
    private ProvisioningManager provisioningManager;
}
