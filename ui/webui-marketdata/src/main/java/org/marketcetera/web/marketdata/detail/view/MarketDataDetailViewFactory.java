package org.marketcetera.web.marketdata.detail.view;

import org.marketcetera.web.view.AbstractContentViewFactory;
import org.marketcetera.web.view.ContentView;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Creates {@link MarketDataDetailView} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
public class MarketDataDetailViewFactory
        extends AbstractContentViewFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.view.AbstractContentViewFactory#getViewType()
     */
    @Override
    protected Class<? extends ContentView> getViewType()
    {
        return MarketDataDetailView.class;
    }
}
