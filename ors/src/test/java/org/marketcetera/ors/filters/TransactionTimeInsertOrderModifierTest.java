package org.marketcetera.ors.filters;

import junit.framework.Test;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.TransactTime;

/**
 * @author Toli Kuznets
 * @version $Id$
 */

public class TransactionTimeInsertOrderModifierTest extends FIXVersionedTestCase {
    public TransactionTimeInsertOrderModifierTest(String inName, FIXVersion version) {
        super(inName, version);
    }

    public static Test suite() {
        return new FIXVersionTestSuite(TransactionTimeInsertOrderModifierTest.class, FIXVersionTestSuite.ALL_VERSIONS);
    }

    public void testTTNotSetOnUnapplicable() throws Exception {
        TransactionTimeInsertMessageModifier mod = new TransactionTimeInsertMessageModifier();
        final Message msg = new Message();
        msg.getHeader().setField(new MsgType(MsgType.DERIVATIVE_SECURITY_LIST));

        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                msg.getString(TransactTime.FIELD);
            }
        }.run();

        mod.modifyMessage(msg, null, msgFactory.getMsgAugmentor());
        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                msg.getString(TransactTime.FIELD);
            }
        }.run();
    }

    public void testTTSet() throws Exception {
        TransactionTimeInsertMessageModifier mod = new TransactionTimeInsertMessageModifier();
        final Message msg = new Message();

        msg.getHeader().setField(new MsgType(MsgType.EXECUTION_REPORT));

        new ExpectedTestFailure(FieldNotFound.class) {
            protected void execute() throws Throwable {
                msg.getString(TransactTime.FIELD);
            }
        }.run();


        mod.modifyMessage(msg, null, msgFactory.getMsgAugmentor());

        assertNotNull("TransactTime was not set", msg.getString(TransactTime.FIELD)); //$NON-NLS-1$
    }
}
