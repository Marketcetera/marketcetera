package org.marketcetera.strategy;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.DividendEvent;
import org.marketcetera.event.MarketstatEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.OrderCancelReject;

/* $License$ */

/**
 * Tests a strategy's ability to create custom data flows.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.0.0
 */
public class DataFlow
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        dataFlowID = doDataFlow();
        if(dataFlowID != null) {
            setProperty("dataFlowID",
                        dataFlowID.getValue());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStop()
     */
    @Override
    public void onStop()
    {
        boolean shouldSkipCancel = Boolean.parseBoolean(getParameter("shouldSkipCancel"));
        if(dataFlowID != null &&
           !shouldSkipCancel) {
            cancelDataFlow(dataFlowID);
            setProperty("dataFlowStopped",
                        "true");
        }
        boolean shouldMakeNewRequest = Boolean.parseBoolean(getParameter("shouldMakeNewRequest"));
        if(shouldMakeNewRequest) {
            setProperty("newDataFlowAttempt",
                        "false");
            DataFlowID newDataFlowID = doDataFlow();
            setProperty("newDataFlowAttempt",
                        "true");
            if(newDataFlowID == null) {
                setProperty("newDataFlowID",
                            "null");
            } else {
                setProperty("newDataFlowID",
                            newDataFlowID.getValue());
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
        send(inEvent);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        send(inAsk);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
        send(inBid);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCallback(java.lang.Object)
     */
    @Override
    public void onCallback(Object inData)
    {
        boolean shouldCancelDataFlow = Boolean.parseBoolean(getParameter("shouldCancelDataFlow"));
        if(shouldCancelDataFlow) {
            if(inData instanceof DataFlowID) {
                DataFlowID localDataFlowID = (DataFlowID)inData;
                cancelDataFlow(localDataFlowID);
                setProperty("localDataFlowStopped",
                            "true");
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onCancelReject(org.marketcetera.trade.OrderCancelReject)
     */
    @Override
    public void onCancelReject(OrderCancelReject inCancel)
    {
        send(inCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onExecutionReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        send(inExecutionReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onMarketstat(org.marketcetera.event.MarketstatEvent)
     */
    @Override
    public void onMarketstat(MarketstatEvent inStatistics)
    {
        send(inStatistics);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
        send(inTrade);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onDividend(org.marketcetera.event.DividendEvent)
     */
    @Override
    public void onDividend(DividendEvent inDividend)
    {
        send(inDividend);
    }
    /**
     * Sets up the data flow as dictated by strategy parameters.
     *
     * @return a <code>DataFlowID</code> containing the data flow ID or <code>null</code> if the
     *   data flow could not be established
     */
    private DataFlowID doDataFlow()
    {
        String baseURNList = getParameter("urns");
        boolean routeToSink = Boolean.parseBoolean(getParameter("routeToSink"));
        List<DataRequest> requests = new ArrayList<DataRequest>();
        if(baseURNList != null) {
            String[] urns = baseURNList.split(",");
            for(String urn : urns) {
                requests.add(new DataRequest(new ModuleURN(urn)));
            }
            if(getParameter("useStrategyURN") != null) {
                if(routeToSink) {
                    requests.add(new DataRequest(getURN(),
                                                 OutputType.ALL));
                } else {
                    requests.add(new DataRequest(getURN()));
                }
            }
        }
        return createDataFlow(routeToSink,
                              baseURNList == null ? null : requests.toArray(new DataRequest[0]));
    }
    /**
     * data flow ID of the data flow created when the strategy starts
     */
    private DataFlowID dataFlowID = null;
}
