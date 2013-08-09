package org.marketcetera.agent;

import java.util.Set;

import org.marketcetera.service.Service;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 * Provides an entry point for bootstrapping a host.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Agent
        extends Lifecycle
{
    public Set<Service> getServices();
}
