package org.marketcetera.ui.service;

/* $License$ */

/**
 * Provides information and services for connecting to MATP servers.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ServerConnectionService
{
    /**
     * get the server connection data value.
     *
     * @return a <code>ServerConnectionData</code> value
     */
    ServerConnectionData getConnectionData();
    /**
     * Set the server connection data value.
     *
     * @param inServerConnectionData a <code>ServerConnectionData</code> value
     */
    void setConnectionData(ServerConnectionData inServerConnectionData);
    /**
     * Provides the connection data.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public interface ServerConnectionData
    {
        /**
         * Get the server hostname or IP address value.
         *
         * @return a <code>String</code> value
         */
        String getHostname();
        /**
         * Set the server hostname or IP address value.
         *
         * @param inHostname a <code>String</code> value
         */
        void setHostname(String inHostname);
        /**
         * Get the server port value.
         *
         * @return an <code>int</code> value
         */
        int getPort();
        /**
         * Set the server port value.
         *
         * @param inPort an <code>int</code> value
         */
        void setPort(int inPort);
        /**
         * Indicates whether to use SSL or not.
         *
         * @return a <code>boolean</code> value
         */
        boolean useSsl();
        /**
         * Sets the use SSL value.
         *
         * @param inUseSsl a <code>boolean</code> value
         */
        void setUseSsl(boolean inUseSsl);
    }
}
