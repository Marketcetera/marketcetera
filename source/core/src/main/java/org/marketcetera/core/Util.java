package org.marketcetera.core;

import java.net.URL;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

/**
 * Collection of random utilities
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
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
        if((inFileName == null) || ("".equals(inFileName)))  return null;

        URL resource = inObj.getClass().getResource(inFileName);
        if((resource == null) && !inFileName.startsWith("/")) {
            resource = inObj.getClass().getResource("/"+inFileName);
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

}
