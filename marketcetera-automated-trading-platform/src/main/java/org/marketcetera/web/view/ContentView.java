package org.marketcetera.web.view;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/* $License$ */

/**
 * Identifies a view that has content for the application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ContentView
        extends VerticalLayout
{
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    public abstract String getViewName();
    private static final long serialVersionUID = 8586381469467733948L;
}
