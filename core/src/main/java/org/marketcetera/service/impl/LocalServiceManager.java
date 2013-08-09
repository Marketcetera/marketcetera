package org.marketcetera.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang.Validate;
import org.marketcetera.service.DataFlow;
import org.marketcetera.service.Service;
import org.marketcetera.service.ServiceManager;

/* $License$ */

/**
 * <code>DataFlowManager</code> which is aware of <code>DataFlow</code> objects in the local space.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class LocalServiceManager
        implements ServiceManager
{
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#getActiveDataFlows()
     */
    @Override
    public Set<DataFlow> getRunningDataFlows()
    {
        Lock getLock = lock.readLock();
        try {
            getLock.lockInterruptibly();
            return Collections.unmodifiableSet(runningDataFlows);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            getLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#getAllDataFlows()
     */
    @Override
    public Set<DataFlow> getAllDataFlows()
    {
        Lock getLock = lock.readLock();
        try {
            getLock.lockInterruptibly();
            return Collections.unmodifiableSet(allDataFlows);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            getLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#addDataFlow(org.marketcetera.flow.DataFlow)
     */
    @Override
    public void addDataFlow(DataFlow inDataFlow)
    {
        Validate.notNull(inDataFlow);
        Lock addLock = lock.writeLock();
        try {
            addLock.lockInterruptibly();
            allDataFlows.add(inDataFlow);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            addLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#removeDataFlow(org.marketcetera.flow.DataFlow)
     */
    @Override
    public void removeDataFlow(DataFlow inDataFlow)
    {
        Validate.notNull(inDataFlow);
        Lock removeLock = lock.writeLock();
        try {
            removeLock.lockInterruptibly();
            allDataFlows.remove(inDataFlow);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            removeLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return running.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public void start()
    {
        if(isRunning()) {
            stop();
        }
        Lock startLock = lock.writeLock();
        try {
            startLock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            startLock.unlock();
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        if(!isRunning()) {
            return;
        }
        Lock stopLock = lock.writeLock();
        try {
            stopLock.lockInterruptibly();
            allDataFlows.clear();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            running.set(false);
            stopLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.SmartLifecycle#isAutoStartup()
     */
    @Override
    public boolean isAutoStartup()
    {
        return true;
    }
    /* (non-Javadoc)
     * @see org.springframework.context.SmartLifecycle#stop(java.lang.Runnable)
     */
    @Override
    public void stop(Runnable inCallback)
    {
        try {
            stop();
        } finally {
            inCallback.run();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Phased#getPhase()
     */
    @Override
    public int getPhase()
    {
        return 0;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#setDataFlows(java.util.List)
     */
    @Override
    public void setDataFlows(List<DataFlow> inDataFlows)
    {
        // TODO think about data flow start/stop semantics: this method was designed to be invoked
        //  from Spring config. When is this bean constructed and properties set? Have DataFlows been
        //  started by Spring yet? Calling this method would seem to want to start the passed DataFlows.
        //  Does that make sense? The data flows will be started when the manager is started. That is probably
        //  more consistent.
        Validate.notNull(inDataFlows);
        Lock stopLock = lock.writeLock();
        try {
            stopLock.lockInterruptibly();
            allDataFlows.addAll(inDataFlows);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stopLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#startDataFlow(org.marketcetera.flow.DataFlow)
     */
    @Override
    public void startDataFlow(DataFlow inDataFlow)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.flow.DataFlowManager#stopDataFlow(org.marketcetera.flow.DataFlow)
     */
    @Override
    public void stopDataFlow(DataFlow inDataFlow)
    {
        // TODO Auto-generated method stub
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#getRunningServices()
     */
    @Override
    public Set<Service> getRunningServices()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#getAllServices()
     */
    @Override
    public Set<Service> getAllServices()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#startService(org.marketcetera.service.Service)
     */
    @Override
    public void startService(Service inService)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#stopService(org.marketcetera.service.Service)
     */
    @Override
    public void stopService(Service inService)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#addService(org.marketcetera.service.Service)
     */
    @Override
    public void addService(Service inService)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#removeService(org.marketcetera.service.Service)
     */
    @Override
    public void removeService(Service inService)
    {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.marketcetera.service.ServiceManager#setServices(java.util.List)
     */
    @Override
    public void setServices(List<Service> inServices)
    {
        // TODO Auto-generated method stub
        
    }
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicBoolean running = new AtomicBoolean(false);
    @GuardedBy("lock")
    private final Set<DataFlow> allDataFlows = new HashSet<DataFlow>();
    private final Set<DataFlow> runningDataFlows = new HashSet<DataFlow>();
}
