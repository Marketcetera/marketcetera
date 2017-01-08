package org.marketcetera.persist;

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
     * zero-indexed page number
     */
    private int pageNumber;
    /**
     * size of the page
     */
    private int pageSize;
}
