package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ExecTransType;
import quickfix.field.MsgType;
import quickfix.field.OrdType;
import quickfix.field.TimeInForce;

import java.util.Arrays;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageAugmentor_43 extends FIXMessageAugmentor_42 {
    private  static String[] TT_APPLICABLE_MESSAGE_CODES = new String[] {
            MsgType.NEW_ORDER_MULTILEG,
            MsgType.MULTILEG_ORDER_CANCEL_REPLACE,
            MsgType.TRADE_CAPTURE_REPORT_REQUEST,
            MsgType.TRADE_CAPTURE_REPORT,
            MsgType.QUOTE_REQUEST_REJECT,
            MsgType.QUOTE_STATUS_REPORT,
            MsgType.MASS_QUOTE_ACKNOWLEDGEMENT,
            MsgType.ORDER_MASS_CANCEL_REQUEST,
            MsgType.ORDER_MASS_CANCEL_REPORT,
            MsgType.NEW_ORDER_CROSS,
            MsgType.CROSS_ORDER_CANCEL_REPLACE_REQUEST,
            MsgType.CROSS_ORDER_CANCEL_REQUEST
    };


    public FIXMessageAugmentor_43() {
        applicableMsgTypes.addAll(Arrays.asList(TT_APPLICABLE_MESSAGE_CODES));
    }


    public Message newOrderSingleAugment(Message inMessage) {
        inMessage = super.newOrderSingleAugment(inMessage);
        return handleOnCloseBehaviour(inMessage);
    }

    public Message cancelReplaceRequestAugment(Message inMessage) {
        inMessage = super.newOrderSingleAugment(inMessage);
        return handleOnCloseBehaviour(inMessage);
    }

    /** As of FIX43, we no longer use {@link quickfix.field.ExecTransType} so override this method to not do anything */
    public Message executionReportAugment(Message inMessage) throws FieldNotFound {
        super.executionReportAugment(inMessage);
        // remove the ExecTransType field
        inMessage.removeField(ExecTransType.FIELD);
        // exec types no longer have 'fill' and 'partial fill' 
        if(inMessage.isSetField(quickfix.field.ExecType.FIELD)) {
            quickfix.field.ExecType execType = new quickfix.field.ExecType();
            inMessage.getField(execType);
            switch(execType.getValue()) {
                case quickfix.field.ExecType.FILL:
                case quickfix.field.ExecType.PARTIAL_FILL:
                    execType.setValue(quickfix.field.ExecType.TRADE);
                    inMessage.setField(execType);
                    break;
                default:
                    // do nothing
            }
        }
        if(inMessage.isSetField(quickfix.field.ExecTransType.FIELD)) {
            quickfix.field.ExecTransType execTransType = new quickfix.field.ExecTransType();
            inMessage.getField(execTransType);
            inMessage.removeField(quickfix.field.ExecTransType.FIELD);
            switch(execTransType.getValue()) {
                case quickfix.field.ExecTransType.CANCEL:
                    inMessage.setField(new quickfix.field.ExecType(quickfix.field.ExecType.TRADE_CANCEL));
                    break;
                case quickfix.field.ExecTransType.CORRECT:
                    inMessage.setField(new quickfix.field.ExecType(quickfix.field.ExecType.TRADE_CORRECT));
                    break;
                case quickfix.field.ExecTransType.NEW:
                    // do nothing
                    break;
                case quickfix.field.ExecTransType.STATUS:
                    inMessage.setField(new quickfix.field.ExecType(quickfix.field.ExecType.ORDER_STATUS));
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return inMessage;
    }

    /** Undo the changes made in FIX_40 augmentor
     * Starting with FIX.4.3, the {@link OrdType#MARKET_ON_CLOSE} is deprecated
     * so we want to use {@link TimeInForce#AT_THE_CLOSE} instead
     * @param inMessage
     * @return
     */
    private Message handleOnCloseBehaviour(Message inMessage) {
        try {
            if((OrdType.MARKET_ON_CLOSE == inMessage.getChar(OrdType.FIELD)) &&
               (TimeInForce.DAY == inMessage.getChar(TimeInForce.FIELD))) {
                inMessage.setField(new OrdType(OrdType.MARKET));
                inMessage.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
            } else if((OrdType.LIMIT_ON_CLOSE == inMessage.getChar(OrdType.FIELD)) &&
               (TimeInForce.DAY == inMessage.getChar(TimeInForce.FIELD))) {
                inMessage.setField(new OrdType(OrdType.LIMIT));
                inMessage.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
            }
        } catch (FieldNotFound fieldNotFound) {
            return inMessage;
        }
        return inMessage;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.messagefactory.NoOpFIXMessageAugmentor#getFixVersion()
     */
    @Override
    protected FIXVersion getFixVersion()
    {
        return FIXVersion.FIX43;
    }
}
