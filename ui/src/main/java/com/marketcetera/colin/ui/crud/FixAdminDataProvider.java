package com.marketcetera.colin.ui.crud;

import java.util.List;

import org.marketcetera.fix.ActiveFixSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.vaadin.artur.spring.dataprovider.FilterablePageableDataProvider;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminDataProvider
        extends FilterablePageableDataProvider<ActiveFixSession,String>
{
    /* (non-Javadoc)
     * @see org.vaadin.artur.spring.dataprovider.PageableDataProvider#fetchFromBackEnd(com.vaadin.flow.data.provider.Query, org.springframework.data.domain.Pageable)
     */
    @Override
    protected Page<ActiveFixSession> fetchFromBackEnd(Query<ActiveFixSession,String> inArg0,
                                                      Pageable inArg1)
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see org.vaadin.artur.spring.dataprovider.PageableDataProvider#getDefaultSortOrders()
     */
    @Override
    protected List<QuerySortOrder> getDefaultSortOrders()
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.data.provider.AbstractBackEndDataProvider#sizeInBackEnd(com.vaadin.flow.data.provider.Query)
     */
    @Override
    protected int sizeInBackEnd(Query<ActiveFixSession,String> inQuery)
    {
        throw new UnsupportedOperationException(); // TODO
    }
}
