package org.marketcetera.photon.views;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Test;

import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.photon.marketdata.OptionContractData;
import org.marketcetera.quickfix.FIXVersion;
import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

public class OptionOrderTicketModelTest extends FIXVersionedTestCase {

	public OptionOrderTicketModelTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(OptionOrderTicketModelTest.class, FIXVersion.values());
	}
	
	public void testClearOrderMessage() throws FieldNotFound {
		OptionOrderTicketModel model = new OptionOrderTicketModel(this.msgFactory);
		
		Message newOrder = this.msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.TEN, new MSymbol("FOO"), BigDecimal.ONE, TimeInForce.DAY, "123");
		model.setOrderMessage(newOrder);
		
		model.clearOrderMessage();
		
		final Message clearedMessage = model.getOrderMessage();
		assertNotSame(newOrder, clearedMessage);
		new ExpectedTestFailure(FieldNotFound.class) {
			@Override
			protected void execute() throws Throwable {
				clearedMessage.getChar(Side.FIELD);
			}
		}.run();
		new ExpectedTestFailure(FieldNotFound.class) {
			@Override
			protected void execute() throws Throwable {
				clearedMessage.getDecimal(OrderQty.FIELD);
			}
		}.run();
		new ExpectedTestFailure(FieldNotFound.class) {
			@Override
			protected void execute() throws Throwable {
				clearedMessage.getString(Symbol.FIELD);
			}
		}.run();
		new ExpectedTestFailure(FieldNotFound.class) {
			@Override
			protected void execute() throws Throwable {
				clearedMessage.getDecimal(Price.FIELD);
			}
		}.run();

		assertEquals(SecurityType.OPTION, clearedMessage.getString(SecurityType.FIELD));
	}

	public void testUpdateOptionInfo() {
		OptionOrderTicketModel model = new OptionOrderTicketModel(this.msgFactory);
		model.updateOptionInfo();

		Message newOrder = this.msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.TEN, new MSymbol("FOO"), BigDecimal.ONE, TimeInForce.DAY, "123");
		newOrder.setDecimal(StrikePrice.FIELD, new BigDecimal("25"));
		newOrder.setField(new PutOrCall(PutOrCall.PUT));
		newOrder.setField(new MaturityMonthYear("200811"));
		model.setOrderMessage(newOrder);

		List<OptionContractData> contractData;
		contractData = getContractData();

		model.addOptionContractData(contractData);

		assertTrue(model.getExpirationMonthList().size()>1);
		assertTrue(model.getExpirationYearList().size()>1);
		assertTrue(model.getStrikePriceList().size()>1);
		model.updateOptionInfo();
		
		assertEquals(1, model.getExpirationMonthList().size());
		assertEquals("NOV", model.getExpirationMonthList().get(0));
		assertEquals(1, model.getExpirationYearList().size());
		assertEquals("2008", model.getExpirationYearList().get(0));
		assertEquals(1, model.getStrikePriceList().size());
		assertEquals("25", model.getStrikePriceList().get(0));
		assertEquals("FOO+FE", model.getCurrentOptionSymbol().getValue().toString());
		
		newOrder.removeField(PutOrCall.FIELD);
		
		model.updateOptionInfo();

		assertEquals(1, model.getExpirationMonthList().size());
		assertEquals(1, model.getExpirationYearList().size());
		assertEquals(1, model.getStrikePriceList().size());
		assertEquals(null, model.getCurrentOptionSymbol().getValue());
		
	}

	private List<OptionContractData> getContractData() {
		List<OptionContractData> contractData = new LinkedList<OptionContractData>();
		OptionContractData ocd;
		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+FE"), 2008, 11, new BigDecimal(25), PutOrCall.PUT);
		contractData.add(ocd);
		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+RE"), 2008, 11, new BigDecimal(25), PutOrCall.CALL);
		contractData.add(ocd);
		
		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+FA"), 2008, 11, new BigDecimal(20), PutOrCall.PUT);
		contractData.add(ocd);
		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+RA"), 2008, 11, new BigDecimal(20), PutOrCall.CALL);
		contractData.add(ocd);

		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+FD"), 2008, 10, new BigDecimal(20), PutOrCall.PUT);
		contractData.add(ocd);
		ocd = new OptionContractData(new MSymbol("FOO"), new MSymbol("FOO+RD"), 2008, 10, new BigDecimal(20), PutOrCall.CALL);
		contractData.add(ocd);

		ocd = new OptionContractData(new MSymbol("BAR"), new MSymbol("BAR+FD"), 2009, 1, new BigDecimal(10), PutOrCall.PUT);
		contractData.add(ocd);
		ocd = new OptionContractData(new MSymbol("BAR"), new MSymbol("BAR+RD"), 2009, 1, new BigDecimal(10), PutOrCall.CALL);
		contractData.add(ocd);
		return contractData;
	}

	public void testAddOptionContractData_OptionContractData() {
		OptionOrderTicketModel model = new OptionOrderTicketModel(this.msgFactory);
		model.updateOptionInfo();

		Message newOrder = this.msgFactory.newLimitOrder("ASDF", Side.BUY, BigDecimal.TEN, new MSymbol("FOO"), BigDecimal.ONE, TimeInForce.DAY, "123");
		newOrder.setDecimal(StrikePrice.FIELD, new BigDecimal("25"));
		newOrder.setField(new PutOrCall(PutOrCall.PUT));
		newOrder.setField(new MaturityMonthYear("200811"));
		model.setOrderMessage(newOrder);

		List<OptionContractData> contractData = getContractData();

		for (OptionContractData optionContractData : contractData) {
			model.addOptionContractData(optionContractData);
		}
		model.updateOptionInfo();
		
		assertEquals(1, model.getExpirationMonthList().size());
		assertEquals("NOV", model.getExpirationMonthList().get(0));
		assertEquals(1, model.getExpirationYearList().size());
		assertEquals("2008", model.getExpirationYearList().get(0));
		assertEquals(1, model.getStrikePriceList().size());
		assertEquals("25", model.getStrikePriceList().get(0));
		assertEquals("FOO+FE", model.getCurrentOptionSymbol().getValue().toString());
	}

	public void testRemoveDataForOptionRoot() {
		OptionOrderTicketModel model = new OptionOrderTicketModel(this.msgFactory);
		model.updateOptionInfo();

		List<OptionContractData> contractData = getContractData();
		model.addOptionContractData(contractData);
		model.updateOptionInfo();
		assertEquals(3, model.getStrikePriceList().size());
		
		model.removeDataForOptionRoot("BAR");
		assertEquals(2, model.getStrikePriceList().size());
		
	}

}
