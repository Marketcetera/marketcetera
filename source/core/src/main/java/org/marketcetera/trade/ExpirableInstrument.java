package org.marketcetera.trade;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public abstract class ExpirableInstrument
        extends Instrument
{
    /**
     * Returns the expiry.
     * 
     * @return a <code>String</code> value containing the expiry
     */
    public String getExpiry()
    {
        return expiry;
    }
    /**
     * Returns the root symbol.
     * 
     * @return a <code>String</code> value containing the root symbol
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Create a new ExpirableInstrument instance.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     */
    protected ExpirableInstrument(String inSymbol,
                                  String inExpiry)
    {
        inSymbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(inSymbol);
        symbol = inSymbol;
        inExpiry = StringUtils.trimToNull(inExpiry);
        expiry = inExpiry;
    }
    /**
     * Create a new ExpirableInstrument instance.
     * 
     * <p>Parameterless constructor for use only by JAXB.
     */
    protected ExpirableInstrument()
    {
        expiry = null;
        symbol = null;
    }
    /**
     * 
     */
    private final String expiry;
    /**
     * 
     */
    private final String symbol;
    private static final long serialVersionUID = 1L;
}
