package org.marketcetera.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * CopierModule passes the data coming in as part of DataRequest to the receiver.
 * 
 * @author anshul@marketcetera.com
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CopierModule extends Module implements DataEmitter {
    protected CopierModule() {
        super(CopierModuleFactory.INSTANCE_URN, true);
    }

    protected void preStart() throws ModuleException {
    }

    protected void preStop() throws ModuleException {
    }

    /** Passes the incoming data to the receiver   */
    public void requestData(final DataRequest inRequest,
                            final DataEmitterSupport inSupport)
            throws RequestDataException {
        Future<?> result = mService.submit(new Callable<Object>(){
            public Object call() throws Exception {
                Object req = inRequest.getData();
                Semaphore requester = null;
                if(req instanceof SynchronousRequest) {
                    SynchronousRequest synchronousRequest = (SynchronousRequest)req;
                    requester = synchronousRequest.semaphore;
                    if(requester.availablePermits() != 0) {
                        inSupport.dataEmitError(TestMessages.INCORRECT_SEMAPHORE_STATE, true);
                        return null;
                    }
                    req = synchronousRequest.getPayload();
                }
                if(req instanceof Object[]) {
                    for(Object o:(Object[])req) {
                        inSupport.send(o);
                    }
                } else if (req instanceof Collection) {
                    for(Object o: (Collection<?>)req) {
                        inSupport.send(o);
                    }
                } else {
                    inSupport.send(req);
                }
                if(requester != null) {
                    requester.release();
                }
                return null;
            }
        });
        mRequestTable.put(inSupport.getRequestID(), result);
    }

    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        Future<?> f = mRequestTable.get(inRequestID);
        if(f != null) {
            f.cancel(true);
        }
    }
    private final ExecutorService mService = Executors.newCachedThreadPool();
    private final Map<RequestID, Future<?>> mRequestTable = new HashMap<RequestID, Future<?>>();
    /**
     * A special kind of request that indicates that the given payload should be delivered synchronously.
     * 
     * <p> This class is intended to be used with {@link CopierModule}.  Normally, {@link CopierModule} delivers
     * the payload passed to it via {@link CopierModule#requestData(DataRequest, DataEmitterSupport)} asynchronously.
     * However, if the data request payload is actually an instance of <code>SynchronousRequest</code>, the
     * payload <em>can</em> be induced to behave synchronously by means of a {@link Semaphore}.
     * 
     * <p>For example:
     * <pre>
     *     ModuleURN myDestinationURN;
     *     Object[] payload = new Object[] { "Some object", "that I want copier to send", "while I wait" };
     *     SynchronousRequest request = new SynchronousRequest(payload);
     *     request.semaphore.acquire();
     *     moduleManager.createDataFlow(new DataRequest[] { new DataRequest(CopierModuleFactory.INSTANCE_URN,
     *                                                                      request),
     *                                                      new DataRequest(myDestinationURN) },
     *                                  false);
     *     request.semaphore.acquire();
     * </pre>
     * 
     * <p>Note that at the conclusion of the above block, the request should be allowed to go out of scope and
     * be garbage-collected or the permit acquired by the calling code should be released:
     * <pre>
     *     request.semaphore.release();
     * </pre>
     * 
     * <p>Proper use of this class guarantees only that the payload will be delivered synchronously.  The recipient
     * of the payload may, of course, choose to act asynchronously itself.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    public static class SynchronousRequest
    {
        /**
         * the payload for the {@link CopierModule} to deliver
         */
        private final Object payload;
        /**
         * the semaphore used by the {@link CopierModule} to indicate that the payload has been delivered
         */
        public final Semaphore semaphore;
        /**
         * Create a new SynchronousRequest instance.
         *
         * @param inPayload an <code>Object</code> value to be delivered by the {@link CopierModule}.
         */
        public SynchronousRequest(Object inPayload)
        {
            payload = inPayload;
            semaphore = new Semaphore(1);
        }
        /**
         * Returns the payload to be delivered. 
         *
         * @return an <code>Object</code> value
         */
        public Object getPayload()
        {
            return payload;
        }
    }
}
