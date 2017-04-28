package org.marketcetera.marketdata.core.provider;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.marketcetera.marketdata.Content;
import org.marketcetera.marketdata.core.request.MarketDataRequestAtom;

/**
 * Represents a single market data request item.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractMarketDataProvider.java 17068 2015-12-07 17:26:31Z colin $
 * @since 2.4.0
 */
@Immutable
public class MarketDataRequestAtomImpl
        implements MarketDataRequestAtom
{
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#getExchange()
     */
    @Override
    public String getExchange()
    {
        return exchange;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.request.MarketDataRequestAtom#isUnderlyingSymbol()
     */
    @Override
    public boolean isUnderlyingSymbol()
    {
        return isUnderlyingSymbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.provider.MarketDataRequestAtom#getContent()
     */
    @Override
    public Content getContent()
    {
        return content;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(content).append(" : ").append(symbol); //$NON-NLS-1$
        if(exchange != null) {
            builder.append(" : ").append(exchange); //$NON-NLS-1$
        }
        if(isUnderlyingSymbol) {
            builder.append(" (underlying)"); //$NON-NLS-1$
        }
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(content).append(symbol).append(exchange).append(isUnderlyingSymbol).toHashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MarketDataRequestAtomImpl)) {
            return false;
        }
        MarketDataRequestAtomImpl other = (MarketDataRequestAtomImpl) obj;
        return new EqualsBuilder().append(symbol,other.symbol)
                                  .append(exchange,other.exchange)
                                  .append(content,other.content)
                                  .append(isUnderlyingSymbol,other.isUnderlyingSymbol).isEquals();
    }
    /**
     * Create a new MarketDataRequestAtomImpl instance.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExchange a <code>String</code> value or <code>null</code>
     * @param inContent a <code>Content</code> value
     * @param inIsUnderlyingSymbol a <code>boolean</code> value
     */
    public MarketDataRequestAtomImpl(String inSymbol,
                                     String inExchange,
                                     Content inContent,
                                     boolean inIsUnderlyingSymbol)
    {
        symbol = inSymbol;
        exchange = inExchange;
        content = inContent;
        isUnderlyingSymbol = inIsUnderlyingSymbol;
    }
    /**
     * symbol value, may be a symbol, an underlying symbol, or a symbol fragment
     */
    private final String symbol;
    /**
     * exchange value or <code>null</code>
     */
    private final String exchange;
    /**
     * indicates if the symbol is supposed to be a symbol or an underlying symbol
     */
    private final boolean isUnderlyingSymbol;
    /**
     * content value of the request
     */
    private final Content content;
}
