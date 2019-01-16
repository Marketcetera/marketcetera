package org.marketcetera.persist;

import java.util.List;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Request for a page of data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PageRequest
{
    /**
     * Get the pageNumber value.
     *
     * @return an <code>int</code> value
     */
    public int getPageNumber()
    {
        return pageNumber;
    }
    /**
     * Sets the pageNumber value.
     *
     * @param inPageNumber an <code>int</code> value
     */
    public void setPageNumber(int inPageNumber)
    {
        pageNumber = inPageNumber;
    }
    /**
     * Get the pageSize value.
     *
     * @return an <code>int</code> value
     */
    public int getPageSize()
    {
        return pageSize;
    }
    /**
     * Sets the pageSize value.
     *
     * @param inPageSize an <code>int</code> value
     */
    public void setPageSize(int inPageSize)
    {
        pageSize = inPageSize;
    }
    /**
     * Get the sortOrder value.
     *
     * @return a <code>List&lt;Sort&gt;</code> value
     */
    public List<Sort> getSortOrder()
    {
        return sortOrder;
    }
    /**
     * Sets the sortOrder value.
     *
     * @param inSortOrder a <code>List&lt;Sort&gt;</code> value
     */
    public void setSortOrder(List<Sort> inSortOrder)
    {
        sortOrder = inSortOrder;
    }
    /**
     * Get the next page request value.
     *
     * @return a <code>PageRequest</code> value
     */
    public PageRequest getNextPageRequest()
    {
        pageNumber += 1;
        return this;
    }
    /**
     * optional sort directive
     */
    private List<Sort> sortOrder = Lists.newArrayList();
    /**
     * zero-indexed page number
     */
    private int pageNumber;
    /**
     * size of the page
     */
    private int pageSize;
}
