package org.marketcetera.modules.cep.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage0P;
import org.marketcetera.module.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

/* $License$ */
/**
 * CopierModule passes the data coming in as part of DataRequest to the receiver, unless it sees an incoming exception
 * in which case we treat it as a "stop flow" marker and emit an error
 * @author anshul@marketcetera.com
 * @authoer toli@marketcetera.com
 * @version $Id$
 * @since $Release$
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
                if(req instanceof Object[]) {
                    for(Object o:(Object[])req) {
                        inSupport.send(o);
                    }
                } else if (req instanceof Collection) {
                    for(Object o: (Collection)req) {
                        inSupport.send(o);
                    }
                } else {
                    inSupport.send(req);
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
        //To change body of implemented methods use File | Settings | File Templates.
    }
    private final ExecutorService mService = Executors.newCachedThreadPool();
    private final Map<RequestID, Future<?>> mRequestTable = new HashMap<RequestID, Future<?>>();
}
