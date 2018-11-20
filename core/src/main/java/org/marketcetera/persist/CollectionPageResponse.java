package org.marketcetera.persist;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * Create a new CollectionPageResponse instance.
     */
    public CollectionPageResponse() {}
    /**
     * Create a new CollectionPageResponse instance.
     *
     * @param inPageRequest a <code>Pageable</code> value
     * @param inPage a <code>Page&lt;Clazz&gt:</code> value
     */
    public CollectionPageResponse(Pageable inPageRequest,
                                  Page<Clazz> inPage)
    {
        setElements(inPage.getContent());
        setHasContent(inPage.hasContent());
        setPageMaxSize(inPage.getSize());
        setPageNumber(inPage.getNumber());
        setPageSize(inPage.getNumberOfElements());
        setTotalPages(inPage.getTotalPages());
        setTotalSize(inPage.getTotalElements());
        List<Sort> sortOrder = Lists.newArrayList();
        inPageRequest.getSort().forEach(order -> sortOrder.add(getSort(order)));
        setSortOrder(sortOrder);
    }
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
