package org.marketcetera.marketdata.request;

import java.util.Map;
import java.util.Set;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequest
{
    public Set<Instrument> getInstruments();
    public Set<Instrument> getUnderlyingInstruments();
    public Set<Content> getContent();
    public String getProvider();
    public String getExchange();
    public Map<String,String> getParameters();
}
