package org.marketcetera.core;

import java.net.URL;

/**
 * Collection of random utilities
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class Util
{
    public static final String EMPTY_ARR_STRING = "[empty]";

    public static <T> String getStringFromArray(T[] inArr)
    {
        return getStringFromArray(inArr, ",");
    }

    public static <T> String getStringFromArray(T[] inArr, String delim)
    {
        if(inArr == null) return EMPTY_ARR_STRING;

        int size = inArr.length;
        StringBuffer buf = new StringBuffer(512);
        for(int i=0; i < size; i++)
        {
            if(i!=0) buf.append(delim);
            buf.append(inArr[i]);
        }

        return(buf.toString());
    }

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
        if(resource == null) {
            if(!inFileName.startsWith("/")) {
                resource = inObj.getClass().getResource("/"+inFileName);
            }
        }
        return resource;
    }
}
