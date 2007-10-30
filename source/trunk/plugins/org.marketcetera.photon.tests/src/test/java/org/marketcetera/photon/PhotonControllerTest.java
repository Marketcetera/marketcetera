package org.marketcetera.photon;

import java.math.BigDecimal;
import java.util.Vector;

import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.core.FIXMessageHistory;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.quickfix.FIXMessageUtilTest;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.TimeInForce;

/**
 * Verify the functions in PhotonController
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class PhotonControllerTest extends TestCase {
    private static FIXMessageFactory msgFactory = FIXVersion.FIX42.getMessageFactory();
    private MyPhotonController photonController;
    private FIXMessageHistory fixMessageHistory;

    public PhotonControllerTest(String inName) {
        super(inName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        photonController = new MyPhotonController();
        fixMessageHistory = new FIXMessageHistory(msgFactory);
        photonController.setMessageHistory(fixMessageHistory);
        photonController.setIDFactory(new InMemoryIDFactory(1000));
        photonController.setMessageFactory(msgFactory);
    }

    public void testCancelAllOpenOrders() throws Exception {
        fixMessageHistory.addIncomingMessage(msgFactory.newExecutionReport("123", "10001", "201", OrdStatus.NEW,
                Side.BUY, new BigDecimal(10), new BigDecimal(10.10), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("XYZ"), "tester"));
        fixMessageHistory.addIncomingMessage(msgFactory.newExecutionReport("123", "10002", "201", OrdStatus.NEW,
                Side.BUY, new BigDecimal(10), new BigDecimal(10.10), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("BOB"), "tester"));
        photonController.cancelAllOpenOrders();
        assertEquals("not enough orders canceled", 2, photonController.sentMessages.size());
        assertEquals(MsgType.ORDER_CANCEL_REQUEST, photonController.sentMessages.get(0).getHeader().getString(MsgType.FIELD));
        assertEquals(MsgType.ORDER_CANCEL_REQUEST, photonController.sentMessages.get(1).getHeader().getString(MsgType.FIELD));
        assertEquals("10001", photonController.sentMessages.get(0).getString(OrigClOrdID.FIELD));
        assertEquals("10002", photonController.sentMessages.get(1).getString(OrigClOrdID.FIELD));
    }

    public void testNewOrderAugmentorApplied() throws Exception {
    	Message msg = FIXMessageUtilTest.createMarketNOS("IBM", 100, Side.BUY, msgFactory);
    	msg.setField(new TimeInForce(TimeInForce.AT_THE_CLOSE));
    	photonController.handleInternalMessage(msg);
    	
    	assertEquals(1, photonController.sentMessages.size());
    	assertEquals(OrdType.MARKET_ON_CLOSE, photonController.sentMessages.get(0).getChar(OrdType.FIELD));
    	assertEquals(TimeInForce.DAY, photonController.sentMessages.get(0).getChar(TimeInForce.FIELD));
    }
    
    /** Store the messages that are meant to go out */
    private class MyPhotonController extends PhotonController {
        private Vector<Message> sentMessages = new Vector<Message>();
        public void convertAndSend(Message fixMessage) {
            sentMessages.add(fixMessage);
        }
    }
}
