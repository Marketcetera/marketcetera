package org.marketcetera.util.auth;

import java.util.Properties;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.spring.SpringUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * A context for Spring setters ({@link SpringSetter}). This context
 * provides a Spring context, which is partially created via a Spring
 * <i>configuration file</i> that instantiates a <i>properties file
 * bean</i> like this one:
 *
 * <pre>
 * &lt;bean
 *  id="propertiesFiles"
 *  class="java.lang.String"&gt;
 *   &lt;constructor-arg
 *    value="file:src/test/sample_data/auth/auth_user.properties" /&gt;
 * &lt;/bean&gt;
 * </pre>

 * or this one

 * <pre>
 * &lt;util:list id="propertiesFiles"&gt;
 *   &lt;value&gt;file:src/test/sample_data/auth/auth_pwd.properties&lt;/value&gt;
 * &lt;/util:list&gt;
 * </pre>
 *
 * The Spring configuration file and the name of the bean are given as
 * arguments to the constructors of this class. In both cases, one or
 * more properties files are thus identified. The newly created
 * context guides its setters to set holder data based on the property
 * values contained in these files: each {@link SpringSetter} is
 * associated with a property name, and sets its holder data to the
 * property value.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class SpringContext
    extends Context<SpringSetter<?>>
{

    // CLASS DATA.

    private static final String CONFIGURER_BEAN_NAME=
        SpringContext.class.getName()+".configurer"; //$NON-NLS-1$


    // INSTANCE DATA.

    private String mConfigLocation;
    private String mPropertiesFilesBean;


    // CONSTRUCTORS.

    /**
     * Constructor mirroring superclass constructor. The new context
     * will use the given Spring configuration file and properties
     * file bean.
     *
     * @param configLocation The location of the Spring configuration
     * file.
     * @param propertiesFilesBean The name of the properties file
     * bean.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public SpringContext
        (I18NBoundMessage name,
         boolean override,
         String configLocation,
         String propertiesFilesBean)
    {
        super(name,override);
        mConfigLocation=configLocation;
        mPropertiesFilesBean=propertiesFilesBean;
    }

    /**
     * Constructor mirroring superclass constructor. The context name
     * is set automatically to a default value. The new context will
     * use the given Spring configuration file and properties file
     * bean.
     *
     * @param configLocation The location of the Spring configuration
     * file.
     * @param propertiesFilesBean The name of the properties file
     * bean.
     *
     * @see Context#Context(I18NBoundMessage,boolean)
     */

    public SpringContext
        (boolean override,
         String configLocation,
         String propertiesFilesBean)
    {
        this(Messages.SPRING_NAME,override,
             configLocation,propertiesFilesBean);
    }


    // INSTANCE METHODS.

    /**
     * Returns the location of the Spring configuration file.
     *
     * @return The location.
     */

    public String getConfigLocation()
    {
        return mConfigLocation;
    }

    /**
     * Returns the name of the properties file bean.
     *
     * @return The name.
     */

    public String getPropertiesFilesBean()
    {
        return mPropertiesFilesBean;
    }


    // Context.

    @Override
    public void setValues()
    {
        GenericApplicationContext context=new GenericApplicationContext
            (new FileSystemXmlApplicationContext(getConfigLocation()));
        SpringUtils.addPropertiesConfigurer
            (context,CONFIGURER_BEAN_NAME,getPropertiesFilesBean());
        Properties properties=new Properties();
        int index=0;
        for (SpringSetter<?> setter:getSetters()) {
            if (shouldProcess(setter)) {
                setter.setup(context,properties,index++);
            }
        }
        ((PropertyPlaceholderConfigurer)context.getBean
         (CONFIGURER_BEAN_NAME)).setProperties(properties);
        context.refresh();
        for (SpringSetter<?> setter:getSetters()) {
            if (shouldProcess(setter)) {
                setter.setValue(context);
            }
        }
    }
}
