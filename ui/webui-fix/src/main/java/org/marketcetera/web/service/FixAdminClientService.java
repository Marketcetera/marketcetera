package org.marketcetera.web.service;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marketcetera.brokers.BrokerStatusListener;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixAdminClient;
import org.marketcetera.fix.FixAdminRpcClientFactory;
import org.marketcetera.fix.FixAdminRpcClientParameters;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.fix.FixSessionInstanceData;
import org.marketcetera.persist.CollectionPageResponse;
import org.marketcetera.persist.PageRequest;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Provides access to FIX services for a given user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixAdminClientService
        implements ConnectableService
{
    /**
     * Get the <code>AdminClientService</code> instance for the current session.
     *
     * @return an <code>AdminClientService</code> value or <code>null</code>
     */
    public static FixAdminClientService getInstance()
    {
        return ServiceManager.getInstance().getService(FixAdminClientService.class);
    }
    /**
     * Create a new FixAdminClientService instance.
     */
    public FixAdminClientService() {}
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#isRunning()
     */
    @Override
    public boolean isRunning()
    {
        return fixAdminClient != null && fixAdminClient.isRunning();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.ConnectableService#disconnect()
     */
    @Override
    public void disconnect()
    {
        if(fixAdminClient != null) {
            try {
                fixAdminClient.close();
            } catch (IOException e) {
                SLF4JLoggerProxy.warn(this,
                                      e);
            }
        }
        fixAdminClient = null;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.services.ConnectableService#connect(java.lang.String, java.lang.String, java.lang.String, int)
     */
    @Override
    public boolean connect(String inUsername,
                           String inPassword,
                           String inHostname,
                           int inPort)
            throws Exception
    {
        if(fixAdminClient != null) {
            try {
                fixAdminClient.stop();
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(this,
                                      "Unable to stop existing fix admin client for {}: {}",
                                      inUsername,
                                      ExceptionUtils.getRootCauseMessage(e));
            } finally {
                fixAdminClient = null;
            }
        }
        SLF4JLoggerProxy.debug(this,
                               "Creating fixAdmin client for {} to {}:{}",
                               inUsername,
                               inHostname,
                               inPort);
        FixAdminRpcClientParameters fixParams = new FixAdminRpcClientParameters();
        fixParams.setHostname(inHostname);
        fixParams.setPort(inPort);
        fixParams.setUsername(inUsername);
        fixParams.setPassword(inPassword);
        fixAdminClient = fixAdminClientFactory.create(fixParams);
        fixAdminClient.start();
        if(fixAdminClient.isRunning()) {
            VaadinSession.getCurrent().setAttribute(FixAdminClientService.class,
                                                    this);
        }
        return fixAdminClient.isRunning();
    }
    /**
     * Get FIX sessions.
     *
     * @return a <code>Collection&lt;ActiveFixSession&gt;</code> value
     */
    public Collection<ActiveFixSession> getFixSessions()
    {
        return fixAdminClient.readFixSessions();
    }
    /**
     * Get a page of FIX sessions.
     *
     * @param inPageRequest a <code>PageRequest</code> value
     * @return a <code>CollectionPageResponse&lt;ActiveFixSession&gt;</code> value
     */
    public CollectionPageResponse<ActiveFixSession> getFixSessions(PageRequest inPageRequest)
    {
        return fixAdminClient.readFixSessions(inPageRequest);
    }
    /**
     * Get the FIX session attribute descriptors.
     *
     * @return a <code>Collection&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    public Collection<FixSessionAttributeDescriptor> getFixSessionAttributeDescriptors()
    {
        return fixAdminClient.getFixSessionAttributeDescriptors();
    }
    /**
     * Create a new FIX session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @return a <code>FixSession</code> value
     */
    public FixSession createFixSession(FixSession inFixSession)
    {
        return fixAdminClient.createFixSession(inFixSession);
    }
    /**
     * Update the FIX session with the given original name.
     *
     * @param inIncomingName a <code>String</code> value
     * @param inFixSession a <code>FixSession</code> value
     */
    public void updateFixSession(String inIncomingName,
                                 FixSession inFixSession)
    {
        fixAdminClient.updateFixSession(inIncomingName,
                                        inFixSession);
    }
    /**
     * Enable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void enableSession(String inName)
    {
        fixAdminClient.enableFixSession(inName);
    }
    /**
     * Disable the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void disableSession(String inName)
    {
        fixAdminClient.disableFixSession(inName);
    }
    /**
     * Delete the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void deleteSession(String inName)
    {
        fixAdminClient.deleteFixSession(inName);
    }
    /**
     * Stop the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void stopSession(String inName)
    {
        fixAdminClient.stopFixSession(inName);
    }
    /**
     * Start the FIX session with the given name.
     *
     * @param inName a <code>String</code> value
     */
    public void startSession(String inName)
    {
        fixAdminClient.startFixSession(inName);
    }
    /**
     * Update sender and target sequence numbers for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    public void updateSequenceNumbers(String inSessionName,
                                      int inSenderSequenceNumber,
                                      int inTargetSequenceNumber)
    {
        fixAdminClient.updateSequenceNumbers(inSessionName,
                                             inSenderSequenceNumber,
                                             inTargetSequenceNumber);
    }
    /**
     * Update the sender sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inSenderSequenceNumber an <code>int</code> value
     */
    public void updateSenderSequenceNumber(String inSessionName,
                                           int inSenderSequenceNumber)
    {
        fixAdminClient.updateSenderSequenceNumber(inSessionName,
                                                  inSenderSequenceNumber);
    }
    /**
     * Update the target sequence number for the given session.
     *
     * @param inSessionName a <code>String</code> value
     * @param inTargetSequenceNumber an <code>int</code> value
     */
    public void updateTargetSequenceNumber(String inSessionName,
                                           int inTargetSequenceNumber)
    {
        fixAdminClient.updateTargetSequenceNumber(inSessionName,
                                                  inTargetSequenceNumber);
    }
    /**
     * Get the instance data for the given affinity.
     *
     * @param inAffinity an <code>int</code> value
     * @return an <code>InstanceData</code> value
     */
    public FixSessionInstanceData getFixSessionInstanceData(int inAffinity)
    {
        return fixAdminClient.getFixSessionInstanceData(inAffinity);
    }
    /**
     * Add the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    public void addBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        fixAdminClient.addBrokerStatusListener(inBrokerStatusListener);
    }
    /**
     * Remove the given broker status listener.
     *
     * @param inBrokerStatusListener a <code>BrokerStatusListener</code> value
     */
    public void removeBrokerStatusListener(BrokerStatusListener inBrokerStatusListener)
    {
        fixAdminClient.removeBrokerStatusListener(inBrokerStatusListener);
    }
    /**
     * Sets the fixAdminClientFactory value.
     *
     * @param inFixAdminClientFactory a <code>FixAdminRpcClientFactory</code> value
     */
    public void setFixAdminClientFactory(FixAdminRpcClientFactory inFixAdminClientFactory)
    {
        fixAdminClientFactory = inFixAdminClientFactory;
    }
    /**
     * creates a FIX admin client to connect to the fix admin server
     */
    private FixAdminRpcClientFactory fixAdminClientFactory;
    /**
     * provides access to FIX admin services
     */
    private FixAdminClient fixAdminClient;
}
