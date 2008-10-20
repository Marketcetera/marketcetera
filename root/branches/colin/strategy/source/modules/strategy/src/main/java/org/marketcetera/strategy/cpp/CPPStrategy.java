package org.marketcetera.strategy.cpp;

import java.util.Date;
import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.EventBase;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.AbstractStrategy;
import org.marketcetera.strategy.TradeSuggestion;
import org.marketcetera.systemmodel.Order;
import org.marketcetera.systemmodel.Position;

import quickfix.Message;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public class CPPStrategy
        extends AbstractStrategy
{
    /**
     * Create a new CPPStrategy instance.
     *
     */
    public CPPStrategy()
    {
    }
    protected final native void CancelAllOrders();
    protected final native void CancelOrder(String inOrderHandle);
    protected final native void CancelReplaceOrder(String inOrderHandle,
                                                   Order inOrder);
    protected final native Position GetCurrentPosition(String inSecurity);
    protected final native Position GetCurrentPositionAtOpen(String inSecurity);
    protected final native Date GetCurrentTime();
    protected final native List<ExecutionReport> GetExecutionReport(String inOrderHandle);
    protected final native void RequestCallback(long inDelay);
    protected final native void SendEvent(EventBase inEvent);
    protected final native void SendMessage(Message inMessage);
    protected final native String SendOrder(Order inOrder);
    protected final native void SubscribeToMarketData(MSymbol inSecurity);
    protected final native void SubscribeToNews(MSymbol inSecurity);
    protected final native void SuggestTrade(TradeSuggestion inSuggestion);
    protected native void OnAsk(AskEvent inAsk);
    protected native void OnBid(BidEvent inBid);
    protected native void OnCallback();
    protected native void OnCancel(CancelEvent inCancel);
    protected native void OnExecutionReport(ExecutionReport inExecutionReport);
    protected native void OnNews(MSymbol inSecurity,
                                 String inText);
    protected native void OnTrade(TradeEvent inTrade);
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public final void onAsk(AskEvent inAsk)
    {
        OnAsk(inAsk);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public final void onBid(BidEvent inBid)
    {
        OnBid(inBid);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCallback()
     */
    @Override
    public final void onCallback(Object inData)
    {
        OnCallback();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCancel(org.marketcetera.event.CancelEvent)
     */
    @Override
    public final void onCancel(CancelEvent inCancel)
    {
        OnCancel(inCancel);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public final void onExecutionReport(ExecutionReport inExecutionReport)
    {
        OnExecutionReport(inExecutionReport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onNews(org.marketcetera.core.MSymbol, java.lang.String)
     */
    @Override
    public final void onNews(MSymbol inSecurity,
                             String inText)
    {
        OnNews(inSecurity,
               inText);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public final void onTrade(TradeEvent inTrade)
    {
        OnTrade(inTrade);
    }
}
