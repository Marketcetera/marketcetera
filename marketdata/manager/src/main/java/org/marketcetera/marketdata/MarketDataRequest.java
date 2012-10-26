package org.marketcetera.marketdata;

import java.util.Map;
import java.util.Set;

import org.marketcetera.core.trade.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: MarketDataRequest.java 16323 2012-10-25 17:35:43Z colin $
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
    public Set<Capability> getRequiredCapabilities();
}
