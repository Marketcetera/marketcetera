package org.marketcetera.security;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.api.attributes.ClassVersion;
import org.springframework.context.support.GenericApplicationContext;

/**
 * A setter for a string holder that obtains the data via Spring
 * configuration properties.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: SpringSetterString.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: SpringSetterString.java 82324 2012-04-09 20:56:08Z colin $")
public class SpringSetterString
    extends SpringSetter<Holder<String>>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see SpringSetter#SpringSetter(Holder,I18NBoundMessage,String)
     */

    public SpringSetterString
        (Holder<String> holder,
         I18NBoundMessage usage,
         String propertyName)
    {
        super(holder,usage,propertyName);
    }


    // SpringSetter.

    @Override
    public void setValue
        (GenericApplicationContext context)
    {
        String value=(String)getPropertyValue(context);
        Holder<String> holder=getHolder();
        if (!StringUtils.EMPTY.equals(value)) {
            holder.setValue(value);
        }
    }
}
