package org.marketcetera.symbol;

import java.util.List;

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
@ClassVersion("$Id: SpringConfig.java 16663 2013-08-23 14:40:19Z colin $")
public class IterativeSymbolResolver
        implements SymbolResolverService, InitializingBean
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.symbol.SymbolResolverServices#resolveSymbol(java.lang.String)
     */
    @Override
    public Instrument resolveSymbol(String inSymbol)
    {
        for(SymbolResolver resolver : symbolResolvers) {
            try {
                Instrument instrument = resolver.resolveSymbol(inSymbol);
                if(instrument != null) {
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
     * list of symbol resolvers
     */
    private List<SymbolResolver> symbolResolvers = Lists.newArrayList();
}
