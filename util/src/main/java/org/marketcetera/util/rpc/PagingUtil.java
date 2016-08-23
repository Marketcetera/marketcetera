package org.marketcetera.util.rpc;

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
}
