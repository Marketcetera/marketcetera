package org.marketcetera.modules.cep.system;

import org.marketcetera.core.Pair;
import org.marketcetera.core.notifications.Notification;
import org.marketcetera.event.*;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.module.*;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Simple straight-through implementation of the CEP module that
 * only allows for "select * from xyz" type of queries.
 * The maps in the data structure are as follows:
 * <ul>
 * <li>{@link #typeLookupMap} is a mapping of all expected data objects to their aliases, ie TradeEvent --> trade
 * for the puproses of doing the 'select * from <em>alias</em>' query</li>
 * <li>{@link #typeToEmitterMap} contains a list of emitters for each alias that we have listeners registered for</li>
 * <li>{@link #requestMap} - map of {@link RequestID} --> list of pairs of {typeAlias, {@link DataEmitterSupport}}. Given a requestID,
 * we can get a list of all the type aliases and corresponding emitters registered to listen on that type. For cancels,
 * we pull out all the aliases, and remove the emitters subscribed to listen on that event type</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @since $Release$
 * @version $Id$
 */
public class CEPSystemProcessor extends Module
        implements DataReceiver, DataEmitter, CEPSystemProcessorMXBean {

    private static final String QUERY_PREFIX = "select * from ";

    private Map<String, List<DataEmitterSupport>> typeToEmitterMap;
    private Map<RequestID, List<Pair<String, DataEmitterSupport>>> requestMap;
    private int receivedCounter = 0;
    private int emittedCounter = 0;

    private Map<String, String> typeLookupMap = new HashMap<String, String>(20);

    protected CEPSystemProcessor(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        typeToEmitterMap = new HashMap<String, List<DataEmitterSupport>>();
        requestMap = new HashMap<RequestID, List<Pair<String, DataEmitterSupport>>>();
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
        // do we need to clear teh maps? or will it just get garbage-collected?
        for(List<DataEmitterSupport> list : typeToEmitterMap.values()) {
            list.clear();
        }
        typeToEmitterMap.clear();
        for(List<Pair<String, DataEmitterSupport>> list: requestMap.values()) {
            list.clear();
        }
        requestMap.clear();
        receivedCounter = 0;
        emittedCounter = 0;
    }

    /** Map the incoming data to some type, find the list of all {@link DataEmitterSupport} objects
     * and send the data on its way there
     * Ignore the flowID
     */
    @Override
    public void receiveData(DataFlowID inFlowID, Object inData) throws ReceiveDataException {
        if(inData != null) {
            receivedCounter++;

            // if it's an xxxImpl, remove the Impl from type
            String type = inData.getClass().getName();
            if (type.endsWith("Impl")) {
                type = type.replace("Impl", "");
            }
            String alias = typeLookupMap.get(type);
            List<DataEmitterSupport> emitterList = typeToEmitterMap.get(alias);
            // special case for Maps, there are too many of them to add up front
            if(emitterList ==null && inData instanceof Map) {
                typeLookupMap.put(inData.getClass().getName(), CEPDataTypes.MAP);
                emitterList = typeToEmitterMap.get(CEPDataTypes.MAP);
            }
            // subscribers to {@link CEPDataTypes.MARKET_DATA} should get all the market-data events too
            if(inData instanceof SymbolExchangeEvent) {
                List<DataEmitterSupport> mdataList = typeToEmitterMap.get(CEPDataTypes.MARKET_DATA);
                // no explicit subscription for that type of event, so just use mdata listeners
                if (emitterList == null) {
                    emitterList = mdataList;
                } else if(mdataList != null){
                    emitterList.addAll(mdataList);
                }
            }

            if (emitterList != null) {
                for(DataEmitterSupport des : emitterList) {
                    emittedCounter++;
                    des.send(inData);
                }
            }
        }
        //ignore null data
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport) throws RequestDataException {
        Object obj = inRequest.getData();
        if(obj == null) {
            throw new IllegalRequestParameterValue(getURN(), null);
        }
        String query;
        if(obj instanceof String) {
            query = (String)obj;
        } else if (obj instanceof String[]) {
            throw new IllegalRequestParameterValue(getURN(), obj);
        } else {
            throw new UnsupportedRequestParameterType(getURN(), obj);
        }

        LinkedList<Pair<String, DataEmitterSupport>> requestList = new LinkedList<Pair<String, DataEmitterSupport>>();
        if (!query.startsWith(QUERY_PREFIX)) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.INVALID_QUERY, query));
        }

        String type = query.substring(QUERY_PREFIX.length());
        // remove ; if it's present
        type = (type.endsWith(";")) ? type.substring(0, type.length() - 1) : type;
        String alias = typeLookupMap.get(type);
        if (alias == null) {
            throw new RequestDataException(new I18NBoundMessage1P(Messages.UNSUPPORTED_TYPE, type));
        }
        List<DataEmitterSupport> list = typeToEmitterMap.get(alias);
        if (list == null) {
            list = new LinkedList<DataEmitterSupport>();
            typeToEmitterMap.put(alias, list);
        }
        list.add(inSupport);
        requestList.add(new Pair<String, DataEmitterSupport>(type, inSupport));

        requestMap.put(inSupport.getRequestID(), requestList);
    }

    /** Find the request, and go through all its types and remove all the {@link DataEmitterSupport}
     * object associated with it */
    @Override
    public void cancel(RequestID inRequestID) {
        List<Pair<String, DataEmitterSupport>> list = requestMap.get(inRequestID);
        if(list == null) return;

        SLF4JLoggerProxy.debug(this, "Cancelling for request {}", inRequestID);
        for (Pair<java.lang.String, DataEmitterSupport> pair : list) {
            List<DataEmitterSupport> emitterList = typeToEmitterMap.get(pair.getFirstMember());
            if(emitterList!=null) {
                boolean removed = emitterList.remove(pair.getSecondMember());
                SLF4JLoggerProxy.debug(this, "Removed type {} for request {}: {}", pair.getFirstMember(), inRequestID, removed);
            } else {
                SLF4JLoggerProxy.debug(this, "Type {} for request {} does not have any emitters subscribed", pair.getFirstMember(), inRequestID);
            }
        }
    }

    @Override
    public long getNumEventsEmitted() {
        return emittedCounter;
    }

    @Override
    public long getNumEventsReceived() {
        return receivedCounter;
    }
}
