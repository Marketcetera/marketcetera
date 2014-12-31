package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;

/**
 * Test implementation of <code>AbstractMessageTranslator</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
public class MockDataRequestTranslator
        implements DataRequestTranslator<String>
{
    private static boolean sTranslateThrows = false;
    /**
     * Create a new TestMessageTranslator instance.
     *
     */
    public MockDataRequestTranslator()
    {
    }
    public static boolean getTranslateThrows()
    {
        return sTranslateThrows;
    }
    public static void setTranslateThrows(boolean inTranslateThrows)
    {
        sTranslateThrows = inTranslateThrows;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.module.DataRequest)
     */
    @Override
    public String fromDataRequest(MarketDataRequest inMessage)
            throws CoreException
    {
        if(getTranslateThrows()) {
            throw new NullPointerException("This exception is expected");
        }
        return inMessage.toString();
    }
}
