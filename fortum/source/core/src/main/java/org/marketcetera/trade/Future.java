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
     * @throws IllegalArgumentException if the symbol is invalid or the expiration cannot be inferred from the symbol
     */
    public Future(String inSymbol)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
        expiration = guessExpirationFromSymbol(symbol);
    }
    /**
     * Create a new Future instance.
     *
     * @param inSymbol a <code>String</code> value containing the future base symbol
     * @param inExpiry a <code>String</code> value containing the expiry in MMMMYY format
     * @throws IllegalArgumentException if the symbol is invalid or the expiration cannot be inferred from the symbol
     */
    public Future(String inSymbol,
                  String inExpiry)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
        expiration = StringUtils.trimToNull(inExpiry);
        Validate.notNull(inExpiry);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    public String getFullSymbol()
    {
        if(StringUtils.trimToNull(expiration) == null) {
            return getSymbol();
        }
        return String.format("%s-%s",
                             getSymbol(),
                             getExpiration());
    }
    /**
     * Get the expiration value.
     *
     * @return a <code>String</code> value
     */
    public String getExpiration()
    {
        return expiration;
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
     * Gets the future maturity as a <code>MaturityMonthYear</code> value.
     *
     * @return a <code>MaturityMonthYear</code> value
     */
    public MaturityMonthYear getExpiryAsMaturityMonthYear()
    {
        return new MaturityMonthYear(expiration);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
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
        if (expiration == null) {
            if (other.expiration != null)
                return false;
        } else if (!expiration.equals(other.expiration))
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
        return String.format("Future [symbol=%s, expiration=%s]", //$NON-NLS-1$
                             symbol,
                             expiration);
    }
    /**
     * Create a new Future instance.
     * 
     * Parameterless constructor for use only by JAXB.
     */
    protected Future()
    {
        symbol = null;
        expiration = null;
    }
    /**
     * Tries to determine the expiration value for the given symbol.
     *
     * @param inSymbol a <code>String</code> value
     * @return a <code>String</code>
     * @throws IllegalArgumentException if the symbol could not be determined
     */
    private static String guessExpirationFromSymbol(String inSymbol)
    {
        try {
            StringBuffer expiration = new StringBuffer();
            int partYear = 2000;
            FutureExpirationMonth month;
            // Nordies: ENOQ1-11 ENOYR-10 ENOQ4-11 ENOW32-11 ENOD1007-10 ENOW28-1 ENOMAUG-10
            if(NORD_YEAR.matcher(inSymbol).matches()) {
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('-')+1));
                month = FutureExpirationMonth.DECEMBER;
            } else if(NORD_QUARTER.matcher(inSymbol).matches()) {
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('-')+1));
                int q = Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('Q')+1,
                                                            inSymbol.lastIndexOf('Q')+2));
                if(q == 1) {
                    month = FutureExpirationMonth.MARCH;
                } else if(q == 2) {
                    month = FutureExpirationMonth.JUNE;
                } else if(q == 3) {
                    month = FutureExpirationMonth.SEPTEMBER;
                } else if(q == 4) {
                    month = FutureExpirationMonth.DECEMBER;
                } else {
                    throw new IllegalArgumentException(Messages.INVALID_QUARTER.getText(inSymbol,
                                                                                        q));
                }
            } else if(NORD_MONTH.matcher(inSymbol).matches()) {
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('-')+1));
                String monthDescription = inSymbol.substring(inSymbol.lastIndexOf('M')+1,
                                                             inSymbol.lastIndexOf('M')+4);
                month = FutureExpirationMonth.getByMonthShortName(monthDescription);
            } else
            if(NORD_WEEK.matcher(inSymbol).matches()) {
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('-')+1));
                int week = Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('W')+1,
                                                               inSymbol.lastIndexOf('W')+3));
                month = FutureExpirationMonth.getByWeekOfYear(week,
                                                                             partYear);
            } else
            if(NORD_DAY.matcher(inSymbol).matches()) {
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.lastIndexOf('-')+1));
                String monthVal = inSymbol.substring(inSymbol.lastIndexOf('D')+3,
                                                     inSymbol.lastIndexOf('D')+5);
                month = FutureExpirationMonth.getByMonthOfYear(monthVal);
            } else
            if(SKE_MONTH1.matcher(inSymbol).matches()) {
                // BRN14U
                partYear += Integer.parseInt(inSymbol.substring(inSymbol.length()-3,
                                                                inSymbol.length()-1));
                month = FutureExpirationMonth.getByCfiCode(inSymbol.substring(inSymbol.length()-1));
            } else
                if(SKE_MONTH2.matcher(inSymbol).matches()) {
                    // ECFU0
                    partYear += Integer.parseInt(inSymbol.substring(inSymbol.length()-1))+10;
                    month = FutureExpirationMonth.getByCfiCode(inSymbol.substring(inSymbol.length()-2,
                                                                                  inSymbol.length()-1));
            } else {
                return "";
            }
            expiration.append(partYear).append(month.getMonthOfYear());
            return expiration.toString();
        } catch (Exception e)  {
            return "";
        }
    }
    /**
     * the symbol root
     */
    private final String symbol;
    /**
     * the expiration pattern
     */
    private final String expiration;
    /**
     * pattern for matching nord pool year-based instruments
     */
    private static final Pattern NORD_YEAR = Pattern.compile("[A-Z]*YR-[0-9]{2}"); //$NON-NLS-1$
    /**
     * pattern for matching nord pool quarter-based instruments
     */
    private static final Pattern NORD_QUARTER = Pattern.compile("[A-Z]*Q[0-9]-[0-9]{2}"); //$NON-NLS-1$
    /**
     * pattern for matching nord pool month-based instruments
     */
    private static final Pattern NORD_MONTH = Pattern.compile("[A-Z]*M[A-Z]{3}-[0-9]{2}"); //$NON-NLS-1$
    /**
     * pattern for matching nord pool week-based instruments
     */
    private static final Pattern NORD_WEEK = Pattern.compile("[A-Z]*W[0-9]{2}-[0-9]{2}"); //$NON-NLS-1$
    /**
     * pattern for matching nord pool day-based instruments
     */
    private static final Pattern NORD_DAY = Pattern.compile("[A-Z]*D[0-9]{4}-[0-9]{2}"); //$NON-NLS-1$
    /**
     * pattern for matching ICE month-based instruments
     */
    private static final Pattern SKE_MONTH1 = Pattern.compile("[A-Z]*[0-9]{2}[F,G,H,J,K,M,N,Q,U,V,X,Z]"); //$NON-NLS-1$
    private static final Pattern SKE_MONTH2 = Pattern.compile("[A-Z]*[F,G,H,J,K,M,N,Q,U,V,X,Z][0-9]{1}"); //$NON-NLS-1$
    private static final long serialVersionUID = 1L;
}
