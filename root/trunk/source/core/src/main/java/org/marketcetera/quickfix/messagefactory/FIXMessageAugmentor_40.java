package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_40 extends NoOpFIXMessageAugmentor {

    // list of messages that need transactTime
    protected Set<String> applicableMsgTypes = new HashSet<String>();

    public FIXMessageAugmentor_40() {
        applicableMsgTypes.addAll(Arrays.asList(TT_APPLICABLE_MESSAGE_CODES));
    }

    private static String[] TT_APPLICABLE_MESSAGE_CODES = new String[] {
                MsgType.ADVERTISEMENT,
                MsgType.EXECUTION_REPORT,
                MsgType.ALLOCATION_INSTRUCTION_ACK,
                MsgType.ALLOCATION_INSTRUCTION
    };

    /**
     * MarketOnClose order handling: FIX up to 4.2 doesn't have {@link TimeInForce#AT_THE_CLOSE}
     * so we need to check if the order is a market order and of {@link OrdType#MARKET}
     * and modify it appropriately to set the OrdType to be {@link OrdType#MARKET_ON_CLOSE}
     * @param inMessage
     * @return
     */
    public Message newOrderSingleAugment(Message inMessage) {
        inMessage = super.newOrderSingleAugment(inMessage);
        try {
            if((OrdType.MARKET == inMessage.getChar(OrdType.FIELD)) &&
               (TimeInForce.AT_THE_CLOSE == inMessage.getChar(TimeInForce.FIELD))) {
                inMessage.setField(new OrdType(OrdType.MARKET_ON_CLOSE));
                inMessage.setField(new TimeInForce(TimeInForce.DAY));
            }
        } catch (FieldNotFound fieldNotFound) {
            return inMessage;
        }
        return inMessage;
    }

    public Message executionReportAugment(Message inMessage) throws FieldNotFound {
        inMessage.setField(new ExecTransType(ExecTransType.NEW));
        return inMessage;
    }

    /** If the {@link CxlType} field is defined then set it */
    public Message cancelRequestAugment(Message inMessage) {
        super.cancelRequestAugment(inMessage);
        if(FIXDataDictionaryManager.getCurrentFIXDataDictionary().getDictionary().isField(CxlType.FIELD)) {
            inMessage.setField(new CxlType(CxlType.FULL_REMAINING_QUANTITY));
        }
        return inMessage;
    }

    public boolean needsTransactTime(Message inMsg) {
        String theType = null;
        try {
            theType = inMsg.getHeader().getString(MsgType.FIELD);
        } catch (FieldNotFound ex) {
            return false;
        }

        return applicableMsgTypes.contains(theType);
    }

    protected Set<String> getApplicableMsgTypes() {
        return applicableMsgTypes;
    }
}
