package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.Hashtable;

/* $License$ */
/**
 * The module for sending orders.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
//$NON-NLS-1$
public class OrderSenderModule extends Module implements DataEmitter {
    protected OrderSenderModule(ModuleURN inURN, Object[] inOrders) {
        super(inURN, true);
        mOrders = inOrders;
    }

    @Override
    protected void preStart() throws ModuleException {
    }

    @Override
    protected void preStop() throws ModuleException {
    }

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException {
        mTable.put(inSupport.getRequestID(), sExecutors.submit(
                new OrderSender(inSupport)));
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        Future<?> future = mTable.remove(inRequestID);
        future.cancel(true);
    }

    public class OrderSender implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            for (Object o : mOrders) {
                if (Boolean.FALSE.equals(o)) {
                    //Help test client failure duing data flow
                    ClientManager.getInstance().close();
                } else {
                    mSupport.send(o);
                }
            }
            //Sleep until the flow is cancelled
            synchronized(this) {
                wait();
            }
            return null;
        }

        public OrderSender(DataEmitterSupport inSupport) {
            mSupport = inSupport;
        }

        private DataEmitterSupport mSupport;
    }

    private final Object[] mOrders;
    private final Map<RequestID, Future<?>> mTable = new Hashtable<RequestID, Future<?>>();
    private final static ExecutorService sExecutors =
            Executors.newCachedThreadPool();
}
