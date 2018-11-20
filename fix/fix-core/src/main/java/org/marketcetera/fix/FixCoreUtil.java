package org.marketcetera.fix;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import quickfix.SessionFactory;
import quickfix.SessionID;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class FixCoreUtil
{
    public static void applyFixSettings(MutableFixSession fixSession,
                                        Map<String,String> inAttributes)
    {
        Map<String,String> settings = new HashMap<>(inAttributes);
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
    }
}
