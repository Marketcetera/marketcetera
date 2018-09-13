package org.marketcetera.web.events;

import com.vaadin.ui.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MenuEvent
{
    String getWindowTitle();

    /**
     *
     *
     * @return
     */
    Component getComponent();
}
