package org.marketcetera.core.event;

import java.math.BigDecimal;

import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Represents a dividend for an equity at a particular time.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DividendEvent.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DividendEvent.java 16063 2012-01-31 18:21:55Z colin $")
public interface DividendEvent
        extends Event, HasEquity, HasEventType
{
    /**
     * Gets the amount of the dividend.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getAmount();
    /**
     * Gets the currency in which the dividend was or will be issued.
     *
     * @return a <code>String</code> value
     */
    public String getCurrency();
    /**
     * Gets the declare date of the dividend, if available.
     * 
     * <p>The format of the date is dependent on the market data provider.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getDeclareDate();
    /**
     * Gets the execution date of the dividend.
     *
     * <p>The format of the date is dependent on the market data provider.
     *
     * @return a <code>String</code> value
     */
    public String getExecutionDate();
    /**
     * Gets the frequency of the dividend. 
     *
     * @return a <code>DividendFrequency</code> value
     */
    public DividendFrequency getFrequency();
    /**
     * Gets the Equity of the dividend.
     * 
     * @return a <code>Equity</code> value
     */
    public Equity getEquity();
    /**
     * Gets the payment date of the dividend, if available.
     *
     * <p>The format of the date is dependent on the market data provider.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getPaymentDate();
    /**
     * Gets the record date of the dividend, if available.
     *
     * <p>The format of the date is dependent on the market data provider.
     *
     * @return a <code>String</code> value or <code>null</code>
     */
    public String getRecordDate();
    /**
     * Gets the status of the dividend.
     *
     * @return a <code>DividendStatus</code> value
     */
    public DividendStatus getStatus();
    /**
     * Gets the type of the dividend.
     *
     * @return a <code>DividendType</code> value
     */
    public DividendType getType();
    /**
     * Gets the type of the event.
     *
     * @return an <code>EventType</code> value
     */
    public EventType getEventType();
    /**
     * Sets the type of the event.
     *
     * @param inEventType an <code>EventType</code> value
     */
    public void setEventType(EventType inEventType);
}
