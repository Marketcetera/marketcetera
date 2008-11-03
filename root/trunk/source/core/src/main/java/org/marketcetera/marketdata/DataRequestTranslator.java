package org.marketcetera.marketdata;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * Translates between the specified external data type <code>T</code> and {@link DataRequest} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
    public T fromDataRequest(DataRequest inRequest)
        throws CoreException;
    /**
     * Translate from an external data type to <code>DataRequest</code> format.
     *
     * @param inData an <code>T</code> value
     * @return a <code>DataRequest</code> value
     * @throws CoreException if the message cannot be translated
     */
    public DataRequest toDataRequest(T inData)
        throws CoreException;
}
