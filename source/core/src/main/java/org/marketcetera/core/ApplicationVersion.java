package org.marketcetera.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * A class that is used to keep track of the application version
 * and build number.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ApplicationVersion
{
    /**
     * Returns the application version number.
     *
     * @return a <code>String</code> value containing the application version number
     */
    public static String getVersion()
    {
        return getProperty("VersionNumber", //$NON-NLS-1$
                           DEFAULT_VERSION,
                           ApplicationVersion.class);
    }
    /**
     * Returns the application version number.
     *
     * @param inResourceClass a <code>Class&lt;?&gt;</code> value
     * @return a <code>String</code> value containing the application version number
     */
    public static String getVersion(Class<?> inResourceClass)
    {
        return getProperty("VersionNumber", //$NON-NLS-1$
                           DEFAULT_VERSION,
                           inResourceClass);
    }
    /**
     * Returns the applicatino build number.
     *
     * @return a <code>String</code> value containing the application build number
     */
    public static String getBuildNumber()
    {
        return getProperty("BuildNumber", //$NON-NLS-1$
                           DEFAULT_BUILD,
                           ApplicationVersion.class);
    }
    /**
     * Gets the build number using the given class as a resource source.
     *
     * @param inResourceClass a <code>Class&lt;?&gt;</code> value
     * @return a <code>String</code> value
     */
    public static String getBuildNumber(Class<?> inResourceClass)
    {
        return getProperty("BuildNumber", //$NON-NLS-1$
                           DEFAULT_BUILD,
                           inResourceClass);
    }
    /**
     * Returns the property value from the version properties instance.
     *
     * @param inName a <code>String</code> value containing the property name
     * @param inDefaultValue a <code>String</code> value containing the default property value
     * @param inResourceClass a <code>Class&lt;?&gt;</code> value containing the resource owning class
     * @return a <code>String</code> value containing the property value.
     */
    private static String getProperty(String inName,
                                      String inDefaultValue,
                                      Class<?> inResourceClass)
    {
        return getProperties(inResourceClass).getProperty(inName,
                                                          inDefaultValue);
    }
    /**
     * Returns the properties instance containing version
     * and build information.
     *
     * @param inResourceClass a <code>Class&lt;?&gt;</code> value containing the resource owning class
     * @return properties instance.
     */
    private static Properties getProperties(Class<?> inResourceClass)
    {
        synchronized(properties) {
            Properties p = properties.get(inResourceClass.getName());
            if(p == null) {
                p = new Properties();
                properties.put(inResourceClass.getName(),
                               p);
                try {
                    InputStream stream = inResourceClass.getResourceAsStream("/META-INF/metc_version.properties");  //$NON-NLS-1$
                    if(stream != null) {
                        p.load(stream);
                        stream.close();
                    }
                } catch(IOException e) {
                    Messages.ERROR_FETCHING_VERSION_PROPERTIES.warn(ApplicationVersion.class,
                                                                    e);
                }
            }
            return p;
        }
    }
    /**
     * properties by owning resource class
     */
    private static final Map<String,Properties> properties = new HashMap<String,Properties>();
    /**
     * No instances of this class can exist.
     */
    private ApplicationVersion() {
    }

    static final String DEFAULT_BUILD = "No Build";   //$NON-NLS-1$

    public static final String DEFAULT_VERSION =
        "No Version"; //$NON-NLS-1$
    public static final String VERSION_1_5_0 =
        "1.5.0"; //$NON-NLS-1$
    public static final String VERSION_1_5_1 =
        "1.5.1"; //$NON-NLS-1$
    public static final String VERSION_1_6_0 =
        "1.6.0"; //$NON-NLS-1$
    public static final String VERSION_2_0_0 =
        "2.0.0"; //$NON-NLS-1$
    public static final String VERSION_2_1_0 =
        "2.1.0"; //$NON-NLS-1$
    public static final String VERSION_2_1_1 = "2.1.1"; //$NON-NLS-1$
    public static final String VERSION_2_1_2 = "2.1.2"; //$NON-NLS-1$
    public static final String VERSION_2_1_3 = "2.1.3"; //$NON-NLS-1$
    public static final String VERSION_2_1_4 = "2.1.4"; //$NON-NLS-1$
}
