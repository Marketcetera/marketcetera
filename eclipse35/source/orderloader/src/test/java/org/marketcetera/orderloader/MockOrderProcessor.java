package org.marketcetera.orderloader;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.trade.Order;

import java.util.List;
import java.util.LinkedList;

/**
 * A mock order processor for unit testing.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
public class MockOrderProcessor implements OrderProcessor {
    @Override
    public void done() {
        mDoneInvoked = true;
    }
    @Override
    public void processOrder(Order inOrder, int inOrderIndex) throws Exception {
        mOrders.add(inOrder);
        if(mFail) {
            throw new IllegalArgumentException(ORDER_FAILURE_STRING);
        }
    }
    public List<Order> getOrders() {
        return mOrders;
    }

    public void setFail(boolean inFail) {
        mFail = inFail;
    }

    public boolean isDoneInvoked() {
        return mDoneInvoked;
    }

    private boolean mFail = false;
    private boolean mDoneInvoked = false;
    private final List<Order> mOrders = new LinkedList<Order>();
    static final String ORDER_FAILURE_STRING = "OrderFailure";
}
