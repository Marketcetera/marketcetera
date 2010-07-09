package org.marketcetera.ors;

import org.junit.Test;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.UserID;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class OrderInfoCacheTest
    extends TestCaseBase
{
    protected static final UserID TEST_ACTOR_ID1=
        new UserID(1);
    protected static final UserID TEST_ACTOR_ID2=
        new UserID(2);
    protected static final OrderID TEST_ORDER_ID1=
        new OrderID("o1");
    protected static final OrderID TEST_ORDER_ID2=
        new OrderID("o2");


    @Test
    public void cacheOperation()
    {
        SimpleOrderInfoCache cache=new SimpleOrderInfoCache();

        SimpleOrderInfo info=
            cache.put(TEST_ORDER_ID1,TEST_ORDER_ID2,TEST_ACTOR_ID1);
        assertSame(info,cache.get(TEST_ORDER_ID1));
        cache.remove(TEST_ORDER_ID1);
        assertNull(cache.get(TEST_ORDER_ID1));
    }

    @Test
    public void entryFieldRetrieval()
    {
        SimpleOrderInfoCache cache=new SimpleOrderInfoCache();

        SimpleOrderInfo info=
            cache.put(TEST_ORDER_ID1,TEST_ORDER_ID2,TEST_ACTOR_ID1);
        assertSame(TEST_ORDER_ID1,info.getOrderID());
        assertSame(TEST_ORDER_ID2,info.getOrigOrderID());
        assertSame(TEST_ACTOR_ID1,info.getActorID());
        assertFalse(info.isViewerIDSet());

        // Set viewer ID manually.
        info.setViewerID(TEST_ACTOR_ID2);
        assertSame(TEST_ACTOR_ID2,info.getViewerID());
        assertTrue(info.isViewerIDSet());

        // No status updates initially.
        assertNull(info.getERPersisted());
        assertNull(info.getAckExpected());
        assertNull(info.getAckProcessed());
        assertNull(info.getResponseExpected());
        assertNull(info.getResponseProcessed());

        // Status updates (with checks for auto-removal).
        info.setERPersisted(false);
        assertFalse(info.getERPersisted());
        assertSame(info,cache.get(TEST_ORDER_ID1));
        info.setAckExpected(false);
        assertFalse(info.getAckExpected());
        assertSame(info,cache.get(TEST_ORDER_ID1));
        info.setAckProcessed(false);
        assertFalse(info.getAckProcessed());
        assertSame(info,cache.get(TEST_ORDER_ID1));
        info.setResponseExpected(false);
        assertFalse(info.getResponseExpected());
        assertNull(cache.get(TEST_ORDER_ID1));
        info.setResponseProcessed(false);
        assertFalse(info.getResponseProcessed());
        assertNull(cache.get(TEST_ORDER_ID1));
    }

    @Test
    public void entryFieldRetrievalNoParentOrderIF()
    {
        SimpleOrderInfoCache cache=new SimpleOrderInfoCache();

        SimpleOrderInfo info=
            cache.put(TEST_ORDER_ID1,null,TEST_ACTOR_ID1);
        assertNull(info.getOrigOrderID());
        assertSame(TEST_ACTOR_ID1,info.getActorID());

        // Viewer ID is set automatically.
        assertTrue(info.isViewerIDSet());
        assertSame(TEST_ACTOR_ID1,info.getViewerID());
    }
}
