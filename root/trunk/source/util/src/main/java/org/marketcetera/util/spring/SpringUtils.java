package org.marketcetera.util.spring;

import org.marketcetera.core.ClassVersion;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Utilities for the Spring framework.
 *
 * @author tlerios
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
     **/

    public static void addStringBean
        (GenericApplicationContext context,
         String name,
         String value)
    {
        RootBeanDefinition bean=new RootBeanDefinition(String.class);
        ConstructorArgumentValues values=new ConstructorArgumentValues();
        values.addGenericArgumentValue(value);
        bean.setConstructorArgumentValues(values);
        context.registerBeanDefinition(name,bean);
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private SpringUtils() {}
}
