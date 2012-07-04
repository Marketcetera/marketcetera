package org.marketcetera.core.position.impl;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.marketcetera.core.trade.Future;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * A comparator for {@link org.marketcetera.core.trade.Future} instruments.
 *
 * @author colin@marketcetera.com
 * @version $Id: FuturePositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.1.0
 */
@ClassVersion("$Id: FuturePositionKeyComparator.java 16063 2012-01-31 18:21:55Z colin $")
public class FuturePositionKeyComparator
        extends InstrumentPositionKeyComparator<Future>
{
    /**
     * Creates an instance.
     */
    public FuturePositionKeyComparator()
    {
        super(Future.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.InstrumentPositionKeyComparator#getRank()
     */
    @Override
    public int getRank()
    {
        return 300;
    }
    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    @Override
    public int compare(Future inO1,
                       Future inO2)
    {
        return new CompareToBuilder().append(inO1.getSymbol(),
                                             inO2.getSymbol())
                                     .append(inO1.getExpirationMonth(),
                                             inO2.getExpirationMonth())
                                     .append(inO1.getExpirationYear(),
                                             inO2.getExpirationYear()).toComparison();
    }
}