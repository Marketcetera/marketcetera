package org.marketcetera.fix.provisioning;

import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/* $License$ */

/**
 * Enables FIX session configuration from properties.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties("fix")
public class FixSessionsConfiguration
{
    /**
     * Get the sessionDescriptors value.
     *
     * @return a <code>List<FixSessionDescriptor></code> value
     */
    public List<FixSessionDescriptor> getSessionDescriptors()
    {
        return sessionDescriptors;
    }
    /**
     * Sets the sessionDescriptors value.
     *
     * @param inSessionDescriptors a <code>List<FixSessionDescriptor></code> value
     */
    public void setSessionDescriptors(List<FixSessionDescriptor> inSessionDescriptors)
    {
        sessionDescriptors = inSessionDescriptors;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionsConfiguration ").append(sessionDescriptors);
        return builder.toString();
    }
    /**
     * Describes a set of FIX sessions.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class FixSessionDescriptor
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("FixSessionDescriptor [sessionSettings=").append(settings).append(", sessions=")
                    .append(sessions).append("]");
            return builder.toString();
        }
        /**
         * Get the settings value.
         *
         * @return a <code>Map<String,String></code> value
         */
        public Map<String,String> getSettings()
        {
            return settings;
        }
        /**
         * Sets the settings value.
         *
         * @param inSettings a <code>Map<String,String></code> value
         */
        public void setSettings(Map<String,String> inSettings)
        {
            settings = inSettings;
        }
        /**
         * Get the sessions value.
         *
         * @return a <code>List<Session></code> value
         */
        public List<Session> getSessions()
        {
            return sessions;
        }
        /**
         * Sets the sessions value.
         *
         * @param inSessions a <code>List<Session></code> value
         */
        public void setSessions(List<Session> inSessions)
        {
            sessions = inSessions;
        }
        /**
         * settings for these sessions
         */
        private Map<String,String> settings = Maps.newHashMap();
        /**
         * sessions with these settings
         */
        private List<Session> sessions = Lists.newArrayList();
    }
    /**
     * Describes a FIX session.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    public static class Session
    {
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("Session [name=").append(name).append(", description=").append(description).append(", host=")
                    .append(host).append(", port=").append(port).append(", brokerId=").append(brokerId)
                    .append(", mappedBrokerId=").append(mappedBrokerId).append(", affinity=").append(affinity).append(", settings=").append(settings)
                    .append("]");
            return builder.toString();
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
        /**
         * Get the description value.
         *
         * @return a <code>String</code> value
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * Sets the description value.
         *
         * @param inDescription a <code>String</code> value
         */
        public void setDescription(String inDescription)
        {
            description = inDescription;
        }
        /**
         * Get the host value.
         *
         * @return a <code>String</code> value
         */
        public String getHost()
        {
            return host;
        }
        /**
         * Sets the host value.
         *
         * @param inHost a <code>String</code> value
         */
        public void setHost(String inHost)
        {
            host = inHost;
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
         * Get the brokerId value.
         *
         * @return a <code>String</code> value
         */
        public String getBrokerId()
        {
            return brokerId;
        }
        /**
         * Sets the brokerId value.
         *
         * @param inBrokerId a <code>String</code> value
         */
        public void setBrokerId(String inBrokerId)
        {
            brokerId = inBrokerId;
        }
        /**
         * Get the mappedBrokerId value.
         *
         * @return a <code>String</code> value
         */
        public String getMappedBrokerId()
        {
            return mappedBrokerId;
        }
        /**
         * Sets the mappedBrokerId value.
         *
         * @param inMappedBrokerId a <code>String</code> value
         */
        public void setMappedBrokerId(String inMappedBrokerId)
        {
            mappedBrokerId = inMappedBrokerId;
        }
        /**
         * Get the affinity value.
         *
         * @return an <code>int</code> value
         */
        public int getAffinity()
        {
            return affinity;
        }
        /**
         * Sets the affinity value.
         *
         * @param inAffinity an <code>int</code> value
         */
        public void setAffinity(int inAffinity)
        {
            affinity = inAffinity;
        }
        /**
         * Get the settings value.
         *
         * @return a <code>Map<String,String></code> value
         */
        public Map<String,String> getSettings()
        {
            return settings;
        }
        /**
         * Sets the settings value.
         *
         * @param inSettings a <code>Map<String,String></code> value
         */
        public void setSettings(Map<String,String> inSettings)
        {
            settings = inSettings;
        }
        /**
         * name value
         */
        private String name;
        /**
         * description value
         */
        private String description;
        /**
         * host value
         */
        private String host;
        /**
         * port value
         */
        private int port;
        /**
         * broker id value
         */
        private String brokerId;
        /**
         * optional mapped broker id value, may be <code>null</code>
         */
        private String mappedBrokerId;
        /**
         * affinity value
         */
        private int affinity = 1;
        /**
         * settings for this session
         */
        private Map<String,String> settings = Maps.newHashMap();
    }
    /**
     * holds session descriptors
     */
    private List<FixSessionDescriptor> sessionDescriptors = Lists.newArrayList();
}
