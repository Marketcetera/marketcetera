package org.marketcetera.core.symbolresolver.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.attributes.ClassVersion;
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
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SymbolResolverManagerImpl.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@ThreadSafe
@ClassVersion("$Id: SymbolResolverManagerImpl.java 82347 2012-05-03 19:30:54Z colin $")
public class SymbolResolverManagerImpl
        implements SymbolResolverManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolverManager#getSymbolResolvers()
     */
    @Override
    public List<SymbolResolver> getSymbolResolvers()
    {
        synchronized(resolvers) {
            return Collections.unmodifiableList(resolvers);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.symbolresolver.SymbolResolverManager#setSymbolResolvers(java.util.List)
     */
    @Override
    public void setSymbolResolvers(List<SymbolResolver> inResolvers)
    {
        synchronized(resolvers) {
            resolvers.clear();
            resolvers.addAll(inResolvers);
        }
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
        SLF4JLoggerProxy.debug(SymbolResolverManagerImpl.class,
                               "{} resolved to {}",
                               inSymbol,
                               instrument);
        return instrument;
    }
    /**
     * resolves to apply
     */
    private final List<SymbolResolver> resolvers = new ArrayList<SymbolResolver>();
}
