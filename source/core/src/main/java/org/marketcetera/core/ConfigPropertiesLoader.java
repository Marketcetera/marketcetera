package org.marketcetera.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Given a name of the resources, loads the related {@link java.util.ResourceBundle} and
 * gets the properties from it
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ConfigPropertiesLoader
{
    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USER_KEY = "db.user";
    public static final String DB_PASS_KEY = "db.pass";
    public static final String DB_SQL_DIALECT_KEY = "db.sql.dialect";
    public static final String DB_DRIVER_KEY = "db.driver";
    public static final String PROP_FILE_EXT = ".properties";

    /** Tries to create a properties object from the filename passed in.
     * The file can be either in the classpath, or be located in the "current working dir"
     * @param propFileName name of the file
     * @return  Properties read from the file
     * @throws ConfigFileLoadingException
     */
    public static Properties loadProperties(String propFileName)
        throws ConfigFileLoadingException
    {
        ResourceBundle rb = null;
        try {
            rb = ResourceBundle.getBundle(propFileName);
        } catch(MissingResourceException ex) {
            // try loading from local file dir
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(propFileName+PROP_FILE_EXT));
                return props;
            } catch(IOException ioex){
                throw new ConfigFileLoadingException(propFileName, ioex);
            }
        }
        Properties result = new Properties ();
        for (Enumeration keys = rb.getKeys (); keys.hasMoreElements ();)
        {
            final String key = (String) keys.nextElement ();
            final String value = rb.getString (key);

            result.put (key, value);
        }
        return result;
    }
}
