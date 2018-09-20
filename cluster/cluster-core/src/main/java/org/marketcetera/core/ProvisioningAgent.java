package org.marketcetera.core;

import java.io.File;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.file.DirectoryWatcherImpl;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides dynamic provisioning services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ProvisioningAgent
        implements DirectoryWatcherSubscriber
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.file.DirectoryWatcherSubscriber#received(java.io.File, java.lang.String)
     */
    @Override
    public void received(File inFile,
                         String inOriginalFileName)
    {
        SLF4JLoggerProxy.info(this,
                              "Reading provisioning from {}",
                              inOriginalFileName);
        try {
            try(ConfigurableApplicationContext newContext = new FileSystemXmlApplicationContext(new String[] { "file:"+inFile.getAbsolutePath() },applicationContext)) {
                newContext.start();
            }
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Unable to read provisioning file: " + inOriginalFileName,
                                             e);
        }
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        clusterData = clusterService.getInstanceData();
        String actualDirectory = provisioningDirectory + clusterData.getInstanceNumber();
        try {
            DirectoryWatcherImpl watcher = new DirectoryWatcherImpl();
            watcher.setDirectoriesToWatch(Lists.newArrayList(new File(actualDirectory)));
            watcher.setPollingInterval(pollingInterval);
            watcher.addWatcher(this);
            watcher.start();
            SLF4JLoggerProxy.info(this,
                                  "Watching {} for provisioning files",
                                  actualDirectory);
        } catch (Exception e) {
            PlatformServices.handleException(this,
                                             "Unable to watch for provisioning files",
                                             e);
        }
    }
    /**
     * Get the pollingInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getPollingInterval()
    {
        return pollingInterval;
    }
    /**
     * Sets the pollingInterval value.
     *
     * @param a <code>long</code> value
     */
    public void setPollingInterval(long inPollingInterval)
    {
        pollingInterval = inPollingInterval;
    }
    /**
     * Get the provisioningDirectory value.
     *
     * @return a <code>String</code> value
     */
    public String getProvisioningDirectory()
    {
        return provisioningDirectory;
    }
    /**
     * Sets the provisioningDirectory value.
     *
     * @param a <code>String</code> value
     */
    public void setProvisioningDirectory(String inProvisioningDirectory)
    {
        provisioningDirectory = inProvisioningDirectory;
    }
    /**
     * Get the clusterService value.
     *
     * @return a <code>ClusterService</code> value
     */
    public ClusterService getClusterService()
    {
        return clusterService;
    }
    /**
     * Sets the clusterService value.
     *
     * @param a <code>ClusterService</code> value
     */
    public void setClusterService(ClusterService inClusterService)
    {
        clusterService = inClusterService;
    }
    /**
     * interval at which to poll for provisioning files
     */
    private long pollingInterval = 5000;
    /**
     * generated cluster data
     */
    private ClusterData clusterData;
    /**
     * directory to watch for provisioning files
     */
    private String provisioningDirectory = FileUtils.getTempDirectoryPath()+File.pathSeparator+"provisioning";
    /**
     * provides access to cluster services
     */
    @Autowired
    private ClusterService clusterService;
    /**
     * application context value
     */
    @Autowired
    private ApplicationContext applicationContext;
}
