package org.marketcetera.fix.impl;

import java.util.List;
import java.util.Set;

import org.marketcetera.admin.User;
import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.ServerFixSession;
import org.marketcetera.quickfix.FIXDataDictionary;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.quickfix.FIXVersion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickfix.DataDictionary;
import quickfix.SessionID;

/* $License$ */

/**
 * Provides a POJO {@link ServerFixSession} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleServerFixSession
        implements ServerFixSession
{
    /**
     * Create a new SimpleServerFixSession instance.
     *
     * @param inServerFixSession a <code>ServerFixSession</code> value
     */
    public SimpleServerFixSession(ServerFixSession inServerFixSession)
    {
        activeFixSession = inServerFixSession.getActiveFixSession();
        fixVersion = inServerFixSession.getFIXVersion();
        orderModifiers.addAll(inServerFixSession.getOrderModifiers());
        responseModifiers.addAll(inServerFixSession.getResponseModifiers());
        userBlacklist.addAll(inServerFixSession.getUserBlacklist());
        userWhitelist.addAll(inServerFixSession.getUserWhitelist());
    }
    /**
     * Create a new SimpleServerFixSession instance.
     *
     * @param inActiveFixSession an <code>ActiveFixSession</code> value
     * @param inSessionCustomization a <code>SessionCustomization</code> value or <code>null</code>
     */
    public SimpleServerFixSession(ActiveFixSession inActiveFixSession,
                                  SessionCustomization inSessionCustomization)
    {
        activeFixSession = inActiveFixSession;
        // TODO will this work for FIXT.T?
        fixVersion = FIXVersion.getFIXVersion(new SessionID(inActiveFixSession.getFixSession().getSessionId()));
        if(inSessionCustomization != null) {
            orderModifiers.addAll(inSessionCustomization.getOrderModifiers());
            responseModifiers.addAll(inSessionCustomization.getResponseModifiers());
            // TODO these are all strings and need to be user?
//            userBlacklist.addAll(inSessionCustomization.getUserBlacklist());
//            userWhitelist.addAll(inSessionCustomization.getUserWhitelist());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getActiveFixSession()
     */
    @Override
    public ActiveFixSession getActiveFixSession()
    {
        return activeFixSession;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getUserWhitelist()
     */
    @Override
    public Set<User> getUserWhitelist()
    {
        return userWhitelist;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getUserBlacklist()
     */
    @Override
    public Set<User> getUserBlacklist()
    {
        return userBlacklist;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getFIXMessageFactory()
     */
    @Override
    public FIXMessageFactory getFIXMessageFactory()
    {
        return fixVersion.getMessageFactory();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getDataDictionary()
     */
    @Override
    public DataDictionary getDataDictionary()
    {
        return FIXMessageUtil.getDataDictionary(fixVersion);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getFIXDataDictionary()
     */
    @Override
    public FIXDataDictionary getFIXDataDictionary()
    {
        if(dataDictionary == null) {
            dataDictionary=new FIXDataDictionary(getDataDictionary());
        }
        return dataDictionary;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getFIXVersion()
     */
    @Override
    public FIXVersion getFIXVersion()
    {
        return fixVersion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getOrderModifiers()
     */
    @Override
    public List<MessageModifier> getOrderModifiers()
    {
        return orderModifiers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.ServerFixSession#getResponseModifiers()
     */
    @Override
    public List<MessageModifier> getResponseModifiers()
    {
        return responseModifiers;
    }
    /**
     * FIX version value
     */
    private FIXVersion fixVersion;
    /**
     * data dictionary value
     */
    private FIXDataDictionary dataDictionary;
    /**
     * active FIX session value
     */
    private ActiveFixSession activeFixSession;
    /**
     * user whitelist value
     */
    private Set<User> userWhitelist = Sets.newHashSet();
    /**
     * user blacklist value
     */
    private Set<User> userBlacklist = Sets.newHashSet();
    /**
     * response modifiers value
     */
    private List<MessageModifier> responseModifiers = Lists.newArrayList();
    /**
     * order modifiers value
     */
    private List<MessageModifier> orderModifiers = Lists.newArrayList();
}
