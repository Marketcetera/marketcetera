package org.marketcetera.persist;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Request for a page of data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name="pageRequest")
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
     * Create a new PageRequest instance.
     */
    public PageRequest() {}
    /**
     * Create a new MyPageRequest instance.
     *
     * @param inPage an <code>int</code> value
     * @param inSize an <code>int</code> value
     */
    public PageRequest(int inPage,
                       int inSize)
    {
        pageNumber = inPage;
        pageSize = inSize;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("PageRequest [pageNumber=").append(pageNumber).append(", pageSize=").append(pageSize)
                .append(", sortOrder=").append(sortOrder).append("]");
        return builder.toString();
    }
    /**
     * page request which requests all data in a single page
     */
    public transient static final PageRequest ALL = new PageRequest(0,Integer.MAX_VALUE);
    /**
     * optional sort directive
     */
    @XmlElement
    private List<Sort> sortOrder = Lists.newArrayList();
    /**
     * zero-indexed page number
     */
    @XmlAttribute
    private int pageNumber;
    /**
     * size of the page
     */
    @XmlAttribute
    private int pageSize;
}
