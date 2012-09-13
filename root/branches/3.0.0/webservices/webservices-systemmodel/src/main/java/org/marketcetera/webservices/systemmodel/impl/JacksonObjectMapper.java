package org.marketcetera.webservices.systemmodel.impl;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

/* $License$ */

/**
 * Provides object mapping services for the webservices package for the Jackson JSON libraries.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JacksonObjectMapper
        extends ObjectMapper
{
    /**
     * Create a new JacksonObjectMapper instance.
     */
    public JacksonObjectMapper()
    {
        super.configure(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE,
                        true);
    }
}
