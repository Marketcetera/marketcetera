package org.marketcetera.web.view;

import java.util.Properties;

import org.marketcetera.web.events.NewWindowEvent;

import com.vaadin.flow.component.dialog.Dialog;

/* $License$ */

/**
 * Creates a new content view.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ContentViewFactory
{
    /**
     * Create a new content view.
     *
     * @param inParent a <code>Dialog</code> value
     * @param inEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     * @return a <code>T</code> value
     */
    ContentView create(Dialog inParent,
                       NewWindowEvent inEvent,
                       Properties inViewProperties);
}
