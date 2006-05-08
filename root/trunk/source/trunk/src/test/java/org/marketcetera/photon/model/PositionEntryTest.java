package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

import org.marketcetera.core.AccountID;
import org.marketcetera.core.InternalID;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TransactTime;

public class PositionEntryTest extends TestCase {

	
	private static final String ROOT_PORTFOLIO_NAME = "Root portfolio";
	private static Date THE_TRANSACT_TIME;
	private static Portfolio parent = new Portfolio(null, ROOT_PORTFOLIO_NAME);
	private static Date THE_DATE;
	private static String POSITION_NAME = "Testable position entry";
	private static InternalID INTERNAL_ID = new InternalID("123");
	private static char SIDE_BUY = Side.BUY;
	private static String SYMBOL = "SYMB";
	private static AccountID ACCOUNT_ID = new AccountID("asdf");
	private static InternalID CL_ORD_ID = new InternalID("CLORDID");
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		 THE_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("1974-12-24");
		 THE_TRANSACT_TIME = new SimpleDateFormat("yyyy-MM-dd").parse("2006-10-04");
	}

	private PositionEntry getTestablePositionEntry()
	{
		return new PositionEntry(parent, POSITION_NAME, INTERNAL_ID, SIDE_BUY, SYMBOL, ACCOUNT_ID, THE_DATE);
	}

	private PositionEntry getTestablePositionEntryWithMessage() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		Message aMessage = FIXMessageUtil.newExecutionReport(new InternalID("456"), CL_ORD_ID, "987", ExecTransType.STATUS,
				ExecType.PARTIAL_FILL, OrdStatus.PARTIALLY_FILLED, Side.BUY, new BigDecimal(1000), new BigDecimal("12.3"), new BigDecimal(500), 
				new BigDecimal("12.3"), new BigDecimal(500), new BigDecimal(500), new BigDecimal("12.3"), SYMBOL);
		aMessage.setUtcTimeStamp(TransactTime.FIELD, THE_TRANSACT_TIME);
		testablePositionEntry.addIncomingMessage(aMessage);
		return testablePositionEntry;
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getName()'
	 */
	public void testGetName() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(POSITION_NAME, testablePositionEntry.getName());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getParent()'
	 */
	public void testGetParent() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(parent, testablePositionEntry.getParent());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getProgress()'
	 */
	public void testGetProgress() {

	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getInternalID()'
	 */
	public void testGetInternalID() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(INTERNAL_ID, testablePositionEntry.getInternalID());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getOrdStatus()'
	 */
	public void testGetOrdStatus() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals('\0', testablePositionEntry.getOrdStatus());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLastMessageForClOrdID(InternalID)'
	 */
	public void testGetLastMessageForClOrdID() throws FieldNotFound {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		
		Message lastMessageForClOrdID = testablePositionEntry.getLastMessageForClOrdID(CL_ORD_ID);
		assertEquals(MsgType.EXECUTION_REPORT, lastMessageForClOrdID.getHeader().getString(MsgType.FIELD));
		assertEquals(SYMBOL, lastMessageForClOrdID.getString(Symbol.FIELD));
	}


	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getSymbol()'
	 */
	public void testGetSymbol() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(SYMBOL, testablePositionEntry.getSymbol());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getSide()'
	 */
	public void testGetSide() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(SIDE_BUY, testablePositionEntry.getSide());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getTargetQuantity()'
	 */
	public void testGetTargetQuantity() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(new BigDecimal("1000"), testablePositionEntry.getTargetQuantity());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLeavesQty()'
	 */
	public void testGetLeavesQty() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(new BigDecimal("500"), testablePositionEntry.getLeavesQty());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getCumQty()'
	 */
	public void testGetCumQty() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(new BigDecimal("500"), testablePositionEntry.getCumQty());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getAvgPrice()'
	 */
	public void testGetAvgPrice() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(12.3, testablePositionEntry.getAvgPrice().doubleValue(), .0001);
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getStartTime()'
	 */
	public void testGetStartTime() {
		PositionEntry testablePositionEntry = getTestablePositionEntry();
		assertEquals(THE_DATE, testablePositionEntry.getStartTime());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLastFillTime()'
	 */
	public void testGetLastFillTime() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(THE_TRANSACT_TIME, testablePositionEntry.getLastFillTime());

	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getAccountID()'
	 */
	public void testGetAccountID() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(ACCOUNT_ID, testablePositionEntry.getAccountID());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLastQty()'
	 */
	public void testGetLastQty() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(new BigDecimal("500"), testablePositionEntry.getLastQty());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLastPrice()'
	 */
	public void testGetLastPrice() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals(new BigDecimal("12.3"), testablePositionEntry.getLastPrice());
	}

	/*
	 * Test method for 'org.marketcetera.photon.model.PositionEntry.getLastMarket()'
	 */
	public void testGetLastMarket() {
		PositionEntry testablePositionEntry = getTestablePositionEntryWithMessage();
		assertEquals("", testablePositionEntry.getLastMarket());
	}


}
