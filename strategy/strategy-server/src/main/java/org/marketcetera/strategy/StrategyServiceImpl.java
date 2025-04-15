package org.marketcetera.strategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.core.file.DirectoryWatcherSubscriber;
import org.marketcetera.core.notifications.INotification.Severity;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.strategy.dao.PersistentStrategyInstance;
import org.marketcetera.strategy.dao.PersistentStrategyMessage;
import org.marketcetera.strategy.dao.StrategyInstanceDao;
import org.marketcetera.strategy.dao.StrategyMessageDao;
import org.marketcetera.strategy.events.SimpleStrategyStatusChangedEvent;
import org.marketcetera.strategy.events.SimpleStrategyUploadFailedEvent;
import org.marketcetera.strategy.events.SimpleStrategyUploadSucceededEvent;
import org.marketcetera.strategy.events.StrategyEvent;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

/**
 * Stub implementation of {@link StrategyService} for Spring Boot 3 migration.
 * This is a temporary implementation that logs warnings and returns empty values
 * until QueryDSL supports Jakarta Persistence.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 */
@Component
@AutoConfiguration
public class StrategyServiceImpl
        implements StrategyService, DirectoryWatcherSubscriber
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void start()
    {
        serviceName = PlatformServices.getServiceName(getClass());
        SLF4JLoggerProxy.info(this,
                             "{} starting - STUB IMPLEMENTATION for Spring Boot 3 migration",
                             serviceName);
        strategyIncomingDirectoryName = strategyIncomingDirectoryName + "1"; // Assuming instance 1
        incomingStrategyDirectoryPath = Paths.get(strategyIncomingDirectoryName);
        storageStrategyDirectoryPath = Paths.get(strategyStorageDirectoryName);
        temporaryStrategyDirectoryPath = Paths.get(strategyTemporaryDirectoryName);
    }

    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                             "{} stopped",
                             serviceName);
    }

    /**
     * Requests loaded strategy instances.
     *
     * @return a <code>Collection&lt;StrategyInstance&gt;</code> value
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public Collection<? extends StrategyInstance> getStrategyInstances(String inCurrentUserName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of getStrategyInstances - QueryDSL support pending for Jakarta");
        return strategyInstanceDao.findAll();
    }

    /**
     * Requests strategy messages.
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public CollectionPageResponse<? extends StrategyMessage> getStrategyMessages(String inStrategyName,
                                                                               Severity inSeverity,
                                                                               org.marketcetera.persist.PageRequest inPageRequest)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of getStrategyMessages - QueryDSL support pending for Jakarta");
        Page<PersistentStrategyMessage> emptyPage = new PageImpl<>(Collections.emptyList());
        return new CollectionPageResponse<>(emptyPage);
    }

    /**
     * Start a strategy instance.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void startStrategyInstance(String inStrategyInstanceName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of startStrategyInstance - QueryDSL support pending for Jakarta");
        Optional<PersistentStrategyInstance> strategyInstanceOption = strategyInstanceDao.findByName(inStrategyInstanceName);
        if (strategyInstanceOption.isPresent()) {
            PersistentStrategyInstance strategyInstance = strategyInstanceOption.get();
            StrategyStatus oldStatus = strategyInstance.getStatus();
            strategyInstance.setStatus(StrategyStatus.RUNNING);
            strategyInstance.setStarted(new Date());
            strategyInstanceDao.save(strategyInstance);
            // Send status changed event
            if(oldStatus != StrategyStatus.RUNNING) {
                strategySendEvent(new SimpleStrategyStatusChangedEvent(strategyInstance, oldStatus, StrategyStatus.RUNNING));
            }
        }
    }

    /**
     * Stop a strategy instance.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void stopStrategyInstance(String inStrategyInstanceName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of stopStrategyInstance - QueryDSL support pending for Jakarta");
        Optional<PersistentStrategyInstance> strategyInstanceOption = strategyInstanceDao.findByName(inStrategyInstanceName);
        if (strategyInstanceOption.isPresent()) {
            PersistentStrategyInstance strategyInstance = strategyInstanceOption.get();
            StrategyStatus oldStatus = strategyInstance.getStatus();
            strategyInstance.setStatus(StrategyStatus.STOPPED);
            strategyInstanceDao.save(strategyInstance);
            // Send status changed event
            if(oldStatus != StrategyStatus.STOPPED) {
                strategySendEvent(new SimpleStrategyStatusChangedEvent(strategyInstance, oldStatus, StrategyStatus.STOPPED));
            }
        }
    }

    /**
     * Unload a strategy instance.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void unloadStrategyInstance(String inStrategyInstanceName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of unloadStrategyInstance - QueryDSL support pending for Jakarta");
        Optional<PersistentStrategyInstance> strategyInstanceOption = strategyInstanceDao.findByName(inStrategyInstanceName);
        if (strategyInstanceOption.isPresent()) {
            strategyInstanceDao.delete(strategyInstanceOption.get());
        }
    }

    /**
     * Delete a strategy message by ID.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void deleteStrategyMessage(long inStrategyMessageId)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of deleteStrategyMessage - QueryDSL support pending for Jakarta");
        Optional<PersistentStrategyMessage> strategyMessageOption = strategyMessageDao.findByStrategyMessageId(inStrategyMessageId);
        if (strategyMessageOption.isPresent()) {
            strategyMessageDao.delete(strategyMessageOption.get());
        }
    }

    /**
     * Delete all strategy messages for a strategy instance.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void deleteAllStrategyMessages(String inStrategyInstanceName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of deleteAllStrategyMessages - QueryDSL support pending for Jakarta");
        // In the stub implementation, we don't delete messages as we'd need QueryDSL functionality
    }

    /**
     * Get the incoming strategy directory.
     */
    @Override
    public Path getIncomingStrategyDirectory()
    {
        return incomingStrategyDirectoryPath;
    }

    /**
     * Get the temporary strategy directory.
     */
    @Override
    public Path getTemporaryStrategyDirectory()
    {
        return temporaryStrategyDirectoryPath;
    }

    /**
     * Process received file.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public void received(File inFile, String inOriginalFileName)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of received - QueryDSL support pending for Jakarta");
        // Simple implementation for file reception
        Optional<PersistentStrategyInstance> strategyInstance = Optional.empty();
        try {
            String nonce = org.apache.commons.io.FilenameUtils.getBaseName(inOriginalFileName);
            strategyInstance = strategyInstanceDao.findByNonce(nonce);
            if (strategyInstance.isPresent()) {
                PersistentStrategyInstance instance = strategyInstance.get();
                StrategyStatus oldStatus = instance.getStatus();
                instance.setStatus(StrategyStatus.STOPPED);
                strategyInstanceDao.save(instance);
                strategySendEvent(new SimpleStrategyUploadSucceededEvent(instance));
                if(oldStatus != StrategyStatus.STOPPED) {
                    strategySendEvent(new SimpleStrategyStatusChangedEvent(instance, oldStatus, StrategyStatus.STOPPED));
                }
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this, e);
            if (strategyInstance.isPresent()) {
                strategySendEvent(new SimpleStrategyUploadFailedEvent(strategyInstance.get(), e.getMessage()));
            }
        }
    }

    /**
     * Load a strategy instance.
     */
    @Override
    @Transactional(readOnly=false,propagation=Propagation.REQUIRED)
    public StrategyStatus loadStrategyInstance(StrategyInstance inStrategyInstance)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of loadStrategyInstance - QueryDSL support pending for Jakarta");
        PersistentStrategyInstance pInstance;
        if(inStrategyInstance instanceof PersistentStrategyInstance) {
            pInstance = (PersistentStrategyInstance)inStrategyInstance;
            pInstance.setStatus(StrategyStatus.LOADING);
            pInstance = strategyInstanceDao.save(pInstance);
            return pInstance.getStatus();
        } else {
            return StrategyStatus.ERROR;
        }
    }

    /**
     * Create a strategy message.
     */
    @Override
    public StrategyMessage createStrategyMessage(StrategyMessage inStrategyMessage)
    {
        SLF4JLoggerProxy.warn(this, "Using stub implementation of createStrategyMessage - QueryDSL support pending for Jakarta");
        if(inStrategyMessage instanceof PersistentStrategyMessage) {
            return strategyMessageDao.save((PersistentStrategyMessage)inStrategyMessage);
        }
        return inStrategyMessage;
    }

    /**
     * Find a strategy instance by name.
     */
    @Override
    @Transactional(readOnly=true,propagation=Propagation.REQUIRED)
    public Optional<? extends StrategyInstance> findByName(String inName)
    {
        return strategyInstanceDao.findByName(inName);
    }

    /**
     * Add a strategy event listener.
     */
    @Override
    public void addStrategyEventListener(StrategyEventListener inListener)
    {
        strategyEventListeners.add(inListener);
    }

    /**
     * Remove a strategy event listener.
     */
    @Override
    public void removeStrategyEventListener(StrategyEventListener inListener)
    {
        strategyEventListeners.remove(inListener);
    }

    /**
     * Send a strategy event to all registered listeners.
     */
    private void strategySendEvent(StrategyEvent event) {
        for (StrategyEventListener listener : strategyEventListeners) {
            try {
                listener.receiveStrategyEvent(event);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this, e);
                removeStrategyEventListener(listener);
            }
        }
    }

    /**
     * provides access to the {@link StrategyMessage} data store
     */
    @Autowired
    private StrategyMessageDao strategyMessageDao;

    /**
     * directory which is monitored for incoming strategies
     */
    private Path incomingStrategyDirectoryPath;

    /**
     * directory which is used to store uploaded strategies before they are verified
     */
    private Path temporaryStrategyDirectoryPath;

    /**
     * directory which is used to store uploaded strategies after they are verified
     */
    private Path storageStrategyDirectoryPath;

    /**
     * strategy storage directory base
     */
    @Value("${metc.strategy.storage.directory}")
    private String strategyStorageDirectoryName;

    /**
     * strategy incoming directory base
     */
    @Value("${metc.strategy.incoming.directory}")
    private String strategyIncomingDirectoryName;

    /**
     * strategy temporary directory base
     */
    @Value("${metc.strategy.temporary.directory}")
    private String strategyTemporaryDirectoryName;

    /**
     * name of this service
     */
    private String serviceName;

    /**
     * provides access to the {@link StrategyInstance} data store
     */
    @Autowired
    private StrategyInstanceDao strategyInstanceDao;

    /**
     * holds event listener subscribers
     */
    private final Set<StrategyEventListener> strategyEventListeners = new CopyOnWriteArraySet<>();
}