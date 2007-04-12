package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.quickfix.FIXDataDictionaryManager;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.CxlType;
import quickfix.field.ExecTransType;
import quickfix.field.MsgType;

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
