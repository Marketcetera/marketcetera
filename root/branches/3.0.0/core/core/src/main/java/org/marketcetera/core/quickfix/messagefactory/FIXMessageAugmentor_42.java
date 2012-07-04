package org.marketcetera.core.quickfix.messagefactory;

import java.util.Arrays;

import org.marketcetera.core.attributes.ClassVersion;

import quickfix.field.MsgType;

/**
 * @author toli
 * @version $Id: FIXMessageAugmentor_42.java 16063 2012-01-31 18:21:55Z colin $
 */

@ClassVersion("$Id: FIXMessageAugmentor_42.java 16063 2012-01-31 18:21:55Z colin $")
public class FIXMessageAugmentor_42 extends FIXMessageAugmentor_41 {
    private  static String[] TT_APPLICABLE_MESSAGE_CODES = new String[] {
                MsgType.ORDER_CANCEL_REJECT,
                MsgType.ORDER_SINGLE,
                MsgType.ORDER_CANCEL_REQUEST,
                MsgType.ORDER_CANCEL_REPLACE_REQUEST,
                MsgType.LIST_CANCEL_REQUEST,
                MsgType.LIST_EXECUTE,
                MsgType.LIST_STATUS,
                MsgType.SECURITY_STATUS,
                MsgType.ORDER_LIST,
                MsgType.QUOTE_REQUEST,
                MsgType.MASS_QUOTE
    };


    public FIXMessageAugmentor_42() {
        applicableMsgTypes.addAll(Arrays.asList(TT_APPLICABLE_MESSAGE_CODES));
    }
}
