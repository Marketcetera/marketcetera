package org.marketcetera.ui.view;

import java.util.Properties;

import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.service.UiMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javafx.scene.Node;

/* $License$ */

/**
 * Provides common behavior for {@link ContentViewFactory} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractContentViewFactory
        implements ContentViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.ContentViewFactory#create(com.vaadin.ui.Window, org.marketcetera.web.events.NewWindowEvent, java.util.Properties)
     */
    @Override
    public ContentView create(Node inParent,
                              NewWindowEvent inEvent,
                              Properties inViewProperties)
    {
        return applicationContext.getBean(getViewType(),
                                          inParent,
                                          inEvent,
                                          inViewProperties);
    }
    /**
     * Get the {@link ContentView} subclass to create for the view.
     *
     * @return a <code>Class&lt;? extends ContentView&gt;</code> value
     */
    protected abstract Class<? extends ContentView> getViewType();
    /**
     * Get the applicationContext value.
     *
     * @return an <code>ApplicationContext</code> value
     */
    protected ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * provides access to web message services
     */
    @Autowired
    protected UiMessageService webMessageService;
}
