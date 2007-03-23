package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;
import quickfix.FieldNotFound;
import quickfix.field.ExecTransType;
import quickfix.field.MsgType;

import java.util.Arrays;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
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


    /** As of FIX43, we no longer use {@link quickfix.field.ExecTransType} so override this method to not do anything */
    public Message executionReportAugment(Message inMessage) throws FieldNotFound {
        super.executionReportAugment(inMessage);
        // remove the ExecTransType field
        inMessage.removeField(ExecTransType.FIELD);
        return inMessage;
    }
}
