package org.marketcetera.fix.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionFactory;

import quickfix.SessionFactory;
import quickfix.SessionID;

/* $License$ */

/**
 * Creates {@link FixSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PersistentFixSessionFactory
        implements FixSessionFactory
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionFactory#create()
     */
    @Override
    public PersistentFixSession create()
    {
        return new PersistentFixSession();
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionFactory#create(com.marketcetera.ors.brokers.FixSession)
     */
    @Override
    public PersistentFixSession create(FixSession inFixSession)
    {
        PersistentFixSession fixSession = new PersistentFixSession();
        fixSession.update(inFixSession);
        return fixSession;
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.FixSessionFactory#create(java.util.Map)
     */
    @Override
    public PersistentFixSession create(Map<String,String> inAttributes)
    {
        Map<String,String> settings = new HashMap<>(inAttributes);
        PersistentFixSession fixSession = new PersistentFixSession();
        // TODO copy fields
        // TODO tighten this up
        String rawValue = StringUtils.trimToNull(settings.remove("affinity"));
        if(rawValue != null) {
            fixSession.setAffinity(Integer.parseInt(rawValue));
        }
        rawValue = StringUtils.trimToNull(settings.remove("host"));
        if(rawValue != null) {
            fixSession.setHost(rawValue);
        }
        rawValue = StringUtils.trimToNull(settings.remove("enabled"));
        if(rawValue != null) {
            fixSession.setIsEnabled(Boolean.parseBoolean(rawValue));
        }
        rawValue = StringUtils.trimToNull(settings.remove("acceptor"));
        if(rawValue != null) {
            boolean acceptorValue = Boolean.parseBoolean(rawValue);
            fixSession.setIsAcceptor(acceptorValue);
            if(acceptorValue) {
                fixSession.getSessionSettings().put(SessionFactory.SETTING_CONNECTION_TYPE,
                                                    SessionFactory.ACCEPTOR_CONNECTION_TYPE);
            } else {
                fixSession.getSessionSettings().put(SessionFactory.SETTING_CONNECTION_TYPE,
                                                    SessionFactory.INITIATOR_CONNECTION_TYPE);
            }
        }
        rawValue = StringUtils.trimToNull(settings.remove("name"));
        if(rawValue != null) {
            fixSession.setName(rawValue);
        }
        rawValue = StringUtils.trimToNull(settings.remove("description"));
        if(rawValue != null) {
            fixSession.setDescription(rawValue);
        }
        rawValue = StringUtils.trimToNull(settings.remove("port"));
        if(rawValue != null) {
            fixSession.setPort(Integer.parseInt(rawValue));
        }
        rawValue = StringUtils.trimToNull(settings.remove("id"));
        if(rawValue != null) {
            fixSession.setBrokerId(rawValue);
        }
        rawValue = StringUtils.trimToNull(settings.remove("mappedId"));
        if(rawValue != null) {
            fixSession.setMappedBrokerId(rawValue);
        }
        String beginString = null;
        String senderCompId = null;
        String targetCompId = null;
        // TODO add bits in for further qualified session ids
        for(Map.Entry<String,String> entry : settings.entrySet()) {
            if(entry.getKey().equals("BeginString")) {
                beginString = entry.getValue();
            }
            if(entry.getKey().equals("SenderCompID")) {
                senderCompId = entry.getValue();
            }
            if(entry.getKey().equals("TargetCompID")) {
                targetCompId = entry.getValue();
            }
            fixSession.getSessionSettings().put(entry.getKey(),
                                                entry.getValue());
        }
        SessionID sessionId = new SessionID(beginString,
                                            senderCompId,
                                            targetCompId);
        fixSession.setSessionId(sessionId.toString());
        fixSession.validate();
        return fixSession;
    }
}
