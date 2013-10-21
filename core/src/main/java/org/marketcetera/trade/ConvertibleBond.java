package org.marketcetera.trade;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.time.TimeFactory;
import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents a Convertible Security instrument.
 *
 * @version $Id: ConvertibleSecurityImpl.java 16327 2012-10-26 21:14:08Z colin $
 * @since $Release$
 */
@ThreadSafe
@XmlRootElement(name="convertibleBond")
@XmlAccessorType(XmlAccessType.NONE)
@ClassVersion("$Id$")
public class ConvertibleBond
        extends Instrument
{
    /**
     * Create a new Convertible Security instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the given symbol is <code>null</code>, empty, neither a valid CUSIP nor a valid ISIN nor a valid symbol of the type <code>SYMBOL RATE% DUE-DATE</code>
     */
    public ConvertibleBond(String inSymbol)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        if(isinPattern.matcher(inSymbol).matches()) {
            cusip = getCusipFromIsin(inSymbol);
            symbol = cusip;
        } else if(cusipPattern.matcher(inSymbol).matches()) {
            cusip = inSymbol;
            symbol = cusip;
        } else if(symbolPattern.matcher(inSymbol).matches()) {
            String[] parts = inSymbol.split(" ");
            timeFactory.create(parts[2]);
            symbol = inSymbol;
            cusip = null;
        } else {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Create a new ConvertibleBond instance.
     *
     * @param inTicker a <code>String</code> value
     * @param inCouponRate a <code>BigDecimal</code> value
     * @param inMaturity a <code>String</code> value
     */
    public ConvertibleBond(String inTicker,
                           BigDecimal inCouponRate,
                           String inMaturity)
    {
        ticker = inTicker;
        couponRate = inCouponRate;
        maturity = inMaturity;
        symbol = inTicker + " " + inCouponRate + "% " + inMaturity;
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
     * @see org.marketcetera.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ConvertibleBond [").append(getSymbol()).append("]");
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
     * Gets the ISIN for this security using the given country code.
     *
     * @param inCountryCode a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String getIsin(String inCountryCode)
    {
        StringBuilder output = new StringBuilder();
        output.append(inCountryCode).append(cusip);
        int sum = 0;
        int index = 0;
        for(int i=cusip.length()-1;i>=0;i--) {
            char c = cusip.charAt(i);
            int asciiValue = (int)c;
            if(Character.isLetter(c)) {
                asciiValue = ((int)Character.toUpperCase(c)) - 55;
            } else {
                asciiValue -= 48;
            }
            if(index++ % 2 == 0) {
                asciiValue *= 2;
                String asciiValueAsString = String.valueOf(asciiValue);
                for(char asciiValueChar : asciiValueAsString.toCharArray()) {
                    int innerValue = (int)asciiValueChar - 48;
                    sum += innerValue;
                }
            } else {
                sum += asciiValue;
            }
        }
        int subtrahend = sum;
        while(subtrahend % 10 != 0) {
            subtrahend += 1;
        }
        sum = subtrahend - sum;
        output.append(sum);
        return output.toString();
    }
    /**
     * Get the underlying value.
     *
     * @return a <code>String</code> value
     */
    public String getTicker()
    {
        return ticker;
    }
    /**
     * Get the coupon rate value.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getCouponRate()
    {
        return couponRate;
    }
    /**
     * Get the maturity value.
     *
     * @return a <code>String</code> value
     */
    public String getMaturity()
    {
        return maturity;
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
    private ConvertibleBond() {}
    private String symbol;
    /**
     * ticker value
     */
    private String ticker;
    /**
     * rate value
     */
    private BigDecimal couponRate;
    /**
     * maturity value
     */
    private String maturity;
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
    /**
     * pattern used to validate symbols of the type "ticker rate maturity", as in "IBM 2.54% 03/15/2014"
     */
    public static final Pattern symbolPattern = Pattern.compile("^\\w* \\d{1}(\\.{1}\\d{1,3}){0,1}% \\d{1,2}/\\d{1,2}/\\d{1,4}$");
    /**
     * converts due dates as necessary
     */
    private static final TimeFactory timeFactory = new TimeFactoryImpl();
    private static final long serialVersionUID = -7797829861069074193L;
}
