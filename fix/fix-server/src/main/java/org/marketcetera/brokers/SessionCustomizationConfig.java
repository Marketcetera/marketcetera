package org.marketcetera.brokers;

import java.util.List;
import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a {@link SessionCustomization} implementation for use in configuration.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionCustomizationConfig
        implements SessionCustomization
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getOrderModifiers()
     */
    @Override
    public List<MessageModifier> getOrderModifiers()
    {
        return orderModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getResponseModifiers()
     */
    @Override
    public List<MessageModifier> getResponseModifiers()
    {
        return responseModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getBrokerAlgos()
     */
    @Override
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getUserWhitelist()
     */
    @Override
    public Set<String> getUserWhitelist()
    {
        return userWhiteList;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getUserBlacklist()
     */
    @Override
    public Set<String> getUserBlacklist()
    {
        return userBlackList;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getLogonActions()
     */
    @Override
    public List<LogonAction> getLogonActions()
    {
        return logonActions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getLogoutActions()
     */
    @Override
    public List<LogoutAction> getLogoutActions()
    {
        return logoutActions;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SessionCustomizationConfig [name=").append(name).append(", orderModifiers=")
                .append(orderModifiers).append(", responseModifiers=").append(responseModifiers)
                .append(", brokerAlgos=").append(brokerAlgos).append(", userWhiteList=").append(userWhiteList)
                .append(", userBlackList=").append(userBlackList).append(", logonActions=").append(logonActions)
                .append(", logoutActions=").append(logoutActions).append("]");
        return builder.toString();
    }
    /**
     * Get the userWhiteList value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getUserWhiteList()
    {
        return userWhiteList;
    }
    /**
     * Sets the userWhiteList value.
     *
     * @param inUserWhiteList a <code>Set&lt;String&gt;</code> value
     */
    public void setUserWhiteList(Set<String> inUserWhiteList)
    {
        userWhiteList = inUserWhiteList;
    }
    /**
     * Get the userBlackList value.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getUserBlackList()
    {
        return userBlackList;
    }
    /**
     * Sets the userBlackList value.
     *
     * @param inUserBlackList a <code>Set&lt;String&gt;</code> value
     */
    public void setUserBlackList(Set<String> inUserBlackList)
    {
        userBlackList = inUserBlackList;
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
     * Sets the orderModifiers value.
     *
     * @param inOrderModifiers a <code>List&lt;MessageModifier&gt;</code> value
     */
    public void setOrderModifiers(List<MessageModifier> inOrderModifiers)
    {
        orderModifiers = inOrderModifiers;
    }
    /**
     * Sets the responseModifiers value.
     *
     * @param inResponseModifiers a <code>List&lt;MessageModifier&gt;</code> value
     */
    public void setResponseModifiers(List<MessageModifier> inResponseModifiers)
    {
        responseModifiers = inResponseModifiers;
    }
    /**
     * Sets the brokerAlgos value.
     *
     * @param inBrokerAlgos a <code>Set&lt;BrokerAlgoSpec&gt;</code> value
     */
    public void setBrokerAlgos(Set<BrokerAlgoSpec> inBrokerAlgos)
    {
        brokerAlgos = inBrokerAlgos;
    }
    /**
     * Sets the logonActions value.
     *
     * @param inLogonActions a <code>List&lt;LogonAction&gt;</code> value
     */
    public void setLogonActions(List<LogonAction> inLogonActions)
    {
        logonActions = inLogonActions;
    }
    /**
     * Sets the logoutActions value.
     *
     * @param inLogoutActions a <code>List&lt;LogoutAction&gt;</code> value
     */
    public void setLogoutActions(List<LogoutAction> inLogoutActions)
    {
        logoutActions = inLogoutActions;
    }
    /**
     * name value;
     */
    private String name;
    /**
     * order modifiers value
     */
    private List<MessageModifier> orderModifiers = Lists.newArrayList();
    /**
     * response modifiers value
     */
    private List<MessageModifier> responseModifiers = Lists.newArrayList();
    /**
     * broker algos value
     */
    private Set<BrokerAlgoSpec> brokerAlgos = Sets.newHashSet();
    /**
     * user white list value
     */
    private Set<String> userWhiteList = Sets.newHashSet();
    /**
     * user black list value
     */
    private Set<String> userBlackList = Sets.newHashSet();
    /**
     * logon actions value
     */
    private List<LogonAction> logonActions = Lists.newArrayList();
    /**
     * logout actions value
     */
    private List<LogoutAction> logoutActions = Lists.newArrayList();
}
