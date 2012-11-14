package org.marketcetera.core.node;

import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface Service
        extends Lifecycle
{
    public String getName();
    public String getDescription();
}
