package org.marketcetera.core.symbolresolver;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Provides configurable symbol resolution.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class MockSymbolResolver
        implements SymbolResolver
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.symbolresolver.SymbolResolver#resolve(java.lang.String)
     */
    @Override
    public Instrument resolve(String inSymbol)
    {
        return resolve(inSymbol,
                       DEFAULT_CONTEXT); 
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.symbolresolver.SymbolResolver#resolve(java.lang.String, java.lang.Object)
     */
    @Override
    public Instrument resolve(String inSymbol,
                              Object inContext)
    {
        if(inContext == null) {
            return resolve(inSymbol,
                           DEFAULT_CONTEXT);
        }
        Map<String,Instrument> resolver = resolversByContext.get(inContext);
        if(resolver == null) {
            SLF4JLoggerProxy.warn(this,
                                  "No symbol resolver context for {}, falling back to the default context", // TODO
                                  inContext);
            return resolve(inSymbol,
                           DEFAULT_CONTEXT);
        }
        // resolver is non-null, context may be default or other
        Instrument instrument = resolver.get(inSymbol);
        if(instrument == null) {
            if(inContext == DEFAULT_CONTEXT) {
                throw new NoInstrumentForSymbol();
            }
            // try to resolve the symbol from the default context
            return resolve(inSymbol,
                           DEFAULT_CONTEXT);
        }
        return instrument;
    }
    /**
     * Adds the given symbol resolution with the given context.
     *
     * @param inSymbol a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     * @param inContext an <code>Object</code> value or <code>null</code> to use the default context
     */
    public void addSymbolMap(String inSymbol,
                             Instrument inInstrument,
                             Object inContext)
    {
        if(inContext == null) {
            inContext = DEFAULT_CONTEXT;
        }
        Map<String,Instrument> resolver = resolversByContext.get(inContext);
        if(resolver == null) {
            resolver = new HashMap<String,Instrument>();
            resolversByContext.put(inContext,
                                   resolver);
        }
        resolver.put(inSymbol,
                     inInstrument);
    }
    /**
     * Adds the given symbol map to the default context.
     *
     * @param inSymbol a <code>String</code> value
     * @param inInstrument an <code>Instrument</code> value
     */
    public void addSymbolMap(String inSymbol,
                             Instrument inInstrument)
    {
        addSymbolMap(inSymbol,
                     inInstrument,
                     DEFAULT_CONTEXT);
    }
    /**
     * Create a new MockSymbolResolver instance.
     */
    public MockSymbolResolver()
    {
        resolversByContext.put(DEFAULT_CONTEXT,
                               new HashMap<String,Instrument>());
    }
    /**
     * context object to use if no context is provided
     */
    private static final Object DEFAULT_CONTEXT = new Object();
    /**
     * instruments by symbol with context
     */
    private Map<Object,Map<String,Instrument>> resolversByContext = new HashMap<Object,Map<String,Instrument>>();
}
