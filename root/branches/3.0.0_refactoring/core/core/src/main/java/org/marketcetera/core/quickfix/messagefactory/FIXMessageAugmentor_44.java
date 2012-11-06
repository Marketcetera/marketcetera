package org.marketcetera.core.quickfix.messagefactory;

import java.util.Arrays;

import quickfix.field.MsgType;

/**
 * @version $Id: FIXMessageAugmentor_44.java 16063 2012-01-31 18:21:55Z colin $
 */

public class FIXMessageAugmentor_44 extends FIXMessageAugmentor_43 {
    private  static String[] TT_APPLICABLE_MESSAGE_CODES = new String[] {
            MsgType.QUOTE_RESPONSE,
            MsgType.CONFIRMATION,
            MsgType.POSITION_MAINTENANCE_REQUEST,
            MsgType.POSITION_MAINTENANCE_REPORT,
            MsgType.REQUEST_FOR_POSITIONS,
            MsgType.TRADE_CAPTURE_REPORT_ACK,
            MsgType.ALLOCATION_REPORT,
            MsgType.ALLOCATION_REPORT_ACK,
            MsgType.CONFIRMATION_ACK,
            MsgType.SETTLEMENT_INSTRUCTION_REQUEST,
            MsgType.COLLATERAL_REQUEST,
            MsgType.COLLATERAL_ASSIGNMENT,
            MsgType.COLLATERAL_RESPONSE,
            MsgType.CONFIRMATION_REQUEST
    };


    public FIXMessageAugmentor_44() {
        applicableMsgTypes.addAll(Arrays.asList(TT_APPLICABLE_MESSAGE_CODES));
    }

}
