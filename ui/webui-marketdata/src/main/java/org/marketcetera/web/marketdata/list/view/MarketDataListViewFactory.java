package org.marketcetera.web.marketdata.list.view;

import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.font.MarketceteraFont;
import org.marketcetera.web.view.AbstractContentViewFactory;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.MenuContent;

import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/* $License$ */

/**
 * Creates {@link MarketDataListView} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class MarketDataListViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
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
                webMessageService.post(new NewWindowEvent() {
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Class<? extends ContentViewFactory> getViewFactoryType()
                    {
                        return MarketDataListViewFactory.class;
                    }
                });
            }
            private static final long serialVersionUID = 49365592058433460L;
        };
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Market Data";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 600;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getCategory()
     */
    @Override
    public MenuContent getCategory()
    {
        return null;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuIcon()
     */
    @Override
    public Resource getMenuIcon()
    {
        return MarketceteraFont.Market_Data;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return MarketDataListView.class;
    }
}
