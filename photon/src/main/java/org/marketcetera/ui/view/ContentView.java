package org.marketcetera.ui.view;

import javafx.scene.Node;
import javafx.stage.WindowEvent;

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
     * @return a <code>Node</code> value
     */
    Node getNode();
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    String getViewName();
    /**
     * Invoked when the content view is closed.
     *
     * @param inEvent a <code>WindowEvent</code> value
     */
    default void onClose(WindowEvent inEvent) {}
}
