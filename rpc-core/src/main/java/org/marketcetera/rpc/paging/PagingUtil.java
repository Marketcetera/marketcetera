package org.marketcetera.rpc.paging;

import java.util.Collections;
import java.util.List;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.persist.PageResponse;
import org.marketcetera.persist.Sort;
import org.marketcetera.persist.SortDirection;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides utilities for RPC paging requests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PagingUtil
{
    /*
     * returns a view (not a new list) of the sourceList for the 
     * range based on page and pageSize
     * @param sourceList
     * @param page
     * @param pageSize
     * @return
     */
    public static <T> List<T> getPage(List<T> sourceList, int page, int pageSize)
    {
        if(pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if(sourceList == null || sourceList.size() < fromIndex){
            return Collections.emptyList();
        }
        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }    
    /**
     * Build an RPC page request from the given inputs.
     *
     * @param inPageNumber an <code>int</code> value
     * @param inPageSize an <code>int</code> value
     * @return a <code>PagingRpc.PageRequest</code> value
     */
    public static PagingRpc.PageRequest buildPageRequest(int inPageNumber,
                                                         int inPageSize)
    {
        PagingRpc.PageRequest.Builder pageBuilder = PagingRpc.PageRequest.newBuilder();
        pageBuilder.setPage(inPageNumber);
        pageBuilder.setSize(inPageSize);
//        if(inPageRequest.getSortOrder() != null && !inPageRequest.getSortOrder().isEmpty()) {
//            BaseRpc.SortOrder.Builder sortOrderBuilder = BaseRpc.SortOrder.newBuilder();
//            BaseRpc.Sort.Builder sortBuilder = BaseRpc.Sort.newBuilder();
//            for(Sort sort : inPageRequest.getSortOrder()) {
//                sortBuilder.setDirection(sort.getDirection()==SortDirection.ASCENDING?BaseRpc.SortDirection.ASCENDING:BaseRpc.SortDirection.DESCENDING);
//                sortBuilder.setProperty(sort.getProperty());
//                sortOrderBuilder.addSort(sortBuilder.build());
//                sortBuilder.clear();
//            }
//            pageRequestBuilder.setSortOrder(sortOrderBuilder.build());
//        }
        return pageBuilder.build();
    }
    /**
     * Add the results from the given RPC page to the given response object.
     *
     * @param inRpcPage a <code>BaseRpc.PageResponse</code> value
     * @param inResponse a <code>PageResponse</code> value
     */
    public static void addPageToResponse(PagingRpc.PageResponse inRpcPage,
                                         PageResponse inResponse)
    {
        inResponse.setPageMaxSize(inRpcPage.getPageMaxSize());
        inResponse.setPageNumber(inRpcPage.getPageNumber());
        inResponse.setPageSize(inRpcPage.getPageSize());
        inResponse.setTotalPages(inRpcPage.getTotalPages());
        inResponse.setTotalSize(inRpcPage.getTotalSize());
        List<Sort> sortOrder = Lists.newArrayList();
        for(PagingRpc.Sort rpcSort : inRpcPage.getSortOrder().getSortList()) {
            sortOrder.add(getSort(rpcSort));
        }
        inResponse.setSortOrder(sortOrder);
    }
    /**
     * Get the page number from the given page request.
     *
     * @param inPageRequest a <code>PagingRpc.PageRequest</code> value
     * @return an <code>int</code> value
     */
    public static int getPageNumber(PagingRpc.PageRequest inPageRequest)
    {
        return inPageRequest.getPage();
    }
    /**
     * Get the page size from the given page request.
     *
     * @param inPageRequest a <code>PagingRpc.PageRequest</code> value
     * @return an <code>int</code> value
     */
    public static int getPageSize(PagingRpc.PageRequest inPageRequest)
    {
        return inPageRequest.getSize();
    }
    /**
     * Get an RPC page response from the given page.
     *
     * @param inPage a <code>PageResponse</code> value
     * @return a <code>PagingRpc.PageResponse</code> value
     */
    public static PagingRpc.PageResponse getPageResponse(PageResponse inPage)
    {
        PagingRpc.PageResponse.Builder pageResponseBuilder = PagingRpc.PageResponse.newBuilder();
        pageResponseBuilder.setPageMaxSize(inPage.getPageMaxSize());
        pageResponseBuilder.setPageNumber(inPage.getPageNumber());
        pageResponseBuilder.setPageSize(inPage.getPageSize());
        pageResponseBuilder.setTotalPages(inPage.getTotalPages());
        pageResponseBuilder.setTotalSize(inPage.getTotalSize());
        return pageResponseBuilder.build();
    }
    /**
     * Set the page response values on the given page response from the given RPC page response.
     *
     * @param inPageResponse a <code>PagingRpc.PageResponse</code> value
     * @param inResults a <code>CollectionPageResponse&lt;Clazz&gt;</code> value
     */
    public static <Clazz> void setPageResponse(PagingRpc.PageResponse inPageResponse,
                                               CollectionPageResponse<Clazz> inResults)
    {
        inResults.setHasContent(inResults.getElements().isEmpty());;
        inResults.setPageMaxSize(inPageResponse.getPageMaxSize());;
        inResults.setPageNumber(inPageResponse.getPageNumber());
        inResults.setPageSize(inPageResponse.getPageSize());
//        inResults.setSortOrder(inSortOrder); TODO
        inResults.setTotalPages(inPageResponse.getTotalPages());
        inResults.setTotalSize(inPageResponse.getTotalSize());
    }
    /**
     * Get the page request from the given RPC value.
     *
     * @param inPage a <code>PagingRpc.PageRequest</code> value
     * @return a <code>PageRequest</code> value
     */
    public static PageRequest getPageRequest(PagingRpc.PageRequest inPage)
    {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNumber(inPage.getPage());
        pageRequest.setPageSize(inPage.getSize());
        if(inPage.hasSortOrder()) {
            List<Sort> sortOrder = Lists.newArrayList();
            for(PagingRpc.Sort rpcSort : inPage.getSortOrder().getSortList()) {
                sortOrder.add(getSort(rpcSort));
            }
            pageRequest.setSortOrder(sortOrder);
        }
        return pageRequest;
    }
    /**
     * Get the sort from the given RPC value.
     *
     * @param inSort a <code>PagingRpc.Sort</code> value
     * @return a <code>Sort</code> value
     */
    public static Sort getSort(PagingRpc.Sort inSort)
    {
        Sort sort = new Sort();
        sort.setDirection(getSortDirection(inSort.getDirection()));
        sort.setProperty(inSort.getProperty());
        return sort;
    }
    /**
     * Get the sort direction from the given RPC value.
     *
     * @param inDirection a <code>PagingRpc.SortDirection</code> value
     * @return a <code>SortDirection</code> value
     */
    public static SortDirection getSortDirection(PagingRpc.SortDirection inDirection)
    {
        switch(inDirection) {
            case ASCENDING:
                return SortDirection.ASCENDING;
            case DESCENDING:
                return SortDirection.DESCENDING;
            case UNRECOGNIZED:
            default:
                throw new UnsupportedOperationException(inDirection.name());
        }
    }
    /**
     * Build an RPC page request from the given value.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>PagingRpc.PageRequest</code> value
     */
    public static PagingRpc.PageRequest buildPageRequest(PageRequest inPageRequest)
    {
        PagingRpc.PageRequest.Builder builder = PagingRpc.PageRequest.newBuilder();
        builder.setPage(inPageRequest.getPageNumber());
        builder.setSize(inPageRequest.getPageSize());
        if(inPageRequest.getSortOrder() != null) {
            PagingRpc.SortOrder.Builder sortBuilder = PagingRpc.SortOrder.newBuilder();
            for(Sort sort : inPageRequest.getSortOrder()) {
                sortBuilder.addSort(getRpcSort(sort));
            }
            builder.setSortOrder(sortBuilder.build());
        }
        return builder.build();
    }
    /**
     * Get the RPC sort from the given value.
     *
     * @param inSort a <code>Sort</code> value
     * @return a <code>PagingRpc.Sort</code> value
     */
    public static PagingRpc.Sort getRpcSort(Sort inSort)
    {
        PagingRpc.Sort.Builder builder = PagingRpc.Sort.newBuilder();
        if(inSort.getDirection() != null) {
            builder.setDirection(getRpcSortDirection(inSort.getDirection()));
        }
        if(inSort.getProperty() != null) {
            builder.setProperty(inSort.getProperty());
        }
        return builder.build();
    }
    /**
     * Get the RPC sort direction from the given value.
     *
     * @param inDirection a <code>SortDirection</code> value
     * @return a <code>PagingRpc.SortDirection</code> value
     */
    public static PagingRpc.SortDirection getRpcSortDirection(SortDirection inDirection)
    {
        switch(inDirection) {
            case ASCENDING:
                return PagingRpc.SortDirection.ASCENDING;
            case DESCENDING:
                return PagingRpc.SortDirection.DESCENDING;
            default:
                throw new UnsupportedOperationException(inDirection.name());
        }
    }
}
