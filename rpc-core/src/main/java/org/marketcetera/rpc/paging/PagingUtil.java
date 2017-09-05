package org.marketcetera.rpc.paging;

import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageResponse;

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
        return pageBuilder.build();
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
