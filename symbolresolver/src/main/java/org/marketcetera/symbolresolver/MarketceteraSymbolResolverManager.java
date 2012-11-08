package org.marketcetera.symbolresolver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.symbolresolver.Messages;
import org.marketcetera.core.symbolresolver.NoInstrumentForSymbol;
import org.marketcetera.core.symbolresolver.SymbolResolver;
import org.marketcetera.core.symbolresolver.SymbolResolverManager;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.util.log.I18NBoundMessage1P;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides symbol resolution services.
 *
 * @version $Id: SymbolResolverManagerImpl.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@ThreadSafe
public class MarketceteraSymbolResolverManager
        implements SymbolResolverManager
{
    /**
     * Sets the resolvers value.
     *
     * @param inResolvers a <code>List&lt;SymbolResolver&gt;</code> value
     */
    public void setResolvers(List<SymbolResolver> inResolvers)
    {
        resolvers = inResolvers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolverManager#resolve(java.lang.String)
     */
    @Override
    public Instrument resolve(String inSymbol)
    {
        return resolve(inSymbol,
                       null);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolverManager#resolve(java.lang.String, java.lang.Object)
     */
    @Override
    public Instrument resolve(String inSymbol,
                              Object inContext)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(inSymbol);
        Instrument instrument = null;
        synchronized(resolvers) {
            for(SymbolResolver resolver : resolvers) {
                instrument = resolver.resolve(inSymbol,
                                              inContext);
                if(instrument != null) {
                    break;
                }
            }
        }
        if(instrument == null) {
            throw new NoInstrumentForSymbol(new I18NBoundMessage1P(Messages.UNABLE_TO_RESOLVE_SYMBOL,
                                                                   inSymbol));
        }
        SLF4JLoggerProxy.debug(MarketceteraSymbolResolverManager.class,
                               "{} resolved to {}",
                               inSymbol,
                               instrument);
        return instrument;
    }
    /**
     * resolves to apply
     */
    private List<SymbolResolver> resolvers = new ArrayList<SymbolResolver>();
}
