package org.marketcetera.core;

import java.io.File;

import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * This class provides means to configure the logger for unit tests.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class LoggerConfiguration {
    /**
     * Sets up logging.
     */
    public static void logSetup() {
        if(!LOGGER_CONFIG.exists()) {
            SLF4JLoggerProxy.warn(LoggerConfiguration.class,
                    "logger configuration file {} not found", //$NON-NLS-1$
                    LOGGER_CONFIG.getAbsolutePath());
        }
        System.setProperty(XmlConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
                           LOGGER_CONFIG.getAbsolutePath());
    }

    public static final File TEST_ROOT = new File("src" + File.separator + "test");
    public static final File TEST_SAMPLE_DATA = new File(TEST_ROOT, "sample_data");
    public static final File TEST_CONF = new File(TEST_SAMPLE_DATA, "conf");
    public static final File LOGGER_CONFIG = new File(TEST_CONF, "log4j2.xml");
}
