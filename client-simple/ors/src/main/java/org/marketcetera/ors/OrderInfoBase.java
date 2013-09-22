package org.marketcetera.ors;

import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.Message;

/**
 * A foundation order information class for entries in an {@link
 * OrderInfoCache} cache.
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class OrderInfoBase<T extends OrderInfoCache>
    implements OrderInfo
{

    // INSTANCE DATA.

    private final T mCache;
    private final OrderID mOrderID;
    private final OrderID mOrigOrderID;
    private final UserID mActorID;
    private boolean mViewerIDSet;
    private UserID mViewerID;


    // CONSTRUCTORS.

    /**
     * Creates a new cache entry of the given cache for an order with
     * the given self and parent IDs, and the given actor ID. The
     * viewer ID of the new entry is either marked unknown-at-present
     * (if this order is a child of an earlier one), or set to the
     * actor ID (if it's the root of a chain).
     *
     * @param cache The cache.
     * @param orderID The order ID.
     * @param origOrderID The parent order ID. It may be null for
     * orders that are chain roots.
     * @param actorID The actor ID.
     */

    public OrderInfoBase
        (T cache,
         OrderID orderID,
         OrderID origOrderID,
         UserID actorID)
    {
        mCache=cache;
        mOrderID=orderID;
        mOrigOrderID=origOrderID;
        mActorID=actorID;
        if (getOrigOrderID()==null) {
            setViewerID(getActorID());
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's cache.
     *
     * @return The cache.
     */

    protected T getCache()
    {
        return mCache;
    }


    // OrderInfo.

    @Override
    public OrderID getOrderID()
    {
        return mOrderID;
    }

    @Override
    public OrderID getOrigOrderID()
    {
        return mOrigOrderID;
    }

    @Override
    public UserID getActorID()
    {
        return mActorID;
    }

    @Override
    public synchronized boolean isViewerIDSet()
    {
        return mViewerIDSet;
    }

    @Override
    public synchronized UserID getViewerID()
    {
        if (!isViewerIDSet()) {
            throw new IllegalStateException();
        }
        return mViewerID;
    }

    @Override
    public synchronized void setViewerID
        (UserID viewerID)
    {
        mViewerIDSet=true;
        mViewerID=viewerID;
    }

    @Override
    public void setERPersisted
         (boolean erPersisted) {}

    @Override
    public void setAckExpected
        (boolean ackExpected) {}

    @Override
    public void setAckProcessed
        (boolean ackProcessed) {}

    @Override
    public void setResponseExpected
        (boolean responseExpected) {}
    
    @Override
    public void setResponseProcessed
        (boolean responseProcessed) {}
    
    @Override
    public void setMessageProcessed
        (Message msg) {}
}
