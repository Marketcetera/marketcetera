package org.marketcetera.core;

import org.jcyclone.core.cfg.MapConfig;
import org.jcyclone.core.boot.JCyclone;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.JMException;
import java.util.Properties;
import java.util.Set;
import java.lang.management.ManagementFactory;

/**
 * Abstract superclass to all applications
 * Initializes the logger and registers with the MBean server for JMX introspection
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class ApplicationBase implements Clock, ApplicationMBeanBase {
    protected static LoggerAdapter sLogger;

    // for Applications that take in a config file name
    protected String mCfgFileName;
    public static final String JCYCLONE_PREFIX = "jcyclone.";

    private Properties properties;
    private MapConfig jcycloneConfig;
    private JCyclone jcyclone;

    public ApplicationBase(Properties inProps)
    {
        commonInit(inProps);
    }
    public ApplicationBase(String inConfigFile) throws ConfigFileLoadingException
    {
        mCfgFileName = inConfigFile;
        properties = ConfigPropertiesLoader.loadProperties(mCfgFileName);
        commonInit(properties);
    }

    private void commonInit(Properties inProps)
    {
        sLogger = LoggerAdapter.initializeLogger("mktctrRoot");
        final ApplicationBase outerThis = this;

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                outerThis.shutdown();
            }
        });
        properties = inProps;
        jcycloneConfig = generateJCycloneConfig(properties);
    }

    public void init() throws Exception
    {
        registerMBean(true);
    }

    /** Register the JMX MBean for querying/administering this application
     * @param fExitOnFail Whether or not to exit the app if the registration fails
     */
    protected void registerMBean(boolean fExitOnFail)
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            String pkgName = this.getClass().getPackage().toString();
            String className = this.getClass().getSimpleName();
            ObjectName name = new ObjectName(pkgName +":type="+className);
            mbs.registerMBean(this, name);
        } catch (JMException ex) {
            LoggerAdapter.error("Unable to register JMX Mbean: ", ex, this);
            if(fExitOnFail) {System.exit(-1); }
        }
    }

    /* (non-Javadoc)
      * @see Clock#getTime()
      */
    public long getTime() {
        return System.currentTimeMillis();
    }

    public String getCfgFileName() {
        return mCfgFileName;
    }

    /** Returns the name of the config file */


    /* (non-Javadoc)
    * @see Clock#getApproximateTime
    */
    public long getApproximateTime() {
        // TODO: make this read a variable that is updated periodically by a thread
        return System.currentTimeMillis();
    }

    public static MapConfig generateJCycloneConfig(Properties props){
        MapConfig newConfig = new MapConfig();
        Set<Object> keySet = props.keySet();
        for (Object aKeyObject : keySet){
            String aKey = (String)aKeyObject;
            if (aKey.startsWith(JCYCLONE_PREFIX)){
                String strippedKey = aKey.substring(JCYCLONE_PREFIX.length());
                newConfig.putString(strippedKey, props.getProperty(aKey));
            }
        }
        return newConfig;
    }

    public void run() throws Exception {
        jcyclone = new JCyclone(jcycloneConfig);

    }

    public void shutdown() {
        LoggerAdapter.info("Shutting down application", this);
        try {
            if(jcyclone!=null) {
                jcyclone.stop();
                // reset the var so that we don't try to shutdown twice (this may happen in unit tests)
                jcyclone = null;
            }
        } catch (Exception e) {
            LoggerAdapter.error("Error while shutting down JCyclone framework", e, this);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
