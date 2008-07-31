package org.marketcetera.core;

import org.marketcetera.util.log.I18NBoundMessage;

/**
 * Thrown when applications can't read their respective config files
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ConfigFileLoadingException extends CoreException
{
    public ConfigFileLoadingException(Throwable inNested)
    {
        super(inNested);
    }

    public ConfigFileLoadingException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }

    public ConfigFileLoadingException(Throwable inNested, I18NBoundMessage inMessage)
    {
        super(inNested, inMessage);
    }
}
