package org.marketcetera.photon.parser;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ExpectedTestFailure;
import org.marketcetera.core.InMemoryIDFactory;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.parser.Parser.Command;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.Account;
import quickfix.field.MsgType;
import quickfix.field.OrderQty;
import quickfix.field.OrigClOrdID;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TimeInForce;

@ClassVersion("$Id$")
public class ParserTest extends TestCase {

    public ParserTest(String name)
    {
        super(name);
    }
    
    public void testNewOrder() throws ParserException, NoMoreIDsException, FieldNotFound {
    	Parser aParser = new Parser();
    	aParser.init(new InMemoryIDFactory(10));
    	String order;  
    	order = "B 100 IBM 1";
    	aParser.setInput(order);
    	Command command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("1"), TimeInForce.DAY, null);

    	order = "SS 1234 IBM 1.8";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.SELL_SHORT, new BigDecimal("1234"), "IBM", new BigDecimal("1.8"), TimeInForce.DAY, null);

    	order = "SSE 999 IBM .7";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.SELL_SHORT_EXEMPT, new BigDecimal("999"), "IBM", new BigDecimal(".7"), TimeInForce.DAY, null);

    	order = "S 0 IBM 0.0";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.SELL, new BigDecimal("0"), "IBM", new BigDecimal("0.0"), TimeInForce.DAY, null);

    	order = "B 100 IBM 94.8\tDAY";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.DAY, null);

    	order = "B 100 IBM 94.8 OPG";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.AT_THE_OPENING, null);

    	order = "B 100 IBM 94.8 CLO";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.AT_THE_CLOSE, null);

    	order = "B 100 IBM 94.8 FOK";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.FILL_OR_KILL, null);

    	order = "B 100 IBM 94.8 IOC";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.IMMEDIATE_OR_CANCEL, null);

    	order = "B 100 IBM 94.8 GTC";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.GOOD_TILL_CANCEL, null);

    	order = "SS 100 IBM 94.8 DAY 123.45";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.SELL_SHORT, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.DAY, "123.45");
    	
    	order = "B 100 IBM 94.8 DAY AAA?A/a-A";
    	aParser.setInput(order);
    	command = aParser.command();
    	assertEquals(MsgType.ORDER_SINGLE, command.mCommandType);
    	verifyNewOrder((Message)(command.mResults.get(0)), Side.BUY, new BigDecimal("100"), "IBM", new BigDecimal("94.8"), TimeInForce.DAY, "AAA?A/a-A");

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "A 100 IBM 94.8 DAY AAA?A/a-A";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "SS A IBM 94.8 DAY AAA?A/a-A";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "SS 100 IBM XXX DAY AAA?A/a-A";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "SS 100 IBM 123.45 ASDF AAA?A/a-A";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "SS 100 IBM ";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerOrder = "SS 100.0 IBM 123.45";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerOrder);
            	innerParser.command();
            }
        }).run();

    }
    
    void verifyNewOrder(Message message, char side, BigDecimal quantity,
    		String symbol, BigDecimal price, char timeInForce, String account) throws FieldNotFound
    {
    	assertEquals(side, message.getChar(Side.FIELD));
    	assertEquals(quantity, new BigDecimal(message.getString(OrderQty.FIELD)));
    	assertEquals(symbol, message.getString(Symbol.FIELD));
    	assertEquals(price, new BigDecimal(message.getString(Price.FIELD)));
    	assertEquals(timeInForce, message.getChar(TimeInForce.FIELD));
    	try {
    		assertEquals(account, message.getString(Account.FIELD));
    	} catch (FieldNotFound ex) {
    		assertNull(account);
    	}
    }

    public void testCancelOrder() throws ParserException, NoMoreIDsException, FieldNotFound{
    	Parser aParser = new Parser();
    	aParser.init(new InMemoryIDFactory(10));

    	String command;

    	command = "C 12345";
    	aParser.setInput(command);
    	Message aMessage = (Message)aParser.command().mResults.get(0);
    	assertEquals(MsgType.ORDER_CANCEL_REQUEST, aMessage.getHeader().getString(MsgType.FIELD));
    	assertEquals("12345", aMessage.getString(OrigClOrdID.FIELD));
    
    	command = "C 12345 12345";
    	aParser.setInput(command);
    	for (Object loopMessage : aParser.command().mResults){
	    	assertEquals(MsgType.ORDER_CANCEL_REQUEST, ((Message)loopMessage).getHeader().getString(MsgType.FIELD));
	    	assertEquals("12345", ((Message)loopMessage).getString(OrigClOrdID.FIELD));
    	}

    	(new ExpectedTestFailure(ParserException.class) {
            protected void execute() throws Throwable
            {
            	String innerCommand = "C 12345 ASDF";
            	Parser innerParser = new Parser();
            	innerParser.init(new InMemoryIDFactory(10));
            	innerParser.setInput(innerCommand);
            	innerParser.command();
           }
        }).run();
}
    
    public void testCancelAll() throws ParserException, NoMoreIDsException, FieldNotFound
    {
    	Parser aParser = new Parser();
    	aParser.init(new InMemoryIDFactory(10));

    	String command;

    	command = "CA";
    	aParser.setInput(command);
    	Message aMessage = (Message)aParser.command().mResults.get(0);
    	assertEquals(MsgType.ORDER_CANCEL_REQUEST, aMessage.getHeader().getString(MsgType.FIELD));
    }
   

   
}

