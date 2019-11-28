package org.marketcetera.web.trade.executionreport.view;

import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.web.trade.view.AbstractTradeViewFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 * Provides common behavior for view factories that use {@link ExecutionReport} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractExecutionReportViewFactory
        extends AbstractTradeViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.admin.AbstractAdminViewFactory#getViewName()
     */
    @Override
    protected String getViewName()
    {
        return getMenuCaption();
    }
    /**
     * provides access to the application context
     */
    @Autowired
    protected ApplicationContext applicationContext;
}
