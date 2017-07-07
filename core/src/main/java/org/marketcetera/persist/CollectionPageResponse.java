package org.marketcetera.persist;

import java.util.Collection;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Response to a paged data request that includes a collection of results.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class CollectionPageResponse<Clazz>
        extends PageResponse
{
    /**
     * Get the elements value.
     *
     * @return a <code>Collection&lt;Clazz&gt;</code> value
     */
    public Collection<Clazz> getElements()
    {
        return elements;
    }
    /**
     * Sets the elements value.
     *
     * @param inElements a <code>Collection&lt;Clazz&gt;</code> value
     */
    public void setElements(Collection<Clazz> inElements)
    {
        elements = inElements;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("CollectionPageResponse [totalSize=")
                .append(getTotalSize()).append(", totalPages=").append(getTotalPages())
                .append(", pageNumber=").append(getPageNumber()).append(", pageSize=").append(getPageSize())
                .append(", pageMaxSize=").append(getPageMaxSize()).append("] data: ").append(elements);
        return builder.toString();
    }
    /**
     * data elements in the collection
     */
    private Collection<Clazz> elements = Lists.newArrayList();
}
