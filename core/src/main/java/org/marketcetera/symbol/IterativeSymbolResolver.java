package org.marketcetera.symbol;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.lang.Validate;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Resolves symbols by managing a list of individual symbol resolvers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class IterativeSymbolResolver
        implements SymbolResolverService, InitializingBean
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        cachedSymbols = new LRUMap<>(cacheSize);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.symbol.SymbolResolverServices#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        Instrument instrument = cachedSymbols.get(inSymbol);
        if(instrument != null) {
            return instrument;
        }
        for(SymbolResolver resolver : symbolResolvers) {
            try {
                instrument = resolver.resolveSymbol(inSymbol);
                if(instrument != null) {
                    cachedSymbols.put(inSymbol,
                                      instrument);
                    return instrument;
                }
            } catch (Exception e) {
                Messages.SYMBOL_RESOLVER_ERROR.warn(this,
                                                    e);
            }
        }
        return null;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(symbolResolvers,
                         Messages.MISSING_SYMBOL_RESOLVERS.getText());
        Validate.notEmpty(symbolResolvers,
                          Messages.MISSING_SYMBOL_RESOLVERS.getText());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbol.SymbolResolverService#generateSymbol(org.marketcetera.trade.Instrument)
     */
    @Override
    public String generateSymbol(Instrument inInstrument)
    {
        for(SymbolResolver resolver : symbolResolvers) {
            try {
                String symbol = resolver.generateSymbol(inInstrument);
                if(symbol != null) {
                    return symbol;
                }
            } catch (Exception e) {
                Messages.SYMBOL_RESOLVER_ERROR.warn(this,
                                                    e);
            }
        }
        return null;
    }
    /**
     * Get the symbolResolvers value.
     *
     * @return a <code>List&lt;SymbolResolver&gt;</code> value
     */
    public List<SymbolResolver> getSymbolResolvers()
    {
        return symbolResolvers;
    }
    /**
     * Sets the symbolResolvers value.
     *
     * @param inSymbolResolvers a <code>List&lt;SymbolResolver&gt;</code> value
     */
    public void setSymbolResolvers(List<SymbolResolver> inSymbolResolvers)
    {
        symbolResolvers = inSymbolResolvers;
    }
    /**
     * Get the cacheSize value.
     *
     * @return an <code>int</code> value
     */
    public int getCacheSize()
    {
        return cacheSize;
    }
    /**
     * Sets the cacheSize value.
     *
     * @param an <code>int</code> value
     */
    public void setCacheSize(int inCacheSize)
    {
        cacheSize = inCacheSize;
    }
    /**
     * list of symbol resolvers
     */
    private List<SymbolResolver> symbolResolvers = Lists.newArrayList();
    /**
     * number of symbols to cache
     */
    private int cacheSize = 1000;
    /**
     * cache for symbols
     */
    private Map<String,Instrument> cachedSymbols;
}
