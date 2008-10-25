package org.marketcetera.marketdata;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
public final class SecurityListRequest
        extends DataRequest
{
    public static SecurityListRequest newSecurityListRequest()
    {
        return new SecurityListRequest(counter.incrementAndGet());
    }
    /**
     * Create a new SecurityListRequest instance.
     *
     * @param inId
     */
    private SecurityListRequest(long inId)
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
