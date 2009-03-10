package org.marketcetera.photon;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.TradeReportsHistory;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.trade.Order;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderID;

import quickfix.field.OrdStatus;
import quickfix.field.Side;

/**
 * Verify the functions in PhotonController
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class PhotonControllerTest extends TestCase {
    private static FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();
    private MyPhotonController photonController;
    private TradeReportsHistory fixMessageHistory;

    public PhotonControllerTest(String inName) {
        super(inName);
    }

    protected void setUp() throws Exception {
        super.setUp();
        photonController = new MyPhotonController();
        fixMessageHistory = new TradeReportsHistory(msgFactory);
        photonController.setMessageHistory(fixMessageHistory);
    }

    public void testCancelAllOpenOrders() throws Exception {
        fixMessageHistory.addIncomingMessage(OrderManagerTest.createReport(msgFactory.newExecutionReport("123", "10001", "201", OrdStatus.NEW,
                Side.BUY, new BigDecimal(10), new BigDecimal(10.10), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("XYZ"), "tester")));
        fixMessageHistory.addIncomingMessage(OrderManagerTest.createReport(msgFactory.newExecutionReport("123", "10002", "201", OrdStatus.NEW,
                Side.BUY, new BigDecimal(10), new BigDecimal(10.10), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, new MSymbol("BOB"), "tester")));
        IProgressMonitor mockMonitor = mock(IProgressMonitor.class);
        photonController.cancelAllOpenOrders(mockMonitor);
        verify(mockMonitor).beginTask(Messages.PHOTON_CONTROLLER_CANCEL_ALL_ORDERS_TASK
				.getText(), 2);
        verify(mockMonitor, times(2)).worked(1);
        verify(mockMonitor).done();
        assertEquals("not enough orders canceled", 2, photonController.sentOrders.size());
        Order order1 = photonController.sentOrders.get(0);
        Order order2 = photonController.sentOrders.get(1);
        assertEquals(new OrderID("10001"), ((OrderCancel)order1).getOriginalOrderID());
        assertEquals(new OrderID("10002"), ((OrderCancel)order2).getOriginalOrderID());
    }

   
    /** Store the messages that are meant to go out */
    private class MyPhotonController extends PhotonController {
		@Override
		public void sendOrder(Order inOrder) {
			sentOrders.add(inOrder);
		}
		private final Vector<Order> sentOrders = new Vector<Order>();
        
    }
    
}
