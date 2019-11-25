package org.marketcetera.web.trade.fills.view;

import java.util.Properties;

import org.marketcetera.web.trade.executionreport.AbstractExecutionReportView;
import org.marketcetera.web.trade.report.model.DisplayExecutionReportSummary;
import org.marketcetera.web.view.PagedDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

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
     * @param inViewProperties a <code>Properties</code> value
     */
    FillsView(Properties inViewProperties)
    {
        super(inViewProperties);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.web.view.AbstractGridView#createBeanItemContainer()
     */
    @Override
    protected PagedDataContainer<DisplayExecutionReportSummary> createDataContainer()
    {
        return new FillsPagedDataContainer(this);
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
