package org.marketcetera.photon;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.marketcetera.core.AccountID;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.quickfix.FIXMessageFactory;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.Message;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.TransactTime;

public class PhotonAdapterFactoryTest extends TestCase {

    private FIXMessageFactory msgFactory = FIXVersion.FIX_SYSTEM.getMessageFactory();

    /*
	 * Test method for 'org.marketcetera.photon.PhotonAdapterFactory.getAdapterList()'
	 */
	public void testGetAdapterList() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		Class<?>[] adapterList = fact.getAdapterList();
		assertEquals(1, adapterList.length);
		assertEquals(IWorkbenchAdapter.class, adapterList[0]);
	}
	
	
	public void testMessageAdapter() throws Exception {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		Message aMessage = msgFactory.newExecutionReport("456", OrderManagerTest.CL_ORD_ID, "987", 
				OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(100), 
				new BigDecimal("12.3"), new BigDecimal(100), new BigDecimal("12.3"), OrderManagerTest.SYMBOL, null);
		aMessage.setUtcTimeStamp(TransactTime.FIELD, OrderManagerTest.THE_TRANSACT_TIME);
		ReportHolder holder = new ReportHolder(OrderManagerTest.createReport(aMessage));
		
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(holder, IWorkbenchAdapter.class);
		String label = adapter.getLabel(holder);
		assertEquals("Message", label);
		assertNull(adapter.getParent(holder));
		
		assertEquals(0, adapter.getChildren(holder).length);
	}
	public void testAccountAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		AccountID anAccount = new AccountID("MyAccount","Nickname");
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(anAccount, IWorkbenchAdapter.class);
		
		assertEquals("MyAccount (Nickname)", adapter.getLabel(anAccount));
		assertNull(adapter.getParent(anAccount));
		Object[] children = adapter.getChildren(anAccount);
		assertEquals(0, children.length);
	}
	public void testSymbolAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		MSymbol symbol = new MSymbol("Q");
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(symbol, IWorkbenchAdapter.class);
		assertEquals("Q", adapter.getLabel(symbol));
		assertNull(adapter.getParent(symbol));
		Object[] children = adapter.getChildren(symbol);
		assertEquals(0, children.length);
	}


}
