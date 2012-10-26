package org.marketcetera.core.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 * Creates <code>Option</code> values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface OptionFactory
        extends InstrumentFactory<Option>
{
    /**
     * Creates an <code>Option</code> value.
     *
     * @param inSymbol a <code>String</code> value
     * @param inExpiry a <code>String</code> value
     * @param inStrikePrice a <code>BigDecimal</code> value
     * @param inType an <code>OptionType</code> value
     * @return an <code>Option</code> value
     */
    public Option create(String inSymbol,
                         String inExpiry,
                         BigDecimal inStrikePrice,
                         OptionType inType);
}
