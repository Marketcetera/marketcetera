package org.marketcetera.modules.remote.receiver;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;
import org.marketcetera.event.LogEvent;

/* $License$ */
/**
 * Management interface for {@link ReceiverModule}.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
@DisplayName("Management Interface for Remote Receiver module")
public interface ReceiverModuleMXBean {
    /**
     * Gets the URL at which remote emitters should be able to connect to this
     * module.
     *
     * @return the URL value.
     */
    @DisplayName("The URL at which remote emitters should connect")
    public String getURL();

    /**
     * Sets the URL at which remote emitters should be able to connect to this
     * module.
     *
     * @param inURL the URL value.
     *
     * @throws IllegalStateException if the module is started. The URL
     * can only be set when the module is not started.
     */
    @DisplayName("The URL at which remote emitters should connect")
    public void setURL(
            @DisplayName("The URL at which remote emitters should connect")
            String inURL);

    /**
     * Gets the minimum log level of log events remotely transmitted
     * by this module.
     * <p>
     * The returned log level corresponds to the log-level for the system-wide
     * logger category {@link org.marketcetera.core.Messages#USER_MSG_CATEGORY}.
     *
     * @return the log level value.
     */
    @DisplayName("Minimum log level of log events to transmit")
    public LogEvent.Level getLogLevel();

    /**
     * Sets the minimum log level of log events remotely transmitted
     * by this module.
     * <p>
     * Setting this log level also sets the log level for the system-wide
     * logger category {@link org.marketcetera.core.Messages#USER_MSG_CATEGORY}.
     * <p>
     * Do note for this function to work correctly the system logger
     * category should not be specified in the log4j configuration file.
     * The log4j configuration file is reread whenever it's updated. If the
     * log4j configuration file contains configuration for
     * the system logger category, it will over-write the configuration
     * carried out via this method and result in confusing system behavior.
     *
     * @param inLevel the log level value.
     */
    @DisplayName("Minimum log level of log events to transmit")
    public void setLogLevel(
            @DisplayName("Minimum log level of log events to transmit")
            LogEvent.Level inLevel);

    /**
     * If the module should skip the automatic JAAS configuration when
     * it's started.
     *
     * @return if the module should skip the automatic JAAS configuration
     * when it's started.
     */
    @DisplayName("Skip Automatic JAAS Configuration")
    public boolean isSkipJAASConfiguration();

    /**
     * Sets if the module should skip the automatic JAAS configuration when
     * it's started.
     *
     * @param inSkipJAASConfiguration if the automatic JAAS configuration
     * should be skipped.
     */
    @DisplayName("Skip Automatic JAAS Configuration")
    public void setSkipJAASConfiguration(
            @DisplayName("Skip Automatic JAAS Configuration")
            boolean inSkipJAASConfiguration);
}
