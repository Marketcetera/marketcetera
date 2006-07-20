package org.marketcetera.oms;

import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.DoNotSend;
import quickfix.field.MsgType;
import org.jcyclone.core.queue.SinkException;
import org.jcyclone.core.stage.IStageManager;
import org.jcyclone.core.internal.ISystemManager;
import org.marketcetera.jcyclone.*;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.quickfix.QuickFIXSessionAdapter;
import org.marketcetera.quickfix.QuickFIXInitiator;

/**
 * Acts as a JCyclone Source - this receives all the FIX messages and passes them on
 * to the next stage
 * Acts as a "plugin" to send envents to the Ordermanager stage
 *
 * This class creates a {@link QuickFIXInitiator} which, in turn, registers with
 * FIX to recieve fix messages.
 * When FIX messages come in, they are routed through this FIX Adapter
 * {@link #fromApp} and {@link #fromAdmin} methods
 *
 * @author Toli Kuznets
 * @version $Id$
 */
public class FIXSessionAdapterSource extends JCyclonePluginSource implements QuickFIXSessionAdapter {

    private static final char HEARTBEAT_CHAR = MsgType.HEARTBEAT.charAt(0);
    private QuickFIXInitiator qfInitiator;

    /** JCyclone initialization
     * get the next stage and our init params
     */
    public void initialize(IStageManager stagemgr, ISystemManager sysmgr, String pluginName) throws Exception {
        super.initialize(stagemgr, sysmgr, pluginName);

        // create the FIX Adapter
        qfInitiator = new QuickFIXInitiator(this);
        qfInitiator.init(OrderManagementSystem.getOMS().getInitProps());
        OrderManagementSystem.getOMS().registerDefaultSessionID(qfInitiator.getDefaultSessionID());
    }

    // used by stages and plugins
    public void destroy() throws Exception {
        qfInitiator.shutdown();
        super.destroy();
    }

    public void fromApp(quickfix.Message message)
    {
        sendHelper(message);
    }

    /** For the admin messages, only pass through the hearbeats */
    public void fromAdmin(quickfix.Message message)
            throws FieldNotFound {
        // pass though the heartbeat messages b/c we can
        if (HEARTBEAT_CHAR == message.getHeader().getChar(MsgType.FIELD)) {
            sendHelper(message);
        }
    }

    /** Wrap the message into a JCyclone wrapper and send it to the next stage */
    protected void sendHelper(quickfix.Message inMessage)
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            try {
                if (HEARTBEAT_CHAR != inMessage.getHeader().getChar(MsgType.FIELD)) {
                    LoggerAdapter.debug("FixSource received incoming msg: " + inMessage, this);
                }
            } catch (FieldNotFound ex) {
                // ignore
            }
        }

        try {
            getNextStage().enqueue(new JMSStageOutput(inMessage, OrderManagementSystem.getOMS().getJmsOutputInfo()));
        } catch (SinkException ex) {
            LoggerAdapter.error("Failed while sending a message", ex, this);
            throw new RuntimeException("Failed while sending a message", ex);
        }
    }

    public void onLogon() {
        // do nothing
    }

    public void onLogout() {
        // do nothing
    }

    public void toAdmin(Message message) {
        // noops - we don't send messages out of here
    }

    public void toApp(Message message) throws DoNotSend {
        // noops - we don't send messages out of here
    }
}
