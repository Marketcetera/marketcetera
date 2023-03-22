package org.marketcetera.ui.view;

import javafx.scene.layout.Region;

/* $License$ */

/**
 * Identifies a view that has content for the application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContentView
{
    /**
     * Contains the root content of the view.
     *
     * @return a <code>Region</code> value
     */
    Region getMainLayout();
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    String getViewName();
    /**
     * Invoked when the content view is closed.
     */
    default void onClose() {}
}
