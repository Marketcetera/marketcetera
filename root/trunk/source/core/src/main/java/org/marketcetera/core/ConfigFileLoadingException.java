package org.marketcetera.core;

/**
 * Thrown when applications can't read their respective config files
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class ConfigFileLoadingException extends MarketceteraException
{
    public ConfigFileLoadingException(String inFile)
    {
        super("Unable to open config file "+inFile);
    }

    public ConfigFileLoadingException(String inFile, Throwable nested)
    {
        super("Unable to open config file '"+inFile+"'", nested);
    }
}
