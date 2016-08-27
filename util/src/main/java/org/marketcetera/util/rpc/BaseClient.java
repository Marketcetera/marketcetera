package org.marketcetera.util.rpc;

/* $License$ */

/**
 * Base client implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BaseClient
{
    /**
     * 
     *
     *
     * @throws Exception
     */
    void start()
            throws Exception;
    /**
     * 
     *
     *
     * @throws Exception
     */
    void stop()
            throws Exception;
    /**
     * 
     *
     *
     * @return
     */
    boolean isRunning();
}
