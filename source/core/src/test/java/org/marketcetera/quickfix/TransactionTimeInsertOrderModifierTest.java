package org.marketcetera.quickfix;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.MarketceteraTestSuite;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.TransactTime;

/**
 * @author Toli Kuznets
 * @version $Id
 */
@ClassVersion("Id")
public class TransactionTimeInsertOrderModifierTest extends TestCase {
    public TransactionTimeInsertOrderModifierTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(TransactionTimeInsertOrderModifierTest.class);
    }

    public void testApplicability() throws Exception {
        TransactionTimeInsertOrderModifier mod = new TransactionTimeInsertOrderModifier();
        Message msg = new Message();

        String[] notApplicable = new String[] { MsgType.DERIVATIVE_SECURITY_LIST,
                                                MsgType.BID_REQUEST};

        for (String code : notApplicable) {
            msg = new Message();
            msg.getHeader().setField(new MsgType(code));
            assertFalse(mod.needsTransactTime(msg));
        }

        for (String code : TransactionTimeInsertOrderModifier.applicableMsgTypeCodes) {
            msg = new Message();
            msg.getHeader().setField(new MsgType(code));

            assertTrue(mod.needsTransactTime(msg));
        }
    }

    public void testTTSet() throws Exception {
        TransactionTimeInsertOrderModifier mod = new TransactionTimeInsertOrderModifier();
        final Message msg = new Message();

        msg.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REJECT));

        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                msg.getString(TransactTime.FIELD);
            }
        }.run();


        mod.modifyOrder(msg);

        assertNotNull(msg.getString(TransactTime.FIELD));
    }
}
