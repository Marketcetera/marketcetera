package org.marketcetera.marketdata;

import java.io.IOException;
import java.util.Properties;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * A request for a security list from a market data provider.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public final class SecurityListRequest
        extends DataRequest
{
    /**
     * the request type string for <code>SecurityListRequest</code> objects
     */
    public static final String TYPE = "securitylist"; //$NON-NLS-1$
    /**
     * Constructs a new <code>SecurityListRequest</code> object.
     *
     * @return a <code>SecurityListRequest</code> value
     */
    public static SecurityListRequest newSecurityListRequest()
    {
        Properties values = new Properties();
        values.setProperty(TYPE_KEY,
                           TYPE);
        return new SecurityListRequest(values);
    }
    /**
     * does class-level initialization for <code>SecurityListRequest</code>
     */
    static
    {
        DataRequest.registerType(TYPE,
                                 SecurityListRequest.class);
    }
    /**
     * Creates a security list request from the given string.
     * 
     * <p>Note that the subcomponents of the request string themselves are allowed to contain neither
     * the {@link #KEY_VALUE_DELIMITER} nor the {@link MarketDataRequest#SYMBOL_DELIMITER}.  If a subcomponent contains the
     * <code>KEY_VALUE_DELIMITER</code>, that subcomponent will be <b>truncated</b> at the first occurrence of the delimiter.
     * If a subcomponent contains the <code>SYMBOL_DELIMITER</code>, an <code>IllegalArgumentException</code> will be thrown.  
     *
     * @param inRequestString a <code>String</code> value
     * @return a <code>MarketDataRequest</code> value
     * @throws IOException if the <code>String</code> could not be converted to a <code>SecurityListRequest</code>
     * @throws IllegalArgumentException if <code>inRequestString</code> cannot be parsed properly
     */
    protected static SecurityListRequest newRequestFromString(Properties inRequest)
    {
        validateAndSetRequestDefaultsIfNecessary(inRequest);
        return new SecurityListRequest(inRequest);
    }
    /**
     * Create a new SecurityListRequest instance.
     *
     * @param inId
     */
    private SecurityListRequest(Properties inRequest)
    {
        super(inRequest);
    }
}
