package org.marketcetera.photon.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.OrdStatus;
import quickfix.field.Side;

public class DBFIXMessageHistoryTest extends FIXMessageHistoryTest {

	/* (non-Javadoc)
	 * @see org.marketcetera.photon.model.FIXMessageHistoryTest#getMessageHistory()
	 */
	@Override
	protected FIXMessageHistory getMessageHistory() {
		return new DBFIXMessageHistory();
	}

	public void testAveragePriceResultSet() throws Exception {
		DBFIXMessageHistory messageHistory = (DBFIXMessageHistory)getMessageHistory();
		InternalID orderID1 = new InternalID("1");
		InternalID clOrderID1 = new InternalID("2");
		String execID = "3";
		char execTransType = ExecTransType.STATUS;
		char execType = ExecType.PARTIAL_FILL;
		char ordStatus = OrdStatus.PARTIALLY_FILLED;
		char side = Side.SELL_SHORT;
		BigDecimal orderQty = new BigDecimal(1000);
		BigDecimal orderPrice = new BigDecimal(789);
		BigDecimal lastQty = new BigDecimal(100);
		BigDecimal lastPrice = new BigDecimal("12.3");
		BigDecimal leavesQty = new BigDecimal(900);
		BigDecimal cumQty = new BigDecimal("100");
		BigDecimal avgPrice = new BigDecimal("12.3");
		MSymbol symbol = new MSymbol("ASDF");

		Message message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
		messageHistory.addIncomingMessage(message);
		
		orderID1 = new InternalID("3");
		clOrderID1 = new InternalID("4");
		cumQty = new BigDecimal(900);
		avgPrice = new BigDecimal("12.4");

		message = FIXMessageUtil.newExecutionReport(orderID1, clOrderID1, execID, execTransType, execType, ordStatus, side, orderQty, orderPrice, lastQty, lastPrice, leavesQty, cumQty, avgPrice, symbol);
		messageHistory.addIncomingMessage(message);
		
		ResultSet averagePriceResultSet = messageHistory.getAveragePriceResultSet();

		ResultSetMetaData metaData = averagePriceResultSet.getMetaData();
		assertTrue(averagePriceResultSet.next());
		
		BigDecimal returnedAvgPrice = ((BigDecimal)averagePriceResultSet.getObject("AvgPx"));
		String string = returnedAvgPrice.toPlainString();
		assertEquals( ((12.3*100)+(12.4*900))/1000, returnedAvgPrice.doubleValue(), .0001);
		assertTrue( new BigDecimal("1000").compareTo((BigDecimal) averagePriceResultSet.getObject("CumQty")) == 0);
		
	}


	}

