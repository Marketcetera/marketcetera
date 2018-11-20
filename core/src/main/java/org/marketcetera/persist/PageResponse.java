package org.marketcetera.persist;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Response to a request for a page of data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PageResponse
{
    /**
     * Get the totalSize value.
     *
     * @return a <code>long</code> value
     */
    public long getTotalSize()
    {
        return totalSize;
    }
    /**
     * Sets the totalSize value.
     *
     * @param inTotalSize a <code>long</code> value
     */
    public void setTotalSize(long inTotalSize)
    {
        totalSize = inTotalSize;
    }
    /**
     * Get the totalPages value.
     *
     * @return an <code>int</code> value
     */
    public int getTotalPages()
    {
        return totalPages;
    }
    /**
     * Sets the totalPages value.
     *
     * @param inTotalPages an <code>int</code> value
     */
    public void setTotalPages(int inTotalPages)
    {
        totalPages = inTotalPages;
    }
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
     * Get the pageMaxSize value.
     *
     * @return an <code>int</code> value
     */
    public int getPageMaxSize()
    {
        return pageMaxSize;
    }
    /**
     * Sets the pageMaxSize value.
     *
     * @param inPageMaxSize an <code>int</code> value
     */
    public void setPageMaxSize(int inPageMaxSize)
    {
        pageMaxSize = inPageMaxSize;
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
     * Get the hasContent value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasContent()
    {
        return hasContent;
    }
    /**
     * Sets the hasContent value.
     *
     * @param inHasContent a <code>boolean</code> value
     */
    public void setHasContent(boolean inHasContent)
    {
        hasContent = inHasContent;
    }
    /**
     * Get a sublist view of the given source.
     * 
     * <p>Changes to the given list will result in changes to the returned list, i.e.,
     * the returned list is a view, not a new list. Both page and page size are 1-indexed,
     * not 0-indexed.
     *
     * @param inSource a <code>List&lt;T&gt;</code> value
     * @param inPage an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>List&lt;T&gt;</code> value
     */
    public static <T> List<T> getPage(List<T> inSource,
                                      int inPage,
                                      int inPageSize)
    {
        if(inPageSize <= 0 || inPage <= 0) {
            throw new IllegalArgumentException("invalid page size: " + inPageSize);
        }
        int fromIndex = (inPage - 1) * inPageSize;
        if(inSource == null || inSource.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return inSource.subList(fromIndex,
                                Math.min(fromIndex + inPageSize,
                                         inSource.size()));
    }
    public static int getNumberOfPages(PageRequest inPageRequest,
                                       int inTotalSize)
    {
        return -1;
    }
    /**
     * Get the sort from the given order.
     *
     * @param inOrder an <code>Order</code> value
     * @return a <code>Sort</code> value
     */
    public static Sort getSort(Order inOrder)
    {
        return new Sort(inOrder.getProperty(),
                        getSortDirection(inOrder.getDirection()));
    }
    /**
     * Get the indicated sort direction.
     *
     * @param inDirection a <code>Direction</code> value
     * @return a <code>SortDirection</code> value
     */
    public static SortDirection getSortDirection(Direction inDirection)
    {
        switch(inDirection) {
            case ASC:
                return SortDirection.ASCENDING;
            case DESC:
                return SortDirection.DESCENDING;
            default:
                throw new UnsupportedOperationException();
        }
    }
    /**
     * indicate if the response has content
     */
    private boolean hasContent;
    /**
     * optional sort order
     */
    private List<Sort> sortOrder = Lists.newArrayList();
    /**
     * the total number of items
     */
    private long totalSize;
    /**
     * the total number of pages
     */
    private int totalPages;
    /**
     * the zero-indexed page number
     */
    private int pageNumber;
    /**
     * the actual page size
     */
    private int pageSize;
    /**
     * the requested page size
     */
    private int pageMaxSize;
}
