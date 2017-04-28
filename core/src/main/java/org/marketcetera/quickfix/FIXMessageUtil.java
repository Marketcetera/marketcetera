package org.marketcetera.quickfix;

import static org.marketcetera.quickfix.Messages.CANNOT_CREATE_FIX_FIELD;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.quickfix.cficode.OptionCFICode;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.trade.ExecutionTransType;
import org.marketcetera.trade.ExecutionType;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.OrderStatus;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.quickfix.AnalyzedMessage;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.Message.Header;
import quickfix.MessageUtils;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.StringField;
import quickfix.field.AvgPx;
import quickfix.field.CFICode;
import quickfix.field.ClOrdID;
import quickfix.field.CollReqID;
import quickfix.field.ConfirmReqID;
import quickfix.field.CumQty;
import quickfix.field.EncodedText;
import quickfix.field.ExecID;
import quickfix.field.ExecRefID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.MDEntryType;
import quickfix.field.MDReqID;
import quickfix.field.MarketDepth;
import quickfix.field.MsgType;
import quickfix.field.NetworkRequestID;
import quickfix.field.NoMDEntries;
import quickfix.field.OrdStatus;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.PosReqID;
import quickfix.field.PutOrCall;
import quickfix.field.QuoteID;
import quickfix.field.QuoteReqID;
import quickfix.field.QuoteStatusReqID;
import quickfix.field.RFQReqID;
import quickfix.field.SecurityReqID;
import quickfix.field.SecurityStatusReqID;
import quickfix.field.SettlInstReqID;
import quickfix.field.Symbol;
import quickfix.field.Text;
import quickfix.field.TradSesReqID;
import quickfix.field.TradeRequestID;
import quickfix.field.UserRequestID;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Collection of utilities to create work with FIX messages
 *
 * @author gmiller
 *         $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageUtil {
    /**
     * caches data dictionaries by FIX Version
     */
    private static final LoadingCache<FIXVersion,DataDictionary> cachedDataDictionaries = CacheBuilder.newBuilder().build(new CacheLoader<FIXVersion,DataDictionary>() {
        @Override
        public DataDictionary load(FIXVersion inKey)
                throws Exception
        {
            return new DataDictionary(inKey.getDataDictionaryName());
        }
    } );
    public static final String prettyPrintCategory = "fix.prettyprint";
    public static final String FIX_RESTORE_LOGGER_NAME = "metc.restore";
    private static final String LOGGER_NAME = FIXMessageUtil.class.getName();
    private static final int MAX_FIX_FIELDS = 2000;     // What we think the ID of the last fix field is
    public static final Pattern optionSymbolPattern = Pattern.compile("(\\w{1,3})\\+(\\w)(\\w)"); //$NON-NLS-1$

    /**
     * Creates a new instance of FIXMessageUtil
     */
    public FIXMessageUtil() {
    }

    public static int getMaxFIXFields() {
    	return MAX_FIX_FIELDS;
    }

    private static boolean msgTypeHelper(Message fixMessage, String msgType) {
    	if (fixMessage != null){
	    	try {
	            MsgType msgTypeField = new MsgType();
	            Header header = fixMessage.getHeader();
				if (header.isSetField(msgTypeField)){
	            	header.getField(msgTypeField);
	            	return msgType.equals(msgTypeField.getValue());
	            }
	        } catch (Exception ignored) {
                // ignored
            }
    	}
        return false;
    }
    /**
     * Get the <code>Instrument</code> value indicated in the given <code>Message</code> or <code>Group</code>.
     *
     * @param inFragment a <code>FieldMap</code> value
     * @return an <code>Instrument</code> value or <code>null</code> if no <code>Instrument</code> could be extracted
     */
    public static Instrument getInstrumentFromMessageFragment(FieldMap inFragment)
    {
        return InstrumentFromMessage.SELECTOR.forValue(inFragment).extract(inFragment);
    }
    /**
     * Get the <code>SecurityExchange</code> (207) value from the given <code>Message</code> or <code>Group</code>.
     *
     * @param inFragment a <code>FieldMap</code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    public static String getSecurityExchangeFromMessageFragment(FieldMap inFragment)
    {
        if(inFragment.isSetField(quickfix.field.SecurityExchange.FIELD)) {
            try {
                return inFragment.getString(quickfix.field.SecurityExchange.FIELD);
            } catch (FieldNotFound e) {
                // this shouldn't happen because we just checked for it, but return null anyway
            }
        }
        return null;
    }
    /**
     * Get the session ID of the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>SessionID</code> value
     * @throws FieldNotFound if the session ID could not be built
     */
    public static SessionID getSessionId(Message inMessage)
            throws FieldNotFound
    {
        return MessageUtils.getSessionID(inMessage);
    }
    /**
     * Get the mirror image of the given session id.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @return a <code>SessionID</code> value
     */
    public static SessionID getReversedSessionId(SessionID inSessionId)
    {
        SessionID reversedSessionId = new SessionID(inSessionId.getBeginString(),
                                                    inSessionId.getTargetCompID(),
                                                    inSessionId.getSenderCompID());
        return reversedSessionId;
    }
    /**
     * Log the given message.
     *
     * @param inMessage a <code>Message</code> value
     */
    public static void logMessage(Message inMessage)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
            try {
                SLF4JLoggerProxy.debug(FIXMessageUtil.prettyPrintCategory,
                                       new AnalyzedMessage(FIXMessageUtil.getDataDictionary(FIXVersion.getFIXVersion(inMessage)),
                                                           inMessage).toString());
            } catch (FieldNotFound e) {
                SLF4JLoggerProxy.warn(FIXMessageUtil.prettyPrintCategory,
                                      e);
            }
        }
    }
    /**
     * Log the given message.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     */
    public static void logMessage(SessionID inSessionId,
                                  Message inMessage)
    {
        if(SLF4JLoggerProxy.isDebugEnabled(FIXMessageUtil.prettyPrintCategory)) {
            SLF4JLoggerProxy.debug(FIXMessageUtil.prettyPrintCategory,
                                   new AnalyzedMessage(FIXMessageUtil.getDataDictionary(FIXVersion.getFIXVersion(inSessionId)),
                                                       inMessage).toString());
        }
    }
    /**
     * Get the data dictionary for the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>DataDictionary</code> value
     * @throws FieldNotFound if the FIX version could not be determined from the given message
     */
    public static DataDictionary getDataDictionary(Message inMessage)
            throws FieldNotFound
    {
        return getDataDictionary(FIXVersion.getFIXVersion(inMessage));
    }
    /**
     * Get the data dictionary for the given session.
     *
     * @param inSessionId a <code>Message</code> value
     * @return a <code>DataDictionary</code> value
     * @throws FieldNotFound if the FIX version could not be determined from the given message
     * @throws UnsupportedOperationException if the given session ID does not correspond to an active session
     */
    public static DataDictionary getDataDictionary(SessionID inSessionId)
            throws FieldNotFound
    {
        Session session = Session.lookupSession(inSessionId);
        if(session == null) {
            throw new UnsupportedOperationException(Messages.MISSING_SESSION.getText(session));
        }
        quickfix.field.ApplVerID applVerId = session.getTargetDefaultApplicationVersionID();
        return getDataDictionary(FIXVersion.getFIXVersion(applVerId));
    }
    /**
     * Get the data dictionary for the given version.
     *
     * @param inVersion a <code>FIXVersion</code> value
     * @return a <code>DataDictionary</code> value
     */
    public static DataDictionary getDataDictionary(FIXVersion inVersion)
    {
        return cachedDataDictionaries.getUnchecked(inVersion);
    }
    public static Message createSessionReject(Message inMessage,
                                              int inReason,
                                              String inText)
            throws FieldNotFound, SessionNotFound, ExecutionException
    {
        FIXVersion version = FIXVersion.getFIXVersion(inMessage);
        Message reject = version.getMessageFactory().createSessionReject(inMessage,
                                                                         inReason);
        FIXMessageUtil.fillFieldsFromExistingMessage(reject,
                                                     inMessage,
                                                     getDataDictionary(inMessage),
                                                     false);
        if(inText != null) {
            reject.setField(new Text(inText));
        }
        return reject;
    }
    /**
     * 
     *
     *
     * @param inSessionId
     * @param inMessage
     * @param inReason
     * @param inText
     * @return
     * @throws FieldNotFound
     */
    public static Message createBusinessReject(SessionID inSessionId,
                                               Message inMessage,
                                               int inReason,
                                               String inText)
            throws FieldNotFound
    {
        FIXVersion version = FIXVersion.getFIXVersion(inSessionId);
        Message reject = version.getMessageFactory().createMessage(MsgType.BUSINESS_MESSAGE_REJECT);
        FIXMessageUtil.fillFieldsFromExistingMessage(reject,
                                                     inMessage,
                                                     getDataDictionary(version),
                                                     false);
        if(inText != null) {
            reject.setField(new Text(inText));
        }
        reject.setString(quickfix.field.RefMsgType.FIELD,
                         inMessage.getHeader().getString(MsgType.FIELD));
        reject.setInt(quickfix.field.RefSeqNum.FIELD,
                      inMessage.getHeader().getInt(quickfix.field.MsgSeqNum.FIELD));
        reject.setInt(quickfix.field.BusinessRejectReason.FIELD,
                      inReason);
        return reject;
    }
    /**
     * Create a business reject (35=j) with the given reason and text for the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inReason an <code>int</code> value
     * @param inText a <code>String</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message could not be built
     */
    public static Message createBusinessReject(Message inMessage,
                                               int inReason,
                                               String inText)
            throws FieldNotFound
    {
        FIXVersion version = FIXVersion.getFIXVersion(inMessage);
        Message reject = version.getMessageFactory().createMessage(MsgType.BUSINESS_MESSAGE_REJECT);
        FIXMessageUtil.fillFieldsFromExistingMessage(reject,
                                                     inMessage,
                                                     getDataDictionary(inMessage),
                                                     false);
        if(inText != null) {
            reject.setField(new Text(inText));
        }
        reject.setString(quickfix.field.RefMsgType.FIELD,
                         inMessage.getHeader().getString(MsgType.FIELD));
        reject.setInt(quickfix.field.RefSeqNum.FIELD,
                      inMessage.getHeader().getInt(quickfix.field.MsgSeqNum.FIELD));
        reject.setInt(quickfix.field.BusinessRejectReason.FIELD,
                      inReason);
        return reject;
    }
    public static char getCancelRejResponseToFor(Message inMessage)
            throws FieldNotFound
    {
        switch(inMessage.getHeader().getString(quickfix.field.MsgType.FIELD)) {
            case quickfix.field.MsgType.ORDER_CANCEL_REPLACE_REQUEST:
                return quickfix.field.CxlRejResponseTo.ORDER_CANCEL_REPLACE_REQUEST;
            case quickfix.field.MsgType.ORDER_CANCEL_REQUEST:
                return quickfix.field.CxlRejResponseTo.ORDER_CANCEL_REQUEST;
        }
        throw new IllegalArgumentException();
    }
    /**
     * Create an order cancel reject based on the given attributes.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     * @param inText a <code>String</code> value
     * @param inCancelRejResponseTo a <code>char</code> value
     * @param inCancelRejReason an <code>int</code> value
     * @return a <code>Message</code> value
     * @throws FieldNotFound if the message cannot be built
     */
    public static Message createOrderCancelReject(SessionID inSessionId,
                                                  Message inMessage,
                                                  String inText,
                                                  char inCancelRejResponseTo,
                                                  int inCancelRejReason)
             throws FieldNotFound
    {
        FIXVersion version = FIXVersion.getFIXVersion(inSessionId);
        Message reject = version.getMessageFactory().createMessage(MsgType.ORDER_CANCEL_REJECT);
        FIXMessageUtil.fillFieldsFromExistingMessage(reject,
                                                     inMessage,
                                                     getDataDictionary(version),
                                                     false);
        if(inText != null) {
            reject.setField(new Text(inText));
        }
        reject.setChar(quickfix.field.CxlRejResponseTo.FIELD,
                       inCancelRejResponseTo);
        if(inCancelRejReason != -1) {
            reject.setInt(quickfix.field.CxlRejReason.FIELD,
                          inCancelRejReason);
        }
        if(inCancelRejReason == quickfix.field.CxlRejReason.UNKNOWN_ORDER) {
            reject.setField(new quickfix.field.OrderID("none"));
        }
        if(version.isFixT()) {
            reject.getHeader().setField(new quickfix.field.ApplVerID(version.getApplicationVersion()));
        }
        return reject;
    }
    public static Message createOrderCancelReject(Message inMessage,
                                                  String inText,
                                                  char inCancelRejResponseTo,
                                                  int inCancelRejReason)
            throws FieldNotFound, SessionNotFound, ExecutionException
    {
        FIXVersion version = FIXVersion.getFIXVersion(inMessage);
        Message reject = version.getMessageFactory().createMessage(MsgType.ORDER_CANCEL_REJECT);
        FIXMessageUtil.fillFieldsFromExistingMessage(reject,
                                                     inMessage,
                                                     getDataDictionary(inMessage),
                                                     false);
        if(inText != null) {
            reject.setField(new Text(inText));
        }
        reject.setChar(quickfix.field.CxlRejResponseTo.FIELD,
                       inCancelRejResponseTo);
        if(inCancelRejReason != -1) {
            reject.setInt(quickfix.field.CxlRejReason.FIELD,
                          inCancelRejReason);
        }
        if(inCancelRejReason == quickfix.field.CxlRejReason.UNKNOWN_ORDER) {
            reject.setField(new quickfix.field.OrderID("none"));
        }
        return reject;
    }
    /**
     * Create an execution report response to the given message.
     *
     * @param inSessionId a <code>SessionID</code> value
     * @param inMessage a <code>Message</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inExecType an <code>ExecutionType</code> value or <code>null</code>
     * @param inExecTransType an <code>ExecutionTransType</code> value or <code>null</code>
     * @param inText a <code>String</code> value or <code>null</code>
     * @return a <code>Message</code> value
     */
    public static Message createExecutionReport(SessionID inSessionId,
                                                Message inMessage,
                                                OrderStatus inOrderStatus,
                                                ExecutionType inExecType,
                                                ExecutionTransType inExecTransType,
                                                String inText)
    {
        try {
            FIXVersion fixVersion = FIXVersion.getFIXVersion(inSessionId);
            Message executionReport = fixVersion.getMessageFactory().createMessage(MsgType.EXECUTION_REPORT);
            FIXMessageAugmentor messageAugmentor = fixVersion.getMessageFactory().getMsgAugmentor();
            DataDictionary dataDictionary = getDataDictionary(fixVersion);
            FIXMessageUtil.fillFieldsFromExistingMessage(executionReport,
                                                         inMessage,
                                                         dataDictionary,
                                                         false);
            if(dataDictionary.isField(quickfix.field.ExecTransType.FIELD)) {
                if(inExecTransType != null) {
                    executionReport.setField(new ExecTransType(inExecTransType.getFIXValue()));
                    if(inExecTransType == ExecutionTransType.Cancel || inExecTransType == ExecutionTransType.Correct) {
                        if(inMessage.isSetField(ClOrdID.FIELD)) {
                            executionReport.setField(new ExecRefID(inMessage.getString(ClOrdID.FIELD)));
                        } else {
                            executionReport.setField(new ExecRefID(UUID.randomUUID().toString()));
                        }
                    }
                }
            }
            if(inOrderStatus != null) {
                executionReport.setField(new OrdStatus(inOrderStatus.getFIXValue()));
            }
            if(inExecType != null) {
                executionReport.setField(new ExecType(inExecType.getFIXValue()));
            }
            executionReport.setField(new ExecID(UUID.randomUUID().toString()));
            if(inText != null) {
                executionReport.setField(new Text(inText));
            }
            if(inMessage.isSetField(ClOrdID.FIELD)) {
                ClOrdID clOrdId = new ClOrdID();
                inMessage.getField(clOrdId);
                executionReport.setField(clOrdId);
            }
            if(inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                quickfix.field.OrderID orderId = new quickfix.field.OrderID();
                inMessage.getField(orderId);
                executionReport.setField(orderId);
            }
            if(!executionReport.isSetField(quickfix.field.OrderID.FIELD)) {
                executionReport.setField(new quickfix.field.OrderID(UUID.randomUUID().toString()));
            }
            if(!executionReport.isSetField(quickfix.field.LeavesQty.FIELD)) {
                executionReport.setField(new LeavesQty(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.CumQty.FIELD)) {
                executionReport.setField(new CumQty(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.AvgPx.FIELD)) {
                executionReport.setField(new AvgPx(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.LastShares.FIELD)) {
                executionReport.setField(new LastShares(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.LastPx.FIELD)) {
                executionReport.setField(new LastPx(BigDecimal.ZERO));
            }
            BigDecimal orderQty = BigDecimal.ZERO;
            if(inMessage.isSetField(OrderQty.FIELD)) {
                OrderQty fixOrderQty = new OrderQty();
                inMessage.getField(fixOrderQty);
                orderQty = fixOrderQty.getValue();
            }
            executionReport.setField(new OrderQty(orderQty));
            if(fixVersion.isFixT()) {
                executionReport.getHeader().setField(new quickfix.field.ApplVerID(fixVersion.getApplicationVersion()));
            }
            messageAugmentor.executionReportAugment(executionReport);
            return executionReport;
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Create an execution report response to the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @param inOrderStatus an <code>OrderStatus</code> value
     * @param inExecType an <code>ExecutionType</code> value or <code>null</code>
     * @param inExecTransType an <code>ExecutionTransType</code> value or <code>null</code>
     * @param inText a <code>String</code> value or <code>null</code>
     * @return a <code>Message</code> value
     */
    public static Message createExecutionReport(Message inMessage,
                                                OrderStatus inOrderStatus,
                                                ExecutionType inExecType,
                                                ExecutionTransType inExecTransType,
                                                String inText)
    {
        try {
            FIXVersion fixVersion = FIXVersion.getFIXVersion(inMessage);
            Message executionReport = fixVersion.getMessageFactory().createMessage(MsgType.EXECUTION_REPORT);
            FIXMessageAugmentor messageAugmentor = fixVersion.getMessageFactory().getMsgAugmentor();
            DataDictionary dataDictionary = getDataDictionary(fixVersion);
            FIXMessageUtil.fillFieldsFromExistingMessage(executionReport,
                                                         inMessage,
                                                         dataDictionary,
                                                         false);
            if(dataDictionary.isField(quickfix.field.ExecTransType.FIELD)) {
                if(inExecTransType != null) {
                    executionReport.setField(new ExecTransType(inExecTransType.getFIXValue()));
                    if(inExecTransType == ExecutionTransType.Cancel || inExecTransType == ExecutionTransType.Correct) {
                        if(inMessage.isSetField(ClOrdID.FIELD)) {
                            executionReport.setField(new ExecRefID(inMessage.getString(ClOrdID.FIELD)));
                        } else {
                            executionReport.setField(new ExecRefID(UUID.randomUUID().toString()));
                        }
                    }
                }
            }
            if(inOrderStatus != null) {
                executionReport.setField(new OrdStatus(inOrderStatus.getFIXValue()));
            }
            if(inExecType != null) {
                executionReport.setField(new ExecType(inExecType.getFIXValue()));
            }
            executionReport.setField(new ExecID(UUID.randomUUID().toString()));
            if(inText != null) {
                executionReport.setField(new Text(inText));
            }
            if(inMessage.isSetField(ClOrdID.FIELD)) {
                ClOrdID clOrdId = new ClOrdID();
                inMessage.getField(clOrdId);
                executionReport.setField(clOrdId);
            }
            if(inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                quickfix.field.OrderID orderId = new quickfix.field.OrderID();
                inMessage.getField(orderId);
                executionReport.setField(orderId);
            }
            if(!executionReport.isSetField(quickfix.field.OrderID.FIELD)) {
                executionReport.setField(new quickfix.field.OrderID(UUID.randomUUID().toString()));
            }
            if(!executionReport.isSetField(quickfix.field.LeavesQty.FIELD)) {
                executionReport.setField(new LeavesQty(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.CumQty.FIELD)) {
                executionReport.setField(new CumQty(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.AvgPx.FIELD)) {
                executionReport.setField(new AvgPx(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.LastShares.FIELD)) {
                executionReport.setField(new LastShares(BigDecimal.ZERO));
            }
            if(!executionReport.isSetField(quickfix.field.LastPx.FIELD)) {
                executionReport.setField(new LastPx(BigDecimal.ZERO));
            }
            BigDecimal orderQty = BigDecimal.ZERO;
            if(inMessage.isSetField(OrderQty.FIELD)) {
                OrderQty fixOrderQty = new OrderQty();
                inMessage.getField(fixOrderQty);
                orderQty = fixOrderQty.getValue();
            }
            executionReport.setField(new OrderQty(orderQty));
            if(fixVersion.isFixT()) {
                executionReport.getHeader().setField(new quickfix.field.ApplVerID(fixVersion.getApplicationVersion()));
            }
            messageAugmentor.executionReportAugment(executionReport);
            return executionReport;
        } catch (FieldNotFound e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Gets the human-readable message identifier from the given message.
     *
     * @param inMessage an <code>LvtsMessage</code> value
     * @return a <code>String</code> value
     */
    public static String getMessageIdentifier(Message inMessage)
    {
        StringBuilder output = new StringBuilder();
        try {
            SessionID sessionId = null;
            try {
                sessionId = getSessionId(inMessage);
            } catch(FieldNotFound ignored) {}
            String msgType = inMessage.getHeader().getString(quickfix.field.MsgType.FIELD);
            if(sessionId != null) {
                output.append(sessionId).append(' ');
            }
            output.append(msgType).append(' ');
            switch(msgType) {
                case MsgType.ORDER_SINGLE:
                case MsgType.ORDER_STATUS_REQUEST:
                    if(inMessage.isSetField(ClOrdID.FIELD)) {
                        output.append(inMessage.getString(ClOrdID.FIELD));
                    } else {
                        output.append("no-clordid");
                    }
                    break;
                case MsgType.ORDER_CANCEL_REPLACE_REQUEST:
                case MsgType.ORDER_CANCEL_REQUEST:
                    if(inMessage.isSetField(ClOrdID.FIELD)) {
                        output.append(inMessage.getString(ClOrdID.FIELD));
                    } else {
                        output.append("no clordid");
                    }
                    output.append(' ');
                    if(inMessage.isSetField(OrigClOrdID.FIELD)) {
                        output.append(inMessage.getString(OrigClOrdID.FIELD));
                    } else {
                        output.append("no-orig-clordid");
                    }
                    break;
                case MsgType.DONT_KNOW_TRADE:
                    if(inMessage.isSetField(quickfix.field.OrderID.FIELD)) {
                        output.append(inMessage.getString(quickfix.field.OrderID.FIELD));
                    } else {
                        output.append("no-orderid");
                    }
                    output.append(' ');
                    if(inMessage.isSetField(quickfix.field.ExecID.FIELD)) {
                        output.append(inMessage.getString(quickfix.field.ExecID.FIELD));
                    } else {
                        output.append("no-execid");
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            SLF4JLoggerProxy.warn(FIXMessageUtil.class,
                                  e);
        }
        return output.toString();
    }
    public static boolean isExecutionReport(Message message) {
        return msgTypeHelper(message, MsgType.EXECUTION_REPORT);
    }

    public static boolean isOrderSingle(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_SINGLE);
    }

    public static boolean isReject(Message message) {
        return msgTypeHelper(message, MsgType.REJECT);
    }

    public static boolean isBusinessMessageReject(Message message) {
    	return msgTypeHelper(message, MsgType.BUSINESS_MESSAGE_REJECT);
    }

    public static boolean isCancelReject(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REJECT);
    }

    public static boolean isStatusRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_STATUS_REQUEST);
    }

    public static boolean isCancelRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REQUEST);
    }

    public static boolean isCancelReplaceRequest(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_CANCEL_REPLACE_REQUEST);
    }

    public static boolean isOrderList(Message message) {
        return msgTypeHelper(message, MsgType.ORDER_LIST);
    }

    public static boolean isLogon(Message message){
    	return msgTypeHelper(message, MsgType.LOGON);
    }

    public static boolean isLogout(Message message){
    	return msgTypeHelper(message, MsgType.LOGOUT);
    }

	public static boolean isMarketDataRequest(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_REQUEST);
	}

	public static boolean isMarketDataSnapshotFullRefresh(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH);
	}

	public static boolean isMarketDataIncrementalRefresh(Message message) {
    	return msgTypeHelper(message, MsgType.MARKET_DATA_INCREMENTAL_REFRESH);
	}

	public static boolean isResendRequest(Message message) {
		return msgTypeHelper(message, MsgType.RESEND_REQUEST);
	}

	public static boolean isDerivativeSecurityList(
			Message message) {
		return msgTypeHelper(message, MsgType.DERIVATIVE_SECURITY_LIST);
	}

	public static boolean isDerivativeSecurityListRequest(Message message) {
		return msgTypeHelper(message, MsgType.DERIVATIVE_SECURITY_LIST_REQUEST);
	}

	public static boolean isSecurityDefinitionRequest(Message message) {
		return msgTypeHelper(message, MsgType.SECURITY_DEFINITION_REQUEST);
	}

	public static boolean isSecurityDefinition(Message message) {
		return msgTypeHelper(message, MsgType.SECURITY_DEFINITION);
	}

    public static boolean isTradingSessionStatus(Message message) {
        return msgTypeHelper(message, MsgType.TRADING_SESSION_STATUS);
    }

    public static boolean isHeartbeat(Message message) {
		return msgTypeHelper(message, MsgType.HEARTBEAT);
	}
    public static boolean isSecurityListRequest(Message inMessage)
    {
        return msgTypeHelper(inMessage,
                             MsgType.SECURITY_LIST_REQUEST);
    }

    @Deprecated
	public static boolean isEquityOptionOrder(Message message)
	{
		try {
			return isOrderSingle(message)
			&& (
					message.isSetField(PutOrCall.FIELD) ||
					(message.isSetField(Symbol.FIELD) && optionSymbolPattern.matcher(message.getString(Symbol.FIELD)).matches()) ||
					message.isSetField(CFICode.FIELD) && OptionCFICode.isOptionCFICode(message.getString(CFICode.FIELD))
				);
		} catch (FieldNotFound e) {
			// should never happen
			return false;
		}
	}

	public static boolean isCancellable(char ordStatus) {
		switch (ordStatus){
		case OrdStatus.ACCEPTED_FOR_BIDDING:
		case OrdStatus.CALCULATED:
		case OrdStatus.NEW:
		case OrdStatus.PARTIALLY_FILLED:
		case OrdStatus.PENDING_CANCEL:
		case OrdStatus.PENDING_NEW:
		case OrdStatus.PENDING_REPLACE:
		case OrdStatus.STOPPED:
		case OrdStatus.SUSPENDED:
		case OrdStatus.REPLACED:
			return true;
		case OrdStatus.CANCELED:
		case OrdStatus.DONE_FOR_DAY:
		case OrdStatus.EXPIRED:
		case OrdStatus.FILLED:
		case OrdStatus.REJECTED:
		default:
			return false;
		}
	}

	public static boolean isCancellable(Message executionReport){
		if (isExecutionReport(executionReport)){
			try {
				return isCancellable(executionReport.getChar(OrdStatus.FIELD));
			} catch (FieldNotFound e) {
				return false;
			}
		}
		return false;
	}
    /**
     * FIX market depth constant for "best bid or offer" or Level I
     */
    public static final int TOP_OF_BOOK_DEPTH = 1;
	/**
	 * FIX market depth constant for "full book" or Level II
	 */
    public static final int FULL_BOOK_DEPTH = 0;
	/**
	 * Determines if the given message is a Level I or "top of book" message.
	 *
	 * @param inMessage a <code>Message</code> value
	 * @return a <code>boolean</code> value
	 */
	public static boolean isLevelOne(Message inMessage)
	{
	    int depth;
	    try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == TOP_OF_BOOK_DEPTH;
	}
    /**
     * Determines if the given message is a Level II or "depth of book" message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isLevelTwo(Message inMessage)
    {
        int depth;
        try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == FULL_BOOK_DEPTH;
    }
	/**
	 * Determines if the given message is a Level II or "full book" message.
	 *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value
	 */
	public static boolean isFullBook(Message inMessage)
	{
        int depth;
        try {
            depth = getMarketDepth(inMessage);
        } catch (FieldNotFound e) {
            return false;
        }
        return depth == FULL_BOOK_DEPTH;
	}
	/**
	 * Gets the market depth for the given message.
	 *
	 * @param inMessage a <code>Message</code> value
	 * @return an <code>int</code>
	 * @throws FieldNotFound if the given message does not contain market depth information
	 */
	private static int getMarketDepth(Message inMessage)
	    throws FieldNotFound
	{
	    return inMessage.getInt(MarketDepth.FIELD);
	}

    /**
     * See {@link #fillFieldsFromExistingMessage(quickfix.Message, quickfix.Message, boolean, java.util.Set)}.
     * Invokes this api with a null inclusion set.
     *
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     boolean onlyCopyRequiredFields)
    {
        fillFieldsFromExistingMessage(outgoingMessage, existingMessage, onlyCopyRequiredFields, null);
    }

    /**
     * See {@link #fillFieldsFromExistingMessage(quickfix.Message, quickfix.Message, DataDictionary, boolean, java.util.Set)}.
     * Invokes this api with a null inclusion set.
     *
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param dict The data dictionary.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     DataDictionary dict,
                                                     boolean onlyCopyRequiredFields)
    {
        fillFieldsFromExistingMessage(outgoingMessage, existingMessage, dict, onlyCopyRequiredFields, null);
    }

    /**
     * Helper method to extract all useful fields from an existing message into another message
     * This is usually called when the "existing" message is malformed and is missing some fields,
     * and an appropriate "reject" message needs to be sent.
     * Can't say we are proud of this method - it's rather a kludge.
     * Goes through all the required fields in "outgoing" message, and ignores any missing ones
     * Skips over any of the outgoing fields that have already been set
     *
     * Use cases: an order comes in missing a Side, so we need to create an ExecutionReport
     * that's a rejection, and need to extract all the other fields (ClOrdId, size, etc)
     * which may or may not be present since the order is malformed
     * <strong>warning: ignores groups</strong>
     *
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     * @param inclusionSet the set of fields that should be copied. Can be null.
     * If not null, only the fields present in this set are copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     boolean onlyCopyRequiredFields,
                                                     Set<Integer> inclusionSet)
    {
        fillFieldsFromExistingMessage
            (outgoingMessage,
             existingMessage,
             CurrentFIXDataDictionary.getCurrentFIXDataDictionary().
             getDictionary(),
             onlyCopyRequiredFields,
             inclusionSet);
    }
    /**
     * Helper method to extract all useful fields from an existing message into another message
     * This is usually called when the "existing" message is malformed and is missing some fields,
     * and an appropriate "reject" message needs to be sent.
     * Can't say we are proud of this method - it's rather a kludge.
     * Goes through all the required fields in "outgoing" message, and ignores any missing ones
     * Skips over any of the outgoing fields that have already been set
     *
     * Use cases: an order comes in missing a Side, so we need to create an ExecutionReport
     * that's a rejection, and need to extract all the other fields (ClOrdId, size, etc)
     * which may or may not be present since the order is malformed
     * <strong>warning: ignores groups</strong>
     *
     * @param outgoingMessage The message whose fields need to be filled.
     * @param existingMessage The message whose fields need to be copied.
     * @param dict The data dictionary.
     * @param onlyCopyRequiredFields if only required fields should be copied.
     * @param inclusionSet the set of fields that should be copied. Can be null.
     * If not null, only the fields present in this set are copied.
     */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage,
                                                     Message existingMessage,
                                                     DataDictionary dict,
                                                     boolean onlyCopyRequiredFields,
                                                     Set<Integer> inclusionSet)
    {
        String msgType=null;
        try {
            msgType=outgoingMessage.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound ex) {
            Messages.FIX_OUTGOING_NO_MSGTYPE.error(LOGGER_NAME, ex);
            return;
        }

        Iterator<Field<?>> fieldItr=existingMessage.iterator();
        while (fieldItr.hasNext()) {
            Field<?> field = fieldItr.next();
            int fieldInt=field.getTag();
            if(inclusionSet != null && !(inclusionSet.contains(fieldInt))) {
                continue;
            }
            if ((!onlyCopyRequiredFields || dict.isRequiredField(msgType,
                                                                 fieldInt))
                && !outgoingMessage.isSetField(fieldInt)
                && dict.isMsgField(msgType, fieldInt)) {
                try {
                    outgoingMessage.setField(existingMessage
                                             .getField(new StringField(fieldInt)));
                } catch (FieldNotFound e) {
                    // do nothing and ignore
                }
            }
        }
    }
    /**
     * Warning! This method does not handle groups.
     * @param copyTo
     * @param copyFrom
     */
    public static void copyFields(FieldMap copyTo,
                                  FieldMap copyFrom)
    {
    	Iterator<Field<?>> iter = copyFrom.iterator();
    	while (iter.hasNext()){
    		Field<?> field = iter.next();
    		try {
				copyTo.setField(copyFrom.getField(new StringField(field.getTag())));
			} catch (FieldNotFound e) {
				// do nothing
			}
    	}
    }

    public static boolean isRequiredField(Message message, int whichField) {
    	boolean required = false;
		try {
			String msgType;
			msgType = message.getHeader().getString(MsgType.FIELD);
			return isRequiredField(msgType, whichField);
		} catch (Exception e) {
			// Ignore
		}
		return required;
    }

    public static boolean isRequiredField(String msgType, int whichField){
    	boolean required = false;
		try {
			DataDictionary dictionary = CurrentFIXDataDictionary
					.getCurrentFIXDataDictionary().getDictionary();
			required = dictionary.isRequiredField(msgType, whichField);
		} catch (Exception anyException) {
			// Ignore
		}
		return required;
	}

    //cl todo:need to check if this take care of custom fields
    public static boolean isValidField(int whichField) {
		boolean valid = false;
		try {
			DataDictionary dictionary = CurrentFIXDataDictionary
					.getCurrentFIXDataDictionary().getDictionary();
			valid = dictionary.isField(whichField);
		} catch (Exception anyException) {
			// Ignore
		}
		return valid;
	}

    /**
	 * Copy only required fields.
	 */
    public static void fillFieldsFromExistingMessage(Message outgoingMessage, Message existingMessage) {
    	fillFieldsFromExistingMessage(outgoingMessage, existingMessage, true);
    }

	public static void insertFieldIfMissing(int fieldNumber, String value, FieldMap fieldMap) throws CoreException {
		if (fieldMap.isSetField(fieldNumber)){
			StringField testField = new StringField(fieldNumber);
			try {
				fieldMap.getField(testField);
				if(testField.getValue().equals(value)){
					return;
				}
			} catch (FieldNotFound ignored) {
				//Unexpected as isSetField() returned true
				//Don't do anything so that we set the field before we return.
			}
		}
		fieldMap.setField(new StringField(fieldNumber, value));
	}

	public static String getTextOrEncodedText(Message aMessage, String defaultString) {
		String text = defaultString;
		if (aMessage.isSetField(Text.FIELD)){
			try {
				text = aMessage.getString(Text.FIELD);
			} catch (FieldNotFound ignored) {
			}
		} else {
			try {
				text = aMessage.getString(EncodedText.FIELD); //i18n_string todo use the correct MessageEncoding value
			} catch (FieldNotFound ignored) {
			}
		}
		return text;
	}

	/**
	 *
	 * @param msgType the msgType of the request message
	 * @return the field that represents the request/response correlation ID in the request message
	 */
	public static StringField getCorrelationField(FIXVersion version, String msgType) {
		StringField reqIDField = null;
		if (MsgType.COLLATERAL_REQUEST.equals(msgType) || MsgType.COLLATERAL_RESPONSE.equals(msgType)){
			reqIDField = new CollReqID();
		} else if (MsgType.CONFIRMATION_REQUEST.equals(msgType) || MsgType.CONFIRMATION.equals(msgType)){
			reqIDField = new ConfirmReqID();
		} else if (MsgType.DERIVATIVE_SECURITY_LIST_REQUEST.equals(msgType) || MsgType.DERIVATIVE_SECURITY_LIST.equals(msgType)){
			reqIDField = new SecurityReqID();
		} else if (MsgType.MARKET_DATA_REQUEST.equals(msgType) || MsgType.MARKET_DATA_INCREMENTAL_REFRESH.equals(msgType) || MsgType.MARKET_DATA_REQUEST_REJECT.equals(msgType) || MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH.equals(msgType)){
			reqIDField = new MDReqID();
		} else if (MsgType.NETWORK_STATUS_REQUEST.equals(msgType) || MsgType.NETWORK_STATUS_RESPONSE.equals(msgType)){
			reqIDField = new NetworkRequestID();
		} else if (MsgType.POSITION_MAINTENANCE_REQUEST.equals(msgType) || MsgType.POSITION_MAINTENANCE_REPORT.equals(msgType)
				||MsgType.REQUEST_FOR_POSITIONS.equals(msgType) || MsgType.REQUEST_FOR_POSITIONS_ACK.equals(msgType) || MsgType.POSITION_REPORT.equals(msgType)){
			reqIDField = new PosReqID();
		} else if (MsgType.QUOTE_REQUEST.equals(msgType) || MsgType.QUOTE_REQUEST_REJECT.equals(msgType) || MsgType.QUOTE.equals(msgType)){
			reqIDField = new QuoteReqID();
		} else if (MsgType.QUOTE_STATUS_REQUEST.equals(msgType) || MsgType.QUOTE_STATUS_REPORT.equals(msgType)){
			if (FIXVersion.FIX42.equals(version)){
				reqIDField = new QuoteID();
			} else {
				reqIDField = new QuoteStatusReqID();
			}
		} else if (MsgType.RFQ_REQUEST.equals(msgType)){
			reqIDField = new RFQReqID();
		} else if (MsgType.SECURITY_DEFINITION_REQUEST.equals(msgType) || MsgType.SECURITY_DEFINITION.equals(msgType)
				|| MsgType.SECURITY_LIST_REQUEST.equals(msgType) || MsgType.SECURITY_LIST.equals(msgType)
				|| MsgType.SECURITY_TYPE_REQUEST.equals(msgType) || MsgType.SECURITY_TYPES.equals(msgType)){
			reqIDField = new SecurityReqID();
		} else if (MsgType.SECURITY_STATUS_REQUEST.equals(msgType) || MsgType.SECURITY_STATUS.equals(msgType)){
			reqIDField = new SecurityStatusReqID();
		} else if (MsgType.SETTLEMENT_INSTRUCTION_REQUEST.equals(msgType) || MsgType.SETTLEMENT_INSTRUCTIONS.equals(msgType)){
			reqIDField = new SettlInstReqID();
		} else if (MsgType.TRADE_CAPTURE_REPORT_REQUEST.equals(msgType) || MsgType.TRADE_CAPTURE_REPORT.equals(msgType) || MsgType.TRADE_CAPTURE_REPORT_REQUEST_ACK.equals(msgType)){
			reqIDField = new TradeRequestID();
		} else if (MsgType.TRADING_SESSION_STATUS_REQUEST.equals(msgType) || MsgType.TRADING_SESSION_STATUS.equals(msgType)){
			reqIDField = new TradSesReqID();
		} else if (MsgType.USER_REQUEST.equals(msgType) || MsgType.USER_RESPONSE.equals(msgType)){
			reqIDField = new UserRequestID();
		}
		return reqIDField;
	}

	public static void mergeMarketDataMessages(Message marketDataSnapshotFullRefresh, Message marketDataIncrementalRefresh, FIXMessageFactory factory){
		if (!isMarketDataSnapshotFullRefresh(marketDataSnapshotFullRefresh)){
			throw new IllegalArgumentException(Messages.FIX_MD_MERGE_INVALID_INCOMING_SNAPSHOT.getText());
		}
		if (!isMarketDataIncrementalRefresh(marketDataIncrementalRefresh)){
			throw new IllegalArgumentException(Messages.FIX_MD_MERGE_INVALID_INCOMING_INCREMENTAL.getText());
		}

		HashMap<Character, Group> consolidatingSet = new HashMap<Character, Group>();

		addGroupsToMap(marketDataSnapshotFullRefresh, factory, consolidatingSet);
		addGroupsToMap(marketDataIncrementalRefresh, factory, consolidatingSet);
		marketDataSnapshotFullRefresh.removeGroup(NoMDEntries.FIELD);
		for (Group aGroup : consolidatingSet.values()) {
			Group group = factory.createGroup(MsgType.MARKET_DATA_SNAPSHOT_FULL_REFRESH, NoMDEntries.FIELD);
			group.setFields(aGroup);
			marketDataSnapshotFullRefresh.addGroup(group);
		}
	}

	private static void addGroupsToMap(Message marketDataMessage, FIXMessageFactory factory, HashMap<Character, Group> consolidatingSet){
		try {
			int noMDEntries = marketDataMessage.getInt(NoMDEntries.FIELD);
			String msgType = marketDataMessage.getHeader().getString(MsgType.FIELD);
			for (int i = 1; i <= noMDEntries; i++){
				Group group = factory.createGroup(msgType, NoMDEntries.FIELD);
				try {
					marketDataMessage.getGroup(i, group);
					consolidatingSet.put(group.getChar(MDEntryType.FIELD), group);
				} catch (FieldNotFound e) {
					//just continue
				}
			}
		} catch (FieldNotFound e) {
			// ignore
		}

	}
	/**
	 * package name for the quickfix fields
	 */
	private static final String QUICKFIX_PACKAGE = "quickfix.field."; //$NON-NLS-1$
	/**
	 * Create a <code>FIX</code> field of the given name.
	 *
	 * @param inFieldName a <code>String</code> value to containing the name of the field to create
	 * @return a <code>Field&lt;?&gt;</code> value
	 * @throws NullPointerException if <code>inFieldName</code> is null
	 * @throws CoreException if the value cannot be converted to a field
	 */
	public static Field<?> getQuickFixFieldFromName(String inFieldName)
	    throws CoreException
	{
	    if(inFieldName == null) {
	        throw new NullPointerException();
	    }
	    Throwable error = null;
	    try {
	        // try the easy case first: the given field might be one of the pre-packaged quickfix fields
	        return (Field<?>)Class.forName(QUICKFIX_PACKAGE + inFieldName).newInstance();
	    } catch(ClassNotFoundException cnfe) {
	        // if this exception is thrown, that means that the field does not correspond to a pre-packaged quickfix field
	        // see if we can create a custom field - this means that the field name has to be parseable as an int
	        try {
	            int fieldInt = Integer.parseInt(inFieldName);
	            return new CustomField<Integer>(fieldInt,
	                                            null);
	        } catch (Throwable t) {
	            error = t;
	        }
	    } catch (Throwable t) {
	        error = t;
	    }
	    // bah, can't create a field with the stuff we're given
	    throw new CoreException(error,
	                            new I18NBoundMessage1P(CANNOT_CREATE_FIX_FIELD,
	                                                   inFieldName));
	}
    /**
     * Converts the supplied fix message to a pretty string that can be logged.
     * The returned string prints the human readable representations of field
     * tags and enumeration type field values to make them easily readable.
     * <p>
     * This method ignores any repeating groups in the message.
     *
     * @param msg the FIX Message.
     * @param inDict the data dictionary for the message
     *
     * @return the string form of the message.
     */
    public static String toPrettyString(Message msg, FIXDataDictionary inDict) {
        HashMap<String, String> fields = fieldsToMap(msg, inDict);
        fields.put("HEADER", fieldsToMap(msg.getHeader(),  //$NON-NLS-1$
                inDict).toString());
        fields.put("TRAILER", fieldsToMap(msg.getTrailer(),  //$NON-NLS-1$
                inDict).toString());
        return fields.toString();
    }

    /**
     * Converts the supplied FieldMap to a map with human readable field
     * names (based on the supplied dictionary) as keys and field values
     * as string values.
     * <p>
     * This method ignores any repeating groups present in the fieldMap.
     *
     * @param inMap The FIX FieldMap.
     * @param inDict The FIX data dictionary.
     *
     * @return The map containing supplied fieldMap's keys & values.
     */
    private static HashMap<String, String> fieldsToMap(FieldMap inMap,
                                                      FIXDataDictionary inDict) {
        HashMap<String, String> fields = new HashMap<String, String>();
        Iterator<Field<?>> iterator = inMap.iterator();
        while(iterator.hasNext()) {
            Field<?> f = iterator.next();
            String value;
            if(f instanceof StringField) {
                value = ((StringField)f).getValue();
                if (inDict != null) {
                    String humanValue = inDict.getHumanFieldValue(f.getTag(),value);
                    if(humanValue != null) {
                        value = new StringBuilder().append(humanValue).
                                append("(").append(value).  //$NON-NLS-1$
                                append(")").toString();  //$NON-NLS-1$
                    }
                }
            } else {
                value = String.valueOf(f.getObject());
            }
            String name = null;
            if (inDict != null) {
                name = inDict.getHumanFieldName(f.getTag());
            }
            if(name == null) {
                name = String.valueOf(f.getTag());
            } else {
                name = new StringBuilder().append(name).
                        append("(").append(f.getTag()).  //$NON-NLS-1$
                        append(")").toString();  //$NON-NLS-1$
            }
            fields.put(name,value);
        }
        return fields;
    }
}
