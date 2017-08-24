package org.marketcetera.cluster.service;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.cluster.ClusterActivateWorkUnit;
import org.marketcetera.cluster.ClusterData;
import org.marketcetera.cluster.ClusterWorkUnit;
import org.marketcetera.cluster.ClusterWorkUnitDescriptor;
import org.marketcetera.cluster.ClusterWorkUnitSpec;
import org.marketcetera.cluster.ClusterWorkUnitType;
import org.marketcetera.cluster.ClusterWorkUnitUid;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides common behavior for a <code>ClusterService</code> implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: HazelcastClusterService.java 16827 2016-05-24 14:40:08Z colin $
 * @since $Release$
 */
public abstract class AbstractClusterService
        implements ClusterService, ClusterListener, ApplicationContextAware
{
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberAdded(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberAdded(ClusterMember inAddedMember)
    {
        SLF4JLoggerProxy.info(this,
                              "MemberAdded: {} [{}], making {} member(s)",
                              inAddedMember,
                              inAddedMember.getUuid(),
                              getClusterMembers().size());
        try {
            updateClusterMetaData();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberRemoved(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberRemoved(ClusterMember inRemovedMember)
    {
        SLF4JLoggerProxy.info(this,
                              "MemberRemoved: {} [{}], making {} member(s)",
                              inRemovedMember,
                              inRemovedMember.getUuid(),
                              getClusterMembers().size());
        try {
            // remember that this will run on all surviving members at effectively the same time, possibly even a dying member
            scheduleWorkUnitEvaluation();
            updateClusterMetaData();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterListener#memberChanged(com.marketcetera.matp.service.ClusterMember)
     */
    @Override
    public void memberChanged(ClusterMember inChangedMember)
    {
        SLF4JLoggerProxy.debug(this,
                               "MemberAttributeChanged: {}",
                               inChangedMember);
        try {
            updateClusterMetaData();
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
        }
    }
    /**
     * Validate and starts the object.
     */
    @PostConstruct
    public void start()
    {
        instanceName = getInstanceName();
        Validate.notNull(instanceName);
        memberUuid = getMemberUuid();
        String rawValue = StringUtils.trimToNull(System.getProperty("metc.instance"));
        if(rawValue != null) {
            try {
                instanceNumber = Integer.parseInt(rawValue);
            } catch (Exception ignored) {
                instanceNumber = 1;
            }
        }
        rawValue = StringUtils.trimToNull(System.getProperty("metc.max.instances"));
        if(rawValue != null) {
            try {
                totalInstances = Integer.parseInt(rawValue);
            } catch (Exception ignored) {
                totalInstances = 1;
            }
        }
        hostId = StringUtils.trimToNull(System.getProperty("metc.host"));
        if(hostId == null) {
            hostId = UUID.randomUUID().toString();
        }
        setAttribute("metc.host.uuid",
                     hostId);
        addClusterListener(this);
        hostNumber = getHostNumber(hostId);
        setAttribute("metc.host.number",
                     String.valueOf(hostNumber));
        SLF4JLoggerProxy.info(this,
                              "Instance {} assigned instance number {} of {} on host {} [{}]",
                              instanceName,
                              instanceNumber,
                              totalInstances,
                              hostNumber,
                              hostId);
        clusterData = generateClusterData(totalInstances,
                                          hostId,
                                          hostNumber,
                                          instanceNumber,
                                          memberUuid);
        workUnitSpecsEstablished = false;
        register(clusterData);
        SLF4JLoggerProxy.debug(this,
                               "Starting {}",
                               clusterData);
        scheduleWorkUnitEvaluation();
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        if(scheduledService != null) {
            try {
                scheduledService.shutdownNow();
            } catch (Exception ignored) {}
            scheduledService = null;
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterService#getInstanceData()
     */
    @Override
    public ClusterData getInstanceData()
    {
        return clusterData;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterService#addClusterListener(com.marketcetera.matp.service.ClusterListener)
     */
    @Override
    public void addClusterListener(ClusterListener inClusterListener)
    {
        clusterListeners.add(inClusterListener);
    }
    /* (non-Javadoc)
     * @see com.marketcetera.matp.service.ClusterService#removeClusterListener(com.marketcetera.matp.service.ClusterListener)
     */
    @Override
    public void removeClusterListener(ClusterListener inClusterListener)
    {
        clusterListeners.remove(inClusterListener);
    }
    /**
     * Get the lockTimeout value.
     *
     * @return a <code>long</code> value
     */
    public long getLockTimeout()
    {
        return lockTimeout;
    }
    /**
     * Sets the lockTimeout value.
     *
     * @param inLockTimeout a <code>long</code> value
     */
    public void setLockTimeout(long inLockTimeout)
    {
        lockTimeout = inLockTimeout;
    }
    /**
     * Get the abandonedLockTimeout value.
     *
     * @return a <code>long</code> value
     */
    public long getAbandonedLockTimeout()
    {
        return abandonedLockTimeout;
    }
    /**
     * Sets the abandonedLockTimeout value.
     *
     * @param inAbandonedLockTimeout a <code>long</code> value
     */
    public void setAbandonedLockTimeout(long inAbandonedLockTimeout)
    {
        abandonedLockTimeout = inAbandonedLockTimeout;
    }
    /**
     * Get the workUnitEvaluationDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getWorkUnitEvaluationDelay()
    {
        return workUnitEvaluationDelay;
    }
    /**
     * Sets the workUnitEvaluationDelay value.
     *
     * @param inWorkUnitEvaluationDelay a <code>long</code> value
     */
    public void setWorkUnitEvaluationDelay(long inWorkUnitEvaluationDelay)
    {
        workUnitEvaluationDelay = inWorkUnitEvaluationDelay;
    }
    /**
     * Get the instanceName value.
     *
     * @return a <code>String</code> value
     */
    public String getInstanceName()
    {
        return instanceName;
    }
    /**
     * Sets the instanceName value.
     *
     * @param inInstanceName a <code>String</code> value
     */
    public void setInstanceName(String inInstanceName)
    {
        instanceName = inInstanceName;
    }
    /**
     * Get the uuid of this cluster member.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getMemberUuid();
    /**
     * Get the host number for the given host id.
     *
     * @param inHostId a <code>String</code> value
     * @return an <code>int</code> value
     */
    protected abstract int getHostNumber(String inHostId);
    /**
     * Indicate if the cluster service is active.
     *
     * @return a <code>boolean</code> value
     */
    protected abstract boolean isActive();
    /**
     * Generate a <code>ClusteData</code> instance with the given attributes.
     *
     * @param inTotalInstances an <code>int</code> value
     * @param inHostId a <code>String</code> value
     * @param inHostNumber an <code>int</code> value
     * @param inInstanceNumber an <code>int</code> value
     * @param inMemberUuid a <code>String</code> value
     * @return a <code>ClusterData</code> value
     */
    protected ClusterData generateClusterData(int inTotalInstances,
                                              String inHostId,
                                              int inHostNumber,
                                              int inInstanceNumber,
                                              String inMemberUuid)
    {
        return new ClusterData(inTotalInstances,
                               inHostId,
                               inHostNumber,
                               inInstanceNumber,
                               inMemberUuid);
    }
    /**
     * Notify the cluster that the given member has been removed.
     * 
     * <p><code>AbstractClusterService</code> subclasses must call this method
     * when they detect that the given cluster member has been removed.
     *
     * @param inRemovedMember a <code>ClusterMember</code> value
     */
    protected void notifyMemberRemoved(ClusterMember inRemovedMember)
    {
        for(ClusterListener listener : clusterListeners) {
            try {
                listener.memberRemoved(inRemovedMember);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * Notify the cluster that the given member has been added.
     * 
     * <p><code>AbstractClusterService</code> subclasses must call this method
     * when they detect that the given cluster member has been added.
     *
     * @param inAddedMember a <code>ClusterMember</code> value
     */
    protected void notifyMemberAdded(ClusterMember inAddedMember)
    {
        for(ClusterListener listener : clusterListeners) {
            try {
                listener.memberAdded(inAddedMember);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * Notify the cluster that the given member has changed.
     * 
     * <p><code>AbstractClusterService</code> subclasses must call this method
     * when they detect that the attributes or state of the given cluster member
     * has changed.
     *
     * @param inChangedMember a <code>ClusterMember</code> value
     */
    protected void notifyMemberChanged(ClusterMember inChangedMember)
    {
        for(ClusterListener listener : clusterListeners) {
            try {
                listener.memberChanged(inChangedMember);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
    }
    /**
     * Evaluates all known work units and takes corrective action based on those finds as necessary.
     *
     * @throws InterruptedException if the method is interrupted while evaluating
     */
    protected void evaluateWorkUnits()
            throws InterruptedException
    {
        if(!isActive()) {
            return;
        }
        // some work unit specs need to be dynamically generated because part of the spec definition isn't known until runtime and can change
        // this collection will hold the set of dynamically generated cluster work unit specs we discover
        Set<ClusterWorkUnitSpec> dynamicSpecs = new HashSet<>();
        for(ClusterWorkUnitSpec currentWorkUnitSpec : requiredWorkUnitSpecs) {
            // the spec has to be a singleton runtime spec and the master definition for the type
            if(currentWorkUnitSpec.getWorkUnitType() == ClusterWorkUnitType.SINGLETON_RUNTIME && currentWorkUnitSpec.getWorkUnitUid().equals(ClusterWorkUnitSpec.MASTER_UID)) {
                // TODO need to remove/refresh current dynamically defined SR types?
                // this is the set of currently running candidates that implement this spec
                Set<Object> workUnitCandidates = findAllBeansWith(currentWorkUnitSpec,
                                                                  false);
                for(Object workUnitCandidate : workUnitCandidates) {
                    // the candidates are supposed to implement an annotation which provides a UID for this spec, further identifying it. this allows multiple instances of the same overall type
                    String uuid = getUuid(workUnitCandidate);
                    if(uuid != null) {
                        ClusterWorkUnitSpec dynamicSpec = new ClusterWorkUnitSpec(currentWorkUnitSpec.getWorkUnitType(),
                                                                                  currentWorkUnitSpec.getWorkUnitId(),
                                                                                  uuid);
                        dynamicSpecs.add(dynamicSpec);
                        SLF4JLoggerProxy.debug(this,
                                               "Generated dynamic work unit spec {} for {} from {}",
                                               dynamicSpec,
                                               currentWorkUnitSpec,
                                               workUnitCandidate);
                    } else {
                        // TODO no non-null UID definition
                    }
                }
            }
        }
        requiredWorkUnitSpecs.addAll(dynamicSpecs);
        for(ClusterWorkUnitSpec workUnit : requiredWorkUnitSpecs) {
            try {
                Set<ClusterWorkUnitDescriptor> localActiveWorkUnits = getActiveWorkUnitSpecs(workUnit);
                SLF4JLoggerProxy.debug(this,
                                       "Found {} active work unit(s) for {}",
                                       localActiveWorkUnits.size(),
                                       workUnit);
                switch(workUnit.getWorkUnitType()) {
                    case REPLICATED:
                        // this work unit can appear on many different members at the same time
                        // no particular effort needs to be made to start or stop these members
                        break;
                    case SINGLETON_RUNTIME:
                        if(ClusterWorkUnitSpec.MASTER_UID.equals(workUnit.getWorkUnitUid())) {
                            SLF4JLoggerProxy.debug(this,
                                                   "Skipping master singleton runtime work unit {}",
                                                   workUnit);
                            // this spec should not be addressed because it's simply a placeholder for dynamically generated specs
                            continue;
                        } else {
                            SLF4JLoggerProxy.debug(this,
                                                   "{} is a dynamic singleton runtime work unit, continuing",
                                                   workUnit);
                        }
                    case SINGLETON:
                        // this work unit must appear on one member at any one time and there must always be one instance of it
                        // we need to first identify what units are running out there - should be 0 or 1
                        if(localActiveWorkUnits.isEmpty()) {
                            SLF4JLoggerProxy.debug(this,
                                                   "Found no active work units for {}",
                                                   workUnit);
                            Object designation = findAndActivateWorkUnit(workUnit);
                            if(designation == null) {
                                // no candidate is available
                                SLF4JLoggerProxy.debug(this,
                                                       "This instance has no candidate for {}",
                                                       workUnit);
                                continue;
                            }
                            ClusterWorkUnitDescriptor newDescriptor = generateWorkUnitDescriptor(workUnit);
                            localActiveWorkUnits.add(newDescriptor);
                            activeWorkUnits.add(newDescriptor);
                            register(clusterData);
                            SLF4JLoggerProxy.debug(this,
                                                   "Successfully activated {}/{} for {}, making {}",
                                                   designation,
                                                   newDescriptor,
                                                   workUnit,
                                                   activeWorkUnits);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e,
                                      "Unable to evaluate and/or activate {}",
                                      workUnit);
            }
        }
    }
    /**
     * Gets the UUID of the given candidate if possible.
     *
     * @param inCandidate an <code>object</code> value or <code>null</code>
     * @return a <code>String</code> value
     */
    protected String getUuid(Object inCandidate)
    {
        List<Method> uidMethods = getMethod(inCandidate,
                                            ClusterWorkUnitUid.class);
        String uid = null;
        for(Method uidMethod : uidMethods) {
            // TODO multiple definitions implemented methods?
            try {
                Object rawValue = uidMethod.invoke(inCandidate);
                if(rawValue != null) {
                    uid = String.valueOf(rawValue);
                    break;
                } else {
                    // TODO skipping this candidate method because the returned value is null
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                // TODO something bad
            }
        }
        return uid;
    }
    /**
     * Generates the work unit descriptor for a the given work unit spec.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     * @return a <code>ClusterWorkUnitDescriptor</code> value
     */
    protected ClusterWorkUnitDescriptor generateWorkUnitDescriptor(ClusterWorkUnitSpec inWorkUnitSpec)
    {
        return new ClusterWorkUnitDescriptor(inWorkUnitSpec,
                                             memberUuid);
    }
    /**
     * Returns a candidate that suits the given work unit spec if one exists.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     * @return an <code>Object</code> value
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
    protected Object findCandidateFor(ClusterWorkUnitSpec inWorkUnitSpec)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        Set<Object> workUnitCandidates = findAllBeansWith(inWorkUnitSpec,
                                                          true);
        if(workUnitCandidates.isEmpty()) {
            return null;
        }
        return workUnitCandidates.iterator().next();
    }
    /**
     * Activates the given candidate.
     *
     * @param inCandidate an <code>Object</code> value
     * @throws IllegalAccessException if the object's activate method cannot be invoked
     * @throws IllegalArgumentException if the object has no or multiple activate methods
     * @throws InvocationTargetException if the object's activate method cannot be invoked
     */
    protected void activateWorkUnit(Object inCandidate)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        List<Method> activateMethods = getMethod(inCandidate,
                                                 ClusterActivateWorkUnit.class);
        if(activateMethods.isEmpty()) {
            throw new IllegalArgumentException(inCandidate + " cannot be activated because there are no methods marked with @" + ClusterActivateWorkUnit.class.getSimpleName());
        }
        if(activateMethods.size() > 1) {
            throw new IllegalArgumentException(inCandidate + " cannot be activated because there are multiple methods marked with @" + ClusterActivateWorkUnit.class.getSimpleName());
        }
        SLF4JLoggerProxy.debug(this,
                               "Activating {}",
                               inCandidate);
        activateMethods.get(0).invoke(inCandidate);
    }
    /**
     * Finds a candidate bean for the given work unit spec and activates it.
     *
     * @param inWorkUnitSpec a <code>ClusterWorkUnitSpec</code> value
     * @return an <code>Object</code>value
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Object findAndActivateWorkUnit(ClusterWorkUnitSpec inWorkUnitSpec)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // find me somebody that can do this job
        Object candidate = findCandidateFor(inWorkUnitSpec);
        if(candidate == null) {
            return null;
        }
        activateWorkUnit(candidate);
        setAttribute(inWorkUnitSpec.getWorkUnitId(),
                     String.valueOf(true));
        return candidate;
    }
    /**
     * Gets the currently active work units on this instance for the given spec.
     *
     * @param inWorkUnit a <code>ClusterWorkUnitSpec</code> value
     * @return a <code>Set&lt;ClusterWorkUnitDescriptor&gt;</code> value
     */
    protected Set<ClusterWorkUnitDescriptor> getActiveWorkUnitSpecs(ClusterWorkUnitSpec inWorkUnit)
    {
        SLF4JLoggerProxy.trace(this,
                               "Searching for active work units that match {}",
                               inWorkUnit);
        Set<ClusterWorkUnitDescriptor> activeWorkUnits = new HashSet<>();
        for(Map.Entry<String,ClusterMetaData> entry : clusterMetaData.entrySet()) {
            ClusterMetaData metaData = entry.getValue();
            SLF4JLoggerProxy.trace(this,
                                   "Examining {} with {}",
                                   metaData,
                                   metaData.getActiveWorkUnits());
            for(ClusterWorkUnitDescriptor workUnitSpec : metaData.getActiveWorkUnits()) {
                if(workUnitSpec.getWorkUnitSpec().equals(inWorkUnit)) {
                    activeWorkUnits.add(workUnitSpec);
                }
            }
        }
        return activeWorkUnits;
    }
    /**
     * Establishes the set of work unit specs for the cluster.
     *
     * @throws InterruptedException if the appropriate cluster lock cannot be established
     */
    protected void establishWorkUnitSpecs()
    {
        synchronized(requiredWorkUnitSpecs) {
            if(requiredWorkUnitSpecs.isEmpty()) {
                Map<String,Object> workUnits = findAllBeansWith(ClusterWorkUnit.class);
                SLF4JLoggerProxy.debug(this,
                                       "Found the following work units: {}",
                                       workUnits);
                for(Map.Entry<String,Object> entry : workUnits.entrySet()) {
                    ClusterWorkUnit workUnit = getTypeAnnotation(entry.getValue(),
                                                                 ClusterWorkUnit.class);
                    ClusterWorkUnitSpec workUnitSpec = new ClusterWorkUnitSpec(workUnit);
                    requiredWorkUnitSpecs.add(workUnitSpec);
                }
            }
        }
    }
    /**
     * Finds all beans with the given annotation.
     *
     * @param inWorkUnit a <code>ClusterWorkUnitSpec</code> value
     * @param inRequireUuidMatch a <code>boolean</code> value
     * @return a <code>Set&lt;Object&gt;</code> value
     */
    protected Set<Object> findAllBeansWith(ClusterWorkUnitSpec inWorkUnit,
                                           boolean inRequireUuidMatch)
    {
        Set<Object> beans = new HashSet<>();
        Map<String,Object> candidates = findAllBeansWith(ClusterWorkUnit.class);
        for(Map.Entry<String,Object> entry : candidates.entrySet()) {
            Object candidate = entry.getValue();
            ClusterWorkUnit workUnitDefinition = candidate.getClass().getAnnotation(ClusterWorkUnit.class);
            if(workUnitDefinition.id().equals(inWorkUnit.getWorkUnitId())) {
                if(inRequireUuidMatch) {
                    if(inWorkUnit.getWorkUnitUid() == null) {
                        // this work unit spec isn't dynamic (has no UUID), so this is a viable candidate
                        beans.add(candidate);
                        SLF4JLoggerProxy.debug(this,
                                               "{} is a viable candidate for {}",
                                               candidate,
                                               inWorkUnit);
                    } else {
                        // check to see if this candidate was a UUID and if it matches
                        String uuid = getUuid(candidate);
                        if(uuid == null) {
                            SLF4JLoggerProxy.debug(this,
                                                   "{} has no uuid, so is not a viable candidate for {}",
                                                   candidate,
                                                   inWorkUnit);
                        } else {
                            if(uuid.equals(inWorkUnit.getWorkUnitUid())) {
                                SLF4JLoggerProxy.debug(this,
                                                       "{} has uuid {}, and is a viable candidate for {}",
                                                       candidate,
                                                       uuid,
                                                       inWorkUnit);
                                beans.add(candidate);
                            } else {
                                SLF4JLoggerProxy.debug(this,
                                                       "{} has uuid {}, so is not a viable candidate for {}",
                                                       candidate,
                                                       uuid,
                                                       inWorkUnit);
                            }
                        }
                    }
                } else {
                    beans.add(candidate);
                }
            }
        }
        return beans;
    }
   /**
    * Finds all beans in the domain that implement the given annotation.
    *
    * @param inAnnotation a <code>Class&lt;? extends Annotation&gt;</code> value
    * @return a <code>Map&lt;String,Object&gt;</code> value
    */
    protected Map<String,Object> findAllBeansWith(Class<? extends Annotation> inAnnotation)
    {
        return applicationContext.getBeansWithAnnotation(inAnnotation);
    }
    /**
     * Gets the annotation of the given type from the given argument.
     *
     * @param inInstance an <code>Object</code> value
     * @param inAnnotation a <code>Class&lt;AnnotationClazz&gt;</code> value
     * @return an <code>AnnotationClazz</code> value
     * @throws IllegalArgumentException if the given annotation is not present
     */
    protected <Clazz,AnnotationClazz extends Annotation> AnnotationClazz getTypeAnnotation(Object inInstance,
                                                                                           Class<AnnotationClazz> inAnnotation)
    {
        if(inInstance.getClass().isAnnotationPresent(inAnnotation)) {
            return inInstance.getClass().getAnnotation(inAnnotation);
        }
        SLF4JLoggerProxy.warn(this,
                              "Could not find {} on {}",
                              inAnnotation.getSimpleName(),
                              inInstance);
        throw new IllegalArgumentException();
    }
    /**
     * Gets the methods on the given object that have the given annotation.
     *
     * @param inInstance an <code>Object</code> value
     * @param inAnnotation a <code>Class&lt;AnnotationClazz&gt;</code> value
     * @return a <code>List&lt;Method&gt;</code> value
     */
    protected <AnnotationClazz extends Annotation> List<Method> getMethod(Object inInstance,
                                                                          Class<AnnotationClazz> inAnnotation)
    {
        List<Method> methods = new ArrayList<>();
        for(Method method : inInstance.getClass().getMethods()) {
            if(method.isAnnotationPresent(inAnnotation)) {
                methods.add(method);
            }
        }
        return methods;
    }
    /**
     * Registers or updates the registry of the given cluster data on this instance.
     *
     * @param inClusterData a <code>ClusterData</code> value
     */
    protected void register(ClusterData inClusterData)
    {
        try {
            ClusterMetaData metaData = new ClusterMetaData(inClusterData,
                                                           activeWorkUnits);
            String xmlMetaData = marshall(metaData);
            setAttribute(metaDataPrefix+memberUuid,
                         xmlMetaData);
        } catch (JAXBException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Unable to update meta data");
            return;
        }
        SLF4JLoggerProxy.debug(this,
                               "Registering {} with {}",
                               inClusterData,
                               activeWorkUnits);
    }
    /**
     * Updates the meta data for the whole cluster.
     * 
     * @throws Exception if an error occurs updating cluster meta data
     */
    protected void updateClusterMetaData()
            throws Exception
    {
        SLF4JLoggerProxy.debug(this,
                               "Updating cluster meta data");
        Map<String,ClusterMetaData> tempResults = new HashMap<>();
        for(ClusterMember member : getClusterMembers()) {
            for(Map.Entry<String,String> attributeEntry : getAttributes(member.getUuid()).entrySet()) {
                String key = attributeEntry.getKey();
                if(key.startsWith(metaDataPrefix)) {
                    String rawValue = String.valueOf(attributeEntry.getValue());
                    ClusterMetaData status;
                    try {
                        status = (ClusterMetaData)unmarshall(rawValue);
                        tempResults.put(member.getUuid(),
                                        status);
                    } catch (Exception e) {
                        SLF4JLoggerProxy.warn(this,
                                              e,
                                              "Unable to update meta data");
                        return;
                    }
                }
            }
        }
        clusterMetaData.clear();
        clusterMetaData.putAll(tempResults);
        logClusterMetaData();
    }
    /**
     * Logs cluster meta data.
     */
    protected void logClusterMetaData()
    {
        Table table = new Table(4,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell("ClusterData",
                      cellStyle,
                      4);
        table.addCell("Cluster Member UUID",
                      cellStyle);
        table.addCell("Host UUID",
                      cellStyle);
        table.addCell("Cluster Data",
                      cellStyle);
        table.addCell("Active Work Units",
                      cellStyle);
        SortedSet<ClusterMetaData> sortedData = new TreeSet<>(clusterMetaData.values());
        for(ClusterMetaData metaData : sortedData) {
            table.addCell(metaData.getClusterData().getUuid(),
                          cellStyle);
            table.addCell(metaData.getClusterData().getHostId(),
                          cellStyle);
            table.addCell(metaData.getClusterData().toString(),
                          cellStyle);
            table.addCell(metaData.getActiveWorkUnits().toString(),
                          cellStyle);
        }
        String thisMetaDataLog = table.render();
        if(!thisMetaDataLog.equals(lastMetaDataLog)) {
            SLF4JLoggerProxy.info(AbstractClusterService.class,
                                  "{}{}",
                                  System.lineSeparator(),
                                  thisMetaDataLog);
        }
        lastMetaDataLog = thisMetaDataLog;
    }
    /**
     * Marshals the given value as XML.
     *
     * @param inData a <code>Clazz</code> value
     * @return a <code>String</code> value
     * @throws JAXBException if the given object cannot be marshaled
     */
    protected static <Clazz extends Serializable> String marshall(Clazz inData)
            throws JAXBException
    {
        synchronized(marshaller) {
            StringWriter output = new StringWriter();
            marshaller.marshal(inData,
                               output);
            return output.getBuffer().toString();
        }
    }
    /**
     * Unmarshalls the given XML to an object.
     *
     * @param inData a <code>String</code> value
     * @return an <code>Object</code> value
     * @throws JAXBException if an error occurs unmarshalling the given XML data
     */
    protected static Object unmarshall(String inData)
            throws JAXBException
    {
        synchronized(unmarshaller) {
            return unmarshaller.unmarshal(new InputStreamReader(new ByteArrayInputStream(inData.getBytes())));
        }
    }
    /**
     * Schedules work unit evaluation.
     */
    private void scheduleWorkUnitEvaluation()
    {
        SLF4JLoggerProxy.debug(this,
                               "Scheduling work unit evaluation");
        scheduledService.schedule(new WorkUnitEvaluationTask(),
                                  workUnitEvaluationDelay,
                                  TimeUnit.MILLISECONDS);
    }
    /**
     * Evaluates cluster work units.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id: HazelcastClusterService.java 16827 2016-05-24 14:40:08Z colin $
     * @since $Release$
     */
    protected class WorkUnitEvaluationTask
            implements Runnable
    {
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            // evaluate work units
            boolean reschedule = true;
            try {
                SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                       "Evaluating work units");
                if(!workUnitSpecsEstablished) {
                    establishWorkUnitSpecs();
                    workUnitSpecsEstablished = true;
                }
                Lock workUnitSpecLock = getLock(EVALUATE_WORK_UNITS_KEY);
                if(workUnitLockAttempt == 0) {
                    workUnitLockAttempt = System.currentTimeMillis();
                }
                if(workUnitSpecLock.tryLock(lockTimeout,
                                            TimeUnit.MILLISECONDS)) {
                    try {
                        setAttribute(EVALUATE_WORK_UNITS_KEY,
                                       String.valueOf(true));
                        evaluateWorkUnits();
                        reschedule = false;
                    } finally {
                        workUnitLockAttempt = 0;
                        removeAttribute(EVALUATE_WORK_UNITS_KEY);
                        workUnitSpecLock.unlock();
                    }
                } else {
                    long currentTime = System.currentTimeMillis();
                    if(currentTime > workUnitLockAttempt + abandonedLockTimeout) {
                        SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                               "Unable to secure lock to evaluate work units, we have been trying for {}ms, will check to see if the lock has been abandoned",
                                               currentTime-workUnitLockAttempt);
                        for(ClusterMember member : getClusterMembers()) {
                            for(Map.Entry<String,String> entry : getAttributes(member.getUuid()).entrySet()) {
                                if(entry.getKey().equals(EVALUATE_WORK_UNITS_KEY)) {
                                    SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                                           "Member {} holds the work units key lock, will not break at this time",
                                                           member);
                                    return;
                                }
                            }
                        }
                        SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                               "No member holds the work units key lock, it seems like the lock was abandoned, breaking the lock now");
                        try {
                            // TODO dispense with forceUnlock all this stuff?
//                            workUnitSpecLock.forceUnlock();
//                            SLF4JLoggerProxy.debug(AbstractClusterService.this,
//                                                   "Work units key lock was successfully unlocked, this or another cluster member will lock and proceed with the evaluation shortly");
//                            workUnitLockAttempt = 0;
                        } catch (Exception e) {
                            SLF4JLoggerProxy.warn(AbstractClusterService.this,
                                                  e,
                                                  "Could not break abandoned work units key lock, will try again later");
                        }
                    } else {
                        SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                               "Unable to secure lock to evaluate work units, we have been trying for {}ms, will try for {}ms before breaking lock",
                                               currentTime-workUnitLockAttempt,
                                               abandonedLockTimeout);
                    }
                }
            } catch (NullPointerException ignored) {
                // occurs on shutdown, can be safely ignored
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            } finally {
                if(reschedule) {
                    SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                           "Rescheduling work unit evaluation");
                    scheduledService.schedule(this,
                                              workUnitEvaluationDelay,
                                              TimeUnit.MILLISECONDS);
                } else {
                    SLF4JLoggerProxy.debug(AbstractClusterService.this,
                                           "Evaluation complete, not rescheduling");
                }
            }
        }
        /**
         * tracks attempts to unlock the work unit lock
         */
        private long workUnitLockAttempt;
    }
    /**
     * uniquely identifies a cluster member
     */
    private String memberUuid;
    /**
     * data which identifies this cluster member
     */
    private ClusterData clusterData;
    /**
     * cluster instance name to use instead of creating a new one
     */
    private String instanceName = "metc";
    /**
     * the number assigned to this host
     */
    private int hostNumber;
    /**
     * uniquely identifies this host in the cluster
     */
    private String hostId;
    /**
     * Spring application context
     */
    private ApplicationContext applicationContext;
    /**
     * the interval of time to wait for a lock
     */
    private long lockTimeout = 1000;
    /**
     * instance number of this cluster instance
     */
    private int instanceNumber = 1;
    /**
     * total number of instances on this physical node
     */
    private int totalInstances = 1;
    /**
     * indicates how long to wait before breaking an abandoned lock
     */
    private long abandonedLockTimeout = 30000;
    /**
     * determines how long to delay evaluation of work units
     */
    private long workUnitEvaluationDelay = 1000;
    /**
     * caches the last reported meta data
     */
    private String lastMetaDataLog = null;
    /**
     * indicates if work unit specs have been established yet or not
     */
    private volatile boolean workUnitSpecsEstablished;
    /**
     * the name of the work unit to evaluate work unit specs
     */
    private static final String EVALUATE_WORK_UNITS_KEY = "evaluate-work-unit-specs";
    /**
     * describes the style of the table cell
     */
    private static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
    /**
     * holds the meta data for the whole cluster
     */
    private static Map<String,ClusterMetaData> clusterMetaData = new ConcurrentHashMap<>();
    /**
     * holds the collection of currently active work units on this instance
     */
    private final Set<ClusterWorkUnitDescriptor> activeWorkUnits = new HashSet<>();
    /**
     * used to record work units that will be required
     */
    private final Set<ClusterWorkUnitSpec> requiredWorkUnitSpecs = new HashSet<>();
    /**
     * contains subscribed cluster listeners
     */
    private final Set<ClusterListener> clusterListeners = Sets.newConcurrentHashSet();
    /**
     * context used to marshal and unmarshal messages
     */
    private static final JAXBContext context;
    /**
     * marshals objects to XML
     */
    private static final Marshaller marshaller;
    /**
     * unmarshals objects from XML
     */
    private static final Unmarshaller unmarshaller;
    /**
     * indicates an attribute key
     */
    private static final String metaDataPrefix = "metc.data-";
    /**
     * schedules tasks
     */
    private ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();
    /**
     * Performs static initialization for this class
     * 
     * @throws RuntimeException if the initialization fails
     */
    static {
        try {
            context = JAXBContext.newInstance(ClusterMetaData.class);
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
            unmarshaller.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent inEvent)
                {
                    throw new RuntimeException(inEvent.getMessage(),
                                               inEvent.getLinkedException());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
