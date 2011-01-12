package org.marketcetera.util.auth;

import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * A setter that obtains holder data via Spring configuration
 * properties. It is supported by a {@link SpringContext}. It uses a
 * proxy bean to read the (final) value of a property from one (or
 * more overriding) properties files.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class SpringSetter<T extends Holder<?>>
    extends Setter<T>
{

    // CLASS DATA.

    private static final String PROPERTY_PROXY_BEAN_NAME_PREFIX=
        SpringSetter.class.getName()+".propertyProxy"; //$NON-NLS-1$


    // INSTANCE DATA.

    private String mPropertyName;
    private String mBeanName;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor. The property
     * whose value is assigned to the holder data has the given name.
     *
     * @param propertyName The property name.
     *
     * @see Setter#Setter(Holder,I18NBoundMessage)
     */

    public SpringSetter
        (T holder,
         I18NBoundMessage usage,
         String propertyName)
    {
        super(holder,usage);
        mPropertyName=propertyName;
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's property name.
     *
     * @return The property name.
     */

    public String getPropertyName()
    {
        return mPropertyName;
    }

    /**
     * Returns the receiver's property value.
     *
     * @param context The context hosting the receiver's proxy bean.
     *
     * @return The property value.
     */

    public Object getPropertyValue
        (GenericApplicationContext context)
    {
        return context.getBean(mBeanName);
    }

    /**
     * Injects the receiver's proxy bean into the given context. The
     * injected bean is assigned a unique name based on the given
     * index. The given properties are augmented with a default value
     * (empty string, that is, do-not-touch-holder) for the receiver's
     * property.
     *
     * @param context The context.
     * @param properties The properties.
     * @param index The index.
     */

    public void setup
        (GenericApplicationContext context,
         Properties properties,
         int index)
    {
        mBeanName=PROPERTY_PROXY_BEAN_NAME_PREFIX+index;
        SpringUtils.addStringBean
            (context,mBeanName,
             "${"+getPropertyName()+"}"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.put(getPropertyName(),StringUtils.EMPTY);
    }

    /**
     * Sets the holder's data by obtaining it from the given context
     * via the receiver's proxy bean. This method is called by a
     * {@link SpringContext}.
     *
     * @param context The context.
     */

    public abstract void setValue
        (GenericApplicationContext context);
}
