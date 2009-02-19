package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.module.*;
import org.marketcetera.trade.*;
import org.apache.commons.lang.ObjectUtils;

import java.util.Map;
import java.util.Hashtable;
import java.util.Date;

/* $License$ */
/**
 * The module that sends orders to ORS and emits reports
 * received from ORS.
 * <p>
 * The module only accepts data of following types
 * <ul>
 *      <li>{@link org.marketcetera.trade.OrderSingle}</li>
 *      <li>{@link org.marketcetera.trade.OrderCancel}</li>
 *      <li>{@link org.marketcetera.trade.OrderReplace}</li>
 *      <li>{@link org.marketcetera.trade.FIXOrder}</li> 
 * </ul>
 * <p>
 * The module will emit all the reports from the server when requested.
 * Following types of reports may be emitted by the module.
 * <ul>
 *      <li>{@link org.marketcetera.trade.ExecutionReport}</li>
 *      <li>{@link org.marketcetera.trade.OrderCancelReject}</li>
 * </ul>
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class ClientModule extends Module implements DataReceiver,
        DataEmitter, ClientModuleMXBean {

    @Override
    public void receiveData(DataFlowID inFlowID, Object inData)
            throws ReceiveDataException {
        try {
            if(inData instanceof OrderSingle) {
                getClient().sendOrder((OrderSingle) inData);
            } else if(inData instanceof OrderCancel) {
                getClient().sendOrder((OrderCancel) inData);
            } else if(inData instanceof OrderReplace) {
                getClient().sendOrder((OrderReplace) inData);
            } else if(inData instanceof FIXOrder) {
                getClient().sendOrderRaw((FIXOrder) inData);
            } else {
                throw new UnsupportedDataTypeException(new I18NBoundMessage2P(
                        Messages.UNSUPPORTED_DATA_TYPE, inFlowID.getValue(),
                        ObjectUtils.toString(inData)));
            }
        } catch (ConnectionException e) {
            throw new ReceiveDataException(e, new I18NBoundMessage1P(
                    Messages.SEND_ORDER_FAIL_NO_CONNECT,
                    ObjectUtils.toString(inData)));
        } catch (OrderValidationException e) {
            throw new ReceiveDataException(e, new I18NBoundMessage2P(
                        Messages.SEND_ORDER_VALIDATION_FAILED, inFlowID.getValue(),
                        ObjectUtils.toString(inData)));
        } catch (ClientInitException e) {
            throw new StopDataFlowException(e, new I18NBoundMessage1P(
                    Messages.SEND_ORDER_FAIL_NO_CONNECT,
                    ObjectUtils.toString(inData)));
        }
    }

    @Override
    public void requestData(DataRequest inRequest,
                            final DataEmitterSupport inSupport)
            throws RequestDataException {
        //No request parameters are supported.
        //All reports received are emitted.
        //Verify no request parameters are specified
        if(inRequest.getData() != null) {
            throw new IllegalRequestParameterValue(
                    Messages.REQUEST_PARAMETER_SPECIFIED);
        }
        try {
            ReportListener listener = new ReportListenerEmitter(inSupport);
            getClient().addReportListener(listener);
            mRequestTable.put(inSupport.getRequestID(), listener);
        } catch (ClientInitException e) {
            throw new RequestDataException(e,
                    Messages.REQUEST_CLIENT_NOT_INITIALIZED);
        }
    }

    @Override
    public void cancel(DataFlowID inFlowID, RequestID inRequestID) {
        ReportListener listener = mRequestTable.remove(inRequestID);
        try {
            getClient().removeReportListener(listener);
        } catch (ClientInitException e) {
            Messages.LOG_CLIENT_NOT_INIT_CANCEL_REQUEST.error(this, e,
                    inRequestID.toString());
        }
    }

    @Override
    public void reconnect() throws RuntimeException {
        try {
            getClient().reconnect();
        } catch (I18NException e) {
            throw new RuntimeException(e.getLocalizedDetail());
        }
    }

    @Override
    public ClientParameters getParameters() {
        try {
            return getClient().getParameters();
        } catch (ClientInitException e) {
            throw new RuntimeException(e.getLocalizedDetail());
        }
    }

    @Override
    public Date getLastConnectTime() throws RuntimeException {
        try {
            return getClient().getLastConnectTime();
        } catch (ClientInitException e) {
            throw new RuntimeException(e.getLocalizedDetail());
        }
    }

    /**
     * Creates an instance.
     *
     * @param inURN The instance URN
     * @param inAutoStart if the module should be auto-started.
     */
    protected ClientModule(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
    }

    @Override
    protected void preStart() throws ModuleException {
    }

    @Override
    protected void preStop() throws ModuleException {
    }

    private Client getClient() throws ClientInitException {
        return ClientManager.getInstance();
    }

    private final Map<RequestID, ReportListener> mRequestTable =
            new Hashtable<RequestID, ReportListener>();

    /**
     * Instances of this class receive execution report from ORS and
     * emit them into data flows.
     */
    private static class ReportListenerEmitter implements ReportListener {
        /**
         * Creates an instance.
         *
         * @param inSupport the data emitter support instance.
         */
        public ReportListenerEmitter(DataEmitterSupport inSupport) {
            mSupport = inSupport;
        }

        @Override
        public void receiveExecutionReport(ExecutionReport inReport) {
            SLF4JLoggerProxy.debug(this, "Emitting Report {}",  //$NON-NLS-1$
                    inReport);
            mSupport.send(inReport);
        }

        @Override
        public void receiveCancelReject(OrderCancelReject inReport) {
            SLF4JLoggerProxy.debug(this, "Emitting Cancel Reject {}",  //$NON-NLS-1$ 
                    inReport);
            mSupport.send(inReport);
        }

        private final DataEmitterSupport mSupport;
    }
}
