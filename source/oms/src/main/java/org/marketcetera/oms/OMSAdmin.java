package org.marketcetera.oms;

import org.marketcetera.core.ClassVersion;
import quickfix.Session;

import java.io.IOException;

/**
 * Admin interface for the {@link org.marketcetera.oms.OMSAdminMBean}
 * Should be superseded by quickfixJ JMX
 * @author toli
 * @version $Id$
 * @deprecated
 */

@ClassVersion("$Id$")
public class OMSAdmin implements OMSAdminMBean {
    private Session session;
    
    public OMSAdmin(Session session) {
        this.session = session;
    }


    public String getBeginString() {
        return session.getSessionID().getBeginString();
    }

    public String getTargetCompID() {
        return session.getSessionID().getTargetCompID();
    }

    public String getSenderCompID() {
        return session.getSessionID().getSenderCompID();
    }

    public String getSessionID() {
        return session.getSessionID().toString();
    }

    public int getNextSenderMsgSeqNum() throws IOException {
        return session.getExpectedSenderNum();
    }

    public int getNextTargetMsgSeqNum() throws IOException {
        return session.getExpectedTargetNum();
    }

    public boolean isLoggedOn() {
        return session.isLoggedOn();
    }
}
