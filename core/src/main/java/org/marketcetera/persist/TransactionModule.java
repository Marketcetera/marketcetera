package org.marketcetera.persist;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.module.DataEmitter;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.DataFlowID;
import org.marketcetera.module.DataReceiver;
import org.marketcetera.module.DataRequest;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleException;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.module.ReceiveDataException;
import org.marketcetera.module.RequestDataException;
import org.marketcetera.module.RequestID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/* $License$ */

/**
 * Wraps a data flow in a transaction.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TransactionModule
        extends Module
        implements DataEmitter,DataReceiver
{
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws ReceiveDataException
    {
        DataEmitterSupport dataEmitterSupport = dataSupport.getIfPresent(inFlowID);
        if(dataEmitterSupport != null) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("transactionModuleTransaction");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setReadOnly(false);
            TransactionStatus status = txManager.getTransaction(def);
            try {
                dataEmitterSupport.send(inData);
                txManager.commit(status);
            } catch (Exception e) {
                // unable to commit the initial change, rollback
                if(status != null) {
                    try {
                        txManager.rollback(status);
                    } catch (Exception e1) {
                        PlatformServices.handleException(this,
                                                         "Unable to rollback transaction",
                                                         e);
                    }
                }
                throw new ReceiveDataException(e);
            }
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
        dataSupport.put(inSupport.getFlowID(),
                        inSupport);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
        dataSupport.invalidate(inFlowID);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        dataSupport.invalidateAll();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
    }
    /**
     * Create a new TransactionModule instance.
     *
     * @param inURN a <code>ModuleURN</code> value
     */
    TransactionModule(ModuleURN inURN)
    {
        super(inURN,
              false);
    }
    /**
     * transaction manager value
     */
    @Autowired
    private JpaTransactionManager txManager;
    /**
     * if wired into a multi-module flow, this object will assist in passing data to the next object in the flow
     */
    private final Cache<DataFlowID,DataEmitterSupport> dataSupport = CacheBuilder.newBuilder().build();
}
