package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

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
public class ApplicationVersion {
    /**
     * Returns the application version number.
     *
     * @return the application version number.
     */
    public static String getVersion() {
        return getProperty("VersionNumber", DEFAULT_VERSION);  //$NON-NLS-1$
    }

    /**
     * Returns the applicatino build number.
     *
     * @return the build number.
     */
    public static String getBuildNumber() {
        return getProperty("BuildNumber", DEFAULT_BUILD); //$NON-NLS-1$
    }

    /**
     * Returns the property value from the version properties instance.
     *
     * @param inName the property name
     * @param inDefaultValue the default property value
     *
     * @return the property value.
     */
    private static String getProperty(String inName, String inDefaultValue) {
        return getProperties().getProperty(inName, inDefaultValue);
    }

    /**
     * Returns the properties instance containing version
     * and build information.
     *
     * @return properties instance.
     */
    private static Properties getProperties() {
        if(sProperties == null) {
            Properties p = new Properties();
            try {
                InputStream stream = ApplicationVersion.class.
                        getResourceAsStream("/META-INF/metc_version.properties");  //$NON-NLS-1$
                if (stream != null) {
                    p.load(stream);
                    stream.close();
                }
            } catch (IOException e) {
                Messages.ERROR_FETCHING_VERSION_PROPERTIES.warn(
                        ApplicationVersion.class, e);
            }
            sProperties = p;
        }
        return sProperties;
    }
    private static Properties sProperties;
    /**
     * No instances of this class can exist.
     */
    private ApplicationVersion() {
    }

    public static final String DEFAULT_VERSION = "No Version";   //$NON-NLS-1$
    static final String DEFAULT_BUILD = "No Build";   //$NON-NLS-1$
}
