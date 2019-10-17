package org.marketcetera.web.events;

import org.marketcetera.core.Pair;
import org.marketcetera.web.view.ContentViewFactory;

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
     * 
     *
     *
     * @return
     */
    ContentViewFactory getViewFactory();
    /**
     * Get the window size recommended for this window.
     *
     * @return a <code>Pair&lt;String,String&gt;</code> value
     */
    default Pair<String,String> getWindowSize()
    {
        return Pair.create("50%","50%");
    }
    /**
     * 
     *
     *
     * @return
     */
    default boolean isResizable()
    {
        return true;
    }
    /**
     * 
     *
     *
     * @return
     */
    default boolean isDraggable()
    {
        return true;
    }
    /**
     * 
     *
     *
     * @return
     */
    default boolean isModal()
    {
        return false;
    }
}
