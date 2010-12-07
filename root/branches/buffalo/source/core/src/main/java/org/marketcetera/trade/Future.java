package org.marketcetera.trade;

import java.util.regex.Pattern;

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
 * @since 2.1.0
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@ClassVersion("$Id$")
public class Future
        extends Instrument
{
    /**
     * Parses the given <code>String</code> to create a <code>Future</code> instrument.
     *
     * @param inFullSymbol a <code>String</code> value in the form <code>SYMBOL-YYYYMM</code>
     * @return a <code>Future</code> value
     * @throws IllegalArgumentException if the given <code>String</code> cannot be parsed
     */
    public static Future fromString(String inFullSymbol)
    {
        inFullSymbol = StringUtils.trimToNull(inFullSymbol);
        Validate.notNull(inFullSymbol,
                         Messages.NULL_SYMBOL.getText());
        Validate.isTrue(FUTURE_STRING.matcher(inFullSymbol).matches(),
                        Messages.INVALID_SYMBOL.getText(inFullSymbol));
        int dashIndex = inFullSymbol.lastIndexOf('-');
        assert(dashIndex != -1);
        String symbol = inFullSymbol.substring(0,
                                               dashIndex);
        String expiry = inFullSymbol.substring(dashIndex+1);
        return new Future(symbol,
                          expiry);
    }
    /**
     * Create a new Future instance.
     *
     * @param inSymbol a <code>String</code> value containing the future symbol
     * @param inExpirationQuarter a <code>FutureExpirationMonth</code> value containing the future expiry month
     * @param inExpirationYear an <code>int</code> value containing the future expiration year (values &lt; 100 will be considered years
     *  in the 21st century)
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    public Future(String inSymbol,
                  FutureExpirationMonth inExpirationMonth,
                  int inExpirationYear)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
        Validate.notNull(inExpirationMonth,
                         Messages.NULL_MONTH.getText());
        Validate.isTrue(inExpirationYear > 0,
                        Messages.INVALID_YEAR.getText(inExpirationYear));
        if(inExpirationYear < 100) {
            inExpirationYear += 2000;
        }
        expirationMonth = inExpirationMonth;
        expirationYear = inExpirationYear;
    }
    /**
     * Create a new Future instance.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value containing the future expiry in the format YYYYMM  (year values &lt; 100 will be considered years
     *  in the 21st century)
     * @throws IllegalArgumentException if any of the parameters are invalid
     */
    public Future(String inSymbol,
                  String inExpiry)
    {
        symbol = StringUtils.trimToNull(inSymbol); 
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
        inExpiry = StringUtils.trimToNull(inExpiry);
        Validate.notNull(inExpiry,
                         Messages.NULL_EXPIRY.getText());
        Validate.isTrue(YYYYMM.matcher(inExpiry).matches(),
                        Messages.INVALID_EXPIRY.getText(inExpiry));
        int year = Integer.parseInt(inExpiry.substring(0,
                                                       4));
        String month = inExpiry.substring(4);
        Validate.isTrue(year > 0,
                        Messages.INVALID_YEAR.getText(year));
        if(year < 100) {
            year += 2000;
        }
        expirationYear = year;
        expirationMonth = FutureExpirationMonth.getByMonthOfYear(month);
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
     * Gets the symbol in the form SYMBOL-YYYYMM.
     *
     * @return a <code>String</code> value
     */
    public String getFullSymbol()
    {
        String symbol = getSymbol();
        if(FUTURE_STRING.matcher(symbol).matches()) {
            return symbol;
        }
        return String.format("%s-%s", //$NON-NLS-1$
                             symbol,
                             getExpiryAsString());
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
    public final MaturityMonthYear getExpiryAsMaturityMonthYear()
    {
        return new MaturityMonthYear(getExpiryAsString());
    }
    /**
     * Gets the instrument expiry as a <code>String</code> in the format <code>YYYYMM</code>.
     *
     * @return a <code>String</code> value
     */
    public String getExpiryAsString()
    {
        return String.format("%1$4d%2$s", //$NON-NLS-1$
                             getExpirationYear(),
                             getExpirationMonth().getMonthOfYear());
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
        if (expirationMonth != other.expirationMonth)
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
        return String.format("Future %s [%s(%s) %s]", //$NON-NLS-1$
                             symbol,
                             expirationMonth,
                             expirationMonth.getCode(),
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
     * the full symbol
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
    /**
     * regex pattern used to identify (reasonably) valid YYYYMM patterns
     */
    private static final Pattern YYYYMM = Pattern.compile("[0-9]{6}"); //$NON-NLS-1$
    /**
     * regex that tries to find a future symbol of an arbitrarily chosen format
     */
    public static final Pattern FUTURE_STRING = Pattern.compile(".*-[0-9]{6}?"); //$NON-NLS-1$
    private static final long serialVersionUID = 1L;
}
