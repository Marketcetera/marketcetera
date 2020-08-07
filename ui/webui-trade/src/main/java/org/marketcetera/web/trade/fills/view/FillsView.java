package org.marketcetera.web.trade.fills.view;

import java.util.Properties;

import org.marketcetera.web.events.NewWindowEvent;
import org.marketcetera.web.trade.executionreport.view.AbstractExecutionReportView;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Window;

/* $License$ */

/**
 * Provides a view for order fills.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FillsView
        extends AbstractExecutionReportView
{
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.ContentView#getViewName()
     */
    @Override
    public String getViewName()
    {
        return NAME;
    }
    /**
     * Create a new FillsView instance.
     *
     * @param inParentWindow a <code>Window</code> value
     * @param inNewWindowEvent a <code>NewWindowEvent</code> value
     * @param inViewProperties a <code>Properties</code> value
     */
    public FillsView(Window inParentWindow,
                     NewWindowEvent inEvent,
                     Properties inViewProperties)
    {
        super(inParentWindow,
              inEvent,
              inViewProperties);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractGridView#getDataContainerType()
     */
    @Override
    protected Class<FillsPagedDataContainer> getDataContainerType()
    {
        return FillsPagedDataContainer.class;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#getViewSubjectName()
     */
    @Override
    protected String getViewSubjectName()
    {
        return "Fills";
    }
    /**
     * global name of this view
     */
    private static final String NAME = "Fills View";
    private static final long serialVersionUID = 8743932938054580853L;
}
