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
     * Get the window style id to use for this window.
     *
     * @return a <code>String</code> value
     */
    default String getWindowStyleId()
    {
        return "window";
    }
    /**
     * Get the content view factory for this window.
     *
     * @return a <code>ContentViewFactory</code> value
     */
    ContentViewFactory getViewFactory();
    /**
     * Get the window size recommended for this window.
     * 
     * <p>Implementing class may choose to override this call to suggest a different size for the new window.
     *
     * @return a <code>Pair&lt;String,String&gt;</code> value
     */
    default Pair<String,String> getWindowSize()
    {
        return Pair.create("50%",
                           "50%");
    }
    /**
     * Indicate if the new window should be resizable.
     *
     * @return a <code>boolean</code> value
     */
    default boolean isResizable()
    {
        return true;
    }
    /**
     * Indicate if the new window should be draggable.
     *
     * @return a <code>boolean</code> value
     */
    default boolean isDraggable()
    {
        return true;
    }
    /**
     * Indicate if the new window should be modal.
     *
     * @return a <code>boolean</code> value
     */
    default boolean isModal()
    {
        return false;
    }
}
