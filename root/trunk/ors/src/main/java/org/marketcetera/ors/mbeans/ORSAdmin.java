package org.marketcetera.ors.mbeans;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.ors.UserManager;
import org.marketcetera.ors.brokers.Broker;
import org.marketcetera.ors.brokers.Brokers;
import org.marketcetera.quickfix.IQuickFIXSender;
import org.marketcetera.trade.BrokerID;
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

@ClassVersion("$Id$")
public class ORSAdmin implements ORSAdminMBean {
    private Brokers brokers;
    protected IQuickFIXSender quickFIXSender;
    private IDFactory idFactory;
    private UserManager userManager;

    public ORSAdmin(Brokers brokers,
                    IQuickFIXSender qfSender,
                    IDFactory idFactory,
                    UserManager userManager)
            throws NoMoreIDsException, ClassNotFoundException {
        this.brokers = brokers;
        quickFIXSender = qfSender;
        this.idFactory = idFactory;
        this.userManager = userManager;
    }

    @Override
    public void sendPasswordReset(String broker, String oldPassword, String newPassword) {
        Broker b=brokers.getBroker(new BrokerID(broker));
        SLF4JLoggerProxy.debug(this, "Trade session halted, resetting password"); //$NON-NLS-1$
        SessionID session = b.getSessionID();
        Message msg = b.getFIXMessageFactory().createMessage(MsgType.USER_REQUEST);
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

    @Override
    public void syncSessions()
    {
        userManager.sync();
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
