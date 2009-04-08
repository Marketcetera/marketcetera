package org.marketcetera.core;

import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * Collection of random utilities
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class Util
{
    /** Tries to load the named file from a classpath
     * If the file name doesn't start with a leading / then
     * if we fail to load it we prepend the / and try again
     * @param inFileName
     * @param inObj some object to get the classloader from
     * @return The URL to the resource or NULL if it's not found
     */
    public static URL loadFileFromClasspath(String inFileName, Object inObj)
    {
        if((inFileName == null) || ("".equals(inFileName)))  return null; //$NON-NLS-1$

        URL resource = inObj.getClass().getResource(inFileName);
        if((resource == null) && !inFileName.startsWith("/")) { //$NON-NLS-1$
            resource = inObj.getClass().getResource("/"+inFileName); //$NON-NLS-1$
        }
        return resource;
    }

    /**
     * Reads the entire file and stuffs it into a StringBuffer and returns the string
     * The file is loaded from classpath.
     * Use wisely, this will choke on very large files.
     *
     * @param inFileName
     * @param inObj some object to get the classloader from
     * @return The entire contents of the file
     */
    public static String getStringFromFile(String inFileName, Object inObj) throws Exception
    {
        URL url = Util.loadFileFromClasspath(inFileName,  inObj);
        BufferedReader reader = new BufferedReader(new FileReader(new File(url.getPath())));
        String line = null;
        StringBuffer result = new StringBuffer(2000);
        while((line = reader.readLine()) != null) {
            result.append(line);
        }

        return result.toString();
    }
    /**
     * the delimiter used to distinguish key/value pairs in the string representation of properties 
     */
    public static final String KEY_VALUE_DELIMITER = ":"; //$NON-NLS-1$
    /**
     * the separator used to separate key/value pairs in the string representation of properties 
     */
    public static final String KEY_VALUE_SEPARATOR = "="; //$NON-NLS-1$
    /**
     * Creates a <code>Properties</code> object from the given <code>String</code>.
     *
     * <p>This function assumes that the <code>String</code> consists of a series of key/value pairs separated by
     * the {@link #KEY_VALUE_DELIMITER}.  The key/value pairs themselves are separated by the {@link #KEY_VALUE_SEPARATOR}.
     * Any malformed entries are discarded.  A best-effort will be made to retain as many key/value pairs as possible.
     * 
     * @param inCondensedProperties a <code>String</code> value
     * @return a <code>Properties</code> value or null if <code>inCondensedProperties</code> is null or empty
     */
    public static final Properties propertiesFromString(String inCondensedProperties)
    {
        if(inCondensedProperties == null ||
           inCondensedProperties.isEmpty()) {
            return null;
        }
        String[] statements = inCondensedProperties.split(KEY_VALUE_DELIMITER);
        Properties props = new Properties();
        // each statement should be "x=y" - we are going to assume this is the case
        for(String statement : statements) {
            String[] subStatements = statement.split(KEY_VALUE_SEPARATOR);
            if(subStatements != null &&
               subStatements.length == 2) {
                props.setProperty(subStatements[0],
                                  subStatements[1]);
            } else {
                SLF4JLoggerProxy.debug(Util.class,
                                       "Putative key/value \"{}\" discarded", //$NON-NLS-1$
                                       (subStatements == null ? "null" : Arrays.toString(subStatements))); //$NON-NLS-1$
            }
        }
        return props;
    }
    /**
     * Creates a <code>String</code> object from the given <code>Properties</code> object. 
     *
     * <p>This function returns a <code>String</code> containing a series of key/value pairs representing this object.
     * Each key/value pair is separated by the {@link #KEY_VALUE_DELIMITER}.  The pairs themselves are separated by
     * {@link #KEY_VALUE_SEPARATOR}.
     * 
     * <p>Note that if any of the keys or values of the <code>Properties</code> object contains either the
     * {@link #KEY_VALUE_DELIMITER} or the {@link #KEY_VALUE_SEPARATOR} character, the resulting String will
     * not be parseable with {@link propertiesFromString}.
     *
     * @param inProperties a <code>Properties</code> value
     * @return a <code>String</code> value or null if <code>inProperties</code> is null or empty
     */
    public static String propertiesToString(Properties inProperties)
    {
        if(inProperties == null ||
           inProperties.isEmpty()) {
            return null;
        }
        StringBuffer output = new StringBuffer();
        boolean delimiterNeeded = false;
        for(Object key : inProperties.keySet()) {
            if(delimiterNeeded) {
                output.append(KEY_VALUE_DELIMITER);
            } else {
                delimiterNeeded = true;
            }
            output.append(key).append(KEY_VALUE_SEPARATOR).append(inProperties.getProperty((String)key));
        }
        return output.toString();
    }
}
