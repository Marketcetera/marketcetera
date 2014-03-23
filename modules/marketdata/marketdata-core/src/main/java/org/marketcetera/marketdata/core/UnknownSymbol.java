package org.marketcetera.marketdata.core;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a market data request contained a symbol that could not be handled by a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class UnknownSymbol
        extends CoreException
{
    /**
     * Create a new UnknownSymbol instance.
     */
    public UnknownSymbol()
    {
    }
    /**
     * Create a new UnknownSymbol instance.
     *
     * @param inSymbol a <code>String</code> value
     */
    public UnknownSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /**
     * Create a new UnknownSymbol instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public UnknownSymbol(Throwable inNested)
    {
    }
    /**
     * Create a new UnknownSymbol instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownSymbol(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new UnknownSymbol instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownSymbol(Throwable inNested,
                         I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Get the symbol value.
     *
     * @return a <code>String</code> value
     */
    public String getSymbol()
    {
        return symbol;
    }
    /**
     * Sets the symbol value.
     *
     * @param inSymbol a <code>String</code> value
     */
    public void setSymbol(String inSymbol)
    {
        symbol = inSymbol;
    }
    /**
     * symbol value
     */
    private String symbol;
    private static final long serialVersionUID = -1186869969137628092L;
}
