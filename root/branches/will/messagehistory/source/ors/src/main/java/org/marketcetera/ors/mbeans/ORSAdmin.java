package org.marketcetera.ors.mbeans;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.ors.Messages;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.*;

/**
 * Implements the {@link ORSAdminMBean} interface
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class ORSAdmin implements ORSAdminMBean {
    protected IQuickFIXSender quickFIXSender;
    private FIXMessageFactory fixMessageFactory;
    private IDFactory idFactory;

    public ORSAdmin(IQuickFIXSender qfSender, FIXMessageFactory msgFactory, IDFactory idFactory)
            throws NoMoreIDsException, ClassNotFoundException {
        quickFIXSender = qfSender;
        fixMessageFactory = msgFactory;
        this.idFactory = idFactory;
        try {
            this.idFactory.init();
        } catch (Exception ex) {
            SLF4JLoggerProxy.debug(this, ex, "Error initializing the ID Factory"); //$NON-NLS-1$
            // ignore the exception - should get the in-memory id factory instead
        }
    }

    public void sendPasswordReset(String senderCompID, String targetCompID, String oldPassword, String newPassword) {
        SLF4JLoggerProxy.debug(this, "Trade session halted, resetting password"); //$NON-NLS-1$
        SessionID session = new SessionID(fixMessageFactory.getBeginString(), senderCompID,  targetCompID);
        Message msg = fixMessageFactory.createMessage(MsgType.USER_REQUEST);
        // in case of Currenex that uses FIX.4.2 right message won't be created to set the type manually
        if (!msg.getHeader().isSetField(MsgType.FIELD)) {
            msg.getHeader().setField(new MsgType(MsgType.USER_REQUEST));
        }
        msg.setField(new UserRequestID(getNextID()));
        msg.setField(new UserRequestType(UserRequestType.CHANGEPASSWORDFORUSER));
        msg.setField(new Username(session.getSenderCompID()));
        msg.setField(new Password(oldPassword));
        msg.setField(new NewPassword(newPassword));

        try {
            quickFIXSender.sendToTarget(msg, session);
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    private String getNextID() {
        try {
            return idFactory.getNext();
        } catch(NoMoreIDsException ex) {
            Messages.ERROR_GENERATING_EXEC_ID.error(this, ex.getMessage());
            return "ZZ-INTERNAL"; //$NON-NLS-1$
        }
    }
}
