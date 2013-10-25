package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.trade.Currency;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * A comparator for {@link org.marketcetera.trade.Currency} instruments.
 *
 */
@ClassVersion("$Id$")
public class CurrencyPositionKeyComparator
        extends InstrumentPositionKeyComparator<Currency>
{
    /**
     * Creates an instance.
     */
    public CurrencyPositionKeyComparator()
    {
        super(Currency.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.InstrumentPositionKeyComparator#getRank()
     */
    @Override
    public int getRank()
    {
        return 400;
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Currency inO1,Currency inO2)
    {
        return new CompareToBuilder().append(inO1.getSymbol(),
                                             inO2.getSymbol())
                                     .append(inO1.getNearTenor(),
                                             inO2.getNearTenor())
                                     .append(inO1.getFarTenor(),
                                             inO2.getFarTenor()).toComparison();
    }
}