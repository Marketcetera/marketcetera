package org.marketcetera.db;

import java.io.File;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Hello world!
 *
 */
public class App
{
    /**
     * Create a new App instance.
     *
     * @param inArgs
     */
    public App(String[] inArgs)
    {
        mContext = new FileSystemXmlApplicationContext(new String[] {"file:"+CONF_DIR+"server.xml"},
                                                       null);
        mContext.start();
    }
    public static void main(String[] args)
    {
        final App ors;
        try {
            ors = new App(args);
        } catch (Throwable t) {
            System.err.println("Reporting failure"); //$NON-NLS-1$
            t.printStackTrace();
            return;
        }
        System.out.println("App started");
        // Execute application.
        try {
            Thread.sleep(60000);
        } catch (Throwable t) {
            System.err.println("Reporting failure"); //$NON-NLS-1$
            t.printStackTrace();
            return;
        }
    }
    private AbstractApplicationContext mContext;
    public static final String APP_DIR_PROP="org.marketcetera.appDir"; //$NON-NLS-1$
    public static final String APP_DIR;
    public static final String CONF_DIR;
    static {
        String appDir=System.getProperty(APP_DIR_PROP);
        if (appDir==null) {
            appDir="src"+File.separator+"test"+File.separator+"sample_data"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if (!appDir.endsWith(File.separator)) {
            appDir+=File.separator;
        }
        APP_DIR=appDir;
        CONF_DIR=APP_DIR+"conf"+File.separator; //$NON-NLS-1$
    }
}
