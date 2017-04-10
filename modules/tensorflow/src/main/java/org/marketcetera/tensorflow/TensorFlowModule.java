package org.marketcetera.tensorflow;

import org.marketcetera.event.Event;
import org.marketcetera.module.AutowiredModule;
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
import org.marketcetera.trade.Order;
import org.tensorflow.Graph;
import org.tensorflow.Session;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@AutowiredModule
public class TensorFlowModule
        extends Module
        implements DataReceiver,DataEmitter
{
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN
     */
    public TensorFlowModule(ModuleURN inURN)
    {
        super(inURN,
              true);
    }
    /**
     * Create a new TensorFlowModule instance.
     *
     * @param inURN
     * @param inAutoStart
     */
    public TensorFlowModule(ModuleURN inURN,
                            boolean inAutoStart)
    {
        super(inURN,
              inAutoStart);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#requestData(org.marketcetera.module.DataRequest, org.marketcetera.module.DataEmitterSupport)
     */
    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws RequestDataException
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataEmitter#cancel(org.marketcetera.module.DataFlowID, org.marketcetera.module.RequestID)
     */
    @Override
    public void cancel(DataFlowID inFlowID,
                       RequestID inRequestID)
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.DataReceiver#receiveData(org.marketcetera.module.DataFlowID, java.lang.Object)
     */
    @Override
    public void receiveData(DataFlowID inFlowID,
                            Object inData)
            throws ReceiveDataException
    {
        if(inData instanceof Order) {
            Order order = (Order)inData;
            
        } else if(inData instanceof Event) {
            Event event = (Event)inData;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStart()
     */
    @Override
    protected void preStart()
            throws ModuleException
    {
        graph = new Graph();
//        graph.toGraphDef()
//        graph.importGraphDef(graphDef);
        session = new Session(graph);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.Module#preStop()
     */
    @Override
    protected void preStop()
            throws ModuleException
    {
        if(session != null) {
            try {
                session.close();
            } catch (Exception ignored) {}
            session = null;
        }
        if(graph != null) {
            try {
                graph.close();
            } catch (Exception ignored) {}
            graph = null;
        }
    }
    private Graph graph;
    private Session session;
}
