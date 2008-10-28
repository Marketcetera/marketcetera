package org.marketcetera.marketdata;

import java.io.IOException;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * A request for a derivative security list from a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id:$
 * @since $Release$
 */
@ClassVersion("$Id:$") //$NON-NLS-1$
public class DerivativeSecurityListRequest
        extends DataRequest
{
    public static final String TYPE = "securitylist"; //$NON-NLS-1$
    static
    {
        DataRequest.registerType(TYPE,
                                 DerivativeSecurityListRequest.class);
    }
    /**
     * Creates a derivative security list request from the given string.
     * 
     * @param inRequestString a <code>String</code> value
     * @return a <code>DerivativeSecurityListRequest</code> value
     * @throws IOException if the <code>String</code> could not be converted to a <code>DerivativeSecurityListRequest</code>
     * @throws IllegalArgumentException if <code>inRequestString</code> cannot be parsed properly
     */
    protected static DerivativeSecurityListRequest newRequestFromString(Properties inRequest)
    {
        return new DerivativeSecurityListRequest(inRequest);
    }
    /**
     * Create a new DerivativeSecurityListRequest instance.
     *
     * @param inId
     */
    private DerivativeSecurityListRequest(Properties inRequest)
    {
        super(inRequest);
    }
    /**
     *
     *
     * @return
     */
    public static DataRequest newDerivativeSecurityListRequest()
    {
        return new DerivativeSecurityListRequest(null);
    }
}
