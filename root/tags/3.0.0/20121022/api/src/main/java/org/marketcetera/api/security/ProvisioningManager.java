package org.marketcetera.api.security;

/* $License$ */

/**
 * Provides provisioning management services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ProvisioningManager
{
    /**
     * Effects provisioning changes as described in the input.
     *
     * @param inProvisioning a <code>Provisioning</code> value
     */
    public void provision(Provisioning inProvisioning);
}
