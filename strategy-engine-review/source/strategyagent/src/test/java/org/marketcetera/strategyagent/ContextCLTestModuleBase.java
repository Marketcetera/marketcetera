package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.*;

/* $License$ */
/**
 * A module to help verify that the context class loader is correctly setup
 * when the module methods are invoked
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class ContextCLTestModuleBase extends Module
        implements DataEmitter, DataReceiver, DataFlowRequester, ContextCLModuleMXBean {
    protected ContextCLTestModuleBase(ModuleURN inURN) {
        super(inURN, true);
    }

    @Override
    public String getAttribute() {
        sGetAttributeLoader = Thread.currentThread().getContextClassLoader();
        return null;
    }

    @Override
    public void setAttribute(String inValue) {
        sSetAttributeLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void operation() {
        sOperationLoader = Thread.currentThread().getContextClassLoader();
    }
    @Override
    protected void preStart() throws ModuleException {
        sStartLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    protected void preStop() throws ModuleException {
        sStopLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void requestData(DataRequest inRequest, DataEmitterSupport inSupport) throws RequestDataException {
        sRequestLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        sCancelLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData) throws ReceiveDataException {
        sReceiveLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public void setFlowSupport(DataFlowSupport inSupport) {
        sFlowSupportLoader = Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader sStartLoader;
    public static ClassLoader sStopLoader;
    public static ClassLoader sRequestLoader;
    public static ClassLoader sCancelLoader;
    public static ClassLoader sReceiveLoader;
    public static ClassLoader sGetAttributeLoader;
    public static ClassLoader sSetAttributeLoader;
    public static ClassLoader sOperationLoader;
    public static ClassLoader sFlowSupportLoader;
}
