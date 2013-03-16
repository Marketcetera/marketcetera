package org.marketcetera.core.node;

import java.util.Collection;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Node
{
    public Collection<Service> getServices();
    public NodeSpecification getNodeSpecification();
}
