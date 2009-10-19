package org.marketcetera.event;

import java.math.BigDecimal;

import org.marketcetera.trade.Equity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface DividendEvent
        extends Event, EquityEvent, HasEquity
{
    /**
     * 
     *
     *
     * @return
     */
    public BigDecimal getAmount();
    /**
     * 
     *
     *
     * @return
     */
    public String getCurrency();
    /**
     * 
     *
     *
     * @return
     */
    public String getDeclareDate();
    /**
     * 
     *
     *
     * @return
     */
    public String getExecutionDate();
    /**
     * 
     *
     *
     * @return
     */
    public DividendFrequency getFrequency();
    /**
     * 
     */
    public Equity getEquity();
    /**
     * 
     *
     *
     * @return
     */
    public String getPaymentDate();
    /**
     * 
     *
     *
     * @return
     */
    public String getRecordDate();
    /**
     * 
     *
     *
     * @return
     */
    public DividendStatus getStatus();
    /**
     * 
     *
     *
     * @return
     */
    public DividendType getType();
}
