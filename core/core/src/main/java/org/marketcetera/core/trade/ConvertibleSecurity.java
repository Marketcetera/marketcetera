package org.marketcetera.core.trade;

import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/* $License$ */

/**
 * Represents a Convertible Security instrument.
 *
 * @version $Id: ConvertibleSecurityImpl.java 16327 2012-10-26 21:14:08Z colin $
 * @since $Release$
 */
@ThreadSafe
@XmlRootElement(name="convertibleSecurity")
@XmlAccessorType(XmlAccessType.NONE)
public class ConvertibleSecurity
        extends Instrument
{
    /**
     * Create a new Convertible Security instance.
     *
     * @param inIsin a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code> or empty
     */
    public ConvertibleSecurity(String inIsin)
    {
        super(inIsin);
        if(!isinPattern.matcher(inIsin).matches()) {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Gets the CUSIP value.
     *
     * @return a <code>String</code> value
     */
    public String getCusip()
    {
        // symbol is the ISIN
        String isin = getSymbol();
        // remove the first two letters and the last checksum
        return isin.substring(2,isin.length()-1);
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
        if (!(obj instanceof ConvertibleSecurity)) {
            return false;
        }
        ConvertibleSecurity other = (ConvertibleSecurity) obj;
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
     * Create a new ConvertibleSecurityImpl instance.
     */
    @SuppressWarnings("unused")
    private ConvertibleSecurity() {}
    private static final long serialVersionUID = 1L;
    /**
     * isin regex
     */
    public static final Pattern isinPattern = Pattern.compile("[A-Z]{2}([A-Z0-9]){9}[0-9]");
}
