package org.marketcetera.core.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.api.attributes.ClassVersion;

/**
 * Translates between the specified external data type <code>T</code> and {@link MarketDataRequest} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DataRequestTranslator.java 16063 2012-01-31 18:21:55Z colin $
 * @since 0.5.0
 */
@ClassVersion("$Id: DataRequestTranslator.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface DataRequestTranslator<T>
{
    /**
     * Translate from a <code>DataRequest</code> to an external data format. 
     *
     * @param inRequest a <code>MarketDataRequest</code> value
     * @return a <code>T</code> value
     * @throws IllegalArgumentException if the message type is not handled by the translator
     * @throws CoreException if an error occurs during otherwise valid message translation 
     */
    public T fromDataRequest(MarketDataRequest inRequest)
        throws CoreException;
}
