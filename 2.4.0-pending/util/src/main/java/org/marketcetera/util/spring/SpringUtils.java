package org.marketcetera.util.spring;

import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Utilities for the Spring framework.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class SpringUtils
{

    // CLASS METHODS.

    /**
     * Creates a string bean in the given application context. The new
     * bean has the given name and contains the given text value.
     *
     * @param context The context.
     * @param name The bean name.
     * @param value The text value.
     */

    public static void addStringBean
        (GenericApplicationContext context,
         String name,
         String value)
    {
        RootBeanDefinition bean=new RootBeanDefinition(String.class);
        bean.setResourceDescription(SpringUtils.class.getName());
        ConstructorArgumentValues values=new ConstructorArgumentValues();
        values.addGenericArgumentValue(value);
        bean.setConstructorArgumentValues(values);
        context.registerBeanDefinition(name,bean);
    }

    /**
     * Creates a preferences configurer bean in the given application
     * context. The new bean has the given name and refers to another
     * bean with another given name for the properties' locations.
     *
     * @param context The context.
     * @param configurerName The bean name.
     * @param locationRef The name of the bean whose value is the
     * properties' locations (can be a string or a list).
     */

    public static void addPropertiesConfigurer
        (GenericApplicationContext context,
         String configurerName,
         String locationRef)
    {
        RootBeanDefinition bean=
            new RootBeanDefinition(PropertyPlaceholderConfigurer.class);
        bean.setResourceDescription(SpringUtils.class.getName());
        MutablePropertyValues values=new MutablePropertyValues();
        values.addPropertyValue
            ("locations",new RuntimeBeanReference(locationRef)); //$NON-NLS-1$
        bean.setPropertyValues(values);
        context.registerBeanDefinition(configurerName,bean);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private SpringUtils() {}
}
