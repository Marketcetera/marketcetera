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
    Properties getDisplayLayout();
    void setDisplayLayout(Properties inDisplayLayout);
}
