package org.marketcetera.photon.marketdata;

import java.math.BigDecimal;
import java.text.ParseException;

import junit.framework.TestCase;

import org.marketcetera.trade.MSymbol;

import quickfix.FieldNotFound;
import quickfix.field.MaturityMonthYear;
import quickfix.field.PutOrCall;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.fix44.Message;

public class OptionContractDataTest extends TestCase {

	private OptionContractData dataFull;

	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dataFull = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2007, 7, BigDecimal.TEN, PutOrCall.CALL);
	}

	public void testGetPutOrCall() {
		assertEquals(PutOrCall.CALL, dataFull.getPutOrCall());
	}

	public void testGetExpirationMonth() {
		assertEquals((Integer)7, dataFull.getExpirationMonth());
	}

	public void testGetExpirationYear() {
		assertEquals((Integer)2007, dataFull.getExpirationYear());
	}

	public void testGetOptionSymbol() {
		assertEquals(new MSymbol("IBM+RE"), dataFull.getOptionSymbol());
	}

	public void testGetOptionRoot() {
		assertEquals("IBM", dataFull.getOptionRoot());
	}

	public void testGetStrikePrice() {
		assertEquals(BigDecimal.TEN, dataFull.getStrikePrice());
	}

	public void testGetUnderlyingSymbol() {
		assertEquals(new MSymbol("IBM"), dataFull.getUnderlyingSymbol());
	}

	public void testEqualsObject() {
		OptionContractData equalObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2007, 7, BigDecimal.TEN, PutOrCall.CALL);
		assertTrue(equalObject.equals(dataFull));
		OptionContractData notEqualObject = new OptionContractData(new MSymbol("IBR"), new MSymbol("IBM+RE"), 2007, 7, BigDecimal.TEN, PutOrCall.CALL);
		assertFalse(notEqualObject.equals(dataFull));
		notEqualObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBR+RE"), 2007, 7, BigDecimal.TEN, PutOrCall.CALL);
		assertFalse(notEqualObject.equals(dataFull));
		notEqualObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2008, 7, BigDecimal.TEN, PutOrCall.CALL);
		assertFalse(notEqualObject.equals(dataFull));
		notEqualObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2007, 9, BigDecimal.TEN, PutOrCall.CALL);
		assertFalse(notEqualObject.equals(dataFull));
		notEqualObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2007, 7, BigDecimal.ONE, PutOrCall.CALL);
		assertFalse(notEqualObject.equals(dataFull));
		notEqualObject = new OptionContractData(new MSymbol("IBM"), new MSymbol("IBM+RE"), 2007, 7, BigDecimal.TEN, PutOrCall.PUT);
		assertFalse(notEqualObject.equals(dataFull));
	}

	public void testFromFieldMap() throws FieldNotFound, ParseException {
		Message optionMessage = new Message();
		optionMessage.setField(new StrikePrice(10));
		optionMessage.setField(new Symbol("IBM+RE"));
		optionMessage.setField(new MaturityMonthYear("200707"));
		optionMessage.setField(new PutOrCall(PutOrCall.CALL));
		assertTrue(dataFull.equals(OptionContractData.fromFieldMap(new MSymbol("IBM"), optionMessage)));
	}
}
