package org.marketcetera.core.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/* $License$ */

/**
 * Represents a Convertible Bond instrument.
 *
 * @version $Id: ConvertibleBond.java 82347 2012-05-03 19:30:54Z colin $
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConvertibleBond
        extends Instrument
{
    /**
     * Create a new ConvertibleBond instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public ConvertibleBond(String inSymbol)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(inSymbol);
        symbol = inSymbol;
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
     * @see org.marketcetera.trade.Instrument#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return SecurityType.ConvertibleBond;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this,
                                   ToStringStyle.SHORT_PREFIX_STYLE).append("symbol",  //$NON-NLS-1$
                                                                            symbol).toString();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
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
        if (!(obj instanceof ConvertibleBond)) {
            return false;
        }
        ConvertibleBond other = (ConvertibleBond) obj;
        if (symbol == null) {
            if (other.symbol != null) {
                return false;
            }
        } else if (!symbol.equals(other.symbol)) {
            return false;
        }
        return true;
    }
    /**
     * symbol value
     */
    private final String symbol;
    private static final long serialVersionUID = 1L;
}
