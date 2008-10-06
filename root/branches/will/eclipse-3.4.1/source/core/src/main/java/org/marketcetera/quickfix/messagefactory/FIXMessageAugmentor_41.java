package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Message;
import quickfix.StringField;
import quickfix.FieldNotFound;
import quickfix.field.*;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class FIXMessageAugmentor_41 extends FIXMessageAugmentor_40 {

    private static String[] TT_APPLICABLE_MESSAGE_CODES = new String[] {
                 MsgType.INDICATION_OF_INTEREST,
                 MsgType.QUOTE,
                 MsgType.SETTLEMENT_INSTRUCTIONS
     };

    public FIXMessageAugmentor_41() {
        applicableMsgTypes.addAll(Arrays.asList(TT_APPLICABLE_MESSAGE_CODES));
    }

    /** Starting with FIX41 we now need to calculate {@link quickfix.field.LeavesQty}
     * which is basically just initial - {@link quickfix.field.CumQty}
     */
    public Message executionReportAugment(Message inMessage) throws FieldNotFound {
        super.executionReportAugment(inMessage);

        // don't set LeavesQty for rejections or Cancels
        char ordStatus = inMessage.getField(new OrdStatus()).getValue();
        switch(ordStatus) {
            case OrdStatus.REJECTED:
            case OrdStatus.CALCULATED:
            case OrdStatus.CANCELED:
            case OrdStatus.DONE_FOR_DAY:
            case OrdStatus.EXPIRED:
                // set leavesQty to be 0
                if (!inMessage.isSetField(LeavesQty.FIELD)) {
                    inMessage.setField(new LeavesQty(0));
                }
                break;
            default:
                // calculate the LeavesQty = IniitialQty - CumQty
                BigDecimal initial = inMessage.getDecimal(OrderQty.FIELD);
                BigDecimal cumQty = inMessage.getDecimal(CumQty.FIELD);
                inMessage.setField(new LeavesQty(initial.subtract(cumQty)));
                break;
        }

        // set the execType to be same as OrdStatus
        inMessage.setField(new ExecType(ordStatus));
        return inMessage;
    }
}
