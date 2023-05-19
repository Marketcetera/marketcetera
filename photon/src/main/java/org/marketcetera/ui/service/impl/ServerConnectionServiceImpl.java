package org.marketcetera.ui.service.impl;

import org.marketcetera.ui.service.ServerConnectionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides a {@link ServerConnectionService} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@EnableAutoConfiguration
public class ServerConnectionServiceImpl
        implements ServerConnectionService
{
    /* (non-Javadoc)
     * @see org.marketcetera.ui.service.ServerConnectionService#getConnectionData()
     */
    @Override
    public ServerConnectionData getConnectionData()
    {
        return new ServerConnectionDataImpl(hostname,
                                            port,
                                            useSsl);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ui.service.ServerConnectionService#setConnectionData(org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData)
     */
    @Override
    public void setConnectionData(ServerConnectionData inServerConnectionData)
    {
        hostname = inServerConnectionData.getHostname();
        port = inServerConnectionData.getPort();
        useSsl = inServerConnectionData.useSsl();
        // TODO update config file
    }
    /**
     * Provides a {@link ServerConnectionData} implementation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class ServerConnectionDataImpl
            implements ServerConnectionData
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("ServerConnectionDataImpl [hostname=").append(hostname).append(", port=").append(port)
                    .append(", useSsl=").append(useSsl).append("]");
            return builder.toString();
        }
        /**
         * Create a new ServerConnectionDataImpl instance.
         *
         * @param inHostname a <code>String</code> value
         * @param inPort an <code>int</code> value
         * @param inUseSsl a <code>boolean</code> value
         */
        private ServerConnectionDataImpl(String inHostname,
                                         int inPort,
                                         boolean inUseSsl)
        {
            hostname = inHostname;
            port = inPort;
            useSsl = inUseSsl;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#getHostname()
         */
        @Override
        public String getHostname()
        {
            return hostname;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#setHostname(java.lang.String)
         */
        @Override
        public void setHostname(String inHostname)
        {
            hostname = inHostname;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#getPort()
         */
        @Override
        public int getPort()
        {
            return port;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#setPort(int)
         */
        @Override
        public void setPort(int inPort)
        {
            port = inPort;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#useSsl()
         */
        @Override
        public boolean useSsl()
        {
            return useSsl;
        }
        /* (non-Javadoc)
         * @see org.marketcetera.ui.service.ServerConnectionService.ServerConnectionData#setUseSsl(boolean)
         */
        @Override
        public void setUseSsl(boolean inUseSsl)
        {
            useSsl = inUseSsl;
        }
        /**
         * hostname value
         */
        private String hostname;
        /**
         * port value
         */
        private int port;
        /**
         * use SSL value
         */
        private boolean useSsl;
    }
    /**
     * indicates whether to use SSL or not
     */
    @Value("${metc.security.use.ssl:false}")
    private boolean useSsl;
    /**
     * hostname to connect to
     */
    @Value("${host.name}")
    private String hostname;
    /**
     * port to connect to
     */
    @Value("${host.port}")
    private int port;
}
