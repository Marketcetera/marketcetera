package org.marketcetera.web.view;

import java.util.Properties;

/* $License$ */

/**
 * Creates a new content view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContentViewFactory
{
    /**
     * Create a new content view.
     *
     * @param inViewProperties a <code>Properties</code> value
     * @return a <code>T</code> value
     */
    ContentView create(Properties inViewProperties);
}
