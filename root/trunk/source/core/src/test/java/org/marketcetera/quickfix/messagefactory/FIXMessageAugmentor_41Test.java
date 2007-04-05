package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.ClassVersion;
import junit.framework.Test;
import junit.framework.TestCase;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.*;
import quickfix.fix41.NewOrderSingle;

import java.math.BigDecimal;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class FIXMessageAugmentor_41Test extends TestCase {
    public FIXMessageAugmentor_41Test(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(FIXMessageAugmentor_41Test.class);
    }

    public void testExecutionReportAugment() throws Exception {
        FIXMessageAugmentor_41 augmentor = new FIXMessageAugmentor_41();
        Message msg = augmentor.executionReportAugment(createNOS(new BigDecimal(100), new BigDecimal(30)));
        assertEquals("70", msg.getString(LeavesQty.FIELD));

        msg = augmentor.executionReportAugment(createNOS(new BigDecimal(0), new BigDecimal(0)));
        assertEquals("0", msg.getString(LeavesQty.FIELD));

        msg = augmentor.executionReportAugment(createNOS(new BigDecimal(100), new BigDecimal(100)));
        assertEquals("0", msg.getString(LeavesQty.FIELD));

        // verify ExecType is set
        assertEquals(ExecType.NEW, msg.getField(new ExecType()).getValue());

    }

    public void testExecReportAugmentor_decimalQtyAndPrice() throws Exception {
        FIXMessageAugmentor_41 augmentor = new FIXMessageAugmentor_41();
        Message msg = augmentor.executionReportAugment(createNOS(new BigDecimal("100.10"), new BigDecimal("30.50")));
        assertEquals("69.60", msg.getString(LeavesQty.FIELD));

        msg = augmentor.executionReportAugment(createNOS(new BigDecimal("300.3"), BigDecimal.ZERO));
        assertEquals("300.3", msg.getString(LeavesQty.FIELD));
    }

    public void testExecutionReportAugmentor_leavesQty_setToZeroOnRejections() throws Exception {
        FIXMessageAugmentor_41 augmentor = new FIXMessageAugmentor_41();
        Message nos = createNOS(new BigDecimal(100), new BigDecimal(30));
        nos.setField(new OrdStatus(OrdStatus.REJECTED));

        Message augmented = augmentor.executionReportAugment(nos);
        assertEquals("leavesQty should be 0 on rejection", (double) 0, augmented.getField(new LeavesQty()).getValue());

    }

    public void testCountTT_applicableTypes() throws Exception {
        assertEquals(7, new FIXMessageAugmentor_41().getApplicableMsgTypes().size());
    }

    private Message createNOS(BigDecimal initial, BigDecimal cumQty) {
        NewOrderSingle nos = new NewOrderSingle();
        nos.setField(new StringField(OrderQty.FIELD, initial.toPlainString()));
        nos.setField(new StringField(CumQty.FIELD, cumQty.toPlainString()));
        nos.setField(new OrdStatus(OrdStatus.NEW));
        return nos;
    }
}
