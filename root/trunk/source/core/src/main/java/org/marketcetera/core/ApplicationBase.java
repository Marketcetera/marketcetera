package org.marketcetera.core;

import org.jcyclone.core.cfg.MapConfig;
import org.jcyclone.core.cfg.JCycloneConfig;
import org.jcyclone.core.boot.JCyclone;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.JMException;
import java.util.*;
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
    public static final String JCYCLONE_DFLT_THREAD_MGR_FIELD_NAME = "global.defaultThreadManager";

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
        List<MessageBundleInfo> bundles = getLocalMessageBundles();
        MessageBundleManager.registerCoreMessageBundle();
        if (bundles != null){
            for (MessageBundleInfo messageBundleInfo : bundles) {
                MessageBundleManager.registerMessageBundle(messageBundleInfo);
            }
        }

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
            LoggerAdapter.error(MessageKey.JMX_BEAN_FAILURE.getLocalizedMessage(), ex, this);
            if(fExitOnFail) {System.exit(-1); }
        }
    }

    /* (non-Javadoc)
      * @see Clock#getTime()
      */
    public long getTime() {
        return System.currentTimeMillis();
    }

    /** Returns the name of the config file ofr this app */
    public String getCfgFileName() {
        return mCfgFileName;
    }

    /* (non-Javadoc)
    * @see Clock#getApproximateTime
    */
    public long getApproximateTime() {
        // TODO: make this read a variable that is updated periodically by a thread
        return System.currentTimeMillis();
    }

    /** Generates a JCyclone configuration from the passed in properties.
     * Always sets the default thread manager to be {@link JCycloneConfig.THREADMGR_TPSTM_CONCURRENT) initially,
     * but it can be overridden in the config file.
     * @param props Incoming set of properties, prefixed with {@link #JCYCLONE_PREFIX}.
     * @return  new JCyclone config with the specified properties
     */
    public static MapConfig generateJCycloneConfig(Properties props){
        MapConfig newConfig = new MapConfig();
        // set the default thread manager to the concurrent, but allow it to be overridden from a cfg file
        String threadMgrPropName = JCYCLONE_PREFIX+JCYCLONE_DFLT_THREAD_MGR_FIELD_NAME;
        if(props.getProperty(threadMgrPropName) == null) {
            props.setProperty(threadMgrPropName, JCycloneConfig.THREADMGR_TPSTM_CONCURRENT);
        }
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
        try {
            if(jcyclone!=null) {
                LoggerAdapter.info(MessageKey.APP_SHUTDOWN.getLocalizedMessage(), this);
                jcyclone.stop();
                // reset Jcyclone so that we don't try to shutdown twice (this may happen in unit tests)
                jcyclone.dispose();
                jcyclone = null;
            }
        } catch (Exception e) {
            LoggerAdapter.error(MessageKey.JCYCLONE_SHUTDOWN_ERR.getLocalizedMessage(), e, this);
        }
    }

    public Properties getProperties() {
        return properties;
    }

    /** Subclasses can override the implementation if they need to add additoinal or specific
     * message bundles for internationalization.
     */
    protected abstract List<MessageBundleInfo> getLocalMessageBundles();

}
