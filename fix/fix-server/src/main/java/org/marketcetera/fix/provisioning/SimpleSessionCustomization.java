package org.marketcetera.fix.provisioning;

import java.util.List;
import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.SessionCustomization;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a POJO {@link SessionCustomization} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleSessionCustomization
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
        return userWhitelist;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.SessionCustomization#getUserBlacklist()
     */
    @Override
    public Set<String> getUserBlacklist()
    {
        return userBlacklist;
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
     * @param inBrokerAlgos a <code>Set<BrokerAlgoSpec&gt;</code> value
     */
    public void setBrokerAlgos(Set<BrokerAlgoSpec> inBrokerAlgos)
    {
        brokerAlgos = inBrokerAlgos;
    }
    /**
     * Sets the userWhitelist value.
     *
     * @param inUserWhitelist a <code>Set<String&gt;</code> value
     */
    public void setUserWhitelist(Set<String> inUserWhitelist)
    {
        userWhitelist = inUserWhitelist;
    }
    /**
     * Sets the userBlacklist value.
     *
     * @param inUserBlacklist a <code>Set<String&gt;</code> value
     */
    public void setUserBlacklist(Set<String> inUserBlacklist)
    {
        userBlacklist = inUserBlacklist;
    }
    /**
     * name value
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
     * user whitelist value
     */
    private Set<String> userWhitelist = Sets.newHashSet();
    /**
     * user blacklist value
     */
    private Set<String> userBlacklist = Sets.newHashSet();
}
