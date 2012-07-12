package org.marketcetera.modules.async;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.NamedThreadFactory;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.module.*;

import java.util.concurrent.*;
import java.util.*;

import javax.management.*;

/* $License$ */
/**
 * A processor module that can be inserted between any two modules within a
 * data flow to decouple the delivery of data from the emitter module and
 * processing of that data in the receiver module.
 * <p>
 * For each data flow this module is participating in, the module
 * adds every received data item into a queue. Hence the emitter module
 * from which this module is receiving data is not blocked when delivering
 * data. The module creates a separate thread for the data flow that removes
 * the data items from this queue and delivers it to the next receiver module
 * in the data flow.
 * <p>
 * Do note that the if the module that is receiving data emitted by
 * this module is not able to keep up with the module that is emittin data
 * into this module, the data items will keep on accumulating in the queue.
 * If this condition continues on for a while, this may lead to JVM running
 * out of memory. When using this module, be sure to size the heap of the process
 * to ensure that the process does not run out of memory, when there's a disparity
 * in the data emit rate and data receive rate of the modules before and
 * after this module in the data flow.
 * <p>
 * The module exposes an DynamicMBean interface to monitor the current
 * queue sizes for every flow. The MBean offers list of attributes,
 * one per data flow, that it is participating in.
 * Each attribute has name of the form "<code>Flow</code><i>data_flow_id</i>"
 * where <i>data_flow_id</i> is the flowID of the data flow that the attribute
 * represents. The value of the attribute is the current size of the queue
 * for that data flow. The attribute value can be monitored to observe if
 * the module receiving data from this module is able to keep up with the
 * module emitting data into this module in the data flow represented by the
 * attribute. 
 * <p>
 * Note that when the data flow is canceled, the module does not wait for the
 * all the data in the queue to be delivered, it interrupts the delivery thread
 * right away and allows the flow to be canceled.
 * <p>
 * Note that this module is not designed such that the same instance can be
 * used more than once in a single data flow. If you need to use this module
 * more than once in the same data flow make sure that you use different
 * instances of this module in the flow such that the same instance doesn't
 * appear more than once in the same data flow.
 * <p>
 * This module is meant to be used as an intermediate module between two
 * modules in a data flow. Although this module can be used as the first or
 * the last module in a data flow, the features provided by this module offer
 * little merit in such a scenario.
 * <p>
 * <strong>Usage:</strong>
 * <p>
 * The following strategy agent command demonstrates how this module can be
 * inserted between two modules to decouple the data flow between them.
 * <pre>
 * # Start the bogus feed module
 * startModule;metc:mdata:bogus:single
 * # Create data flow
 * createDataFlow;metc:mdata:bogus;single:symbols=AAPL:content=latest_tick,top_of_book^metc:async:simple:myinstance
 * </pre>
 * The command above will create a data flow where the market data events
 * from the bogus feed are fed into the simple async module. The async module
 * adds all the received market data events to the queue and has a separate
 * thread that removes them from the queue and delivers them to the sink module.
 * Since the market data events are always added to the queue, market data
 * delivery is very fast and the market data delivery thread is not blocked
 * while the sink module processes the event (for example, logs it). 
 * <p>
 * Module Features
 * <table>
 * <tr><th>Capabilities</th><td>Data Emitter, Data Reciever</td></tr>
 * <tr><th>DataFlow Request Parameters</th><td>None.</td></tr>
 * <tr><th>Stops data flows</th><td>No.</td></tr>
 * <tr><th>Start Operation</th><td>Initializes the thread pool for emitting data.</td></tr>
 * <tr><th>Stop Operation</th><td>Shuts down the thread pool.</td></tr>
 * <tr><th>Management Interface</th><td>dynamic: see above</td></tr>
 * <tr><th>Factory</th><td>{@link SimpleAsyncProcessorFactory}</td></tr>
 * </table>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class SimpleAsyncProcessor extends Module
        implements DataEmitter, DataReceiver, DynamicMBean {

    /* Module Framework Methods */

    @Override
    public void requestData(DataRequest inRequest,
                            DataEmitterSupport inSupport)
            throws IllegalRequestParameterValue {
        Object obj = inRequest.getData();
        if(obj != null) {
            throw new IllegalRequestParameterValue(getURN(), obj);
        }
        DataFlowHandler handler = new DataFlowHandler(inSupport);
        handler.setFuture(mService.submit(handler));
        addFlow(inSupport, handler);
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        Future<?> future = removeFlow(inFlowID);
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData) {
        DataFlowHandler handler = getHandler(inFlowID);
        if(handler != null) {
            handler.receiveData(inData);
        } else {
            //This cannot happen but it will be good to warn if it does.
            Messages.DATA_RECVD_UNKNOWN_FLOW.warn(this, inFlowID);
        }
    }

    /* Dynamic MBean Methods */

    @Override
    public Object getAttribute(String attribute)
            throws AttributeNotFoundException {
        Integer value = null;
        if (attribute.startsWith(ATTRIB_PREFIX)) {
            String flowID = attribute.substring(ATTRIB_PREFIX.length());
            if(!flowID.isEmpty()) {
                DataFlowID id = new DataFlowID(flowID);
                DataFlowHandler handler = mFlows.get(id);
                if(handler != null) {
                    value = handler.getQueueSize();
                }
            }
        }
        if(value == null) {
            throw new AttributeNotFoundException(attribute);
        } else {
            return value;
        }
    }

    @Override
    public void setAttribute(Attribute attribute)
            throws AttributeNotFoundException {
        //Attributes are not writable
        throw new AttributeNotFoundException(
                Messages.MXBEAN_ATTRIB_NOT_WRITABLE.getText(
                        attribute.getName()));
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        Map<DataFlowID, Integer> sizes = getQueueSizes();
        AttributeList list = new AttributeList();
        for(String attribute: attributes) {
            if (attribute.startsWith(ATTRIB_PREFIX)) {
                Integer value = null;
                String flowID = attribute.substring(ATTRIB_PREFIX.length());
                if(!flowID.isEmpty()) {
                    DataFlowID id = new DataFlowID(flowID);
                    value = sizes.get(id);
                }
                if (value != null) {
                    list.add(new Attribute(attribute, value));
                }
            }
        }
        return list;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        //Always return empty list as all attributes are readonly.
        return new AttributeList();
    }

    @Override
    public Object invoke(String actionName, Object[] params,
                         String[] signature)
            throws ReflectionException {
        throw new ReflectionException(new NoSuchMethodException(actionName));
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        Map<DataFlowID,Integer> queueSizes = getQueueSizes();
        List<MBeanAttributeInfo> attribInfo = new ArrayList<MBeanAttributeInfo>(queueSizes.size());
        for(Map.Entry<DataFlowID,Integer>entry: queueSizes.entrySet()) {
            attribInfo.add(new MBeanAttributeInfo(ATTRIB_PREFIX +entry.getKey(),
                    Integer.class.getName(),
                    Messages.JMX_ATTRIBUTE_FLOW_CNT_DESCRIPTION.getText(entry.getKey()),
                    true, false, false));
        }
        return new MBeanInfo(getClass().getName(),
                Messages.JMX_MXBEAN_DESCRIPTION.getText(),
                attribInfo.toArray(new MBeanAttributeInfo[attribInfo.size()]),
                null,null,null);
    }
    /**
     * Creates an instance.
     *
     * @param inURN the module's instance URN.
     */
    protected SimpleAsyncProcessor(ModuleURN inURN) {
        super(inURN, true);
    }

    @Override
    protected void preStart() {
        mService = Executors.newCachedThreadPool(
                new NamedThreadFactory(new StringBuilder(
                        ASYNC_THREAD_NAME_PREFIX).append("-").append(  //$NON-NLS-1$
                        getURN().instanceName()).toString()));
    }

    @Override
    protected void preStop() {
        mService.shutdownNow();
    }

    /**
     * Adds the flow handler for the data flow the table of requests and flows.
     *
     * @param inSupport the data flow support instance for the flow.
     * @param inFlowHandler the flow handler for the flow.
     */
    private void addFlow(DataEmitterSupport inSupport,
                         DataFlowHandler inFlowHandler) {
        mFlows.put(inSupport.getFlowID(), inFlowHandler);
    }

    /**
     * Removes the flow from the table of flows.
     *
     * @param inFlowID the flowID of the flow being canceled.
     * @return the future value representing the thread delivering
     *         the data for the flow.
     */
    private Future<?> removeFlow(DataFlowID inFlowID) {
        DataFlowHandler handler = mFlows.remove(inFlowID);
        return handler.getFuture();
    }

    /**
     * Fetches the flow handler for the supplied flowID.
     *
     * @param inFlowID the data flowID.
     *
     * @return the handler for the supplied flowID. Null, if no handler
     * was found for the specified flowID.
     */
    private DataFlowHandler getHandler(DataFlowID inFlowID) {
        return mFlows.get(inFlowID);
    }

    /**
     * Returns the queue sizes for all the data flows that this module
     * is currently participating in.
     *
     * @return a map of queue sizes for all the flows.
     */
    private Map<DataFlowID,Integer> getQueueSizes() {
        Map<DataFlowID,Integer> sizes = new HashMap<DataFlowID, Integer>();
        for(Map.Entry<DataFlowID,DataFlowHandler> entry: mFlows.entrySet()) {
            sizes.put(entry.getKey(), entry.getValue().getQueueSize());
        }
        return sizes;
    }

    /**
     * The name prefix for JMX attribute used to communicate the queue sizes
     * for each data flow. 
     */
    static final String ATTRIB_PREFIX = "Flow";  //$NON-NLS-1$

    /**
     * Name prefix for all threads created by this module.
     */
    static final String ASYNC_THREAD_NAME_PREFIX = "SimpleAsyncProc";  //$NON-NLS-1$

    /**
     * The thread pool used for creating threads to publish received data
     * asynchronously.
     */
    private ExecutorService mService;
    /**
     * The map of data flows and their handlers.
     */
    private final Map<DataFlowID, DataFlowHandler> mFlows =
            new ConcurrentHashMap<DataFlowID, DataFlowHandler>();

    /**
     * Instances of this class keep track of the queue of data items
     * accumulated for the data flow and the thread that is responsible
     * for processing those items.
     */
    private static class DataFlowHandler implements Runnable {
        /**
         * Creates an instance.
         *
         * @param inEmitterSupport the emitter support instance to emit
         * data for the data flow.
         */
        DataFlowHandler(DataEmitterSupport inEmitterSupport) {
            mEmitterSupport = inEmitterSupport;
        }

        @Override
        public void run() {
            try {
                //Run until interrupted
                while(true) {
                    mEmitterSupport.send(mDataQueue.take());
                }
            } catch (InterruptedException e) {
                SLF4JLoggerProxy.debug(this, e,
                        "Data publishing interrupted. Discarding {} undelivered items",  //$NON-NLS-1$ 
                        mDataQueue.size());
            }
        }

        /**
         * Returns the total number of unprocessed data items in the queue.
         *
         * @return the total number of unprocessed data items in the queue.
         */
        int getQueueSize() {
            return mDataQueue.size();
        }

        /**
         * Supplies a received data item to the handler. The provided data item
         * is added to the queue of unprocessed items.
         *
         * @param inData the data item received.
         */
        void receiveData(Object inData) {
            mDataQueue.add(inData);
        }

        /**
         * Gets the future that can be used to track this handler's execution.
         *
         * @return the future for this handler.
         */
        Future<?> getFuture() {
            return mFuture;
        }

        /**
         * Sets the future that was obtained after submitting this task to
         * the thread pool.
         *
         * @param inFuture the future obtained after submitting this task to
         * the thread pool.
         */
        void setFuture(Future<?> inFuture) {
            mFuture = inFuture;
        }

        /**
         * The emitter support instance used for publishing data.
         */
        private final DataEmitterSupport mEmitterSupport;
        /**
         * The queue that receives data from the upstream module. The handler
         * removes data from this queue and publishes it to the downstream module.
         */
        private final BlockingQueue<Object> mDataQueue =
                new LinkedBlockingQueue<Object>();
        /**
         * The future for tracking this task.
         */
        private Future<?> mFuture;
    }
}