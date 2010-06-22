package org.marketcetera.trade;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;

import javax.xml.bind.annotation.XmlAttribute;

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
        Validate.notNull(expiry);
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

    /** Explicitly add these to JAXB marshalling/unmarshalling */
    @XmlAttribute
    protected final String expiry;

    /** Explicitly add these to JAXB marshalling/unmarshalling */
    @XmlAttribute
    protected final String symbol;
    private static final long serialVersionUID = 1L;
}
