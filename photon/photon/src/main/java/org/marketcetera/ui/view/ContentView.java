package org.marketcetera.ui.view;

import javafx.scene.Scene;

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
     * Get the scene which contains the content.
     *
     * @return a <code>Scene</code> value
     */
    Scene getScene();
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    String getViewName();
}
