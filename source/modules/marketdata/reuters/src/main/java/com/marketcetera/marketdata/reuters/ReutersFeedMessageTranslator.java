package com.marketcetera.marketdata.reuters;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;
import org.marketcetera.symbolresolver.SymbolResolverManager;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Translates market data requests to Reuters-specific artifacts.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ReutersFeedMessageTranslator.java 82351 2012-05-04 21:46:58Z colin $
 * @since $Release$
 */
@Immutable
@ClassVersion("$Id: ReutersFeedMessageTranslator.java 82351 2012-05-04 21:46:58Z colin $")
public class ReutersFeedMessageTranslator
        implements DataRequestTranslator<List<ReutersRequest>>
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#fromDataRequest(org.marketcetera.marketdata.MarketDataRequest)
     */
    @Override
    public List<ReutersRequest> fromDataRequest(MarketDataRequest inRequest)
            throws CoreException
    {
        List<ReutersRequest> requests = new ArrayList<ReutersRequest>();
        for(String symbol : inRequest.getSymbols()) {
            Instrument instrument = getInstrumentFrom(symbol);
            for(Content content : inRequest.getContent()) {
                requests.add(new ReutersRequest(instrument,
                                                content));
            }
        }
        return requests;
    }
    /**
     * 
     *
     *
     * @param inSymbol
     * @return
     */
    private Instrument getInstrumentFrom(String inSymbol)
    {
        if(symbolResolverManager == null) {
            return new Equity(inSymbol);
        }
        return symbolResolverManager.resolve(inSymbol,
                                             ReutersFeedModuleFactory.IDENTIFIER);
    }
    /**
     * 
     */
    @Autowired
    private volatile SymbolResolverManager symbolResolverManager;
}
