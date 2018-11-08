package com.marketcetera.ors.brokers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.lang.Validate;
import org.marketcetera.algo.BrokerAlgoSpec;

import com.marketcetera.ors.config.LogonAction;
import com.marketcetera.ors.config.LogoutAction;
import com.marketcetera.ors.filters.MessageModifierManager;
import com.marketcetera.ors.filters.MessageRouteManager;

/* $License$ */

/**
 * Contains customizations to be applied to a FIX session.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
public class SessionCustomization
{
    /**
     * Validates and starts the object. 
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(name);
    }
    /**
     * Get the modifiers value.
     *
     * @return a <code>MessageModifierManager</code> value
     */
    public MessageModifierManager getMessageModifiers()
    {
        return messageModifiers;
    }
    /**
     * Sets the modifiers value.
     *
     * @param inModifiers a <code>MessageModifierManager</code> value
     */
    public void setMessageModifiers(MessageModifierManager inModifiers)
    {
        messageModifiers = inModifiers;
    }
    /**
     * Get the routes value.
     *
     * @return a <code>MessageRouteManager</code> value
     */
    public MessageRouteManager getRoutes()
    {
        return routes;
    }
    /**
     * Sets the routes value.
     *
     * @param inRoutes a <code>MessageRouteManager</code> value
     */
    public void setRoutes(MessageRouteManager inRoutes)
    {
        routes = inRoutes;
    }
    /**
     * Get the preSendModifiers value.
     *
     * @return a <code>MessageModifierManager</code> value
     */
    public MessageModifierManager getPreSendModifiers()
    {
        return preSendModifiers;
    }
    /**
     * Sets the preSendModifiers value.
     *
     * @param inPreSendModifiers a <code>MessageModifierManager</code> value
     */
    public void setPreSendModifiers(MessageModifierManager inPreSendModifiers)
    {
        preSendModifiers = inPreSendModifiers;
    }
    /**
     * Get the responseModifiers value.
     *
     * @return a <code>MessageModifierManager</code> value
     */
    public MessageModifierManager getResponseModifiers()
    {
        return responseModifiers;
    }
    /**
     * Sets the responseModifiers value.
     *
     * @param inResponseModifiers a <code>MessageModifierManager</code> value
     */
    public void setResponseModifiers(MessageModifierManager inResponseModifiers)
    {
        responseModifiers = inResponseModifiers;
    }
    /**
     * Get the logonActions value.
     *
     * @return a <code>List&lt;LogonAction&gt;</code> value
     */
    public List<LogonAction> getLogonActions()
    {
        return logonActions;
    }
    /**
     * Sets the logonActions value.
     *
     * @param inLogonActions a <code>List&lt;LogonAction&gt;</code> value
     */
    public void setLogonActions(List<LogonAction> inLogonActions)
    {
        logonActions.clear();
        if(inLogonActions != null) {
            logonActions.addAll(inLogonActions);
        }
    }
    /**
     * Get the logoutActions value.
     *
     * @return a <code>List&lt;LogoutAction&gt;</code> value
     */
    public List<LogoutAction> getLogoutActions()
    {
        return logoutActions;
    }
    /**
     * Sets the logoutActions value.
     *
     * @param inLogoutActions a <code>List&lt;LogoutAction&gt;</code> value
     */
    public void setLogoutActions(List<LogoutAction> inLogoutActions)
    {
        logoutActions.clear();
        if(inLogoutActions != null) {
            logoutActions.addAll(inLogoutActions);
        }
    }
    /**
     * Get the userWhitelist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value or <code>null</code>
     */
    public Set<String> getUserWhitelist()
    {
        return userWhitelist;
    }
    /**
     * Sets the userWhitelist value.
     *
     * @param inUserWhitelist a <code>Set&lt;String&gt;</code> value
     */
    public void setUserWhitelist(Set<String> inUserWhitelist)
    {
        userWhitelist = inUserWhitelist;
    }
    /**
     * Get the userBlacklist value.
     *
     * @return a <code>Set&lt;String&gt;</code> value or <code>null</code>
     */
    public Set<String> getUserBlacklist()
    {
        return userBlacklist;
    }
    /**
     * Sets the userBlacklist value.
     *
     * @param inUserBlacklist a <code>Set&lt;String&gt;</code> value
     */
    public void setUserBlacklist(Set<String> inUserBlacklist)
    {
        userBlacklist = inUserBlacklist;
    }
    /**
     * Get the name value.
     *
     * @return a <code>String</code> value
     */
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /**
     * Get the brokerAlgos value.
     *
     * @return a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /**
     * Sets the brokerAlgos value.
     *
     * @param a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public void setBrokerAlgos(Set<BrokerAlgoSpec> inBrokerAlgos)
    {
        brokerAlgos = inBrokerAlgos;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SessionCustomization [name=").append(name).append("]");
        return builder.toString();
    }
    /**
     * name of the session customization
     */
    private String name;
    /**
     * message modifiers value
     */
    private MessageModifierManager messageModifiers;
    /**
     * routes value
     */
    private MessageRouteManager routes;
    /**
     * pre-send message modifiers value
     */
    private MessageModifierManager preSendModifiers;
    /**
     * response modifiers value
     */
    private MessageModifierManager responseModifiers;
    /**
     * logon actions value
     */
    private final List<LogonAction> logonActions = new ArrayList<LogonAction>();
    /**
     * logout actions alue
     */
    private final List<LogoutAction> logoutActions = new ArrayList<LogoutAction>();
    /**
     * user whitelist value
     */
    private Set<String> userWhitelist;
    /**
     * user blacklist value
     */
    private Set<String> userBlacklist;
    /**
     * broker algos value
     */
    private Set<BrokerAlgoSpec> brokerAlgos;
}
