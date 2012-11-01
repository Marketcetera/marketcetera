package org.marketcetera.core.trade.impl;

import org.marketcetera.core.trade.ConvertibleBond;
import org.marketcetera.core.trade.ConvertibleBondFactory;

/* $License$ */

/**
 * Constructs <code>ConvertibleBond</code> objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondFactoryImpl
        implements ConvertibleBondFactory
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.trade.InstrumentFactory#create(java.lang.String)
     */
    @Override
    public ConvertibleBond create(String inSymbol)
    {
        return new ConvertibleBondImpl(inSymbol);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.trade.InstrumentFactory#createFromFullSymbol(java.lang.String)
     */
    @Override
    public ConvertibleBond createFromFullSymbol(String inFullSymbol)
    {
        return new ConvertibleBondImpl(inFullSymbol);
    }
}
