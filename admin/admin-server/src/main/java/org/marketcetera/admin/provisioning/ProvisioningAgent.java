package org.marketcetera.admin.provisioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.file.DirectoryWatcherImpl;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
                         String inOriginalFilename)
    {
        SLF4JLoggerProxy.info(this,
                              "Reading provisioning from {}",
                              inOriginalFilename);
        try {
            String extension = FilenameUtils.getExtension(inOriginalFilename);
            if(extension == null) {
                extension = "xml";
            }
            extension = extension.toLowerCase();
            switch(extension) {
                case "jar" :
                    handleJar(inFile,
                              inOriginalFilename);
                    break;
                case "xml" :
                default:
                    handleXml(inFile,
                              inOriginalFilename);
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            PlatformServices.handleException(this,
                                             "Unable to read provisioning file: " + inOriginalFilename,
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
        provisioningDirectory = provisioningDirectory + clusterData.getInstanceNumber();
        try {
            DirectoryWatcherImpl watcher = new DirectoryWatcherImpl();
            watcher.setCreateDirectoriesOnStart(true);
            watcher.setDirectoriesToWatch(Lists.newArrayList(new File(provisioningDirectory)));
            watcher.setPollingInterval(pollingInterval);
            watcher.addWatcher(this);
            watcher.start();
            SLF4JLoggerProxy.info(this,
                                  "Watching {} for provisioning files",
                                  provisioningDirectory);
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
     * Handle the received file as a pre-built JAR structured in a specific way.
     *
     * @param inFile a <code>File</code> value
     * @param inOriginalFilename a <code>String</code> value
     * @throws Exception if an error occurs processing the JAR
     */
    private void handleJar(File inFile,
                           String inOriginalFilename)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Handling {} as a JAR file",
                               inOriginalFilename);
        // prepare a new class loader based on the current one to load this class
        URL url = inFile.toURI().toURL();
        // create a new class loader for this operation that will be closed at its conclusion
        try(URLClassLoader newClassloader = new URLClassLoader(new URL[] { url },ClassLoader.getSystemClassLoader())) {
            String mainClass = getMainClassName(inFile);
            SLF4JLoggerProxy.debug(this,
                                   "Selected {} as the mainClass",
                                   mainClass);
            Validate.notNull(mainClass,
                             "No 'Main-Class' attribute in JAR '" + inOriginalFilename + "': we don't know how to start the JAR");
            Class<?> provisioningClass = newClassloader.loadClass(mainClass);
            // create a new Spring context as a sandbox for the loaded code. this allows us to discard the loaded code when done
            try(AnnotationConfigApplicationContext newContext = new AnnotationConfigApplicationContext()) {
                // explicitly use the new class loader
                newContext.setClassLoader(newClassloader);
                // add the parent context to give the provisioning JAR access to all our resources
                newContext.setParent(applicationContext);
                // register the new class
                newContext.register(provisioningClass);
                // refresh the context, which allows it to prepare to use the provisioning JAR
                newContext.refresh();
                // start the context
                newContext.start();
            }
            // new context is closed
        }
        // new class loader is closed
    }
    /**
     * Determines the main class name from the given JAR.
     *
     * <p>The current implementation optimistically assumes the given File is a JAR and contains a <code>Main-Class</code> attribute.
     * 
     * @param inJarFile a <code>File</code> value
     * @return a <code>String</code> value or <code>null</code> if the appropriate attribute cannot be extracted from the given file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException if the file could otherwise not be read
     */
    private String getMainClassName(File inJarFile)
            throws FileNotFoundException, IOException
    {
        String mainClassName;
        try(JarInputStream jarStream = new JarInputStream(new FileInputStream(inJarFile))) {
            Manifest manifest = jarStream.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            mainClassName = mainAttributes.getValue("Main-Class");
        }
        return mainClassName;
    }
    /**
     * Handle the received file as an XML file containing Spring bean descriptors.
     *
     * @param inFile a <code>File</code> value
     * @param inOriginalFilename a <code>String</code> value
     * @throws Exception if an error occurs processing the XML
     */
    private void handleXml(File inFile,
                           String inOriginalFilename)
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Handling {} as an XML command",
                               inOriginalFilename);
        try(ConfigurableApplicationContext newContext = new FileSystemXmlApplicationContext(new String[] { "file:"+inFile.getAbsolutePath() },applicationContext)) {
            newContext.start();
        }
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
    private String provisioningDirectory = FileUtils.getTempDirectoryPath()+File.separator+"provisioning";
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
