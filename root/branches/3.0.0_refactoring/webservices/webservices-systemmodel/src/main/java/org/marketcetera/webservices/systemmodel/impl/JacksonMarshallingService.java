package org.marketcetera.webservices.systemmodel.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.marketcetera.webservices.systemmodel.JsonMarshallingService;

/* $License$ */

/**
 * Provides JSON marshalling services using Jackson.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class JacksonMarshallingService
        implements JsonMarshallingService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.JsonMarshallingService#writeValueAsString(java.lang.Object)
     */
    @Override
    public String marshal(Object inValue)
    {
        try {
            return mapper.writeValueAsString(inValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.JsonMarshallingService#readValueFrom(java.lang.String, java.lang.Class)
     */
    @Override
    public <Clazz> Clazz unmarshal(String inStringValue,
                                   Class<Clazz> inType)
    {
        try {
            return mapper.readValue(inStringValue,
                                    inType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * thread-safe common marshalling/unmarshalling provider
     */
    private static final ObjectMapper mapper = new ObjectMapper();
}
