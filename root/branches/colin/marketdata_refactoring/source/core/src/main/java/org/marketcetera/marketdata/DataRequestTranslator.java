package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;

/**
 * Translates between the specified external data type <code>T</code> and {@link MarketDataRequest} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public interface DataRequestTranslator<T>
{
    /**
     * Translate from a <code>DataRequest</code> to an external data format. 
     *
     * @param inRequest a <code>DataRequest</code> value
     * @return a <code>T</code> value
     * @throws IllegalArgumentException if the message type is not handled by the translator
     * @throws CoreException if an error occurs during otherwise valid message translation 
     */
    public T translate(DataRequest inRequest)
        throws CoreException;
    
    /**
     * Translate from an external data type to <code>FIX</code> format.
     *
     * @param inData an <code>T</code> value
     * @return a <code>DataRequest</code> value
     * @throws CoreException if the message cannot be translated
     */
    public DataRequest asDataRequest(T inData)
        throws CoreException;
}
