package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.field.MaturityMonthYear;

/* $License$ */

/**
 * Identifies a future contract.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class Future
        extends Instrument
{
    /**
     * Create a new Future instance.
     *
     * @param inSymbol a <code>String</code> value containing the future symbol
     * @param inExpirationQuarter a <code>FutureExpirationMonth</code> value containing the future expiry month
     * @param inExpirationYear an <code>int</code> value containing the future expiration year (values &lt; 100 will be considered years
     *  in the 21st century)
     *  @throws IllegalArgumentException if any of the parameters are invalid
     */
    public Future(String inSymbol,
                  FutureExpirationMonth inExpirationMonth,
                  int inExpirationYear)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol);
        Validate.notEmpty(symbol);
        Validate.notNull(inExpirationMonth);
        Validate.isTrue(inExpirationYear > 0);
        if(inExpirationYear < 100) {
            inExpirationYear += 2000;
        }
        expirationMonth = inExpirationMonth;
        expirationYear = inExpirationYear;
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
        return SecurityType.Future;
    }
    /**
     * Get the expirationMonth value.
     *
     * @return a <code>FutureExpirationMonth</code> value
     */
    public FutureExpirationMonth getExpirationMonth()
    {
        return expirationMonth;
    }
    /**
     * Get the expirationYear value.
     *
     * @return a <code>int</code> value
     */
    public int getExpirationYear()
    {
        return expirationYear;
    }
    /**
     * Gets the future maturity as a <code>MaturityMonthYear</code> value.
     *
     * @return a <code>MaturityMonthYear</code> value
     */
    public MaturityMonthYear getExpiryAsMaturityMonthYear()
    {
        return new MaturityMonthYear(String.format("%1$4d%2$s",
                                                   getExpirationYear(),
                                                   getExpirationMonth().getMonthOfYear()));
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expirationMonth == null) ? 0 : expirationMonth.hashCode());
        result = prime * result + expirationYear;
        result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Future other = (Future) obj;
        if (expirationMonth == null) {
            if (other.expirationMonth != null)
                return false;
        } else if (!expirationMonth.equals(other.expirationMonth))
            return false;
        if (expirationYear != other.expirationYear)
            return false;
        if (symbol == null) {
            if (other.symbol != null)
                return false;
        } else if (!symbol.equals(other.symbol))
            return false;
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("Future [symbol=%s, expirationMonth=%s, expirationYear=%s]",
                             symbol,
                             expirationMonth,
                             expirationYear);
    }
    /**
     * Create a new Future instance.
     * 
     * Parameterless constructor for use only by JAXB.
     */
    protected Future()
    {
        symbol = null;
        expirationMonth = null;
        expirationYear = -1;
    }
    /**
     * the symbol root
     */
    private final String symbol;
    /**
     * the expiration month
     */
    private final FutureExpirationMonth expirationMonth;
    /**
     * the expiration year
     */
    private final int expirationYear;
    private static final long serialVersionUID = 1L;
}
