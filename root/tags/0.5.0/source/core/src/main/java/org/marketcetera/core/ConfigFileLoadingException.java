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
        super(MessageKey.CONFIG_FILE_OPEN.getLocalizedMessage(inFile));
    }

    public ConfigFileLoadingException(String inFile, Throwable nested)
    {
        super(MessageKey.CONFIG_FILE_OPEN.getLocalizedMessage(inFile), nested);
    }
}
