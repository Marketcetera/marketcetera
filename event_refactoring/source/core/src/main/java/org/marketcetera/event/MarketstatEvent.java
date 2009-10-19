package org.marketcetera.event;

import java.math.BigDecimal;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketstatEvent
        extends Event, HasInstrument
{
    public BigDecimal getOpen();
    public BigDecimal getHigh();
    public BigDecimal getLow();
    public BigDecimal getClose();
    public BigDecimal getPreviousClose();
    public BigDecimal getVolume();
    public String getCloseDate();
    public String getPreviousCloseDate();
    public String getTradeHighTime();
    public String getTradeLowTime();
    public String getOpenExchange();
    public String getHighExchange();
    public String getLowExchange();
    public String getCloseExchange();
}
