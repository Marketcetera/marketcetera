package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.springframework.context.Lifecycle;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public interface IStrategy
    extends Lifecycle
{
    /**
     * Indicates a <code>TradeEvent</code> has been received.
     * 
     * @param inTrade a <code>TradeEvent</code> value
     */
    public void onTrade(TradeEvent inTrade);
    /**
     * Indicates a <code>BidEvent</code> has been received.
     * 
     * @param inBid a <code>BidEvent</code> value
     */
    public void onBid(BidEvent inBid);
    /**
     * Indicates an <code>AskEvent</code> has been received.
     * 
     * @param inAsk an <code>AskEvent</code> value
     */
    public void onAsk(AskEvent inAsk);
    /**
     * Indicates a <code>CancelEvent</code> has been received.
     * 
     * @param inCancelEvent a <code>CancelEvent</code> value
     */
    public void onCancel(CancelEvent inCancel);
    /**
     * Indicates an <code>ExecutionReport</code> has been received.
     * 
     * @param inExecutionReport an <code>ExecutionReport</code> value
     */
    public void onExecutionReport(ExecutionReport inExecutionReport);
    /**
     * Indicates news has been received for the given security.
     * 
     * @param inSecurity a <code>MSymbol</code> value
     * @param inText a <code>String</code> value
     */
    public void onNews(MSymbol inSecurity,
                       String inText);
    /**
     * Indicates an event has occurred that does not fit any of the other callbacks.
     *
     * @param inEvent an <code>Object</code> value
     */
    public void onOther(Object inEvent);
    /**
     * Indicates a scheduled callback has been executed.
     * @param inData TODO
     * 
     * @see {@link #requestCallback(long)}
     */
    public void onCallback(Object inData);
    public int getStrategyID();
}
