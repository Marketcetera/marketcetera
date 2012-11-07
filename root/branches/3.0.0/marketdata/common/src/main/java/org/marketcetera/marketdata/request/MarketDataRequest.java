package org.marketcetera.marketdata.request;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.marketdata.Content;

/* $License$ */

/**
 * Represents a request for market data.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
public interface MarketDataRequest
{
    /**
     * Get the instruments to which this market data request applies.
     * 
     * <p>The market data request will contain either instruments or underlying
     * instruments, but not both.
     *
     * @return a <code>Set&lt;Instrument&gt;</code> value
     */
    public Set<Instrument> getInstruments();
    public Set<Instrument> getUnderlyingInstruments();
    public Set<Content> getContent();
    public String getProvider();
    public String getExchange();
    public Map<String,String> getParameters();
}
