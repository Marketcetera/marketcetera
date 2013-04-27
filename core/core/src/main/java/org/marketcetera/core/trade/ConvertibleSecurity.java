package org.marketcetera.core.trade;

import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

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
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code>, empty, or neither a valid CUSIP nor a valid ISIN
     */
    public ConvertibleSecurity(String inSymbol)
    {
        super(inSymbol);
        inSymbol = StringUtils.trimToNull(inSymbol);
        if(isinPattern.matcher(inSymbol).matches()) {
            cusip = getCusipFromIsin(inSymbol);
        } else if(cusipPattern.matcher(inSymbol).matches()) {
            cusip = inSymbol;
        } else {
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
        return cusip;
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
        StringBuilder builder = new StringBuilder();
        builder.append("ConvertibleSecurity [").append(getSymbol()).append("]");
        return builder.toString();
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
     * Determines the CUSIP from the given ISIN.
     *
     * @param inIsin a <code>String</code> value
     * @return a <code>String</code> value
     */
    private static String getCusipFromIsin(String inIsin)
    {
        // remove the first two letters and the last checksum
        return inIsin.substring(2,inIsin.length()-1);
    }
    /**
     * Create a new ConvertibleSecurityImpl instance.
     */
    @SuppressWarnings("unused")
    private ConvertibleSecurity() {}
    /**
     * cusip value
     */
    private String cusip;
    /**
     * isin regex
     */
    public static final Pattern isinPattern = Pattern.compile("^[A-Z]{2}([A-Z0-9]){9}[0-9]$");
    /**
     * cusip regex
     */
    public static final Pattern cusipPattern = Pattern.compile("^[0-9]{3}[A-Z0-9]{5}[0-9]$");
    private static final long serialVersionUID = 7922788847017047191L;
}
