package org.marketcetera.web.marketdata.list.view;

import org.marketcetera.web.marketdata.list.view.MarketDataListView.MarketDataRow;
import org.marketcetera.web.view.GridDataContainer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides a container for {@link MarketDataRow} objects in the {@link MarketDataListView}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MarketDataListDataContainer
        extends GridDataContainer<MarketDataRow>
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
