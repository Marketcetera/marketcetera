package org.marketcetera.core.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.api.systemmodel.instruments.Instrument;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractInstrumentImpl
        implements Instrument
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.instruments.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.instruments.Instrument#getFullSymbol()
     */
    @Override
    public String getFullSymbol()
    {
        return symbol;
    }
    protected AbstractInstrumentImpl(String inSymbol)
    {
        symbol = StringUtils.trimToNull(inSymbol);
        Validate.notNull(symbol,
                         Messages.NULL_SYMBOL.getText());
    }
    protected AbstractInstrumentImpl()
    {
        symbol = null;
    }
    private final String symbol;
    private static final long serialVersionUID = 1L;
}
