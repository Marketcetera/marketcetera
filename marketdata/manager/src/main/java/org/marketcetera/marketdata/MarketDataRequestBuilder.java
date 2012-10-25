package org.marketcetera.marketdata;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.marketcetera.api.systemmodel.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MarketDataRequestBuilder
{
    public MarketDataRequestBuilder withInstruments(Instrument...inInstruments);
    public MarketDataRequestBuilder withInstruments(Set<Instrument> inInstruments);
    public MarketDataRequestBuilder withSymbols(String...inSymbols);
    public MarketDataRequestBuilder withSymbols(Set<String> inSymbols);
    public MarketDataRequestBuilder withUnderlyingInstruments(Instrument...inInstruments);
    public MarketDataRequestBuilder withUnderlyingInstruments(Set<Instrument> inInstruments);
    public MarketDataRequestBuilder withUnderlyingSymbols(String...inSymbols);
    public MarketDataRequestBuilder withUnderlyingSymbols(Set<String> inSymbols);
    public MarketDataRequestBuilder withContent(Content...inContent);
    public MarketDataRequestBuilder withContent(Set<Content> inContent);
    public MarketDataRequestBuilder withContent(String...inContent);
    public MarketDataRequestBuilder withProvider(String inProvider);
    public MarketDataRequestBuilder withExchange(String inExchange);
    public MarketDataRequestBuilder withParameters(String inParameterList);
    public MarketDataRequestBuilder withParameters(Map<String,String> inParametersList);
    public MarketDataRequestBuilder withParameters(Properties inParametersList);
    public MarketDataRequestBuilder withRequiredCapabilities(Set<Capability> inCapabilities);
    public MarketDataRequestBuilder withRequiredCapabilities(Capability...inCapabilities);
    public MarketDataRequestBuilder withRequiredCapabilities(String...inCapabilities);
}
