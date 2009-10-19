package org.marketcetera.event;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface TopOfBookEvent
    extends AggregateEvent
{
    /**
     * 
     *
     *
     * @return
     */
    public BidEvent getBid();
    /**
     * 
     *
     *
     * @return
     */
    public AskEvent getAsk();
}
