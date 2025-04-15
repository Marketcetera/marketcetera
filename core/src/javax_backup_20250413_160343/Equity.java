package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Identifies an equity.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class Equity
        extends Instrument
{
    /**
     * Create a new Equity instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if symbol is null or empty
     */
    public Equity(String inSymbol)
    {
        this(inSymbol,
             null);
    }
    /**
     * Create a new Equity instance.
     *
     * @param inSymbol a <code>String</code> value
     * @param inSymbolSfx a <code>String</code> value
     */
    public Equity(String inSymbol,
                  String inSymbolSfx)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(inSymbol);
        symbol = inSymbol;
        symbolSfx = StringUtils.trimToNull(inSymbolSfx);
        if(symbolSfx == null) {
            fullSymbol = symbol;
        } else {
            fullSymbol = symbol+"."+symbolSfx;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getFullSymbol()
     */
    @Override
    public String getFullSymbol()
    {
        return fullSymbol;
    }
    /**
     * Get the symbolSfx value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbolSfx()
    {
        return symbolSfx;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return SecurityType.CommonStock;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(symbol).append(symbolSfx).toHashCode();
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
        if (!(obj instanceof Equity)) {
            return false;
        }
        Equity other = (Equity) obj;
        return new EqualsBuilder().append(symbol,other.symbol).append(symbolSfx,other.symbolSfx).isEquals();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this,ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("symbol", symbol); //$NON-NLS-1$
        if(symbolSfx != null) {
            builder.append("symbolSfx", symbolSfx); //$NON-NLS-1$
        }
        return builder.toString();
    }
    /**
     * Create a new Equity instance.
     */
    @SuppressWarnings("unused")
    private Equity()
    {
        symbol = null;
        symbolSfx = null;
        fullSymbol = null;
    }
    /**
     * symbol value
     */
    private final String symbol;
    /**
     * symbolSfx value
     */
    private final String symbolSfx;
    /**
     * full symbol display value
     */
    private final String fullSymbol;
    private static final long serialVersionUID = -4249367582080830122L;
}
