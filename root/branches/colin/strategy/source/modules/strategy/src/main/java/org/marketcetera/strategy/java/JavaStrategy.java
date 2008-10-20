package org.marketcetera.strategy.java;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.CancelEvent;
import org.marketcetera.event.ExecutionReport;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.AbstractStrategy;

/* $License$ */

/**
 * 
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
@ClassVersion("$Id: $") //$NON-NLS-1$
abstract class JavaStrategy
        extends AbstractStrategy
{
    /**
     * Create a new Strategy instance.
     * 
     */
    public JavaStrategy()
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onCallback()
     */
    @Override
    public void onCallback(Object inData)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onExecutionReport(org.marketcetera.event.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.IStrategy#onCancel(org.marketcetera.event.CancelEvent)
     */
    @Override
    public void onCancel(CancelEvent inCancel)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onNews(org.marketcetera.core.MSymbol,
     *      java.lang.String)
     */
    @Override
    public void onNews(MSymbol inSecurity,
                       String inText)
    {
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.marketcetera.strategy.IStrategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
    }
}
