package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.ConvertibleSecurity;

/* $License$ */

/**
 * Compares positions of <code>ConvertibleSecurity</code> instruments.
 *
 * @version $Id$
 * @since $Release$
 */
public class ConvertibleSecurityPositionKeyComparator
        extends InstrumentPositionKeyComparator<ConvertibleSecurity>
{
    /**
     * Create a new ConvertibleSecurityPositionKeyComparator instance.
     */
    public ConvertibleSecurityPositionKeyComparator()
    {
        super(ConvertibleSecurity.class);
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(ConvertibleSecurity inSecurity1,
                       ConvertibleSecurity inSecurity2)
    {
        return new CompareToBuilder().append(inSecurity1.getSymbol(),
                                             inSecurity2.getSymbol()).toComparison();
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
