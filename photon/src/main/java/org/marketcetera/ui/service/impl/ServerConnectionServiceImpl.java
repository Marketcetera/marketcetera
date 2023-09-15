package org.marketcetera.ui.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.ui.service.ServerConnectionService;
import org.marketcetera.util.log.SLF4JLoggerProxy;
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
            throws IOException
    {
        hostname = inServerConnectionData.getHostname();
        port = inServerConnectionData.getPort();
        useSsl = inServerConnectionData.useSsl();
        updateConfigFile();
    }
    /**
     * Update the config file with the new settings.
     *
     * @throws IOException if the config file could not be updated
     */
    private void updateConfigFile()
            throws IOException
    {
        SLF4JLoggerProxy.info(this,
                              "Updating config file with: {}:{} SSL: {}",
                              hostname,
                              port,
                              useSsl);
        File configFile = new File("conf/application.properties");
        StringBuilder newConfigFileContents = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line = reader.readLine();
            while(line != null) {
                String lineToRead = StringUtils.trim(line);
                if(lineToRead.startsWith("host.name=")) {
                    newConfigFileContents.append("host.name=").append(hostname).append(System.lineSeparator());
                } else if(lineToRead.startsWith("host.port=")) {
                    newConfigFileContents.append("host.port=").append(port).append(System.lineSeparator());
                } else if(lineToRead.startsWith("metc.security.use.ssl=")) {
                    newConfigFileContents.append("metc.security.use.ssl=").append(useSsl).append(System.lineSeparator());
                } else {
                    newConfigFileContents.append(line).append(System.lineSeparator());
                }
                line = reader.readLine();
            }
        }
        try (FileWriter writer = new FileWriter(configFile)) {
            writer.write(newConfigFileContents.toString());
        }
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
