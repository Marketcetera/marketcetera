package org.marketcetera.event;

import org.marketcetera.util.ws.wrappers.RemoteProperties;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface LogEvent
    extends Event
{
    /**
     * 
     *
     *
     * @return
     */
    public LogEventLevel getLevel();
    /**
     * 
     *
     *
     * @return
     */
    public Throwable getException();
    /**
     * 
     *
     *
     * @return
     */
    public RemoteProperties getRemoteProperties();
    /**
     * 
     *
     *
     * @return
     */
    public String getMessage();
}
