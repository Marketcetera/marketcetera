package org.marketcetera.web.events;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface WindowResizeEvent
{
    // TODO need a callback to recreate the contents of the window
    int getPositionX();
    int getPositionY();
    float getWidth();
    float getHeight();
}
