package org.marketcetera.trade;

import javax.annotation.concurrent.Immutable;

/* $License$ */

/**
 * Represents a spread between two {@link Future} instruments.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
public class Spread
        extends Instrument
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSymbol()
     */
    @Override
    public String getSymbol()
    {
        if(symbol != null) {
            return symbol;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(leg1.getSymbol()).append('-').append(leg2.getSymbol());
        symbol = builder.toString();
        return symbol;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getSecurityType()
     */
    @Override
    public SecurityType getSecurityType()
    {
        return SecurityType.Future;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Instrument#getFullSymbol()
     */
    @Override
    public String getFullSymbol()
    {
        if(fullSymbol != null) {
            return fullSymbol;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(leg1.getFullSymbol()).append('-').append(leg2.getFullSymbol());
        fullSymbol = builder.toString();
        return fullSymbol;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Spread [").append(getFullSymbol()).append("]");
        return builder.toString();
    }
    /**
     * Get the leg1 value.
     *
     * @return a <code>Future</code> value
     */
    public Future getLeg1()
    {
        return leg1;
    }
    /**
     * Get the leg2 value.
     *
     * @return a <code>Future</code> value
     */
    public Future getLeg2()
    {
        return leg2;
    }
    /**
     * Create a new Spread instance.
     *
     * @param inLeg1 a <code>Future</code> value
     * @param inLeg2 a <code>Future</code> value
     */
    public Spread(Future inLeg1,
                  Future inLeg2)
    {
        leg1 = inLeg1;
        leg2 = inLeg2;
    }
    /**
     * symbol value
     */
    private volatile String symbol;
    /**
     * full symbol value
     */
    private volatile String fullSymbol;
    /**
     * leg1 value
     */
    private final Future leg1;
    /**
     * leg2 value
     */
    private final Future leg2;
    private static final long serialVersionUID = -657756495750544387L;
}
