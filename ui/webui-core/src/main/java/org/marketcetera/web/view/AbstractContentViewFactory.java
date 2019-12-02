package org.marketcetera.web.view;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.ui.Window;

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
     * @see org.marketcetera.web.view.ContentViewFactory#create(com.vaadin.ui.Window, java.util.Properties)
     */
    @Override
    public ContentView create(Window inParent,
                              Properties inViewProperties)
    {
        return applicationContext.getBean(getViewType(),
                                          inParent,
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
}
