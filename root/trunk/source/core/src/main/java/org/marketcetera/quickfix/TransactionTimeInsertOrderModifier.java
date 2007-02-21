package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;

import java.util.Set;
import java.util.HashSet;

import quickfix.*;
import quickfix.field.TransactTime;
import quickfix.field.MsgType;

/**
 * Inserts the {@link TransactTime} field into an order if it's not currently present
 *
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public class TransactionTimeInsertOrderModifier implements OrderModifier
{
    // list of messages that need transactTime
    private static Set<String> applicableMsgTypes = new HashSet<String>();
    public TransactionTimeInsertOrderModifier() {
    }

    public boolean modifyOrder(Message order) throws MarketceteraException {
        /** Only put the field in if it's not present */
        try {
            // test for presence
            order.getString(TransactTime.FIELD);
            return false;
        } catch (FieldNotFound ex){
            order.setField(new TransactTime());
            return true;
        }

    }

    protected boolean needsTransactTime(Message inMsg)
    {
        String theType = null;
        try {
            theType = inMsg.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound ex) {
            return false;
        }

        return applicableMsgTypes.contains(theType);
    }

    /**
     * Indication of Interest (6)
    * Advertisement (7)
    * Execution Report (8)
    * Order Cancel Reject (9)
    * New Order - Single (D)
    * Order Cancel Request (F)
    * Order Cancel/Replace Request (G)
    * Allocation (J)
    * List Cancel Request (K)
    * List Execute (L)
    * List Status (N)
    * Allocation ACK (P)
    * Quote (S)
    * Settlement Instructions (T)
    * Security Status (f)
    * New Order - List (E)
    * Quote Request (R)
    * Mass Quote (i)
    */

    protected static String[] applicableMsgTypeCodes = new String[] {
        MsgType.INDICATION_OF_INTEREST,
                MsgType.ADVERTISEMENT,
                MsgType.EXECUTION_REPORT,
                MsgType.ORDER_CANCEL_REJECT,
                MsgType.ORDER_SINGLE,
                MsgType.ORDER_CANCEL_REQUEST,
                MsgType.ORDER_CANCEL_REPLACE_REQUEST,
                MsgType.ALLOCATION_INSTRUCTION,
                MsgType.LIST_CANCEL_REQUEST,
                MsgType.LIST_EXECUTE,
                MsgType.LIST_STATUS,
                MsgType.ALLOCATION_INSTRUCTION_ACK,
                MsgType.QUOTE,
                MsgType.SETTLEMENT_INSTRUCTIONS,
                MsgType.SECURITY_STATUS,
                MsgType.ORDER_LIST,
                MsgType.QUOTE_REQUEST,
                MsgType.MASS_QUOTE
    };


    static {
        for (String applicableMsgTypeCode : applicableMsgTypeCodes) {
            applicableMsgTypes.add(applicableMsgTypeCode);
        }
    }
}
