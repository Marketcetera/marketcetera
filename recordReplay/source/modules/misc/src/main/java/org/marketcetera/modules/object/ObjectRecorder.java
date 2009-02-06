package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;

import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Hashtable;

/* $License$ */
/**
 * A module that records all the data received through each data flow
 * as serialized java objects in a unique file per data flow.
 * The module will act as a pipe, ie. if it asked to emit data, it will
 * emit the data it receives after saving it into the file.
 * <p>
 * This module should not be used within the same data flow more than once. 
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ObjectRecorder extends Module
        implements DataReceiver, DataEmitter, ObjectRecorderMXBean {
    /**
     * Creates an instance.
     *
     * @param inURN The module URN
     * @param inAutoStart if the module should be autostarted.
     */
    protected ObjectRecorder(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        setOutputDirectory(System.getProperty("java.io.tmpdir"));  //$NON-NLS-1$
    }

    @Override
    protected void preStart() throws ModuleException {
    }

    @Override
    protected void preStop() throws ModuleException {
        synchronized (mOutputs) {
            for(DataFlowID id: mOutputs.keySet()) {
                ObjectOutputStream oos = mOutputs.get(id);
                if (oos != null) {
                    try {
                        oos.close();
                    } catch (IOException e) {
                        Messages.LOG_CLOSE_FILE_ERROR.warn(this,e,id.toString());
                    }
                }
            }
        }
        mOutputs.clear();
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException {
        try {
            ObjectOutputStream oos;
            synchronized (mOutputs) {
                oos = mOutputs.get(inFlowID);
                if(oos == null) {
                    oos = new ObjectOutputStream(new FileOutputStream(new File(
                            mOutputDirectory, "flow-" +   //$NON-NLS-1$
                            inFlowID.toString() + ".obj")));  //$NON-NLS-1$
                    mOutputs.put(inFlowID, oos);
                }
            }
            oos.writeObject(inData);
        } catch (IOException e) {
            throw new ReceiveDataException(e);
        }
        DataEmitterSupport support = mEmitTable.get(inFlowID);
        if(support != null) {
            support.send(inData);
        }
    }
    @Override
    public DataFlowID[] getSavedFlows() {
        synchronized (mOutputs) {
            Set<DataFlowID> keys = mOutputs.keySet();
            return keys.toArray(new DataFlowID[keys.size()]);
        }
    }

    @Override
    public String getOutputDirectory() {
        return mOutputDirectory;
    }

    @Override
    public void setOutputDirectory(String inOutputDirectory) {
        File dir = new File(inOutputDirectory);
        if(!dir.isDirectory()) {
            throw new IllegalArgumentException(dir.getAbsolutePath());
        }
        mOutputDirectory = inOutputDirectory;
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport)
            throws RequestDataException {
        mEmitTable.put(inSupport.getFlowID(), inSupport);
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        mEmitTable.remove(inFlowID);
        ObjectOutputStream oos;
        synchronized (mOutputs) {
            oos = mOutputs.remove(inFlowID);
        }
        if(oos != null) {
            try {
                oos.close();
            } catch (IOException e) {
                Messages.LOG_CLOSE_FILE_ERROR.warn(this,e,inFlowID.toString());
            }
        }
    }

    private String mOutputDirectory;
    private final Map<DataFlowID, ObjectOutputStream> mOutputs =
            new HashMap<DataFlowID, ObjectOutputStream>();
    private final Map<DataFlowID, DataEmitterSupport> mEmitTable =
            new Hashtable<DataFlowID, DataEmitterSupport>();
}
