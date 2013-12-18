package org.marketcetera.util.auth;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.BeanFactory;

/**
 * A setter for a character array holder that obtains the data via
 * Spring configuration properties.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringSetterCharArray
    extends SpringSetter<Holder<char[]>>
{

    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor.
     *
     * @see SpringSetter#SpringSetter(Holder,I18NBoundMessage,String)
     */

    public SpringSetterCharArray
        (Holder<char[]> holder,
         I18NBoundMessage usage,
         String propertyName)
    {
        super(holder,usage,propertyName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.auth.SpringSetter#setValue(org.springframework.beans.factory.BeanFactory)
     */
    @Override
    public void setValue(BeanFactory inContext)
    {
        String value=(String)getPropertyValue(inContext);
        Holder<char[]> holder=getHolder();
        if (!StringUtils.EMPTY.equals(value)) {
            holder.setValue(value.toCharArray());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.auth.SpringSetter#setValue(java.lang.String)
     */
    @Override
    public void setValue(String inValue)
    {
        Holder<char[]> holder=getHolder();
        if(inValue != null && !StringUtils.EMPTY.equals(inValue)) {
            holder.setValue(inValue.toCharArray());
        }
    }
}
