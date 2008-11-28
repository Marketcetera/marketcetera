package org.marketcetera.modules.cep.system;

import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.*;
import org.marketcetera.module.*;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple straight-through implementation of the CEP module that
 * filters received data and only emits data that match the type specified in the query.
 * Only allows for "select * from xyz" type of queries.
 * The XYZ types can be any alias or class name listed in @{@link CEPDataTypes}.
 *
 *
 * The maps in the data structure are as follows:
 * <ul>
 * <li>{@link #typeLookupMap} is a mapping of all expected data objects to their aliases, ie TradeEvent --> trade
 * for the puproses of doing the 'select * from <em>alias</em>' query</li>
 * <li>{@link #mTypeToEmitterMap} contains a list of emitters for each alias that we have listeners registered for</li>
 * <li>{@link #mRequestMap} - map of {@link RequestID} --> pair of {typeAlias, {@link DataEmitterSupport}}. Given a requestID,
 * we can get the type alias and corresponding emitter registered to listen on that type. For cancels,
 * we pull out all the alias, and remove the emitter subscribed to listen on that event type</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @since $Release$
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CEPSystemProcessor extends Module
        implements DataReceiver, DataEmitter {

    private static final String QUERY_DELIM = "[ \t]+";
    private static final String QUERY_PREFIX = "select * from ";        //$NON-NLS-1$
    private static final String[] QUERY_SPLIT = QUERY_PREFIX.split(QUERY_DELIM);

    private final Map<String, DataEmitterSupport> mTypeToEmitterMap;
    private final HashMap<RequestID, Pair<String, DataEmitterSupport>> mRequestMap;

    private Map<String, String> typeLookupMap = new HashMap<String, String>(20);

    protected CEPSystemProcessor(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        mTypeToEmitterMap = new HashMap<String, DataEmitterSupport>();
        mRequestMap = new HashMap<RequestID, Pair<String, DataEmitterSupport>>();
    }

    @Override
    protected void preStart() throws ModuleException {
        // marketData is all Bid, Ask, BidAsk, Trade
        typeLookupMap.put(SymbolExchangeEvent.class.getName(), CEPDataTypes.MARKET_DATA);
        typeLookupMap.put(TradeEvent.class.getName(), CEPDataTypes.MARKET_DATA);
        typeLookupMap.put(BidEvent.class.getName(), CEPDataTypes.MARKET_DATA);
        typeLookupMap.put(AskEvent.class.getName(), CEPDataTypes.MARKET_DATA);
        typeLookupMap.put(BidAskEvent.class.getName(), CEPDataTypes.MARKET_DATA);

        // add all the rest
        typeLookupMap.put(BidEvent.class.getName(), CEPDataTypes.BID);
        typeLookupMap.put(AskEvent.class.getName(), CEPDataTypes.ASK);
        typeLookupMap.put(TradeEvent.class.getName(), CEPDataTypes.TRADE);
        typeLookupMap.put(ExecutionReport.class.getName(), CEPDataTypes.REPORT);
        typeLookupMap.put(OrderCancelReject.class.getName(), CEPDataTypes.CANCEL_REJECT);
        typeLookupMap.put(FIXOrder.class.getName(), CEPDataTypes.FIX_ORDER);
        typeLookupMap.put(OrderCancel.class.getName(), CEPDataTypes.ORDER_CANCEL);
        typeLookupMap.put(OrderReplace.class.getName(), CEPDataTypes.ORDER_REPLACE);
        typeLookupMap.put(OrderSingle.class.getName(), CEPDataTypes.ORDER_SINGLE);
        typeLookupMap.put(Suggestion.class.getName(), CEPDataTypes.SUGGEST);
        typeLookupMap.put(OrderSingleSuggestion.class.getName(), CEPDataTypes.SUGGEST);
        typeLookupMap.put(Notification.class.getName(), CEPDataTypes.NOTIFICATION);

        // subclasses of Map will be added on the fly
        typeLookupMap.put(Map.class.getName(), CEPDataTypes.MAP);

        // add self-referencing aliases
        typeLookupMap.put(CEPDataTypes.MARKET_DATA, CEPDataTypes.MARKET_DATA);
        typeLookupMap.put(CEPDataTypes.BID, CEPDataTypes.BID);
        typeLookupMap.put(CEPDataTypes.ASK, CEPDataTypes.ASK);
        typeLookupMap.put(CEPDataTypes.TRADE, CEPDataTypes.TRADE);
        typeLookupMap.put(CEPDataTypes.REPORT, CEPDataTypes.REPORT);
        typeLookupMap.put(CEPDataTypes.CANCEL_REJECT, CEPDataTypes.CANCEL_REJECT);
        typeLookupMap.put(CEPDataTypes.ORDER_SINGLE, CEPDataTypes.ORDER_SINGLE);
        typeLookupMap.put(CEPDataTypes.ORDER_REPLACE, CEPDataTypes.ORDER_REPLACE);
        typeLookupMap.put(CEPDataTypes.ORDER_CANCEL, CEPDataTypes.ORDER_CANCEL);
        typeLookupMap.put(CEPDataTypes.FIX_ORDER, CEPDataTypes.FIX_ORDER);
        typeLookupMap.put(CEPDataTypes.SUGGEST, CEPDataTypes.SUGGEST);
        typeLookupMap.put(CEPDataTypes.NOTIFICATION, CEPDataTypes.NOTIFICATION);
        typeLookupMap.put(CEPDataTypes.MAP, CEPDataTypes.MAP);
    }

    @Override
    protected void preStop() throws ModuleException {
        mTypeToEmitterMap.clear();
        mRequestMap.clear();
    }

    /** Map the incoming data to some type, find the list of all {@link DataEmitterSupport} objects
     * and send the data on its way there
     * Ignore the flowID
     */
    @Override
    public void receiveData(DataFlowID inFlowID, Object inData) throws ReceiveDataException {
        if(inData != null) {

            // if it's an xxxImpl, remove the Impl from type
            String type = inData.getClass().getName();
            if (type.endsWith("Impl")) {
                type = type.replace("Impl", "");
            }
            String alias = typeLookupMap.get(type);
            DataEmitterSupport emitter = mTypeToEmitterMap.get(alias);
            // special case for Maps, there are too many of them to add up front
            if(emitter ==null && inData instanceof Map) {
                typeLookupMap.put(inData.getClass().getName(), CEPDataTypes.MAP);
                emitter = mTypeToEmitterMap.get(CEPDataTypes.MAP);
            }
            // subscribers to {@link CEPDataTypes.MARKET_DATA} should get all the market-data events too
            if(inData instanceof SymbolExchangeEvent) {
                DataEmitterSupport mdataEmitter = mTypeToEmitterMap.get(CEPDataTypes.MARKET_DATA);
                // no explicit subscription for that type of event, so just use mdata listeners
                if (emitter == null) {
                    emitter = mdataEmitter;
                }
            }

            if (emitter != null) {
                emitter.send(inData);
            }
        }
        //ignore null data
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport) throws RequestDataException {
        if(inRequest == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        String query;
        if(obj instanceof String) {
            query = (String)obj;
        } else {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }

        String[] querySplit = query.split(QUERY_DELIM);
        if (querySplit.length != 4 || !QUERY_SPLIT[0].equals(querySplit[0])
                ||!QUERY_SPLIT[1].equals(querySplit[1]) ||!QUERY_SPLIT[2].equals(querySplit[2])) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.INVALID_QUERY, query));
        }

        String type = query.substring(QUERY_PREFIX.length());
        String alias = typeLookupMap.get(type);
        if (alias == null) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.UNSUPPORTED_TYPE, type));
        }
        Pair<String, DataEmitterSupport> request = new Pair<String, DataEmitterSupport>(type, inSupport);
        mTypeToEmitterMap.put(alias, inSupport);
        mRequestMap.put(inSupport.getRequestID(), request);
    }

    /** Find the request, and go through all its types and remove all the {@link DataEmitterSupport}
     * object associated with it */
    @Override
    public void cancel(RequestID inRequestID) {
        Pair<String, DataEmitterSupport>request = mRequestMap.remove(inRequestID);
        if(request == null) return;

        SLF4JLoggerProxy.debug(this, "Cancelling for request {}", inRequestID);
        mTypeToEmitterMap.remove(request.getFirstMember());
    }
}
