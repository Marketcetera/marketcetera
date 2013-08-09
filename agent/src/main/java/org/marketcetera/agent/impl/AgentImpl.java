package org.marketcetera.agent.impl;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.concurrent.ThreadSafe;

import org.marketcetera.agent.Agent;
import org.marketcetera.container.ApplicationContainer;
import org.marketcetera.service.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ThreadSafe
public class AgentImpl
        extends ApplicationContainer
        implements Agent
{
    /**
     * Provides an execution entry point.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String inArgs[])
    {
        ApplicationContainer.main(inArgs);
        /*
         * start:
         *  - identify capabilities
         *  - connect to infrastructure (using either multicast or a context-specified registry - or hazelcast?)
         *  - poll for jobs this agent can perform
         */
        instance = new AgentImpl();
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
        Lock startLock = lock.writeLock();
        try {
            startLock.lockInterruptibly();
            if(isRunning()) {
                stop();
            }
            running.set(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            startLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public void stop()
    {
        Lock stopLock = lock.writeLock();
        try {
            stopLock.lockInterruptibly();
            if(!isRunning()) {
                return;
            }
            // reset all object state
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            running.set(false);
            stopLock.unlock();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.agent.Agent#getServices()
     */
    @Override
    public Set<Service> getServices()
    {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * 
     */
    private static AgentImpl instance;
    /**
     * indicates if the agent is running or not
     */
    private final AtomicBoolean running = new AtomicBoolean();
    /**
     * provides lockable access to this object's state
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
}
