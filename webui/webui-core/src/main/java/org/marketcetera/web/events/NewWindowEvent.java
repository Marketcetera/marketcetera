package org.marketcetera.web.events;

import org.marketcetera.core.Pair;

import com.vaadin.ui.Component;

/* $License$ */

/**
 * Indicates that a new window event has been triggered.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface NewWindowEvent
{
    /**
     * Get the title to display in the window.
     *
     * @return a <code>String</code> value
     */
    String getWindowTitle();
    /**
     * Get the component to display in the window.
     *
     * @return a <code>Component</code> value
     */
    Component getComponent();
    /**
     * Get the window size recommended for this window.
     *
     * @return a <code>Pair&lt;String,String&gt;</code> value
     */
    default Pair<String,String> getWindowSize()
    {
        return Pair.create("50%","50%");
    }
}
