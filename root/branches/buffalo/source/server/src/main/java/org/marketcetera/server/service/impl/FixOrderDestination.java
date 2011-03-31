package org.marketcetera.server.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.server.service.*;
import org.marketcetera.systemmodel.OrderDestinationID;
import org.marketcetera.trade.Order;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.beans.factory.InitializingBean;

/* $License$ */

/**
 * Represents a FIX destination to which orders can be sent.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
class FixOrderDestination
        implements OrderDestination, InitializingBean, UpdatableStatus, HasFixMessageModifiers
{
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getStatus()
     */
    @Override
    public DestinationStatus getStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getId()
     */
    @Override
    public OrderDestinationID getId()
    {
        return id;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#send(org.marketcetera.trade.Order)
     */
    @Override
    public void send(Order inOrder)
    {
        SLF4JLoggerProxy.debug(FixOrderDestination.class,
                               "Routing {} to the FIX port",
                               inOrder);
        engine.send(inOrder,
                    this);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#start()
     */
    @Override
    public synchronized void start()
    {
        if(isRunning()) {
            SLF4JLoggerProxy.warn(FixOrderDestination.class,
                                  "{} already started",
                                  this);
            return;
        }
        try {
            engine.start();
        } catch (Exception e) {
            SLF4JLoggerProxy.error(FixOrderDestination.class,
                                   e,
                                   "Unable to start {}",
                                   this);
            stop();
            if(e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new RuntimeException(e);
        }
        running.set(true);
    }
    /* (non-Javadoc)
     * @see org.springframework.context.Lifecycle#stop()
     */
    @Override
    public synchronized void stop()
    {
        if(!isRunning()) {
            SLF4JLoggerProxy.warn(FixOrderDestination.class,
                                  "{} already stopped",
                                  this);
            return;
        }
        try {
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "{} stopping {}",
                                   this,
                                   engine);
            engine.stop();
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "{} successfully stopped",
                                   engine);
        } finally {
            running.set(false);
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("%s [%s] - %s",
                             name,
                             id,
                             status);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.OrderDestination#getNextId()
     */
    @Override
    public long getNextId()
    {
        return idFactory.getId();
    }
    /**
     * Sets the name value.
     *
     * @param a <code>String</code> value
     */
    public void setName(String inName)
    {
        inName = StringUtils.trimToNull(inName);
        Validate.notNull(inName,
                         "Destination name must not be null");
        name = inName;
    }
    /**
     * Sets the id value.
     *
     * @param an <code>OrderDestinationID</code> value
     */
    public void setId(OrderDestinationID inId)
    {
        Validate.notNull(inId,
                         "Destination id must not be null");
        id = inId;
    }
    /**
     * Get the engine value.
     *
     * @return a <code>FixEngine</code> value
     */
    public FixEngine getEngine()
    {
        return engine;
    }
    /**
     * Sets the engine value.
     *
     * @param a <code>FixEngine</code> value
     */
    public void setEngine(FixEngine inEngine)
    {
        engine = inEngine;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(getName(),
                         "Destination name must not be null");
        Validate.notNull(getId(),
                         "Destination id must not be null");
        Validate.notNull(getEngine(),
                         "Destination needs a Fix engine");
        Validate.notNull(getIdFactory(),
                         "Destination needs an id factory");
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.UpdatableStatus#setStatus(org.marketcetera.server.service.DestinationStatus)
     */
    @Override
    public void setStatus(DestinationStatus inNewStatus)
    {
        if(!inNewStatus.equals(status)) {
            status = inNewStatus;
            SLF4JLoggerProxy.debug(FixOrderDestination.class,
                                   "Status of FIX order destination {} changed to {}",
                                   id,
                                   status);
            synchronized(this) {
                notifyAll();
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.HasFixMessageModifiers#getPreSendOrderModifiers()
     */
    @Override
    public List<MessageModifier> getPreSendMessageModifiers()
    {
        return preSendMessageModifiers;
    }
    /**
     * 
     *
     *
     * @param inPreSendMessageModifiers
     */
    public void setPreSendMessageModifiers(List<MessageModifier> inPreSendMessageModifiers)
    {
        preSendMessageModifiers = inPreSendMessageModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.server.service.HasFixMessageModifiers#getResponseOrderModifiers()
     */
    @Override
    public List<MessageModifier> getResponseMessageModifiers()
    {
        return responseMessageModifiers;
    }
    /**
     * 
     *
     *
     * @param inResponseMessageModifiers
     */
    public void setResponseMessageModifiers(List<MessageModifier> inResponseMessageModifiers)
    {
        responseMessageModifiers = inResponseMessageModifiers;
    }
    /**
     * Get the idFactory value.
     *
     * @return an <code>IdFactory</code> value
     */
    public IdFactory getIdFactory()
    {
        return idFactory;
    }
    /**
     * Sets the idFactory value.
     *
     * @param an <code>IdFactory</code> value
     */
    public void setIdFactory(IdFactory inIdFactory)
    {
        idFactory = inIdFactory;
    }
    /**
     * message modifiers to apply to incoming (received) messages 
     */
    private volatile List<MessageModifier> responseMessageModifiers;
    /**
     * message modifiers to apply to outgoing (sent) messages
     */
    private volatile List<MessageModifier> preSendMessageModifiers;
    /**
     * the status of the order destination
     */
    private volatile DestinationStatus status = DestinationStatus.UNKNOWN;
    /**
     * the name of the order destination, human-readable
     */
    private volatile String name;
    /**
     * the id of the order destination
     */
    private volatile OrderDestinationID id;
    /**
     * 
     */
    private volatile IdFactory idFactory;
    /**
     * the physical FIX engine through which messages are sent and received
     */
    private volatile FixEngine engine;
    /**
     * indicates if the destination is running
     */
    private final AtomicBoolean running = new AtomicBoolean(false);
}
