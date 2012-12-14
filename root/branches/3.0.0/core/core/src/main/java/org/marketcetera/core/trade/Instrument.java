package org.marketcetera.core.trade;

import java.io.Serializable;

import javax.annotation.concurrent.ThreadSafe;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

/* $License$ */

/**
 * Provides common routines for <code>Instrument</code> implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: AbstractInstrumentImpl.java 16345 2012-11-01 21:44:15Z colin $
 * @since $Release$
 */
@ThreadSafe
@XmlRootElement
@XmlSeeAlso({ Equity.class, Option.class, Future.class, ConvertibleBond.class })
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Instrument
        implements Serializable, Comparable<Instrument>
{
    /**
     * Gets the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Gets the full symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getFullSymbol()
    {
        return symbol;
    }
    /**
     * Gets the security type value.
     *
     * @return a <code>SecurityType</code> value
     */
    @XmlAttribute
    public abstract SecurityType getSecurityType();
    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Instrument inO)
    {
        return symbol.compareTo(inO.getSymbol());
    }
    /**
     * Create a new AbstractInstrumentImpl instance.
     *
     * @param inSymbol a <code>String</code> value
     * @throws IllegalArgumentException if the symbol is invalid
     */
    protected Instrument(String inSymbol)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
    }
    /**
     * Create a new AbstractInstrumentImpl instance.
     * 
     * <b>Used for JAXB, should not be used otherwise.
     */
    protected Instrument()
    {
        symbol = null;
    }
    /**
     * symbol value
     */
    @XmlAttribute
    private final String symbol;
    private static final long serialVersionUID = 1L;
}
