package org.marketcetera.webservices.systemmodel.impl;

import org.marketcetera.webservices.systemmodel.JsonMarshallingService;

/* $License$ */

/**
 * Provides JSON marshilling services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JsonMarshallingProvider
{
    /**
     * Gets the instance to use.
     *
     * @return a <code>JsonMarshallingProvider</code> value
     */
    public static JsonMarshallingProvider getInstance()
    {
        return instance;
    }
    /**
     * Create a new JsonMarshallingProvider instance.
     */
    public JsonMarshallingProvider()
    {
        instance = this;
    }
    /**
     * Get the service value.
     *
     * @return a <code>JsonMarshallingService</code> value
     */
    public JsonMarshallingService getService()
    {
        return service;
    }
    /**
     * Sets the service value.
     *
     * @param inService a <code>JsonMarshallingService</code> value
     */
    public static void setService(JsonMarshallingService inService)
    {
        service = inService;
    }
    // TODO this is stubbed in to allow DI when the providers are broken out into separate bundles
    private static JsonMarshallingProvider instance = new JsonMarshallingProvider();
    private static JsonMarshallingService service = new JacksonMarshallingService();
}
