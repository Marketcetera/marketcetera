package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.util.HashMap;
import java.util.Map;

/* $License$ */
/**
 * The system sink module. The Sink module accepts data of any type.
 *
 *
 * Any data received by the sink module can be received by any other
 * component of the system by subscribing as a
 * {@link SinkDataListener listener} to the
 * {@link ModuleManager#addSinkListener(SinkDataListener) ModuleManager}.
 * Other components of syste
 *
 * This module is intended as an attach point for mechanisms that can help a
 * user debug their modules / data flows. Its expected that such debugging
 * aids will subscribe themselves as listeners to the data received by the
 * sink module and display that data to users via logs, screens or any
 * other means.
 *
 * Users can append sink module to any of their data flows to examine
 * / test the output that the data flow is generating.
 *
 * Besides routing data to the listeners, the sink module also
 * maintains statistics on data that it receives and makes those
 * statistics available via JMX.
 *
 * Do note that the sink module's mechanism to receive data is
 * synchronized. Hence, if it is appended to multiple data flows
 * that produce copious amounts of data, the performance of those
 * data flows may be bottlenecked. Its recommended that the sink module
 * be only used for debugging data flows and not be used on flows
 * that emit lots of data into it. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")  //$NON-NLS-1$
public class SinkModule extends Module
        implements DataReceiver, SinkModuleMXBean {

    @Override
    public void preStop() throws ModuleException {
        //do nothing
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws UnsupportedDataTypeException, StopDataFlowException {
        synchronized(this) {
            String type = inData == null
                    ? String.valueOf(inData)
                    : inData.getClass().getName();
            Counter c = mTypeStats.get(type);
            if(c == null) {
                c = new Counter();
                mTypeStats.put(type,c);
            }
            c.increment();
            c = mDataFlowStats.get(inFlowID);
            if(c == null) {
                c = new Counter();
                mDataFlowStats.put(inFlowID, c);
            }
            c.increment();
        }
        if(mManager == null) {
            Messages.LOG_SINK_MODULE_MISCONFIGURED.error(this);
        } else {
            mManager.receiveSinkData(inFlowID, inData);
        }
    }

    @Override
    public synchronized Map<String, Integer> getTypeStats() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for(String s: mTypeStats.keySet()) {
            map.put(s, mTypeStats.get(s).getValue());
        }
        return map;
    }

    @Override
    public synchronized Map<DataFlowID, Integer> getDataFlowStats() {
        HashMap<DataFlowID, Integer> map = new HashMap<DataFlowID, Integer>();
        for(DataFlowID id: mDataFlowStats.keySet()) {
            map.put(id, mDataFlowStats.get(id).getValue());
        }
        return map;
    }
    @Override
    public void resetStats() {
        synchronized (this) {
            mTypeStats.clear();
            mDataFlowStats.clear();
        }
    }

    @Override
    protected void preStart() throws ModuleException {
        //do nothing
    }
    /**
     * Creates an instance.
     */
    SinkModule() {
        super(SinkModuleFactory.INSTANCE_URN, true);
    }

    /**
     * Supplies the module manager instance.
     *
     * @param inManager the module manager instance
     */
    void setManager(ModuleManager inManager) {
        mManager = inManager;
    }

    private volatile ModuleManager mManager;
    private final HashMap<String, Counter> mTypeStats =
            new HashMap<String, Counter>();
    private final HashMap<DataFlowID, Counter> mDataFlowStats =
            new HashMap<DataFlowID, Counter>();

    /**
     * Instances of this class represent various counter values
     */
    private static class Counter {

        /**
         * Gets the current value of this counter.
         *
         * @return the current value of this counter
         */
        int getValue() {
            return mValue;
        }

        /**
         * Increments counter value.
         */
        void increment() {
            mValue++;
        }
        private int mValue = 0;
    }
}
