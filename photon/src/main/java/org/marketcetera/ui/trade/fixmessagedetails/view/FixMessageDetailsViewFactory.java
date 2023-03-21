package org.marketcetera.ui.trade.fixmessagedetails.view;

import org.marketcetera.ui.view.AbstractContentViewFactory;
import org.marketcetera.ui.view.ContentView;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Creates {@link OpenOrderView} content objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class FixMessageDetailsViewFactory
        extends AbstractContentViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return FixMessageDetailsView.class;
    }
}
