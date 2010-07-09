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
     *  @throws IllegalArgumentException if any of the parameters are invalid
     */
    public Future(String inSymbol)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol);
        customerInfo = null;
    }
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
                  String inCustomerInfo)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol);
        customerInfo = StringUtils.trimToNull(inCustomerInfo);
        Validate.notNull(customerInfo);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Get the customerInfo value.
     *
     * @return a <code>String</code> value
     */
    public String getCustomerInfo()
    {
        return customerInfo;
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
        // TODO - figure it out from the symbol
        return null;
    }
    /**
     * Create a new Future instance.
     * 
     * Parameterless constructor for use only by JAXB.
     */
    protected Future()
    {
        symbol = null;
        customerInfo = null;
    }
    /**
     * the symbol root
     */
    private final String symbol;
    /**
     * the customer info
     */
    private final String customerInfo;
    private static final long serialVersionUID = 1L;
}
