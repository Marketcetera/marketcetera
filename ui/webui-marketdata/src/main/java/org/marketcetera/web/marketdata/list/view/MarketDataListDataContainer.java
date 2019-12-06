package org.marketcetera.web.marketdata.list.view;

import org.marketcetera.web.marketdata.list.view.MarketDataListView.MarketDataRow;

import com.vaadin.data.util.BeanItemContainer;

/* $License$ */

/**
 * Provides a container for {@link MarketDataRow} objects in the {@link MarketDataListView}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataListDataContainer
        extends BeanItemContainer<MarketDataRow>
{
    /**
     * Create a new MarketDataListDataContainer instance.
     *
     * @throws IllegalArgumentException
     */
    public MarketDataListDataContainer()
            throws IllegalArgumentException
    {
        super(MarketDataRow.class);
    }
    
    private static final long serialVersionUID = 9212034203343277029L;
}
