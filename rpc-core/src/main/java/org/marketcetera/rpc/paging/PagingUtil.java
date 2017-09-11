package org.marketcetera.rpc.paging;

import java.util.List;

import org.marketcetera.persist.CollectionPageResponse;
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
            Sort sort = new Sort();
            sort.setDirection(rpcSort.getDirection()==PagingRpc.SortDirection.ASCENDING?SortDirection.ASCENDING:SortDirection.DESCENDING);
            sort.setProperty(rpcSort.getProperty());
            sortOrder.add(sort);
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
}
