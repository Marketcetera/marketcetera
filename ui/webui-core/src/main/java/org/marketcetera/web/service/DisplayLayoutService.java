package org.marketcetera.web.service;

import java.util.Properties;

/* $License$ */

/**
 * Provides a service for display layout.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DisplayLayoutService
{
    /**
     * Get the display layout.
     *
     * @return a <code>Properties</code> value
     */
    Properties getDisplayLayout();
    /**
     * Set the display layout.
     *
     * @param inDisplayLayout a <code>Properties</code> value
     */
    void setDisplayLayout(Properties inDisplayLayout);
}
