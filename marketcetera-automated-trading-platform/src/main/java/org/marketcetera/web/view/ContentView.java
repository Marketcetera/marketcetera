package org.marketcetera.web.view;

import com.vaadin.flow.component.Component;

/* $License$ */

/**
 * Identifies a view that has content for the application.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ContentView
        extends Component
{
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    abstract String getViewName();
    private static final long serialVersionUID = 8586381469467733948L;
}
