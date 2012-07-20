package org.marketcetera.quickfix;

import quickfix.*;

/**
 * Dymmy quickfix app that does nothing
 * @author toli
 * @version $Id$
 */

public class DummyQuickFIXApp implements Application {
    public DummyQuickFIXApp() {
    }

    public void onCreate(SessionID sessionId) {

    }

    public void onLogon(SessionID sessionId) {

    }

    public void onLogout(SessionID sessionId) {

    }

    public void toAdmin(Message message, SessionID sessionId) {

    }

    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {

    }

    public void toApp(Message message, SessionID sessionId) throws DoNotSend {

    }

    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {

    }
}
