package org.marketcetera.marketdata;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public class DerivativeSecurityListRequest
        extends DataRequest
{
    public static DerivativeSecurityListRequest newDerivativeSecurityListRequest()
    {
        return new DerivativeSecurityListRequest(counter.incrementAndGet());
    }
    /**
     * Create a new DerivativeSecurityListRequest instance.
     *
     * @param inId
     */
    private DerivativeSecurityListRequest(long inId)
    {
        super(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequest#doEquals(java.lang.Object)
     */
    @Override
    protected boolean doEquals(Object inObj)
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequest#doHashCode()
     */
    @Override
    protected int doHashCode()
    {
        return 0;
    }
}
