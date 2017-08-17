package org.marketcetera.fix.impl;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickfix.DataDictionary;
import quickfix.Session;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a POJO {@link Broker} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleBroker
        implements Broker
{
    /**
     * Create a new SimpleBroker instance.
     *
     * @param inFixSession a <code>FixSession</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value
     */
    SimpleBroker(FixSession inFixSession,
                 SessionCustomization inSessionCustomization)
    {
        fixSession = inFixSession;
        if(inSessionCustomization != null) {
            if(inSessionCustomization.getOrderModifiers() != null) {
                orderModifiers.addAll(inSessionCustomization.getOrderModifiers());
            }
            if(inSessionCustomization.getResponseModifiers() != null) {
                responseModifiers.addAll(inSessionCustomization.getResponseModifiers());
            }
            if(inSessionCustomization.getBrokerAlgos() != null) {
                brokerAlgos.addAll(inSessionCustomization.getBrokerAlgos());
            }
        }
        brokerId = new BrokerID(inFixSession.getBrokerId());
        sessionId = new SessionID(inFixSession.getSessionId());
        if(inFixSession.getSessionSettings().containsKey(Session.SETTING_DEFAULT_APPL_VER_ID)) {
            String applVerId = inFixSession.getSessionSettings().get(Session.SETTING_DEFAULT_APPL_VER_ID);
            fixVersion = FIXVersion.getFIXVersion(applVerId);
        } else {
            fixVersion = FIXVersion.getFIXVersion(sessionId);
        }
        mappedBrokerId = inFixSession.getMappedBrokerId() == null ? null : new BrokerID(inFixSession.getMappedBrokerId());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getName()
     */
    @Override
    public String getName()
    {
        return fixSession.getName();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getSessionId()
     */
    @Override
    public SessionID getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getFixSession()
     */
    @Override
    public FixSession getFixSession()
    {
        return fixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getOrderModifiers()
     */
    @Override
    public List<MessageModifier> getOrderModifiers()
    {
        return orderModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getResponseModifiers()
     */
    @Override
    public List<MessageModifier> getResponseModifiers()
    {
        return responseModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getFixVersion()
     */
    @Override
    public FIXVersion getFIXVersion()
    {
        return fixVersion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getBrokerAlgos()
     */
    @Override
    public Set<BrokerAlgoSpec> getBrokerAlgos()
    {
        return brokerAlgos;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getUserWhitelist()
     */
    @Override
    public Set<User> getUserWhitelist()
    {
        return userWhitelist;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getUserBlacklist()
     */
    @Override
    public Set<User> getUserBlacklist()
    {
        return userBlacklist;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getFIXMessageFactory()
     */
    @Override
    public FIXMessageFactory getFIXMessageFactory()
    {
        return fixVersion.getMessageFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getDataDictionary()
     */
    @Override
    public DataDictionary getDataDictionary()
    {
        return FIXMessageUtil.getDataDictionary(getFIXVersion());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getFIXDataDictionary()
     */
    @Override
    public synchronized FIXDataDictionary getFIXDataDictionary()
    {
        if(dataDictionary==null) {
            dataDictionary=new FIXDataDictionary(getDataDictionary());
        }
        return dataDictionary;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.Broker#getMappedBrokerId()
     */
    @Override
    public BrokerID getMappedBrokerId()
    {
        return mappedBrokerId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Broker [").append(fixSession.getName()).append("]");
        return builder.toString();
    }
    /**
     * data dictionary value
     */
    private transient volatile FIXDataDictionary dataDictionary;
    /**
     * session id value
     */
    private final SessionID sessionId;
    /**
     * broker algo values
     */
    private final Set<BrokerAlgoSpec> brokerAlgos = Sets.newHashSet();
    /**
     * response modifiers value
     */
    private final List<MessageModifier> responseModifiers = Lists.newArrayList();
    /**
     * order modifiers value
     */
    private final List<MessageModifier> orderModifiers = Lists.newArrayList();
    /**
     * underlying FIX session value
     */
    private final FixSession fixSession;
    /**
     * underlying FIX version value
     */
    private final FIXVersion fixVersion;
    /**
     * underlying broker id value
     */
    private final BrokerID brokerId;
    /**
     * virtual broker id value or <code>null</code>
     */
    private final BrokerID mappedBrokerId;
    /**
     * user whitelist value
     */
    private final Set<User> userWhitelist = Sets.newHashSet();
    /**
     * user blacklist value
     */
    private final Set<User> userBlacklist = Sets.newHashSet();
}
