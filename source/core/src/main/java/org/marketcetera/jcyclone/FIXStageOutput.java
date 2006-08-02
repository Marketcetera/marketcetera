package org.marketcetera.jcyclone;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionNotFound;
import quickfix.SessionID;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.ClassVersion;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class FIXStageOutput extends OutputElement {
    SessionID defaultSessionID;
    public FIXStageOutput(Message inElem, SessionID inSessionID) {
        super(inElem);
        defaultSessionID = inSessionID;
    }

    public void output()
    {
        try {
            Message message = (Message) getElement();
            if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("FIX output: "+message, this); }
            Session.sendToTarget(message, defaultSessionID);
        } catch (SessionNotFound snf) {
            LoggerAdapter.error("Error sending fix message.", snf, this);
        }
    }
}
