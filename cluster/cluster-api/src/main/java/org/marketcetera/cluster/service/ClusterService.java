package org.marketcetera.cluster.service;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.marketcetera.cluster.CallableClusterTask;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.QueueDescriptor;
import org.marketcetera.cluster.RunnableClusterTask;

/* $License$ */

/**
 * Provides cluster services for the Marketcetera Automated Trading Platform.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ClusterService.java 17134 2017-01-27 16:41:54Z colin $
 * @since 2.5.0
 */
public interface ClusterService
{
    /**
     * Adds the given object to the given cluster queue.
     *
     * @param inQueueDescriptor a <code>QueueDescriptor&lt;Class&gt;</code> value
     * @param inObject a <code>Clazz</code> value
     */
    <Clazz extends Serializable> void addToQueue(QueueDescriptor<Clazz> inQueueDescriptor,
                                                 Clazz inObject);
    /**
     * Peeks the top value in the given queue.
     *
     * @param inQueueDescriptor a <code>QueueDescriptor&lt;Class&gt;</code> value
     * @return a <code>Clazz</code> value or <code>null</code>
     */
    <Clazz extends Serializable> Clazz peekFromQueue(QueueDescriptor<Clazz> inQueueDescriptor);
    /**
     * Takes the head value from the given cluster queue.
     * 
     * <p>This call will block until a value becomes available.
     *
     * @return a <code>QueueDescriptor&lt;Class&gt;</code> value
     * @throws InterruptedException if this call is interrupted
     */
    <Clazz extends Serializable> Clazz takeFromQueue(QueueDescriptor<Clazz> inQueueDescriptor)
            throws InterruptedException;
    /**
     * Asynchronously executes the given task on members of the cluster as determined by the task.
     *
     * @param inTask a <code>RunnableClusterTask</code> value
     */
    void execute(RunnableClusterTask inTask);
    /**
     * Asynchronously executes the given task on members of the cluster as determined by the task.
     *
     * @param inTask a <code>CallableClusterTask&lt;Clazz&gt;</code> value
     * @return an <code>Map&lt;Object,Future&lt;Clazz&gt;</code> value containing the result tokens by cluster member
     * @throws Exception if an error occurs executing the task
     */
    <Clazz extends Serializable> Map<Object,Future<Clazz>> execute(CallableClusterTask<Clazz> inTask)
            throws Exception;
    /**
     * Gets a cluster-aware map with the given name.
     *
     * <p>All calls to this method with the same name will return the same data structure, creating it if necessary.
     * 
     * <p>There is no guarantee that this map is modifiable or that changes made to this map will be reflected in the
     * cluster. Use {@link #addToMap(String, String, String)} and {{@link #removeFromMap(String, String)} instead.
     *
     * @param inMapName a <code>String</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    Map<String,String> getMap(String inMapName);
    /**
     * Add the given key/value pair to the given map, creating it, if necessary.
     *
     * @param inMapName a <code>String</code> value
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     * @return a <code>String</code> value containing the value previously contained in the map, or <code>null</code>
     */
    String addToMap(String inMapName,
                    String inKey,
                    String inValue);
    /**
     * Remove the key/value pair with the given key from the given map.
     *
     * @param inMapName a <code>String</code> value
     * @param inKey a <code>String</code> value
     * @return a <code>String</code> value containing the value previously contained in the map, or <code>null</code>
     */
    String removeFromMap(String inMapName,
                         String inKey);
    /**
     * Gets the instance data of this cluster member.
     *
     * @return a <code>ClusterData</code> value
     */
    ClusterData getInstanceData();
    /**
     * Set the given key/value pair on the local cluster member.
     *
     * @param inKey a <code>String</code> value
     * @param inValue a <code>String</code> value
     */
    void setAttribute(String inKey,
                      String inValue);
    /**
     * Get the attribute with the given key for the local cluster member.
     *
     * @param inKey a <code>String<code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    String getAttribute(String inKey);
    /**
     * Get the attributes for the given cluster member.
     *
     * @param inUuid a <code>String</code> value
     * @return a <code>Map&lt;String,String&gt;</code> value
     */
    Map<String,String> getAttributes(String inUuid);
    /**
     * Get the attributes for all cluster members.
     *
     * @return a <code>Map&lt;String,Map&l;String,String&gt;&gt;</code> value
     */
    Map<String,Map<String,String>> getAttributes();
    /**
     * Remove the attribute with the given key on the local cluster member.
     *
     * @param inKey a <code>String</code> value
     */
    void removeAttribute(String inKey);
    /**
     * Remove the attribute with the given key on the given cluster member.
     *
     * @param inUuid a <code>String</code> value
     * @param inKey a <code>String</code> value
     */
    void removeAttribute(String inUuid,
                         String inKey);
    /**
     * Add the given cluster listener.
     *
     * @param inClusterListener a <code>ClusterListener</code> value
     */
    void addClusterListener(ClusterListener inClusterListener);
    /**
     * Remove the given cluster listener.
     *
     * @param inClusterListener a <code>ClusterListener</code> value
     */
    void removeClusterListener(ClusterListener inClusterListener);
    /**
     * Get the lock with the given name.
     *
     * @param inLockName a <code>String</code> value
     * @return a <code>Lock</code> value
     */
    Lock getLock(String inLockName);
    /**
     * Get the members of the cluster.
     *
     * @return a <code>Set&lt;ClusterMember&gt;</code> value
     */
    Set<ClusterMember> getClusterMembers();
}
