package org.marketcetera.modules.cep.esper;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;

import javax.management.MXBean;

/* $License$ */
/**
 * The management interface for esper module instances.
 *
 * @author anshul@marketcetera.com
 * @author toli@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
@MXBean(true)
@DisplayName("The Management Interface for Esper Module Instance")
public interface CEPEsperProcessorMXBean {
    /**
     * Specifies the location of the configuration file for the module.
     * The location is first interpreted as a URL. If URL creation fails
     * with a malformed URL exception, an attempt is made to interpret the
     * location as a local file. If the file doesn't exist, the value is
     * interpreted as the name of a classpath resource.
     *
     * If the configuration is not found via any of these interpretations
     * an exception is thrown during module start.
     *
     * If a null value is specified, an empty configuration is used.
     *
     * @return the location of the configuration file.
     */
    @DisplayName("The location of esper configuration file")
    String getConfiguration();

    /**
     * Specifies the location of the configuration file for the module.
     *
     * @param inConfiguration the location of the configuration file.
     * @see #getConfiguration()
     */
    @DisplayName("The location of esper configuration file")
    void setConfiguration(
            @DisplayName("The location of esper configuration file")
            String inConfiguration);

    /**
     * Returns the names of all the statements currently being handled
     * by the runtime.
     *
     * @return the names of all the statements.
     */
    @DisplayName("The list of statement names currently running")
    String[] getStatementNames();

    /**
     * Returns the number of events received over the lifetime of the
     * event stream processing runtime.
     *
     * @return number of events received.
     */
    @DisplayName("The number of events received")
    long getNumEventsReceived();

    /**
     * If the runtime should use external time source.
     *
     * @return if the runtime should use external time source.
     */
    @DisplayName("If external time source should be used")
    boolean isUseExternalTime();

    /**
     * If the runtime should use external time source.
     *
     * @param inUseExternalTime if the runtime should use external time source.
     */
    @DisplayName("If external time source should be used")
    void setUseExternalTime(
            @DisplayName("If external time source should be used")
            boolean inUseExternalTime);
}
