package org.marketcetera.container;

import java.io.File;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/* $License$ */

/**
 * Provides a process container in which to execute application modules.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class ApplicationContainer
{
    /**
     * Provides an execution entry point.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String inArgs[])
    {
        String contextFilenamePath = "file:" + APP_DIR + contextFilename;
        context = new ClassPathXmlApplicationContext(contextFilenamePath);
        context.registerShutdownHook();
    }
    /**
     * 
     *
     *
     * @return a <code>ConfigurableApplicationContext</code> value
     */
    public static ConfigurableApplicationContext getContext()
    {
        return context;
    }
    /**
     * application context for this application
     */
    private static volatile ConfigurableApplicationContext context;
    /**
     * 
     */
    private static final String contextFilename = "main.xml";
    /**
     * 
     */
    public static final String APP_DIR_PROP = "org.marketcetera.appDir"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String APP_DIR;
    /**
     * 
     */
    public static final String CONF_DIR;
    /**
     * 
     */
    static {
        String appDir = System.getProperty(APP_DIR_PROP);
        if(appDir == null) {
            appDir = "src" + File.separator + "test" + File.separator + "sample_data"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if(!appDir.endsWith(File.separator)) {
            appDir += File.separator;
        }
        APP_DIR = appDir;
        CONF_DIR = APP_DIR + "conf" + File.separator; //$NON-NLS-1$
    }
}
