package org.marketcetera.marketdata.bogus;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.marketdata.DataRequest;
import org.marketcetera.marketdata.DataRequestTranslator;
import org.marketcetera.marketdata.MarketDataRequest;

/* $License$ */

/**
 * Bogus feed implementation of {@link DataRequestTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedMessageTranslator.java 9456 2008-07-31 22:28:30Z klim $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusFeedMessageTranslator.java 9456 2008-07-31 22:28:30Z klim $") //$NON-NLS-1$
public class BogusFeedMessageTranslator
    implements DataRequestTranslator<BogusMessage>
{
    /**
     * static instance
     */
    private static final BogusFeedMessageTranslator sInstance = new BogusFeedMessageTranslator();
    /**
     * Gets a <code>BogusFeedMessageTranslator</code> instance.
     * 
     * @return a <code>BogusFeedMessageTranslator</code> value
     */
    static BogusFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    /**
     * Create a new BogusFeedMessageTranslator instance.
     *
     */
    private BogusFeedMessageTranslator()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#asDataRequest(java.lang.Object)
     */
    @Override
    public DataRequest toDataRequest(BogusMessage inData)
            throws CoreException
    {
        return inData.getAsDataRequest();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.marketdata.DataRequestTranslator#translate(org.marketcetera.marketdata.DataRequest)
     */
    @Override
    public BogusMessage fromDataRequest(DataRequest inRequest)
            throws CoreException
    {
        if(inRequest instanceof MarketDataRequest) {
            BogusMessage message = new BogusMessage(inRequest);
            for(String symbol : ((MarketDataRequest)inRequest).getSymbols()) {
                message.addSymbol(symbol);
            }
            return message;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
