package org.marketcetera.util.auth;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.context.support.GenericApplicationContext;

/**
 * A setter for a string holder that obtains the data via Spring
 * configuration properties.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
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
