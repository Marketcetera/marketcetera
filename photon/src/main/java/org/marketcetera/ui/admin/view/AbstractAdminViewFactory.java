package org.marketcetera.ui.admin.view;

import org.marketcetera.core.Pair;
import org.marketcetera.ui.events.NewWindowEvent;
import org.marketcetera.ui.service.UiMessageService;
import org.marketcetera.ui.view.AbstractContentViewFactory;
import org.marketcetera.ui.view.ContentViewFactory;
import org.marketcetera.ui.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Provides common behavior for admin views.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractAdminViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return AdminContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Runnable getCommand()
    {
        return new Runnable() {
            @Override
            public void run()
            {
                webMessageService.post(new AdminViewMenuEvent());
            }
        };
    }
    /**
     * Get the name of the view.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getViewName();
    /**
     * Get the view factory for this view.
     *
     * @return a <code>Class&lt;? extends ContentViewFactory&gt;</code> value
     */
    protected abstract Class<? extends ContentViewFactory> getViewFactory();
    /**
     * Indicates that an admin type has been selected.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class AdminViewMenuEvent
            implements NewWindowEvent
    {
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.MenuEvent#getWindowTitle()
         */
        @Override
        public String getWindowTitle()
        {
            return AbstractAdminViewFactory.this.getViewName();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.events.NewWindowEvent#getWindowSize()
         */
        @Override
        public Pair<Double,Double> getWindowSize()
        {
            return Pair.create(500.0,300.0);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getViewFactoryType()
         */
        @Override
        public Class<? extends ContentViewFactory> getViewFactoryType()
        {
            return getViewFactory();
        }
    }
    /**
     * provides access to web message services
     */
    @Autowired
    protected UiMessageService webMessageService;
}
