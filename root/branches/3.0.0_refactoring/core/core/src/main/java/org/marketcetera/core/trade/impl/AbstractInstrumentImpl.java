package org.marketcetera.core.trade.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.trade.Instrument;
import org.marketcetera.core.trade.Messages;

/* $License$ */

/**
 * Provides common routines for <code>Instrument</code> implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractInstrumentImpl
        implements Instrument
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.trade.Instrument#getFullSymbol()
     */
    @Override
    public String getFullSymbol()
    {
        return symbol;
    }
    /**
     * Create a new AbstractInstrumentImpl instance.
     *
     * @param inSymbol a <code>String</code> value
     */
    protected AbstractInstrumentImpl(String inSymbol)
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
    protected AbstractInstrumentImpl()
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
