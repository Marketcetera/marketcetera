package org.marketcetera.marketdata.webservices;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.request.MarketDataRequest;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="marketDataRequest")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesMarketDataRequest
        implements MarketDataRequest
{
    public WebServicesMarketDataRequest(MarketDataRequest inRequest)
    {
        symbols.addAll(inRequest.getSymbols());
        underlyingSymbols.addAll(inRequest.getUnderlyingSymbols());
        content.addAll(inRequest.getContent());
        provider = inRequest.getProvider();
        exchange = inRequest.getExchange();
        parameters.putAll(inRequest.getParameters());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getSymbols()
     */
    @Override
    public Set<String> getSymbols()
    {
        return symbols;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getUnderlyingSymbols()
     */
    @Override
    public Set<String> getUnderlyingSymbols()
    {
        return underlyingSymbols;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getContent()
     */
    @Override
    public Set<Content> getContent()
    {
        return content;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getProvider()
     */
    @Override
    public String getProvider()
    {
        return provider;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getExchange()
     */
    @Override
    public String getExchange()
    {
        return exchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequest#getParameters()
     */
    @Override
    public Map<String,String> getParameters()
    {
        return parameters;
    }
    /**
     * Sets the provider value.
     *
     * @param inProvider a <code>String</code> value
     */
    public void setProvider(String inProvider)
    {
        provider = inProvider;
    }
    /**
     * Sets the exchange value.
     *
     * @param inExchange a <code>String</code> value
     */
    public void setExchange(String inExchange)
    {
        exchange = inExchange;
    }
    @SuppressWarnings("unused")
    private WebServicesMarketDataRequest() {}
    @XmlElementWrapper(name="symbols")
    private final Set<String> symbols = new LinkedHashSet<String>();
    @XmlElementWrapper(name="underlyingSymbols")
    private final Set<String> underlyingSymbols = new LinkedHashSet<String>();
    @XmlElementWrapper(name="contentTypes")
    private final Set<Content> content = new LinkedHashSet<Content>();
    @XmlAttribute
    private String provider;
    @XmlAttribute
    private String exchange;
    @XmlElementWrapper(name="parameters")
    private final Map<String,String> parameters = new LinkedHashMap<String,String>();
}
