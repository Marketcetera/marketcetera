package org.marketcetera.modules.cep.esper;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.espertech.esper.client.time.TimerControlEvent;
import org.marketcetera.core.Pair;
import org.marketcetera.event.TimestampCarrier;
import org.marketcetera.module.*;
import org.marketcetera.modules.cep.system.CEPDataTypes;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;
import org.w3c.dom.Node;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/* $License$ */
/**
 * A module that processes data using the Esper Runtime.
 * The module can receive any type of data. Null data values are ignored.
 * {@link Map} and {@link Node} data types are supplied to the esper
 * runtime so that esper specific interpretation is possible. All maps
 * are supplied to the runtime with the type name <code>map</code>.
 * <p>
 *
 * When requesting data,
 * either an EPL or a Pattern query has to be specified. The results of
 * the query are emitted to the next module in the data flow.
 * <p>
 * Multiple queries can be submitted when creating a data flow. When
 * multiple queries are submitted, only the results of the last query
 * are emitted to the next stage in the data flow.
 * <p>
 * Any errors in the query syntax will result in an error when setting up
 * the data flow, except when the module is configured to use external
 * time.
 * <p>
 * If the module is configured to use external time, errors in query
 * syntax are not reported until after the module has received data
 * that implements {@link TimestampCarrier}. Errors in query will
 * result in the data flow being cancelled.
 * When configured to use external time, the module creates the
 * query statements after it receives the first {@link TimestampCarrier}.
 * Any non-<code>TimestampCarrier</code> received prior to that are
 * reported and ignored.
 *
 * @author anshul@marketcetera.com
 * @author toli@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CEPEsperProcessor extends Module
        implements DataReceiver, DataEmitter, CEPEsperProcessorMXBean {

    /** Reference counter that keep track if we get events posted back into us from events that we emit
     * ie we emit to a strategy that sends events in back to this Esper instance
     * 0 means "not self-posted event", ie "send regular events to Esper"
     */
    private final ThreadLocal<Integer> mSelfPostingEvents = new ThreadLocal<Integer>()  {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    protected CEPEsperProcessor(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
    }

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws UnsupportedRequestParameterType,
            IllegalRequestParameterValue {
        if(inRequest == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        String [] stmts;
        if(obj instanceof String) {
            stmts = new String[]{(String)obj};
        } else if (obj instanceof String[]) {
            stmts = (String[]) obj;
            if(stmts.length < 1) {
                throw new IllegalRequestParameterValue(getURN(), stmts);
            }
        } else {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }
        try {
            getDelegate().processRequest(stmts, inSupport);
        } catch (RequestDataException e) {
            throw new IllegalRequestParameterValue(e, new I18NBoundMessage1P(Messages.ERROR_CREATING_STATEMENTS, Arrays.toString(stmts)));
        }
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        getDelegate().cancelRequest(inFlowID, inRequestID);
    }

    /** Need to keep a reference count in case of nested events being sent out of Esper and posted back in
     * Increment the count before, and then decrement after
     */
    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws UnsupportedDataTypeException, StopDataFlowException {
        if(inData != null) {
            getDelegate().preProcessData(inFlowID, inData);
            int selfPostedCounter = mSelfPostingEvents.get();
            boolean fSelfPostedEvent = selfPostedCounter > 0;
            mSelfPostingEvents.set(selfPostedCounter+1);
            try {
                if (inData instanceof Map) {
                    if(fSelfPostedEvent) {
                        mService.getEPRuntime().route((Map)inData, CEPDataTypes.MAP);
                    } else {
                        mService.getEPRuntime().sendEvent((Map)inData, CEPDataTypes.MAP);
                    }
                } else if(inData instanceof Node) {
                    if (fSelfPostedEvent) {
                        mService.getEPRuntime().route((Node) inData);
                    } else {
                        mService.getEPRuntime().sendEvent((Node) inData);
                    }
                } else {
                    if (fSelfPostedEvent) {
                        mService.getEPRuntime().route(inData);
                    } else {
                        mService.getEPRuntime().sendEvent(inData);
                    }
                }
            } finally {
                mSelfPostingEvents.set(selfPostedCounter);
            }
        }
        //ignore null data
    }

    @Override
    public String getConfiguration() {
        return mConfiguration;
    }

    @Override
    public void setConfiguration(String inConfiguration) {
        if(getState().isStarted()) {
            throw new IllegalStateException(Messages.ERROR_MODULE_ALREADY_STARTED.getText());
        }
        mConfiguration = inConfiguration;
    }

    @Override
    public String[] getStatementNames() {
        if(getState().isStarted()) {
            return mService.getEPAdministrator().getStatementNames();
        }
        throw new IllegalStateException(Messages.ERROR_MODULE_NOT_STARTED.getText());
    }

    @Override
    public long getNumEventsReceived() {
        if(getState().isStarted()) {
            return mService.getEPRuntime().getNumEventsReceived();
        }
        throw new IllegalStateException(Messages.ERROR_MODULE_NOT_STARTED.getText());
    }

    @Override
    public boolean isUseExternalTime() {
        return mUseExternalTime;
    }

    @Override
    public void setUseExternalTime(boolean inUseExternalTime) {
        if(getState().isStarted()) {
            throw new IllegalStateException(Messages.ERROR_MODULE_ALREADY_STARTED.getText());
        }
        mUseExternalTime = inUseExternalTime;
    }

    /**
     * Creates an instance.
     *
     * @param inURN the module URN.
     */
    protected CEPEsperProcessor(ModuleURN inURN) {
        super(inURN, true);
    }

    @Override
    protected void preStart() throws ModuleException {
        String configFile = getConfiguration();
        Configuration configuration = new Configuration();
        try {
            if(configFile != null) {
                try {
                    //Try URL configuration
                    URL u = new URL(configFile);
                    configuration.configure(u);
                } catch (MalformedURLException ignore) {
                    File f = new File(configFile);
                    //Try File configuration
                    if(f.isFile()) {
                        configuration.configure(f);
                    } else {
                        //Try classpath configuration
                        configuration.configure(configFile);
                    }
                }
            }

            for (Pair<String, Class<?>> stringClassPair : CEPDataTypes.REQUEST_PRECANNED_TYPES) {
                if (stringClassPair.getFirstMember().equals(CEPDataTypes.MAP)) {
                    configuration.addEventTypeAlias(CEPDataTypes.MAP, new Properties());
                } else {
                    configuration.addEventTypeAlias(stringClassPair.getFirstMember(), stringClassPair.getSecondMember());
                }
            }
            configuration.addEventTypeAlias(CEPDataTypes.TIME_CARRIER, TimestampCarrier.class);

            mService = EPServiceProviderManager.getProvider(
                    getURN().instanceName(), configuration);
            if(isUseExternalTime()) {
                mService.getEPRuntime().sendEvent(new TimerControlEvent(
                        TimerControlEvent.ClockType.CLOCK_EXTERNAL));
                mDelegate = new ExternalTimeDelegate();
            } else {
                mDelegate = new RegularDelegate();
            }
        } catch (EPException e) {
            throw new ModuleException(e, Messages.ERROR_CONFIGURING_ESPER.getMessage());
        }
    }

    @Override
    protected void preStop() {
        mService.destroy();
        mService = null;
    }

    /**
     * Submits the supplied queries to the runtime and returns the statement
     * objects representing each one of those queries.
     *
     * @param inQuery the EPL and Pattern queries.
     *
     * @return The statements representing the submitted queries.
     * @throws EPException in case the statements cannot be created
     */
    protected ArrayList<EPStatement> createStatements(String... inQuery) throws EPException {
        ArrayList<EPStatement> stmts = new ArrayList<EPStatement>(inQuery.length);
        try {
            for(String query: inQuery) {
                if(query.startsWith(PATTERN_QUERY_PREFIX)) {
                    stmts.add(mService.getEPAdministrator().
                            createPattern(query.substring(
                                    PATTERN_QUERY_PREFIX.length())));
                } else {
                    stmts.add(mService.getEPAdministrator().createEPL(query));
                }
            }
        } catch(EPException ex) {
            // destroy all pre-created statements so that they don't leak and re-throw exctpion
            for (EPStatement stmt : stmts) {
                stmt.destroy();
            }
            throw ex;
        }
        return stmts;
    }

    private ProcessingDelegate getDelegate() {
        return mDelegate;
    }

    /**
     * The Esper engine runtime.
     */
    private EPServiceProvider mService;
    /**
     * The table of requests that this module is currently processing.
     */
    private final Map<RequestID, List<EPStatement>> mRequests =
            new Hashtable<RequestID, List<EPStatement>>();

    private String mConfiguration;
    private volatile boolean mUseExternalTime;
    /**
     * The prefix for pattern queries - they all start with p:xxxxx
     */
    private static final String PATTERN_QUERY_PREFIX = "p:";  //$NON-NLS-1$

    private final Map<DataFlowID, List<Pair<DataEmitterSupport, String[]>>> mUnprocessedRequests =
            new Hashtable<DataFlowID, List<Pair<DataEmitterSupport, String[]>>>();
    private ProcessingDelegate mDelegate;

    /** Basic interface to describe a data flow processing delegate.
     * This is going to be used by both the "straight-through to Esper" delegate and by
     * the {#link ExternalTimeDelegate} that will behave differently in when
     * external time is being used.
     */
    private static interface ProcessingDelegate {
        /** Sends the request to be processed */
        void processRequest(String[] inStmts, DataEmitterSupport inSupport) throws RequestDataException;
        /** Cancels all existing and pending requests */
        void cancelRequest(DataFlowID inFlowID, RequestID inRequestID);

        void preProcessData(DataFlowID inFlowID, Object inData) throws StopDataFlowException;
    }

    /** Regular "straight-through" delegate - just send all the incoming queries directly to Esper
     * This is for non-external-time (ie for wall-clock time) operation.
     */
    private class RegularDelegate implements ProcessingDelegate {
        /** Creates the incoming statements with Esper, and creates a subscriber for the last one */
        @Override
        public void processRequest(String[] inStmts, DataEmitterSupport inSupport) throws RequestDataException {
            ArrayList<EPStatement> statements;
            try {
                statements = createStatements(inStmts);
                statements.get(statements.size() - 1).setSubscriber(new Subscriber(inSupport));
            } catch (EPException ex) {
                throw new RequestDataException(ex);
            }
            mRequests.put(inSupport.getRequestID(), statements);
        }

        /** Go through and destroy all the existing EPL statements */
        public void cancelRequest(DataFlowID inFlowID, RequestID inRequestID) {
            List<EPStatement> stmts = mRequests.remove(inRequestID);
            if(stmts != null) {
                for(EPStatement s: stmts) {
                    s.destroy();
                }
            }
        }

        // Nothing to pre-process for regular implementation
        public void preProcessData(DataFlowID inFlowID, Object inData) throws StopDataFlowException {
            //do nothing
        }
    }

    /** Responsible for implemneting external time behaviour -
     * instead of sending the querieis straight to Esper, we wait
     * until the first time event comes in, and only start CEP then
     */
    private class ExternalTimeDelegate extends RegularDelegate {
        /** Cache the incoming requests - they will be kicked off after
         * we receive the first time event */
        @Override
        public void processRequest(String[] inStmts, DataEmitterSupport inSupport) {
            //Save off inSupport and statments so that they can be processed
            //in preProcessData
            List<Pair<DataEmitterSupport, String[]>> emitterList = mUnprocessedRequests.get(inSupport.getFlowID());
            if(emitterList == null) {
                emitterList = new ArrayList<Pair<DataEmitterSupport, String[]>>();
                mUnprocessedRequests.put(inSupport.getFlowID(), emitterList);
            }
            emitterList.add(new Pair<DataEmitterSupport, String[]>(inSupport, inStmts));
        }

        @Override
        public void cancelRequest(DataFlowID inFlowID, RequestID inRequestID) {
            super.cancelRequest(inFlowID, inRequestID);
            // remove any pending unprocessed statements for this flowID
            List<Pair<DataEmitterSupport, String[]>> reqList = mUnprocessedRequests.get(inFlowID);
            if(reqList != null) {
                for (Pair<DataEmitterSupport, String[]> dataEmitterSupportPair : reqList) {
                    if(dataEmitterSupportPair.getFirstMember().getRequestID().equals(inRequestID)) {
                        reqList.remove(dataEmitterSupportPair);
                    }
                }
                if(reqList.isEmpty()) {
                    mUnprocessedRequests.remove(inFlowID);
                }
            }
        }

        /** Seed the Esper engine with the incoming time event, then
         * delegate to the regular {@link #processRequest(String[], DataEmitterSupport)} implementation.
         * If the incoming events aren't TimestampCarriers, then just discard them
         */
        public void preProcessData(DataFlowID inFlowID, Object inData) throws StopDataFlowException {
            if(inData instanceof TimestampCarrier) {
                //send the time event
                mService.getEPRuntime().sendEvent(new CurrentTimeEvent(((TimestampCarrier)inData).getTimeMillis()));
                //if we have unprocessed statements process them now
                List<Pair<DataEmitterSupport, String[]>> reqList = mUnprocessedRequests.remove(inFlowID);
                if(reqList != null) {
                    for (Pair<DataEmitterSupport, String[]> oneRequest: reqList) {
                        try {
                            super.processRequest(oneRequest.getSecondMember(), oneRequest.getFirstMember());
                        } catch (RequestDataException e) {
                            throw new StopDataFlowException(e,
                                    new I18NBoundMessage1P(Messages.ERROR_CREATING_STATEMENTS, Arrays.toString(oneRequest.getSecondMember())));
                        }
                    }
                }
            }
        }
    }

    /**
     * A Subscriber class that subscribes to the query statement results
     * and emits them out to the flow that requested that statement.
     */
    public static class Subscriber {
        /**
         * Creates a new instance.
         *
         * @param inSupport the handle to emit data for the data flow.
         */
        private Subscriber(DataEmitterSupport inSupport) {
            mSupport = inSupport;
        }

        /**
         * Receives data from the statement as a map. If the map
         * contains a single value, that value is extracted and emitted.
         * Otherwise, the received value, including nulls, is emitted as is.
         *
         * @param inMap the map of values containing results of the statement.
         */
        public void update(Map inMap) {
            if(inMap != null && inMap.size() == 1) {
                mSupport.send(inMap.values().iterator().next());
            } else {
                mSupport.send(inMap);
            }
        }
        private DataEmitterSupport mSupport;
    }
}
