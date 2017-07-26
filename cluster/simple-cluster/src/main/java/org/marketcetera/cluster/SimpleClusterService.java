package org.marketcetera.cluster;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.cluster.service.ClusterListener;
import org.marketcetera.cluster.service.ClusterMember;
import org.marketcetera.cluster.service.ClusterService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a single node {@link ClusterService} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleClusterService
        implements ClusterService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              this.getClass().getSimpleName());
        instanceData = new ClusterData(1,
                                                   "host1-1",
                                                   1,
                                                   1,
                                                   UUID.randomUUID().toString());
        clusterMembers = Collections.unmodifiableSet(Sets.newHashSet(new ClusterMember() {
            @Override
            public String getUuid()
            {
                return instanceData.getUuid();
            }}));
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              this.getClass().getSimpleName());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#addToQueue(org.marketcetera.matp.cluster.QueueDescriptor, java.io.Serializable)
     */
    @Override
    public <Clazz extends Serializable> void addToQueue(QueueDescriptor<Clazz> inQueueDescriptor,
                                                        Clazz inObject)
    {
        getQueue(inQueueDescriptor).add(inObject);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#peekFromQueue(org.marketcetera.matp.cluster.QueueDescriptor)
     */
    @Override
    public <Clazz extends Serializable> Clazz peekFromQueue(QueueDescriptor<Clazz> inQueueDescriptor)
    {
        return getQueue(inQueueDescriptor).peek();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#takeFromQueue(org.marketcetera.matp.cluster.QueueDescriptor)
     */
    @Override
    public <Clazz extends Serializable> Clazz takeFromQueue(QueueDescriptor<Clazz> inQueueDescriptor)
            throws InterruptedException
    {
        return getQueue(inQueueDescriptor).take();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#execute(org.marketcetera.matp.cluster.RunnableClusterTask)
     */
    @Override
    public void execute(RunnableClusterTask inTask)
    {
        PlatformServices.autowire(inTask,
                                  applicationContext);
        getExecutorService(inTask.getPoolName()).execute(inTask);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#execute(org.marketcetera.matp.cluster.CallableClusterTask)
     */
    @Override
    public <Clazz extends Serializable> Map<Object,Future<Clazz>> execute(CallableClusterTask<Clazz> inTask)
            throws Exception
    {
        PlatformServices.autowire(inTask,
                                  applicationContext);
        Future<Clazz> token = getExecutorService(inTask.getPoolName()).submit(inTask);
        Map<Object,Future<Clazz>> results = Maps.newHashMap();
        results.put(instanceData,
                    token);
        return Collections.unmodifiableMap(results);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getMap(java.lang.String)
     */
    @Override
    public Map<String,String> getMap(String inMapName)
    {
        return Collections.unmodifiableMap(getPrivateMap(inMapName));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#addToMap(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String addToMap(String inMapName,
                           String inKey,
                           String inValue)
    {
        return getPrivateMap(inMapName).put(inKey,inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#removeFromMap(java.lang.String, java.lang.String)
     */
    @Override
    public String removeFromMap(String inMapName,
                                String inKey)
    {
        return getPrivateMap(inMapName).remove(inKey);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getInstanceData()
     */
    @Override
    public ClusterData getInstanceData()
    {
        return instanceData;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#setAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void setAttribute(String inKey,
                             String inValue)
    {
        getPrivateMap(attributeMapName).put(inKey,
                                            inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getAttribute(java.lang.String)
     */
    @Override
    public String getAttribute(String inKey)
    {
        return getPrivateMap(attributeMapName).get(inKey);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getAttributes(java.lang.String)
     */
    @Override
    public Map<String,String> getAttributes(String inUuid)
    {
        if(instanceData.getUuid().equals(inUuid)) {
            return Collections.unmodifiableMap(getPrivateMap(attributeMapName));
        } else {
            return Collections.emptyMap();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getAttributes()
     */
    @Override
    public Map<String,Map<String,String>> getAttributes()
    {
        Map<String,Map<String,String>> allAttributes = Maps.newHashMap();
        allAttributes.put(instanceData.getUuid(),
                          Collections.unmodifiableMap(getPrivateMap(attributeMapName)));
        return Collections.unmodifiableMap(allAttributes);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#removeAttribute(java.lang.String)
     */
    @Override
    public void removeAttribute(String inKey)
    {
        getPrivateMap(attributeMapName).remove(inKey);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#removeAttribute(java.lang.String, java.lang.String)
     */
    @Override
    public void removeAttribute(String inUuid,
                                String inKey)
    {
        if(instanceData.getUuid().equals(inUuid)) {
            getPrivateMap(attributeMapName).remove(inKey);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#addClusterListener(org.marketcetera.matp.cluster.service.ClusterListener)
     */
    @Override
    public void addClusterListener(ClusterListener inClusterListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#removeClusterListener(org.marketcetera.matp.cluster.service.ClusterListener)
     */
    @Override
    public void removeClusterListener(ClusterListener inClusterListener)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getLock(java.lang.String)
     */
    @Override
    public Lock getLock(String inLockName)
    {
        return getPrivateLock(inLockName);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.matp.cluster.service.ClusterService#getClusterMembers()
     */
    @Override
    public Set<ClusterMember> getClusterMembers()
    {
        return clusterMembers;
    }
    /**
     * Get the queue for the given descriptor.
     *
     * @param inQueueDescriptor a <code>QueueDescriptor</code> value
     * @return a <code>BlockingDeque&lt;Clazz&gt;</code>value
     */
    @SuppressWarnings("unchecked")
    private <Clazz extends Serializable> BlockingDeque<Clazz> getQueue(QueueDescriptor<Clazz> inQueueDescriptor)
    {
        return (BlockingDeque<Clazz>)queues.getUnchecked(inQueueDescriptor.getQueuename());
    }
    /**
     * Get the map with the given name.
     *
     * @param inMapName a <code>String</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    private Map<String,String> getPrivateMap(String inMapName)
    {
        return maps.getUnchecked(inMapName);
    }
    /**
     * Get the executor service for the given pool name.
     *
     * @param inPoolName a <code>String</code> value
     * @return an <code>ExecutorService</code> value
     */
    private ExecutorService getExecutorService(String inPoolName)
    {
        return executorServices.getUnchecked(inPoolName);
    }
    /**
     * Get the lock with the given name.
     *
     * @param inLockName a <code>String</code> value
     * @return a <code>Lock</code> value
     */
    private Lock getPrivateLock(String inLockName)
    {
        return locks.getUnchecked(inLockName);
    }
    /**
     * static cluster member collection
     */
    private Set<ClusterMember> clusterMembers;
    /**
     * identifies the local cluster instance
     */
    private ClusterData instanceData;
    /**
     * provides access to the application context
     */
    @Autowired
    private ApplicationContext applicationContext;
    /**
     * name assigned to the attribute map
     */
    private static final String attributeMapName = UUID.randomUUID().toString();
    /**
     * cluster executors
     */
    private final LoadingCache<String,ExecutorService> executorServices = CacheBuilder.newBuilder().build(new CacheLoader<String,ExecutorService>() {
        @Override
        public ExecutorService load(String inKey)
                throws Exception
        {
            return Executors.newCachedThreadPool();
        }});
    /**
     * cluster queues
     */
    private final LoadingCache<String,BlockingDeque<?>> queues = CacheBuilder.newBuilder().build(new CacheLoader<String,BlockingDeque<?>>() {
        @Override
        public BlockingDeque<?> load(String inKey)
                throws Exception
        {
            return Queues.newLinkedBlockingDeque();
        }});
    /**
     * cluster maps
     */
    private final LoadingCache<String,Map<String,String>> maps = CacheBuilder.newBuilder().build(new CacheLoader<String,Map<String,String>>() {
        @Override
        public Map<String,String> load(String inKey)
                throws Exception
        {
            return Maps.newConcurrentMap();
        }});
    /**
     * cluster locks
     */
    private final LoadingCache<String,Lock> locks = CacheBuilder.newBuilder().build(new CacheLoader<String,Lock>() {
        @Override
        public Lock load(String inKey)
                throws Exception
        {
            return new ReentrantLock();
        }});
}
