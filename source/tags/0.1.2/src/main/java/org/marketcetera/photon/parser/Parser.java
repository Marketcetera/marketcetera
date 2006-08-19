package org.marketcetera.photon.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.MSymbol;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.MsgType;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.TimeInForce;


/**
 * The command parser for Photon.  Takes a string as input and parses out 
 * commands for the rest of te system, returning it as a Parser.Command 
 * object. 
 * 
 * Parser implements an LL(1) parser, with polymorphic
 * tokens.  Every method (except for init()) implements one production in the
 * grammar.  Currently the only top-level production is command().
 * 
 * The parser infers some information about a token from its context.
 * For example, the *Token class hierarchy allows the parser to treat a FloatToken
 * as a NumberToken, or a simple string-based Token depending on the context.
 * 
 * Lexical constants are stored in the member enums, CommandImage, PriceImage,
 * TimeInForceImage and CancelReplaceTypeImage.  
 * 
 * @author gmiller
 *
 */
@ClassVersion("$Id$")
public class Parser {

	
    /**
     * The member lexer to tokenize the input.
     */
    Lexer lexer;

    
    /**
     * CommandImage contains the constant token values for the command string.
     * For example in the command to place an order to buy 100 shares of 
     * IBM for 22.05, "B 100 IBM 22.05", the command string is the first "B".
     * The command string is always the first string in the token stream.
     * 
     * @author gmiller
     *
     */
    enum CommandImage {
        BUY("B"),
        SELL("S"),
        SELL_SHORT("SS"),
        SELL_SHORT_EXEMPT("SSE"),
        CANCEL_ALL("CA"),
        CANCEL("C"),
        CANCEL_REPLACE("CXR"),
        SET("SET"),
        UNSET("UNSET");

        public String image;

        CommandImage(String anImage) {
            this.image = anImage;
        }
    }

    /**
     * PriceImage contains only one constant "MKT", representing a market order
     * as other prices are represented as floating point numbers.
     * 
     * @author gmiller
     *
     */
    enum PriceImage {
        MKT("MKT");
        public String image;

        PriceImage(String anImage) {
            this.image = anImage;
        }
    }

    
    /**
     * TimeInForceImage contains the constants used to specify a time-in-force
     * limitation on an order.
     * 
     * @author gmiller
     *
     */
    enum TimeInForceImage {
        DAY("DAY"), // day
        GTC("GTC"), // good-til-cancel
        FOK("FOK"), // fill-or-kill
        CLO("CLO"), // at-the-close
        OPG("OPG"), // at-the-open
        IOC("IOC"); // immediate-or-cancel

        public String image;

        TimeInForceImage(String anImage) {
            this.image = anImage;
        }
    }

    /**
     * CancelReplaceTypeImage represents a discriminator for cancel replace orders.
     * Currently the command line interface of Photon only supports modifying either
     * the quantity or price of an order, not both simultaneously.
     * 
     * @author gmiller
     *
     */
    enum CancelReplaceTypeImage {
        QUANTITY("Q"),
        PRICE("P");

        public String image;

        CancelReplaceTypeImage(String anImage) {
            this.image = anImage;
        }
    }


    protected String currentCommand;

    protected int parsePosition;

    protected IDFactory mIDFactory;


    /**
	 * Create a new Parser object. You must call {@link #init(IDFactory)} and
	 * {@link #setInput(String)} before you can use the parser
	 */
    public Parser() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Initializes this parser, handing it an IDFactory for generating new 
     * unique ID's
     * 
     * @param factory
     */
    public void init(IDFactory factory) {
        mIDFactory = factory;
    }


    /**
     * Set the input for this parser, that is the text that will be parsed
     * 
     * @param theInput the text to be parsed
     */
    public void setInput(String theInput) {
        lexer = new Lexer();
        lexer.setInput(theInput);
    }

    /**
     * The top-level production for the parser.  It attempts to parse out a command
     * from the input set by {@link #setInput(String)}, and return a corresponding
     * ParsedCommand.
     * 
     * @return the parsed command represented as a ParsedCommand
     * @throws ParserException if the input string does not contain a valid command
     * @throws NoMoreIDsException if the member IDFactory has run out of ID's
     */
    public ParsedCommand command() throws ParserException, NoMoreIDsException {
        currentCommand = "Unknown command";
        parsePosition = 0;
        Logger log = Application.getMainConsoleLogger();
        log.debug(lexer.mInput);

        Token firstToken = lexer.peek();
        String image = firstToken.getImage();
        if (CommandImage.BUY.image.equalsIgnoreCase(image)
            || CommandImage.SELL.image.equalsIgnoreCase(image)
            || CommandImage.SELL_SHORT.image.equalsIgnoreCase(image)
            || CommandImage.SELL_SHORT_EXEMPT.image.equalsIgnoreCase(image)) {
            return newOrderCommand();
        }
//        if (CommandImage.SET.image.equalsIgnoreCase(image) ||
//            CommandImage.UNSET.image.equalsIgnoreCase(image))
//        {
//            return newSetCommand();
//        }
        if (CommandImage.CANCEL_ALL.image.equalsIgnoreCase(image)) {
            return cancelAllCommand();
        }
        if (CommandImage.CANCEL.image.equalsIgnoreCase(image)) {
            return cancelCommand();
        }
        if (CommandImage.CANCEL_REPLACE.image.equalsIgnoreCase(image)) {
            return cancelReplaceCommand();
        }
        throwException("Command expected.", CommandImage.values());
        return null;
    }

    
    /**
     * Attempts to parse out a "new order" command from the input string.
     * For example the command string "S 100 IBM MKT" would result in a 
     * ParsedCommand to sell 100 shares of IBM at the market.
     * 
     * @return a new ParsedCommand representing the command in the input string
     * @throws ParserException if the input string does not contain a valid new order command
     * @throws NoMoreIDsException if the member IDFactory has run out of ID's
     */
    public ParsedCommand newOrderCommand() throws ParserException, NoMoreIDsException {
        currentCommand = "New Order Command";

        char side = 0;
        int quantity = 0;
        String symbolString = "";
        String priceString = "";
        boolean isMarket = false;
        String account = "";
        char timeInForce = '\0';

        Token token = consumeStringToken("Expecting command.", CommandImage
                .values());
        String image = token.getImage();
        if (CommandImage.BUY.image.equalsIgnoreCase(image)) {
            side = Side.BUY;
        } else if (CommandImage.SELL.image.equalsIgnoreCase(image)) {
            side = Side.SELL;
        } else if (CommandImage.SELL_SHORT.image.equalsIgnoreCase(image)) {
            side = Side.SELL_SHORT;
        } else if (CommandImage.SELL_SHORT_EXEMPT.image.equalsIgnoreCase(image)) {
            side = Side.SELL_SHORT_EXEMPT;
        } else {
            throw new ParserException("Expecting command", token.getPosition(),
                                      CommandImage.values());
        }

        IntToken intToken = consumeIntToken("Expecting quantity.");
        quantity = intToken.intValue();

        token = consumeStringToken("Expecting symbol", null);
        symbolString = token.getImage();

        token = consumeToken("Expecting price.", PriceImage.values());
        if (token instanceof StringToken) {
            StringToken mktToken = (StringToken) token;
            if (!PriceImage.MKT.image.equalsIgnoreCase(mktToken.getImage())) {
                throwException("Expecting 'MKT' or price.", PriceImage.values());
            } else {
                isMarket = true;
            }
        } else if (token instanceof NumberToken)
        {
            NumberToken priceToken = (NumberToken)token;
            priceString = priceToken.image;
        }

        if (lexer.peek() != null) {
            token = consumeStringToken("Expecting time in force.",
                                       TimeInForceImage.values());
            image = token.getImage();
            if (TimeInForceImage.DAY.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.DAY;
            } else if (TimeInForceImage.GTC.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.GOOD_TILL_CANCEL;
            } else if (TimeInForceImage.FOK.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.FILL_OR_KILL;
            } else if (TimeInForceImage.CLO.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.AT_THE_CLOSE;
            } else if (TimeInForceImage.OPG.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.AT_THE_OPENING;
            } else if (TimeInForceImage.IOC.image.equalsIgnoreCase(image)) {
                timeInForce = TimeInForce.IMMEDIATE_OR_CANCEL;
            } else {
                throwException("Expecting time-in-force.", TimeInForceImage
                        .values());
            }
            if (lexer.peek() != null) {
                token = consumeToken("Expecting account", null);
                account = token.getImage();
            }
        }
        if (timeInForce == '\0')
            timeInForce = TimeInForce.DAY;
        Message aMessage;
        InternalID orderID = new InternalID(mIDFactory.getNext());
        AccountID accountID = (account == null || "".equals(account) ? null
                               : new AccountID(account));

        if (isMarket) {
            aMessage = FIXMessageUtil.newMarketOrder(orderID, side, new BigDecimal(quantity),
                                                      new MSymbol(symbolString), timeInForce, accountID);
        } else {
        	aMessage = null;
        	try {
            aMessage = FIXMessageUtil.newLimitOrder(orderID, side, new BigDecimal(quantity),
            		new MSymbol(symbolString), new BigDecimal(priceString), timeInForce, accountID);
        	} catch (Throwable th) {
        		th.printStackTrace();
        	}
        }
        return new ParsedCommand(MsgType.ORDER_SINGLE, aMessage);
    }

    /**
     * Attempts to parse out a "cancel order" command from the input string.
     * For example the command string "C 12345" would result in a 
     * ParsedCommand to cancel order id 12345.
     * 
     * @return a new ParsedCommand representing the command in the input string
     * @throws ParserException if the input string does not contain a valid cancel order command
     * @throws NoMoreIDsException if the member IDFactory has run out of ID's
     */
    public ParsedCommand cancelCommand() throws ParserException, NoMoreIDsException
    {
        Collection<InternalID> cancelCollection;
        consumeToken("Expected cancel command.", null);
        cancelCollection = orderIDList();
        List<Message> messageList = new ArrayList<Message>(cancelCollection.size());
        for (InternalID id : cancelCollection){
            InternalID orderID = new InternalID(mIDFactory.getNext());
            Message message = new quickfix.fix42.Message();
            message.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));

            message.setField(new OrigClOrdID(id.toString()));
            message.setField(new ClOrdID(orderID.toString()));

            messageList.add(message);
        }
        return new ParsedCommand(MsgType.ORDER_CANCEL_REQUEST, messageList);
    }

    /**
     * Attempts to parse out a "cancel all order" command from the input string.
     * For example the command string "CA" would result in a 
     * ParsedCommand to cancel all open orders
     * 
     * @return a parsed command representing a cancel all
     * @throws ParserException
     */
    @SuppressWarnings("unchecked")
    public ParsedCommand cancelAllCommand() throws ParserException
    {
        consumeToken("Expected cancel all command.", null);
        Message message = new quickfix.fix42.Message();
        message.getHeader().setField(new MsgType(MsgType.ORDER_CANCEL_REQUEST));


        List aList = new ArrayList();
        aList.add(message);
        return new ParsedCommand(MsgType.ORDER_CANCEL_REQUEST, aList);
    }


    /**
     * Attempts to parse out a cancel/replace command from the input string.
     * There are two types of cancel/replace commands, one to adjust quantity
     * and another to adjust price.  For example, "CXR 789 P 12.45" would change
     * the limit price of order number 789 to 12.45.  And the input string 
     * "CXR 789 Q 400" would change the quantity of order 789 to be 400
     * shares.
     * 
     * @return a ParsedCommand representing the cancel/replace request
     * @throws ParserException if the input string does not contain a valid cancel/replace command
     * @throws NoMoreIDsException if the member IDFactory has run out of ID's
     */
    public ParsedCommand cancelReplaceCommand() throws ParserException, NoMoreIDsException
    {
        consumeToken("Expected cancel replace command.", null);
        IntToken orderIDToken = consumeIntToken("Expected order ID");
        StringToken replaceTypeToken = consumeStringToken("Expected P or Q", CancelReplaceTypeImage.values());

        Message cxrMessage = null;
        if (CancelReplaceTypeImage.QUANTITY.image.equalsIgnoreCase(replaceTypeToken.image))
        {
            IntToken quantityToken = consumeIntToken("Expected quantity");
            String quantityString = quantityToken.image;
            InternalID orderID = new InternalID(mIDFactory.getNext());
            cxrMessage = FIXMessageUtil.newCancelReplaceShares(orderID, new InternalID(orderIDToken.toString()), new BigDecimal(quantityString));
        } else if (CancelReplaceTypeImage.PRICE.image.equalsIgnoreCase(replaceTypeToken.image)) {
            NumberToken priceToken = consumeNumberToken("Expected price");
            String priceString = priceToken.image;
            InternalID orderID = new InternalID(mIDFactory.getNext());
            cxrMessage = FIXMessageUtil.newCancelReplacePrice(orderID, new InternalID(orderIDToken.toString()), new BigDecimal(priceString));
        }
        return new ParsedCommand(MsgType.ORDER_CANCEL_REPLACE_REQUEST, cxrMessage);
    }

    /**
     * This internal production is responsible for parsing out a list of
     * ID's, where they are represented by any string of non-whitespace
     * characters.
     * 
     * @return a List of InternalID objects representing the ID list from the input string
     * @throws ParserException
     */
    protected List<InternalID> orderIDList() throws ParserException {
        String orderIDToken = consumeIntToken("Expected order ID").image;

        if (lexer.peek() == null) {
            Vector<InternalID> aVec = new Vector<InternalID>();
            aVec.addAll(Arrays.asList(new InternalID [] { new InternalID(orderIDToken) } ));
            return aVec;
        }
        List<InternalID> results = orderIDList();
        results.add(new InternalID(orderIDToken));
        return results;
    }

    protected void throwException(String message, Enum[] completions)
            throws ParserException {
        throw new ParserException("Error parsing '" + currentCommand + "': "
                                  + message, parsePosition, completions);
    }

//    protected ParsedCommand newSetCommand() throws ParserException
//    {
//        currentCommand = "Set command";
//        Token setCommand = consumeToken("Expecting SET or UNSET command", null);
//        assert(CommandImage.SET.image.equalsIgnoreCase(setCommand.image) ||
//               CommandImage.UNSET.image.equalsIgnoreCase(setCommand.image));
//        Token variableName = consumeToken("Expecting variable name", null);
//        String variableNameString = variableName.image;
//
//        String [] results;
//        String command;
//        if (CommandImage.SET.image.equalsIgnoreCase(setCommand.image))
//        {
//            StringBuilder builder = new StringBuilder();
//            Token aToken = consumeToken("Expecting value", null);
//
//            builder.append(aToken.image);
//
//            while ((aToken = lexer.getNextToken())!=null)
//            {
//                builder.append(" ");
//                builder.append(aToken.image);
//            }
//            results = new String [] {variableNameString, builder.toString()};
//            command = CommandImage.SET.image;
//        } else {
//            results = new String [] {variableNameString};
//            command = CommandImage.UNSET.image;
//        }
//        return new ParsedCommand(command, Arrays.asList(results));
//    }


    /**
     * This helper method attempts to read the next token out of the Lexer,
     * optionally with the specified competions.  If there is no next token
     * an exception is thrown with the specified errorMessage.
     * 
     * @param errorMessage the message to throw if no token is available.
     * @param completions the possible values of the next token or null if it is a free-form input
     * @return the next token in the stream
     * @throws ParserException if no token is available
     */
    protected Token consumeToken(String errorMessage, Enum[] completions)
            throws ParserException {
        Token aToken = lexer.getNextToken();
        if (aToken == null) {
            throwException(errorMessage, completions);
        } else {
            parsePosition = aToken.getPosition();
        }
        return aToken;
    }

    /**
     * This helper method attempts to read a string token out of the Lexer.
     * If no token is available or if the next token is not of the type StringToken,
     * an exception is thrown with the specified error message. Optionally the caller
     * can specify a set of possible values (completions) for the next token, or null
     * if the input is free-form.
     * 
     * @param errorMessage the message to throw if no token is available, or the token is of the wrong type
     * @param completions the possible values of the next token or null if it is a free-form input
     * @return the next token in the stream
     * @throws ParserException if no token is available, or the token is of the wrong type
     */
    protected StringToken consumeStringToken(String errorMessage,
                                                   Enum[] completions) throws ParserException {
        try {
            StringToken aToken = (StringToken) consumeToken(
                    errorMessage, completions);
            return aToken;
        } catch (ClassCastException cce) {
            throwException(errorMessage, completions);
        }
        return null;
    }

    /**
     * This helper method attempts to read a string token out of the Lexer.
     * If no token is available or if the next token is not of the type NumberToken,
     * an exception is thrown with the specified error message. 
     * 
     * @param errorMessage the message to throw if no token is available, or the token is of the wrong type
     * @return the next token in the stream
     * @throws ParserException if no token is available, or the token is of the wrong type
     */
    protected NumberToken consumeNumberToken(String errorMessage)
			throws ParserException {
		try {
			NumberToken aToken = (NumberToken) consumeToken(errorMessage,
					null);
			aToken.doubleValue();
			return aToken;
		} catch (ClassCastException cce) {
			throwException(errorMessage, null);
		} catch (NumberFormatException nfe) {
			throwException(errorMessage, null);
		}
		return null;
	}

    /**
     * This helper method attempts to read a string token out of the Lexer.
     * If no token is available or if the next token is not of the type IntToken,
     * an exception is thrown with the specified error message. 
     * 
     * @param errorMessage the message to throw if no token is available, or the token is of the wrong type
     * @return the next token in the stream
     * @throws ParserException if no token is available, or the token is of the wrong type
     */
    protected IntToken consumeIntToken(String errorMessage)
            throws ParserException {
        try {
            IntToken aToken = (IntToken) consumeToken(errorMessage,
                                                                  null);
            aToken.intValue();
            return aToken;
        } catch (ClassCastException cce) {
            throwException(errorMessage, null);
        } catch (NumberFormatException nfe) {
            throwException(errorMessage, null);
        }
        return null;
    }

    /**
     * This helper method attempts to read a string token out of the Lexer.
     * If no token is available or if the next token is not of the type FloatToken,
     * an exception is thrown with the specified error message. 
     * 
     * @param errorMessage the message to throw if no token is available, or the token is of the wrong type
     * @return the next token in the stream
     * @throws ParserException if no token is available, or the token is of the wrong type
     */
    protected FloatToken consumeFloatToken(String errorMessage)
            throws ParserException {
        try {
            FloatToken aToken = (FloatToken) consumeToken(
                    errorMessage, null);
            aToken.doubleValue();
            return aToken;
        } catch (ClassCastException cce) {
            throwException(errorMessage, null);
        } catch (NumberFormatException nfe) {
            throwException(errorMessage, null);
        }
        return null;
    }


}
