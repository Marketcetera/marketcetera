package org.marketcetera.core.trade;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/* $License$ */

/**
 * Represents a Convertible Bond instrument.
 *
 * @version $Id: ConvertibleBondImpl.java 16327 2012-10-26 21:14:08Z colin $
 * @since $Release$
 */
@ThreadSafe
@XmlRootElement(name="convertibleBond")
@XmlAccessorType(XmlAccessType.NONE)
public class ConvertibleBond
        extends Instrument
{
    /**
     * Create a new Convertible Bond instance.
     *
     * @param inIsin a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public ConvertibleBond(String inIsin)
    {
        super(inIsin);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSecurityType()
     */
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
                                   ToStringStyle.SHORT_PREFIX_STYLE).append("isin",  //$NON-NLS-1$
                                                                            getSymbol()).toString();
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
        if (!(obj instanceof ConvertibleBond)) {
            return false;
        }
        ConvertibleBond other = (ConvertibleBond) obj;
        if (getSymbol() == null) {
            if (other.getSymbol() != null) {
                return false;
            }
        } else if (!getSymbol().equals(other.getSymbol())) {
            return false;
        }
        return true;
    }
    /**
     * Create a new ConvertibleBondImpl instance.
     */
    @SuppressWarnings("unused")
    private ConvertibleBond() {}
    private static final long serialVersionUID = 1L;
}
