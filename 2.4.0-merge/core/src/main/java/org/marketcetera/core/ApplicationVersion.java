package org.marketcetera.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.DateTime;
import org.marketcetera.marketdata.DateUtils;
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
     * @return a <code>VersionInfo</code> value
     */
    public static VersionInfo getVersion()
    {
        String versionProperty = getProperty("VersionNumber", //$NON-NLS-1$
                                             DEFAULT_VERSION.getVersionInfo(),
                                             ApplicationVersion.class);
        if(VersionInfo.isValid(versionProperty)) {
            return new VersionInfo(versionProperty);
        }
        return VersionInfo.DEFAULT_VERSION;
    }
    /**
     * Returns the application version number.
     *
     * @param inResourceClass a <code>Class&lt;?&gt;</code> value
     * @return a <code>VersionInfo</code> value
     */
    public static VersionInfo getVersion(Class<?> inResourceClass)
    {
        String versionProperty = getProperty("VersionNumber", //$NON-NLS-1$
                                             DEFAULT_VERSION.getVersionInfo(),
                                             inResourceClass);
        if(VersionInfo.isValid(versionProperty)) {
            return new VersionInfo(versionProperty);
        }
        return VersionInfo.DEFAULT_VERSION;
    }
    /**
     * Returns the application build number.
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
            Properties propsForClass = properties.get(inResourceClass.getName());
            if(propsForClass == null) {
                propsForClass = new Properties();
                InputStream stream = null;
                try {
                    stream = inResourceClass.getResourceAsStream(PROPERTIES_FILENAME);
                    if(stream != null) {
                        propsForClass.load(stream);
                    }
                } catch(IOException e) {
                    Messages.ERROR_FETCHING_VERSION_PROPERTIES.warn(ApplicationVersion.class,
                                                                    e);
                } finally {
                    if(stream != null) {
                        try {
                            stream.close();
                        } catch (IOException ignored) {}
                    }
                }
                properties.put(inResourceClass.getName(),
                               propsForClass);
                setBuildNumber(propsForClass);
                setVersionNumber(propsForClass);
            }
            return propsForClass;
        }
    }
    /**
     * Sets a more informative version number value into the given properties.
     *
     * @param inProperties a <code>Properties</code> value
     */
    private static void setVersionNumber(Properties inProperties)
    {
        StringBuilder versionNumber = new StringBuilder();
        versionNumber.append(inProperties.getProperty(VERSION_NUMBER,DEFAULT_BUILD));
        inProperties.put(VERSION_NUMBER,
                         versionNumber.toString());
    }
    /**
     * Sets a more informative build number value into the given properties.
     *
     * @param inProperties a <code>Properties</code> value
     */
    private static void setBuildNumber(Properties inProperties)
    {
        StringBuilder buildNumber = new StringBuilder();
        buildNumber.append(inProperties.getProperty(BUILD_NUMBER,DEFAULT_BUILD))
                   .append(' ').append(inProperties.getProperty(REVISION,DEFAULT_REVISION)).append(' ').append(DateUtils.MILLIS_WITH_TZ.print(new DateTime()));
        inProperties.put(BUILD_NUMBER,
                         buildNumber.toString());
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
    /**
     * indicates the filename in the classpath that holds the build values
     */
    private static final String PROPERTIES_FILENAME = "/META-INF/metc_version.properties";   //$NON-NLS-1$
    /**
     * indicates the version number property to read from the classpath filename
     */
    private static final String VERSION_NUMBER = "VersionNumber";   //$NON-NLS-1$
    /**
     * indicates the build number property to read from the classpath filename
     */
    private static final String BUILD_NUMBER = "BuildNumber";   //$NON-NLS-1$
    /**
     * indicates the revision number from the source control system to read from the classpath filename
     */
    private static final String REVISION = "revision";   //$NON-NLS-1$
    /**
     * default build number to show if no build number is available
     */
    static final String DEFAULT_BUILD = "No Build";   //$NON-NLS-1$
    /**
     * revision number to show if no revision number is available
     */
    static final String DEFAULT_REVISION = "No Revision";   //$NON-NLS-1$
    /**
     * version number to show if version number is available
     */
    public static final VersionInfo DEFAULT_VERSION = VersionInfo.DEFAULT_VERSION;
}
