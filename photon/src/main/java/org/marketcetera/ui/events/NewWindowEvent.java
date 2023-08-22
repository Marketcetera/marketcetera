package org.marketcetera.ui.events;

import java.net.URL;
import java.util.Properties;

import org.marketcetera.core.Pair;
import org.marketcetera.ui.view.ContentViewFactory;

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
     * Get the icon to display for the window, if any.
     *
     * @return a <code>URL</code> value or <code>null</code>
     */
    default URL getWindowIcon()
    {
        return null;
    }
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
     * Get the properties with which to seed the new window.
     *
     * @return a <code>Properties</code> value
     */
    default Properties getProperties()
    {
        return new Properties();
    }
    /**
     * Get the content view factory for this window.
     *
     * @return a <code>Class&lt;? extends ContentViewFactory&gt;</code> value
     */
    Class<? extends ContentViewFactory> getViewFactoryType();
    /**
     * Get the window size recommended for this window.
     * 
     * <p>Implementing class may choose to override this call to suggest a different size for the new window.
     *
     * @return a <code>Pair&lt;Double,Double&gt;</code> value
     */
    default Pair<Double,Double> getWindowSize()
    {
        return Pair.create(200.0,
                           200.0);
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
