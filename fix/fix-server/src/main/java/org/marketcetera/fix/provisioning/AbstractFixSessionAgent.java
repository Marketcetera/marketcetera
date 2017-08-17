package org.marketcetera.fix.provisioning;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.fix.FixSession;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Provides common FIX session agent behavior.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractFixSessionAgent
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        Timer invocationTimer = new Timer();
        invocationTimer.schedule(new TimerTask() {
            @Override
            public void run()
            {
                for(String sessionName : sessionNames) {
                    try {
                        FixSession fixSession = brokerService.findFixSessionByName(sessionName);
                        doSessionAction(fixSession);
                    } catch (Exception e) {
                        PlatformServices.handleException(this,
                                                         "Unable to " + getSessionActionDescription() + " " + sessionName,
                                                         e);
                    }
                }
            }},startDelay);
    }
    /**
     * Get the sessionNames value.
     *
     * @return a <code>List&lt;String&gt;</code> value
     */
    public List<String> getSessionNames()
    {
        return sessionNames;
    }
    /**
     * Sets the sessionNames value.
     *
     * @param inSessionNames a <code>List&lt;String&gt;</code> value
     */
    public void setSessionNames(List<String> inSessionNames)
    {
        sessionNames = inSessionNames;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Get the startDelay value.
     *
     * @return a <code>long</code> value
     */
    public long getStartDelay()
    {
        return startDelay;
    }
    /**
     * Sets the startDelay value.
     *
     * @param inStartDelay a <code>long</code> value
     */
    public void setStartDelay(long inStartDelay)
    {
        startDelay = inStartDelay;
    }
    /**
     * Perform the desired action of the agent on the given session.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @throws Exception if an error occurs performing the action
     */
    protected abstract void doSessionAction(FixSession inFixSession)
            throws Exception;
    /**
     * Provide a human-readable description of the action.
     *
     * @return a <code>String</code> value
     */
    protected abstract String getSessionActionDescription();
    /**
     * interval to wait before executing the action on start
     */
    private long startDelay = 10000;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * session names to activate
     */
    private List<String> sessionNames = Lists.newArrayList();
}
