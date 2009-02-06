package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.module.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.Map;
import java.util.Hashtable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.EOFException;
import java.net.URL;
import java.net.MalformedURLException;


/* $License$ */
/**
 * A module that reads serialized object data contained in a file and emits
 * them into the data flow.
 * The module accepts request parameters of following types:
 * <ul>
 *      <li><b>{@link String}</b> : interpreted as a file path
 *      from which the serialized data can be read.</li>
 *      <li>{@link java.io.File}: path to the file.</li>
 *      <li>{@link java.net.URL}: the url of the file.</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ObjectEmitter extends Module implements DataEmitter {
    /**
     * Creates an instance.
     *
     * @param inURN the module URN.
     * @param inAutoStart if the module should be auto started.
     */
    protected ObjectEmitter(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
    }

    @Override
    protected void preStart() {
        mService = Executors.newCachedThreadPool();
    }

    @Override
    protected void preStop() {
        mService.shutdownNow();
    }

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType,
            IllegalRequestParameterValue {
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        URL url;
        try {
            if(obj instanceof String) {
                try {
                    url = new URL((String)obj);
                } catch(MalformedURLException ignore) {
                    url = new File((String) obj).toURI().toURL();
                }
            } else if (obj instanceof File) {
                url = ((File)obj).toURI().toURL();
            } else if (obj instanceof URL) {
                url = (URL) obj;
            } else {
                throw new UnsupportedRequestParameterType(getURN(), obj);
            }
            ObjectsReader reader = new ObjectsReader(url, inSupport);
            Future<Boolean> future = mService.submit(reader);
            mRequests.put(inSupport.getRequestID(), future);
        } catch (MalformedURLException e) {
            throw new IllegalRequestParameterValue(getURN(), obj, e);
        }

    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        Future<Boolean> future = mRequests.get(inRequestID);
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * This task reads the contents of a file and emits each object read
     * from the file into the data flow.
     */
    private static class ObjectsReader implements Callable<Boolean> {
        /**
         * Creates an instance.
         *
         * @param inSource The URL from which the file can be read.
         * @param inSupport the handle to emit data.
         */
        private ObjectsReader(URL inSource, DataEmitterSupport inSupport) {
            mSource = inSource;
            mSupport = inSupport;
        }

        @Override
        public Boolean call() throws Exception {
            Object obj;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(mSource.openStream());
                while((obj = ois.readObject()) != null) {
                    mSupport.send(obj);
                }
                //Terminate the data flow.
                mSupport.dataEmitError(Messages.NO_MORE_DATA,true);
            } catch (EOFException e) {
                mSupport.dataEmitError(Messages.NO_MORE_DATA,true);
            } catch (Throwable e) {
                //Terminate the data flow if there's any error.
                mSupport.dataEmitError(new I18NBoundMessage1P(
                        Messages.UNEXPECTED_ERROR, e.getLocalizedMessage()),
                        true);
            } finally {
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            return true;
        }

        private URL mSource;
        private DataEmitterSupport mSupport;
    }
    private ExecutorService mService;
    private final Map<RequestID, Future<Boolean>> mRequests =
            new Hashtable<RequestID, Future<Boolean>>();
}