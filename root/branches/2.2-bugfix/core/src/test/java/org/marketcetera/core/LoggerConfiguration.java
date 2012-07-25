package org.marketcetera.core;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;

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
        PropertyConfigurator.configureAndWatch
            (LOGGER_CONFIG.getAbsolutePath(), 10 * 1000l); //10 seconds
    }

    public static final File TEST_ROOT = new File("src" + //$NON-NLS-1$
            File.separator + "test"); //$NON-NLS-1$
    public static final File TEST_SAMPLE_DATA = new File(TEST_ROOT, "sample_data"); //$NON-NLS-1$
    public static final File TEST_CONF = new File(TEST_SAMPLE_DATA, "conf"); //$NON-NLS-1$
    public static final File LOGGER_CONFIG = new File(TEST_CONF, "log4j.properties"); //$NON-NLS-1$
}
