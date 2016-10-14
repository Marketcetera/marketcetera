package org.marketcetera.core.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Watches the given directories for files to be added or modified.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DirectoryWatcherImpl.java 83882 2014-08-01 22:31:54Z colin $
 * @since $Release$
 */
@ThreadSafe
public class DirectoryWatcherImpl
        implements DirectoryWatcher
{
    /**
     * Create a new DirectoryWatcherImpl instance.
     */
    public DirectoryWatcherImpl()
    {
        directoriesToWatch = new ArrayList<File>();
        subscriberList = new ArrayList<DirectoryWatcherSubscriber>();
        executor = Executors.newSingleThreadScheduledExecutor();
    }
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        final Set<File> missingWarnedFiles = Sets.newHashSet();
        final Set<File> accessWarnedFiles = Sets.newHashSet();
        executor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (directoriesToWatch) {
                        Set<File> inputFileList = new TreeSet<File>();
                        for(File inputDir : directoriesToWatch) {
                            if(inputDir.exists() && inputDir.isDirectory()) {
                                if(inputDir.canRead() && inputDir.canWrite()) {
                                    inputFileList.addAll(Arrays.asList(inputDir.listFiles()));
                                } else {
                                    if(!accessWarnedFiles.contains(inputDir)) {
                                        Messages.DIRECTORY_ACCESS_DENIED.warn(DirectoryWatcherImpl.class,
                                                                              inputDir.getAbsolutePath());
                                        accessWarnedFiles.add(inputDir);
                                    }
                                }
                            } else {
                                if(!missingWarnedFiles.contains(inputDir)) {
                                    Messages.MISSING_DIRECTORY.warn(DirectoryWatcherImpl.class,
                                                                    inputDir.getAbsolutePath());
                                    missingWarnedFiles.add(inputDir);
                                }
                            }
                        }
                        synchronized(subscriberList) {
                            for(File curFile : inputFileList) {
                                File curFileCopy = File.createTempFile("watched-file-", //$NON-NLS-1$
                                                                       ".dat"); //$NON-NLS-1$
                                curFileCopy.deleteOnExit();
                                FileUtils.copyFile(curFile,
                                                   curFileCopy);
                                for(DirectoryWatcherSubscriber subscriber : subscriberList) {
                                    try {
                                        subscriber.received(curFileCopy,
                                                            curFile.getName());
                                    } catch (Exception e) {
                                        Messages.SUBSCRIBER_FILE_PROCESSING_FAILED.error(DirectoryWatcherImpl.class,
                                                                                         e,
                                                                                         subscriber.getClass(),
                                                                                         curFile.getAbsolutePath());
                                    }
                                }
                                boolean deleteFlag = curFile.delete();
                                if(!deleteFlag) {
                                    throw new CoreException(new I18NBoundMessage1P(Messages.FILE_DELETE_FAILURE,
                                                                                   curFile.getAbsolutePath()));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    SLF4JLoggerProxy.error(DirectoryWatcherImpl.class,
                                           e);
                }
            }
        }, 0, getPollingInterval(), TimeUnit.MILLISECONDS);
        SLF4JLoggerProxy.debug(this,
                               "Directory watcher started"); //$NON-NLS-1$
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        executor.shutdown();
    }
    /**
     * Set directoriesToWatch value.
     *
     * @param inDirectories a <code>List&lt;File&gt;</code> value
     */
    public void setDirectoriesToWatch(List<File> inDirectories)
    {
        synchronized (directoriesToWatch) {
            directoriesToWatch.clear();
            for(File dir : inDirectories) {
                directoriesToWatch.add(dir);
            }
        }
    }
    /**
     * Get the polling interval value.
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
     * @param inPollingInterval a <code>long</code> value
     */
    public void setPollingInterval(long inPollingInterval)
    {
        Validate.isTrue(inPollingInterval >= 0,
                        "Polling interval " + inPollingInterval + " must be greater than or equal to 0");
        pollingInterval = inPollingInterval;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.tools.DirectoryWatcher#addWatcher(com.marketcetera.ramius.tools.DirectoryWatcherSubscriber)
     */
    @Override
    public void addWatcher(DirectoryWatcherSubscriber inSubscriber)
    {
        synchronized (subscriberList) {
            if (!subscriberList.contains(inSubscriber)) {
                subscriberList.add(inSubscriber);
            }
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ramius.tools.DirectoryWatcher#removeWatcher(com.marketcetera.ramius.tools.DirectoryWatcherSubscriber)
     */
    @Override
    public void removeWatcher(DirectoryWatcherSubscriber inSubscriber)
    {
        synchronized (subscriberList) {
            subscriberList.remove(inSubscriber);
        }
    }
    /**
     * interval at which the directories to watch are polled
     */
    private volatile long pollingInterval = 250;
    /**
     * watch each of these directories for changes 
     */
    @GuardedBy("directoriesToWatch") 
    private final List<File> directoriesToWatch;
    /**
     * inform these subscribers when something changes in one of the directories
     */
    @GuardedBy("subscriberList") 
    private final List<DirectoryWatcherSubscriber> subscriberList;
    /**
     * executor responsible for executing the jobs to watch the directories
     */
    private final ScheduledExecutorService executor;
}
