package org.marketcetera.core.trade;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Identifies an equity.
 * 
 * @version $Id: EquityImpl.java 16327 2012-10-26 21:14:08Z colin $
 * @since 2.0.0
 */
@ThreadSafe
@XmlRootElement(name="equity")
@XmlAccessorType(XmlAccessType.NONE)
public class Equity
        extends Instrument
{
    /**
     * Create a new Equity instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the symbol is invalid
     */
    public Equity(String inSymbol)
    {
        super(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.trade.Instrument#getSecurityType()
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
        if (!(obj instanceof Equity)) {
            return false;
        }
        Equity other = (Equity) obj;
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
    /**
     * Parameterless constructor for use only by JAXB.
     */
    @SuppressWarnings("unused")
    private Equity() {}
    private static final long serialVersionUID = 1L;
}
