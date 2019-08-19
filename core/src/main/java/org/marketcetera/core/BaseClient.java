package org.marketcetera.core;

import java.io.Closeable;
import java.io.IOException;

/* $License$ */

/**
 * Base client implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface BaseClient
        extends Closeable
{
    /**
     * Start the client and connect to the server.
     *
     * @throws Exception if an error occurs starting the client
     */
    void start()
            throws Exception;
    /**
     * Stop the client and disconnect from the server.
     *
     * @throws Exception if an error occurs stopping the client
     */
    void stop()
            throws Exception;
    /**
     * Indicate if the client is running and connected to the server.
     *
     * @return a <code>boolean</code> value
     */
    boolean isRunning();
    /* (non-Javadoc)
     * @see java.io.Closeable#close()
     */
    @Override
    default void close()
            throws IOException
    {
        try {
            stop();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
