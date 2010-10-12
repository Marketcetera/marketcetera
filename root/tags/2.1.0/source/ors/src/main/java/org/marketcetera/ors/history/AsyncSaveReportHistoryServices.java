package org.marketcetera.ors.history;

import javax.xml.bind.JAXBException;
import org.marketcetera.client.jms.JmsManager;
import org.marketcetera.client.jms.ReceiveOnlyHandler;
import org.marketcetera.core.IDFactory;
import org.marketcetera.persist.PersistenceException;
import org.marketcetera.trade.*;
import org.marketcetera.util.misc.ClassVersion;
import org.springframework.jms.core.JmsOperations;

/**
 * Provides services to save and query reports with asynchronous saving.
 *
 * @author tlerios@marketcetera.com
 * @since 2.1.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AsyncSaveReportHistoryServices
    extends BasicReportHistoryServices
{

    // CLASS DATA.

    private static final String PERSIST_QUEUE=
        "persist-queue"; //$NON-NLS-1$

    public class QueueHandler
        implements ReceiveOnlyHandler<TradeMessage>
    {
        @Override
        public void receiveMessage
            (TradeMessage msg)
        {
            final ReportBase report=(ReportBase)msg;
            Messages.RHS_DEQUEUED_REPLY.info(this,report);
            boolean success=false;
            try {
                PersistentReport.save(report);
                success=true;
                Messages.RHS_PERSISTED_REPLY.info(this,report);
            } catch (PersistenceException ex) {
                Messages.RHS_PERSIST_ERROR.error(this,ex,report);
            } finally {
                invokeListener(report,success);
            }
        }
    }


    // INSTANCE DATA.

    private JmsOperations mToPersistQueue;


    // ReportHistoryServices.

    @Override
    public void init
        (IDFactory idFactory,
         JmsManager jmsManager,
         ReportSavedListener reportSavedListener)
        throws ReportPersistenceException
    {
        super.init(idFactory,jmsManager,reportSavedListener);
        try {
            mToPersistQueue=getJmsManager().getOutgoingJmsFactory().
                createJmsTemplateX(PERSIST_QUEUE,false);
            getJmsManager().getIncomingJmsFactory().registerHandlerTMX
                (new QueueHandler(),PERSIST_QUEUE,false);
        } catch (JAXBException ex) {
            throw new ReportPersistenceException
                (ex,Messages.RHS_CANNOT_CREATE_QUEUE);
        }
    }

    @Override
    public void save
        (ReportBase report)
        throws PersistenceException
    {
        boolean success=false;
        try {
            assignID(report);
            success=true;
        } finally {
            if (!success) {
                invokeListener(report,false);
            }
        }
        Messages.RHS_ENQUEUED_REPLY.info(this,report);        
        getToPersistQueue().convertAndSend(report);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's persist queue sender.
     *
     * @return The sender.
     */

    private JmsOperations getToPersistQueue()
    {
        return mToPersistQueue;
    }
}
