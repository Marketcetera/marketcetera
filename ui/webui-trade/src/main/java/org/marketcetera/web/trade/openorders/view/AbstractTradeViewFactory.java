package org.marketcetera.web.trade.openorders.view;

import org.marketcetera.core.Pair;
import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.trade.openorders.view.TradeContentCategory;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/* $License$ */

/**
 * Provides common behavior for trade views.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractTradeViewFactory
        implements ContentViewFactory,MenuContent
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return TradeContentCategory.instance;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCommand()
     */
    @Override
    public Command getCommand()
    {
        return new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem inSelectedItem)
            {
                webMessageService.post(new TradeViewMenuEvent());
            }
            private static final long serialVersionUID = -7269505766947455017L;
        };
    }
    /**
     * Get the initial window size for new windows of this type.
     *
     * @return <code>Pair&lt;String,String&gt;</code> value
     */
    protected Pair<String,String> getWindowSize()
    {
        return Pair.create("50%",
                           "50%");
    }
    /**
     * Get the Vaadin name of the view.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getViewName();
    /**
     * Indicates that an admin type has been selected.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class TradeViewMenuEvent
            implements NewWindowEvent
    {
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.MenuEvent#getWindowTitle()
         */
        @Override
        public String getWindowTitle()
        {
            return AbstractTradeViewFactory.this.getViewName();
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getViewFactory()
         */
        @Override
        public ContentViewFactory getViewFactory()
        {
            return AbstractTradeViewFactory.this;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.web.events.NewWindowEvent#getWindowSize()
         */
        @Override
        public Pair<String,String> getWindowSize()
        {
            return AbstractTradeViewFactory.this.getWindowSize();
        }
    }
    /**
     * provides access to web message services
     */
    @Autowired
    protected WebMessageService webMessageService;
}
