package org.marketcetera.modules.cep.esper;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.TimerControlEvent;
import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.*;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.module.*;
import org.marketcetera.modules.cep.system.CEPDataTypes;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
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
 * @since $Release$
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CEPEsperProcessor extends Module
        implements DataReceiver, DataEmitter, CEPEsperProcessorMXBean {

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
    public void cancel(RequestID inRequestID) {
        //todo update this method to supply data flow ID along with the requestID: not sure what this means (tk)
        getDelegate().cancelRequest(inRequestID);
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws UnsupportedDataTypeException, StopDataFlowException {
        if(inData != null) {
            getDelegate().preProcessData(inFlowID, inData);
            if (inData instanceof Map) {
                mService.getEPRuntime().sendEvent((Map)inData, CEPDataTypes.MAP);
            } else if(inData instanceof Node) {
                mService.getEPRuntime().sendEvent((Node)inData);
            } else {
                mService.getEPRuntime().sendEvent(inData);
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
            configuration.addEventTypeAlias(CEPDataTypes.MARKET_DATA, SymbolExchangeEvent.class);
            configuration.addEventTypeAlias(CEPDataTypes.BID, BidEvent.class);
            configuration.addEventTypeAlias(CEPDataTypes.ASK, AskEvent.class);
            configuration.addEventTypeAlias(CEPDataTypes.TRADE, TradeEvent.class);
            configuration.addEventTypeAlias(CEPDataTypes.REPORT, ExecutionReport.class);
            configuration.addEventTypeAlias(CEPDataTypes.CANCEL_REJECT, OrderCancelReject.class);
            configuration.addEventTypeAlias(CEPDataTypes.ORDER_SINGLE, OrderSingle.class);
            configuration.addEventTypeAlias(CEPDataTypes.ORDER_CANCEL, OrderCancel.class);
            configuration.addEventTypeAlias(CEPDataTypes.ORDER_REPLACE, OrderReplace.class);
            configuration.addEventTypeAlias(CEPDataTypes.FIX_ORDER, FIXOrder.class);
            configuration.addEventTypeAlias(CEPDataTypes.SUGGEST, Suggestion.class);
            configuration.addEventTypeAlias(CEPDataTypes.NOTIFICATION, Notification.class);   // todo: is this correct class?
            configuration.addEventTypeAlias(CEPDataTypes.MAP, Map.class);
            configuration.addEventTypeAlias(CEPDataTypes.TIME_CARRIER, TimestampCarrier.class);
            configuration.addEventTypeAlias(CEPDataTypes.TIME, TimeEvent.class);

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
        for(String query: inQuery) {
            if(query.startsWith(PATTERN_QUERY_PREFIX)) {
                stmts.add(mService.getEPAdministrator().
                        createPattern(query.substring(
                                PATTERN_QUERY_PREFIX.length())));
            } else {
                stmts.add(mService.getEPAdministrator().createEPL(query));
            }
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
    private static final String PATTERN_QUERY_PREFIX = "p:";

    private final Map<DataFlowID, Pair<DataEmitterSupport, String[]>> mUnprocessedRequests =
            new Hashtable<DataFlowID, Pair<DataEmitterSupport, String[]>>();
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
        void cancelRequest(RequestID inRequestID);

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
            } catch (EPException ex) {
                throw new RequestDataException(ex);
            }
            statements.get(statements.size() - 1).setSubscriber(new Subscriber(inSupport));
            mRequests.put(inSupport.getRequestID(), statements);
        }

        /** Go through and destroy all the existing EPL statements */
        public void cancelRequest(RequestID inRequestID) {
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
            //todo account for multiple requests per data flow?
            mUnprocessedRequests.put(inSupport.getFlowID(),
                    new Pair<DataEmitterSupport, String[]>(inSupport, inStmts));
        }

        @Override
        public void cancelRequest(RequestID inRequestID) {
            super.cancelRequest(inRequestID);
            //todo account for unprocessed statement being cancelled.
        }

        /** Seed the Esper engine with the incoming time event, then
         * delegate to the regular {@link #processRequest(String[], DataEmitterSupport)} implementation.
         * If the incoming events aren't TimestampCarriers, then just discard them
         */
        public void preProcessData(DataFlowID inFlowID, Object inData) throws StopDataFlowException {
            if(inData instanceof TimestampCarrier) {
                //send the time event
                mService.getEPRuntime().sendEvent(new TimeEvent(
                        ((TimestampCarrier)inData).getTimeMillis()));
                //send the event directly to downstream module? todo?
                //if we have unprocessed statements process them now
                Pair<DataEmitterSupport, String[]> req = mUnprocessedRequests.remove(inFlowID);
                if(req != null) {
                    try {
                        super.processRequest(req.getSecondMember(), req.getFirstMember());
                    } catch (RequestDataException e) {
                        throw new StopDataFlowException(e,
                                new I18NBoundMessage1P(Messages.ERROR_CREATING_STATEMENTS, Arrays.toString(req.getSecondMember())));
                    }
                }
            }
        }
    }

    /**
     * A Subscriber class that subscribes to the query statement results
     * and emits them out to the flow that requested that statement.
     */
    private static class Subscriber {
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
