package org.marketcetera.core.trade.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.core.trade.Equity;
import org.marketcetera.core.trade.SecurityType;

/* $License$ */

/**
 * Identifies an equity.
 * 
 * @version $Id$
 * @since 2.0.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EquityImpl
        extends AbstractInstrumentImpl
        implements Equity
{
    /**
     * Constructor.
     * 
     * @param symbol symbol
     * @throws IllegalArgumentException if symbol is null or whitespace
     */
    public EquityImpl(String symbol)
    {
        super(symbol);
    }
    /**
     * Parameterless constructor for use only by JAXB.
     */
    @SuppressWarnings("unused")
    private EquityImpl()
    {
    }
    /**
     * Always returns {@link SecurityType#CommonStock}.
     * 
     * @return {@link SecurityType#CommonStock}
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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getSymbol() == null) ? 0 : getSymbol().hashCode());
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
        if (!(obj instanceof EquityImpl)) {
            return false;
        }
        EquityImpl other = (EquityImpl) obj;
        if (getSymbol() == null) {
            if (other.getSymbol() != null) {
                return false;
            }
        } else if (!getSymbol().equals(other.getSymbol())) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Equity[symbol=").append(getSymbol()).append("]");
        return builder.toString();
    }
    private static final long serialVersionUID = 1L;
}
