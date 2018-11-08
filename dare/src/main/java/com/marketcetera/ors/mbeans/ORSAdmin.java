package com.marketcetera.ors.mbeans;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.quickfix.QuickFIXSender;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.Message;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.MsgType;
import quickfix.field.NewPassword;
import quickfix.field.Password;
import quickfix.field.UserRequestID;
import quickfix.field.UserRequestType;
import quickfix.field.Username;

import com.marketcetera.fix.FixSession;
import com.marketcetera.ors.UserManager;
import com.marketcetera.ors.brokers.Broker;
import com.marketcetera.ors.brokers.BrokerService;

/**
 * Implements the {@link ORSAdminMBean} interface
 *
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ORSAdmin.java 17266 2017-04-28 14:58:00Z colin $
 */
@ClassVersion("$Id: ORSAdmin.java 17266 2017-04-28 14:58:00Z colin $")
public class ORSAdmin
        implements ORSAdminMBean
{
    /**
     * Create a new ORSAdmin instance.
     *
     * @param inSender a <code>QuickFIXSender</code> value
     * @param inIdFactory an <code>IDFactory</code> value
     * @param inUserManager a <code>UserManager</code> value
     */
    public ORSAdmin(QuickFIXSender inSender,
                    IDFactory inIdFactory,
                    UserManager inUserManager)
    {
        quickFIXSender = inSender;
        this.idFactory = inIdFactory;
        this.userManager = inUserManager;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    @Override
    public void sendPasswordReset(String broker, String oldPassword, String newPassword)
    {
        FixSession fixSession = brokerService.findFixSessionByBrokerId(new BrokerID(broker));
        if(fixSession == null) {
            SLF4JLoggerProxy.warn(this,
                                  "Cannot reset password for unknown session {}",
                                  broker);
            return;
        }
        Broker b = brokerService.generateBroker(fixSession);
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
    /**
     * Gets the next id value.
     *
     * @return a <code>String</code> value
     */
    private String getNextID()
    {
        try {
            return idFactory.getNext();
        } catch(NoMoreIDsException ex) {
            Messages.ERROR_GENERATING_EXEC_ID.error(this, ex.getMessage());
            return "ZZ-INTERNAL"; //$NON-NLS-1$
        }
    }
    /**
     * provides access to user services
     */
    private UserManager userManager;
    /**
     * manages sending FIX messages
     */
    protected QuickFIXSender quickFIXSender;
    /**
     * provides unique ids
     */
    private IDFactory idFactory;
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
}
