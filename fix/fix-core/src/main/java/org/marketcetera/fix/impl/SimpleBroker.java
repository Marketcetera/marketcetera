package org.marketcetera.fix.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.marketcetera.algo.BrokerAlgoSpec;
import org.marketcetera.brokers.Broker;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.fix.FixSession;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.BrokerID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
     * @param inOrderModifiers a <code>Collection&lt;MessageModifier&gt;</code> value
     * @param inResponseModifiers a <code>Collection&lt;MessageModifier&gt;</code> value
     * @param inBrokerAlgos a <code>Collection&lt;BrokerAlgoSpec&gt;</code> value
     */
    SimpleBroker(FixSession inFixSession,
                 Collection<MessageModifier> inOrderModifiers,
                 Collection<MessageModifier> inResponseModifiers,
                 Collection<BrokerAlgoSpec> inBrokerAlgos)
    {
        fixSession = inFixSession;
        if(inOrderModifiers != null) {
            orderModifiers.addAll(inOrderModifiers);
        }
        if(inResponseModifiers != null) {
            responseModifiers.addAll(inResponseModifiers);
        }
        if(inBrokerAlgos != null) {
            brokerAlgos.addAll(inBrokerAlgos);
        }
        brokerId = new BrokerID(inFixSession.getBrokerId());
        SessionID sessionId = new SessionID(inFixSession.getSessionId());
        // TODO this won't work for FIXT.T
        fixVersion = FIXVersion.getFIXVersion(sessionId);
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
    public FIXVersion getFixVersion()
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
}
