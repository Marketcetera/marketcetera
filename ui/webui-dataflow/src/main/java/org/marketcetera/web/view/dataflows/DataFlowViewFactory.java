package org.marketcetera.web.view.dataflows;

import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.service.WebMessageService;
import org.marketcetera.web.view.AbstractContentViewFactory;
import org.marketcetera.web.view.ContentView;
import org.marketcetera.web.view.ContentViewFactory;
import org.marketcetera.web.view.MenuContent;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;

/* $License$ */

/**
 * Creates {@link DataFlowView} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class DataFlowViewFactory
        extends AbstractContentViewFactory
        implements MenuContent
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return StrategyEngineView.class;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getMenuCaption()
     */
    @Override
    public String getMenuCaption()
    {
        return "Data Flows";
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.MenuContent#getWeight()
     */
    @Override
    public int getWeight()
    {
        return 500;
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
        return FontAwesome.COGS;
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
                webMessageService.post(new NewWindowEvent() {
                    @Override
                    public String getWindowTitle()
                    {
                        return getMenuCaption();
                    }
                    @Override
                    public Class<? extends ContentViewFactory> getViewFactoryType()
                    {
                        return DataFlowViewFactory.class;
                    }
                });
            }
            private static final long serialVersionUID = 49365592058433460L;
        };
    }
    /**
     * provides access to web message services
     */
    @Autowired
    private WebMessageService webMessageService;
}
