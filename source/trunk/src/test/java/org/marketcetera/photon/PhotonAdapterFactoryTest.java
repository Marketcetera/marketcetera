package org.marketcetera.photon;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.eclipse.ui.model.IWorkbenchAdapter;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.photon.model.FIXMessageHistory;
import org.marketcetera.photon.model.IncomingMessageHolder;
import org.marketcetera.photon.views.FIXFilterItem;
import org.marketcetera.photon.views.FilterGroup;
import org.marketcetera.photon.views.FilterItem;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;

public class PhotonAdapterFactoryTest extends TestCase {

	/*
	 * Test method for 'org.marketcetera.photon.PhotonAdapterFactory.getAdapterList()'
	 */
	public void testGetAdapterList() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		Class[] adapterList = fact.getAdapterList();
		assertEquals(1, adapterList.length);
		assertEquals(IWorkbenchAdapter.class, adapterList[0]);
	}
	


	public void testMessageHistoryAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		FIXMessageHistory hist = new FIXMessageHistory();
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(hist, IWorkbenchAdapter.class);
		String label = adapter.getLabel(hist);
		assertNull(label);
		Message aMessage = FIXMessageUtil.newExecutionReport(new InternalID("456"), OrderManagerTest.CL_ORD_ID, "987", ExecTransType.STATUS,
				ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(100), 
				new BigDecimal("12.3"), new BigDecimal(100), new BigDecimal(100), new BigDecimal("12.3"), OrderManagerTest.SYMBOL);
		aMessage.setUtcTimeStamp(TransactTime.FIELD, OrderManagerTest.THE_TRANSACT_TIME);
		hist.addIncomingMessage(aMessage);
		
		Object[] children = adapter.getChildren(hist);
		assertEquals(1, children.length);
		assertEquals(hist.getHistory()[0], children[0]);
		assertNull(adapter.getParent(hist));
	}
	
	public void testMessageAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		Message aMessage = FIXMessageUtil.newExecutionReport(new InternalID("456"), OrderManagerTest.CL_ORD_ID, "987", ExecTransType.STATUS,
				ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(100), 
				new BigDecimal("12.3"), new BigDecimal(100), new BigDecimal(100), new BigDecimal("12.3"), OrderManagerTest.SYMBOL);
		aMessage.setUtcTimeStamp(TransactTime.FIELD, OrderManagerTest.THE_TRANSACT_TIME);
		IncomingMessageHolder holder = new IncomingMessageHolder(aMessage);
		
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(holder, IWorkbenchAdapter.class);
		String label = adapter.getLabel(holder);
		assertEquals("Message", label);
		assertNull(adapter.getParent(holder));
		
		assertEquals(0, adapter.getChildren(holder).length);
	}
	public void testGroupAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		FilterGroup group = new FilterGroup(null, "Root");
		FilterGroup child1 = new FilterGroup(group, "Child1");
		FilterGroup child2 = new FilterGroup(group, "Child2");
		group.addChild(child1);
		group.addChild(child2);

		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(group, IWorkbenchAdapter.class);
		
		assertNull(adapter.getParent(group));
		Object[] children = adapter.getChildren(group);
		assertEquals(2, children.length);
		assertEquals(child1, children[0]);
		assertEquals(child2, children[1]);
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
	public void testFilterAdapter() {
		PhotonAdapterFactory fact = new PhotonAdapterFactory();
		FilterItem item = new FIXFilterItem<String>(Symbol.FIELD, "Q", "SYMBOL Q");
		IWorkbenchAdapter adapter = (IWorkbenchAdapter)fact.getAdapter(item, IWorkbenchAdapter.class);
		assertEquals("SYMBOL Q", adapter.getLabel(item));
		assertNull(adapter.getParent(item));
		Object[] children = adapter.getChildren(item);
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
