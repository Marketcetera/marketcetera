package org.marketcetera.photon.parser;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

import java.math.BigDecimal;
import java.util.regex.Matcher;

import jfun.parsec.ParserException;
import junit.framework.Test;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.photon.Messages;
import org.marketcetera.photon.commands.CancelCommand;
import org.marketcetera.photon.commands.MessageCommand;
import org.marketcetera.photon.commands.SendOrderToOrderManagerCommand;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.BeginSeqNo;
import quickfix.field.EndSeqNo;
import quickfix.field.MaturityMonthYear;
import quickfix.field.MsgType;
import quickfix.field.OpenClose;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.PutOrCall;
import quickfix.field.SecurityType;
import quickfix.field.Side;
import quickfix.field.StrikePrice;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

@ClassVersion("$Id$")
public class ParserTest extends FIXVersionedTestCase {

	public ParserTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite() {
		return new FIXVersionTestSuite(ParserTest.class,
				new FIXVersion[] { FIXVersion.FIX_SYSTEM });
	}

	public void testNewOrder() throws NoMoreIDsException, FieldNotFound {
		CommandParser aParser = new CommandParser();
		aParser.setMessageFactory(msgFactory);
		aParser.setDataDictionary(fixDD.getDictionary());
		String order;
		order = "B 100 IBM 1.";
		MessageCommand command = aParser.parseNewOrder(order);
		Message result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("1"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "SS 1234 IBM 1.8";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.SELL_SHORT, new BigDecimal("1234"), "IBM",
				new BigDecimal("1.8"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "ss 1234 IBM 1.8 gs day";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.SELL_SHORT, new BigDecimal("1234"), "IBM",
				new BigDecimal("1.8"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "S 0 IBM 0.0";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.SELL, new BigDecimal("0"), "IBM",
				new BigDecimal("0.0"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs\tDAY";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs OPG";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.AT_THE_OPENING, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs CLO";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.AT_THE_CLOSE, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs FOK";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.FILL_OR_KILL, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs IOC";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.IMMEDIATE_OR_CANCEL, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs GTC";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.GOOD_TILL_CANCEL, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "SS 100 IBM 94.8 gs DAY 123.45";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.SELL_SHORT, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.DAY, "123.45");
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		order = "B 100 IBM 94.8 gs DAY AAA;A/a-A";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.DAY, "AAA;A/a-A");
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		// Verify EG-216
		order = "S 0 IBM 0";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.SELL, new BigDecimal("0"), "IBM",
				new BigDecimal("0"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "A 100 IBM 94.8 gs DAY AAA?A/a-A";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS A IBM 94.8 gs DAY AAA?A/a-A";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS 100 IBM XXX gs DAY AAA?A/a-A";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS 100 IBM 123.45 gs ASDF AAA?A/a-A";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS 100 IBM ";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "s 15 toli mkt gs bob errtok";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				MessageCommand aCommand = innerParser.parseNewOrder(innerOrder);
				System.out.println("" + aCommand);
			}
		}).run();
	}

	public void testNewOptionOrder() throws NoMoreIDsException, FieldNotFound {
		CommandParser aParser = new CommandParser();
		aParser.setMessageFactory(FIXVersion.FIX_SYSTEM.getMessageFactory());
		aParser.setDataDictionary(fixDD.getDictionary());
		String order;
		order = "B 100 IBM 08OCT25C 1.";
		MessageCommand command = aParser.parseNewOrder(order);
		Message result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("1"), TimeInForce.DAY, null,
				new BigDecimal("25"), "200810", PutOrCall.CALL);

		order = "SS 1234 IBM 2008JAN12.5C 1.8";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.SELL_SHORT, new BigDecimal("1234"),
				"IBM", new BigDecimal("1.8"), TimeInForce.DAY, null,
				new BigDecimal("12.5"), "200801", PutOrCall.CALL);

		order = "ss 1234 IBM JAN25C 1.8 gs day";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		// this will break when the year changes...
		verifyNewOptionOrder(result, Side.SELL_SHORT, new BigDecimal("1234"),
				"IBM", new BigDecimal("1.8"), TimeInForce.DAY, null,
				new BigDecimal("25"), "201001", PutOrCall.CALL);

		order = "S 0 IBM 09MAR1.0P 0.0";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.SELL, new BigDecimal("0"), "IBM",
				new BigDecimal("0.0"), TimeInForce.DAY, null, new BigDecimal(
						"1.0"), "200903", PutOrCall.PUT);

		order = "B 100 IBM 08APR7P 94.8 gs\tDAY";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.DAY, null, new BigDecimal(
						"7"), "200804", PutOrCall.PUT);

		order = "B 100 IBM 13MAY120P 94.8 gs OPG";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.AT_THE_OPENING, null,
				new BigDecimal("120"), "201305", PutOrCall.PUT);

		order = "B 100 IBM 10JUN95P 94.8 gs CLO";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.AT_THE_CLOSE, null,
				new BigDecimal("95"), "201006", PutOrCall.PUT);

		order = "B 100 IBM 10JUL100P 94.8 gs FOK";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.FILL_OR_KILL, null,
				new BigDecimal("100"), "201007", PutOrCall.PUT);

		order = "B 100 IBM 10AUG100C 94.8 gs IOC";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.IMMEDIATE_OR_CANCEL, null,
				new BigDecimal("100"), "201008", PutOrCall.CALL);

		order = "B 100 IBM 08SEP22.5C 94.8 gs GTC";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("94.8"), TimeInForce.GOOD_TILL_CANCEL, null,
				new BigDecimal("22.5"), "200809", PutOrCall.CALL);

		order = "SS 100 IBM 09OCT7.5C 94.8 gs DAY 123.45";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOptionOrder(result, Side.SELL_SHORT, new BigDecimal("100"),
				"IBM", new BigDecimal("94.8"), TimeInForce.DAY, "123.45",
				new BigDecimal("7.5"), "200910", PutOrCall.CALL);

		order = "B 100 IBM+RE 94.8 gs DAY AAA;A/a-A";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM+RE",
				new BigDecimal("94.8"), TimeInForce.DAY, "AAA;A/a-A");
		assertEquals(SecurityType.OPTION, result.getString(SecurityType.FIELD));

		order = "B 100 IBM+RE.C 94.8 gs DAY AAA;A/a-A";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM+RE.C",
				new BigDecimal("94.8"), TimeInForce.DAY, "AAA;A/a-A");
		assertEquals(SecurityType.OPTION, result.getString(SecurityType.FIELD));

		order = "B 100 IBM.+ 94.8 gs DAY AAA;A/a-A";
		command = aParser.parseNewOrder(order);
		result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM.+",
				new BigDecimal("94.8"), TimeInForce.DAY, "AAA;A/a-A");
		assertEquals(SecurityType.COMMON_STOCK, result
				.getString(SecurityType.FIELD));

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "B 100 IBM 2007ASD45C 94.8 gs DAY";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "S IBM 2007DEC44 94.8 gs DAY";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "B 100 IBM 2007DEC44T gs DAY";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS 100 IBM 2007DEC25C 123.45 gs ASDF AAA?A/a-A";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

		(new ExpectedTestFailure(ParserException.class) {
			protected void execute() throws Throwable {
				String innerOrder = "SS 100 IBM 2007DEC25C ";
				CommandParser innerParser = new CommandParser();
				innerParser.setMessageFactory(msgFactory);
				innerParser.setDataDictionary(fixDD.getDictionary());
				innerParser.parseNewOrder(innerOrder);
			}
		}).run();

	}

	void verifyNewOrder(Message message, char side, BigDecimal quantity,
			String symbol, BigDecimal price, char timeInForce, String account)
			throws FieldNotFound {
		assertEquals(side, message.getChar(Side.FIELD));
		assertEquals(quantity, message.getDecimal(OrderQty.FIELD));
		assertEquals(symbol, message.getString(Symbol.FIELD));
		assertEquals(0, price.compareTo(message.getDecimal(Price.FIELD)));
		assertEquals(timeInForce, message.getChar(TimeInForce.FIELD));
		try {
			assertEquals(account, message.getString(Account.FIELD));
		} catch (FieldNotFound ex) {
			assertNull(account);
		}
	}

	void verifyNewOptionOrder(Message message, char side, BigDecimal quantity,
			String symbol, BigDecimal price, char timeInForce, String account,
			BigDecimal strike, String expirationMonthYear, int putOrCall)
			throws FieldNotFound {
		verifyNewOrder(message, side, quantity, symbol, price, timeInForce,
				account);
		assertEquals(strike, message.getDecimal(StrikePrice.FIELD));
		assertEquals(expirationMonthYear, message
				.getString(MaturityMonthYear.FIELD));
		assertEquals(putOrCall, message.getInt(PutOrCall.FIELD));
		assertEquals(message.getString(SecurityType.FIELD), SecurityType.OPTION);
		assertFalse(message.isSetField(OpenClose.FIELD));
	}

	public void testResendRequest() throws FieldNotFound {
		CommandParser aParser = new CommandParser();
		aParser.setMessageFactory(FIXVersion.FIX_SYSTEM.getMessageFactory());
		aParser.setDataDictionary(fixDD.getDictionary());
		String commandText;
		MessageCommand command;
		Message rr;

		commandText = "RR 0 0";
		command = (MessageCommand) aParser.parseCommand(commandText);
		rr = command.getMessage();
		assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(
				MsgType.FIELD));
		assertEquals(0, rr.getInt(BeginSeqNo.FIELD));
		assertEquals(0, rr.getInt(EndSeqNo.FIELD));

		commandText = "RR 0 9";
		command = (MessageCommand) aParser.parseCommand(commandText);
		rr = command.getMessage();
		assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(
				MsgType.FIELD));
		assertEquals(0, rr.getInt(BeginSeqNo.FIELD));
		assertEquals(9, rr.getInt(EndSeqNo.FIELD));

		commandText = "RR 9 0";
		command = (MessageCommand) aParser.parseCommand(commandText);
		rr = command.getMessage();
		assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(
				MsgType.FIELD));
		assertEquals(9, rr.getInt(BeginSeqNo.FIELD));
		assertEquals(0, rr.getInt(EndSeqNo.FIELD));

		commandText = "RR 24 38";
		command = (MessageCommand) aParser.parseCommand(commandText);
		rr = command.getMessage();
		assertEquals(MsgType.RESEND_REQUEST, rr.getHeader().getString(
				MsgType.FIELD));
		assertEquals(24, rr.getInt(BeginSeqNo.FIELD));
		assertEquals(38, rr.getInt(EndSeqNo.FIELD));
	}

	public void testFullOrder() throws FieldNotFound {
		CommandParser aParser = new CommandParser();
		aParser.setMessageFactory(msgFactory);
		aParser.setDataDictionary(fixDD.getDictionary());

		String order;
		order = "O B 100 IBM 1.";
		MessageCommand command = (MessageCommand) aParser.parseCommand(order);
		Message result = command.getMessage();
		verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM",
				new BigDecimal("1"), TimeInForce.DAY, null);
		assertEquals(result.getString(SecurityType.FIELD),
				SecurityType.COMMON_STOCK);

	}

	public void testCancelOrder() throws NoMoreIDsException, FieldNotFound {
		CommandParser aParser = new CommandParser();
		aParser.setMessageFactory(msgFactory);
		aParser.setDataDictionary(fixDD.getDictionary());

		String command;

		command = "C 12345";
		CancelCommand comm = (CancelCommand) aParser.parseCommand(command);
		assertEquals("12345", comm.getID());
	}

	public void testDecimalQuantity() throws Exception {
		String innerOrder = "SS 100.1 IBM 123.4";
		CommandParser innerParser = new CommandParser();
		innerParser.setMessageFactory(msgFactory);
		innerParser.setDataDictionary(fixDD.getDictionary());
		innerParser.parseNewOrder(innerOrder);
	}

	public void testOptionExpirationPattern() throws Exception {
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("2007OCT25C");
			assertTrue(matcher.matches());
			assertEquals(4, matcher.groupCount());
			assertEquals("2007", matcher.group(1));
			assertEquals("OCT", matcher.group(2));
			assertEquals("25", matcher.group(3));
			assertEquals("C", matcher.group(4));
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("OCT25C");
			assertTrue(matcher.matches());
			assertEquals(4, matcher.groupCount());
			assertEquals(null, matcher.group(1));
			assertEquals("OCT", matcher.group(2));
			assertEquals("25", matcher.group(3));
			assertEquals("C", matcher.group(4));
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("08OCT25.5C");
			assertTrue(matcher.matches());
			assertEquals(4, matcher.groupCount());
			assertEquals("08", matcher.group(1));
			assertEquals("OCT", matcher.group(2));
			assertEquals("25.5", matcher.group(3));
			assertEquals("C", matcher.group(4));
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("08OCT.5C");
			assertTrue(matcher.matches());
			assertEquals(4, matcher.groupCount());
			assertEquals("08", matcher.group(1));
			assertEquals("OCT", matcher.group(2));
			assertEquals(".5", matcher.group(3));
			assertEquals("C", matcher.group(4));
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("OCT.5C");
			assertTrue(matcher.matches());
			assertEquals(4, matcher.groupCount());
			assertEquals(null, matcher.group(1));
			assertEquals("OCT", matcher.group(2));
			assertEquals(".5", matcher.group(3));
			assertEquals("C", matcher.group(4));
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("008OCT25C");
			assertFalse(matcher.matches());
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("OCT25.5.5C");
			assertFalse(matcher.matches());
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("2007OCT25");
			assertFalse(matcher.matches());
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("200725C");
			assertFalse(matcher.matches());
		}
		{
			Matcher matcher = CommandParser.optionExpirationPattern
					.matcher("2007OCTC");
			assertFalse(matcher.matches());
		}

	}

	public void testBroker() throws Exception {
    	final CommandParser aParser = new CommandParser();
    	aParser.setMessageFactory(FIXVersion.FIX_SYSTEM.getMessageFactory());
    	aParser.setDataDictionary(fixDD.getDictionary());
    	String order;  
    	order = "B 100 IBM 1 gs fok myaccount";
    	SendOrderToOrderManagerCommand command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	Message result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.FILL_OR_KILL, "myaccount");
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertEquals("gs", command.getBroker());
    	
    	order = "B 100 IBM 1 gs";
    	command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.DAY, null);
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertEquals("gs", command.getBroker());
    	
    	order = "B 100 IBM 1 gs fok";
    	command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.FILL_OR_KILL, null);
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertEquals("gs", command.getBroker());
    	
    	order = "B 100 IBM 1";
    	command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.DAY, null);
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertNull(command.getBroker());
    	
    	order = "B 100 IBM 1 auto";
    	command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.DAY, null);
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertNull(command.getBroker());
    	
    	IBrokerIdValidator mockValidator = mock(IBrokerIdValidator.class);
    	aParser.setBrokerValidator(mockValidator);
    	
    	stub(mockValidator.isValid("ABC")).toReturn(false);
    	
    	new ExpectedFailure<ParserException>(
				Messages.COMMAND_PARSER_INVALID_BROKER_ID.getText("ABC",
						Messages.COMMAND_PARSER_AUTO_SELECT_BROKER_KEYWORD.getText()),
				false) {
			protected void run() throws Exception {
				aParser.parseNewOrder("B 100 IBM 1 ABC");
			}
		};
		
		stub(mockValidator.isValid("ABC")).toReturn(true);
		order = "B 100 IBM 1 ABC";
    	command = (SendOrderToOrderManagerCommand) aParser.parseNewOrder(order);
    	result = command.getMessage();
    	verifyNewOrder(result, Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.DAY, null);
    	assertEquals(result.getString(SecurityType.FIELD), SecurityType.COMMON_STOCK);
    	assertEquals("ABC", command.getBroker());
    }
}
