package org.marketcetera.core.trade.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.marketcetera.core.trade.ConvertibleBond;
import org.marketcetera.core.trade.SecurityType;

/* $License$ */

/**
 * Represents a Convertible Bond instrument.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConvertibleBondImpl
        extends AbstractInstrumentImpl
        implements ConvertibleBond
{
    /**
     * Create a new ConvertibleBond instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public ConvertibleBondImpl(String inSymbol)
    {
        super(inSymbol);
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
        if (!(obj instanceof ConvertibleBondImpl)) {
            return false;
        }
        ConvertibleBondImpl other = (ConvertibleBondImpl) obj;
        if (getSymbol() == null) {
            if (other.getSymbol() != null) {
                return false;
            }
        } else if (!getSymbol().equals(other.getSymbol())) {
            return false;
        }
        return true;
    }
    private static final long serialVersionUID = 1L;
}
