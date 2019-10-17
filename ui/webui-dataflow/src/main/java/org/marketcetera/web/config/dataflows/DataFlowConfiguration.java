package org.marketcetera.web.config.dataflows;

import java.util.List;

import javax.annotation.PostConstruct;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.collect.Lists;
import com.vaadin.spring.annotation.SpringComponent;

/* $License$ */

/**
 * Provides data flow configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringComponent
@EnableAutoConfiguration
@ConfigurationProperties(prefix="dataflow")
public class DataFlowConfiguration
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Data flow engine configuration: {}",
                              dataFlowEngineDescriptors);
    }
    /**
     * Get the dataFlowEngineDescriptors value.
     *
     * @return a <code>List&lt;DataFlowEngineDescriptor&gt;</code> value
     */
    public List<DataFlowEngineDescriptor> getEngineDescriptors()
    {
        return dataFlowEngineDescriptors;
    }
    /**
     * Sets the dataFlowEngineDescriptors value.
     *
     * @param inDataFlowEngineDescriptors a <code>List&lt;DataFlowEngineDescriptor&gt;</code> value
     */
    public void setEngineDescriptors(List<DataFlowEngineDescriptor> inDataFlowEngineDescriptors)
    {
        dataFlowEngineDescriptors = inDataFlowEngineDescriptors;
    }
    /**
     * Describes a connection to a data flow engine.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class DataFlowEngineDescriptor
    {
        /**
         * Get the hostname value.
         *
         * @return a <code>String</code> value
         */
        public String getHostname()
        {
            return hostname;
        }
        /**
         * Sets the hostname value.
         *
         * @param inHostname a <code>String</code> value
         */
        public void setHostname(String inHostname)
        {
            hostname = inHostname;
        }
        /**
         * Get the port value.
         *
         * @return an <code>int</code> value
         */
        public int getPort()
        {
            return port;
        }
        /**
         * Sets the port value.
         *
         * @param inPort an <code>int</code> value
         */
        public void setPort(int inPort)
        {
            port = inPort;
        }
        /**
         * Get the name value.
         *
         * @return a <code>String</code> value
         */
        public String getName()
        {
            return name;
        }
        /**
         * Sets the name value.
         *
         * @param inName a <code>String</code> value
         */
        public void setName(String inName)
        {
            name = inName;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(name).append(" [").append(hostname).append(":").append(port).append("]");
            return builder.toString();
        }
        /**
         * name value
         */
        private String name;
        /**
         * hostname value
         */
        private String hostname;
        /**
         * port value
         */
        private int port;
    }
    /**
     * describes the data flow engine connections
     */
    private List<DataFlowEngineDescriptor> dataFlowEngineDescriptors = Lists.newArrayList();
}
