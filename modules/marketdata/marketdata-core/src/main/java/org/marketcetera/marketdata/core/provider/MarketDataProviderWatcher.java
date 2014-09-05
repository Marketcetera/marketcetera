package org.marketcetera.marketdata.core.provider;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang.Validate;
import org.marketcetera.marketdata.AbstractMarketDataModuleMXBean;
import org.marketcetera.marketdata.FeedStatus;
import org.marketcetera.marketdata.core.MarketDataProviderMBean;
import org.marketcetera.marketdata.core.Messages;
import org.marketcetera.marketdata.core.ProviderStatus;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Monitors a market data provider and takes action based on what it finds.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release%
 */
@ClassVersion("$Id: AbstractMarketDataProvider.java 16913 2014-06-17 17:13:00Z colin $")
public class MarketDataProviderWatcher
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        mbeanServer = ManagementFactory.getPlatformMBeanServer();
        Validate.notNull(mbeanServer,
                         Messages.MBEAN_SERVER_REQUIRED.getText());
        Validate.notNull(moduleName,
                         Messages.MODULE_NAME_REQUIRED.getText());
        timerService = Executors.newSingleThreadScheduledExecutor();
        timerService.scheduleAtFixedRate(new Watcher(),
                                         monitoringInterval,
                                         monitoringInterval,
                                         TimeUnit.MILLISECONDS);
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        if(timerService != null) {
            try {
                timerService.shutdownNow();
            } catch (Exception ignored) {}
            timerService = null;
        }
    }
    /**
     * Get the moduleName value.
     *
     * @return a <code>String</code> value
     */
    public String getModuleName()
    {
        return moduleName;
    }
    /**
     * Sets the moduleName value.
     *
     * @param inModuleName a <code>String</code> value
     */
    public void setModuleName(String inModuleName)
    {
        moduleName = inModuleName;
    }
    /**
     * Get the monitoringInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getMonitoringInterval()
    {
        return monitoringInterval;
    }
    /**
     * Sets the monitoringInterval value.
     *
     * @param inMonitoringInterval a <code>long</code> value
     */
    public void setMonitoringInterval(long inMonitoringInterval)
    {
        monitoringInterval = inMonitoringInterval;
    }
    /**
     * Gets the admin bean for the given session.
     *
     * @return an <code>AbstractMarketDataModuleMXBean</code> value
     * @throws MalformedObjectNameException if an error occurs getting the provider bean
     */
    private AbstractMarketDataModuleMXBean getModuleBean()
            throws MalformedObjectNameException
    {
        ObjectName sessionObjectName = getModuleObjectName();
        AbstractMarketDataModuleMXBean sessionAdmin = JMX.newMXBeanProxy(mbeanServer,
                                                                         sessionObjectName,
                                                                         AbstractMarketDataModuleMXBean.class,
                                                                         true);
        return sessionAdmin;
    }
    /**
     * Gets the admin bean for the given session.
     *
     * @return a <code>MarketDataProviderMBean</code> value
     * @throws MalformedObjectNameException if an error occurs getting the provider bean
     */
    private MarketDataProviderMBean getProviderBean()
            throws MalformedObjectNameException
    {
        ObjectName sessionObjectName = getProviderObjectName();
        MarketDataProviderMBean sessionAdmin = JMX.newMXBeanProxy(mbeanServer,
                                                                  sessionObjectName,
                                                                  MarketDataProviderMBean.class,
                                                                  true);
        return sessionAdmin;
    }
    /**
     * Gets the <code>ObjectName</code> for the module to be watched.
     *
     * @return an <code>ObjectName</code> value
     * @throws MalformedObjectNameException if the object name cannot be constructed
     */
    private ObjectName getModuleObjectName()
            throws MalformedObjectNameException
    {
        // sample for old module: org.marketcetera.module:type=mdata,provider=activ,name=single
        StringBuilder builder = new StringBuilder();
        builder.append("org.marketcetera.module:type=mdata,provider=").append(moduleName).append(",name=single"); //$NON-NLS-1$ //$NON-NLS-2$
        return new ObjectName(builder.toString());
    }
    /**
     * Gets the <code>ObjectName</code> for the module to be watched.
     *
     * @return an <code>ObjectName</code> value
     * @throws MalformedObjectNameException if the object name cannot be constructed
     */
    private ObjectName getProviderObjectName()
            throws MalformedObjectNameException
    {
        // sample for new provider: org.marketcetera.marketdata.provider:name=exegy
        StringBuilder builder = new StringBuilder();
        builder.append("org.marketcetera.marketdata.provider:name=").append(moduleName); //$NON-NLS-1$
        return new ObjectName(builder.toString());
    }
    /**
     * Schedulable watcher task that checks the status of the module to monitor and tries to restart it if necessary.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: MarketDataProviderWatcher.java 83882 2014-08-01 22:31:54Z colin $
     * @since $Release$
     */
    private class Watcher
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            try {
                boolean isRunning = false;
                Object status = null;
                MarketDataProviderMBean providerBean = null;
                AbstractMarketDataModuleMXBean moduleBean = null;
                if(useModule) {
                    try {
                        moduleBean = getModuleBean();
                        FeedStatus feedStatus = FeedStatus.valueOf(moduleBean.getFeedStatus());
                        status = feedStatus;
                        useModule = true;
                        isRunning = feedStatus.isRunning();
                    } catch (Exception e) {
                        SLF4JLoggerProxy.debug(MarketDataProviderWatcher.this,
                                               e,
                                               "Can't use module form, trying provider form"); //$NON-NLS-1$
                        useModule = false;
                    }
                }
                if(!useModule) {
                    providerBean = getProviderBean();
                    ProviderStatus providerStatus = ProviderStatus.valueOf(providerBean.getProviderStatusValue());
                    status = providerStatus;
                    isRunning = providerStatus.isRunning();
                }
                if(isRunning) {
                    SLF4JLoggerProxy.trace(MarketDataProviderWatcher.this,
                                           "{} feed status is {}", //$NON-NLS-1$
                                           moduleName,
                                           status);
                } else {
                    Messages.BAD_FEED_STATUS.warn(MarketDataProviderWatcher.this,
                                                  moduleName,
                                                  status);
                    try {
                        if(useModule) {
                            moduleBean.reconnect();
                        } else {
                            providerBean.stop();
                            providerBean.start();
                        }
                    } catch (Exception e) {
                        Messages.CANNOT_RECONNECT_FEED.warn(MarketDataProviderWatcher.this,
                                                            e,
                                                            moduleName,
                                                            monitoringInterval);
                    }
                }
            } catch (Exception e) {
                Messages.CANNOT_DETERMINE_FEED_STATUS.warn(MarketDataProviderWatcher.this,
                                                           e,
                                                           moduleName);
            }
        }
        /**
         * indicates whether to use the new provider type or the old module type of mbean interface
         */
        private boolean useModule = true;
    }
    /**
     * service used to schedule watcher tasks
     */
    private ScheduledExecutorService timerService;
    /**
     * provides access to mbean objects
     */
    private MBeanServer mbeanServer;
    /**
     * module to monitor
     */
    private String moduleName;
    /**
     * interval at which to monitor
     */
    private long monitoringInterval = 30000;
}
