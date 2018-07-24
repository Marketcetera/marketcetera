package org.marketcetera.web.view;

import com.vaadin.navigator.View;

/* $License$ */

/**
 * Identifies a view that has content for the application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContentView
        extends View
{
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    String getViewName();
}
