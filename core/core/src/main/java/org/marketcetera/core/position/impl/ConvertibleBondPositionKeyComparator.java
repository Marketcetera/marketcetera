package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.impl.ConvertibleBondImpl;

/* $License$ */

/**
 * Compares positions of <code>ConvertibleBond</code> instruments.
 *
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleBondPositionKeyComparator
        extends InstrumentPositionKeyComparator<ConvertibleBondImpl>
{
    /**
     * Create a new ConvertibleBondPositionKeyComparator instance.
     */
    public ConvertibleBondPositionKeyComparator()
    {
        super(ConvertibleBondImpl.class);
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ConvertibleBondImpl inBond1,
                       ConvertibleBondImpl inBond2)
    {
        return new CompareToBuilder().append(inBond1.getSymbol(),
                                             inBond2.getSymbol()).toComparison();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.InstrumentPositionKeyComparator#getRank()
     */
    @Override
    public int getRank()
    {
        return 400;
    }
}
