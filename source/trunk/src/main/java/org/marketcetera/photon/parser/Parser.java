package org.marketcetera.photon.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.marketcetera.core.AccountID;
import org.marketcetera.core.BigDecimalUtils;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.IDFactory;
import org.marketcetera.core.InternalID;
import org.marketcetera.core.NoMoreIDsException;
import org.marketcetera.photon.Application;
import org.marketcetera.photon.parser.Token.NumberToken;
import org.marketcetera.quickfix.FIXMessageUtil;

import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.TimeInForce;


@ClassVersion("$Id$")
public class Parser {

    Lexer lexer;

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

    enum PriceImage {
        MKT("MKT");
        public String image;

        PriceImage(String anImage) {
            this.image = anImage;
        }
    }

    enum TimeInForceImage {
        DAY("DAY"),
        GTC("GTC"),
        FOK("FOK"),
        CLO("CLO"),
        OPG("OPG"),
        IOC("IOC");

        public String image;

        TimeInForceImage(String anImage) {
            this.image = anImage;
        }
    }

    enum CancelReplaceTypeImage {
        SHARES("S"),
        PRICE("P");

        public String image;

        CancelReplaceTypeImage(String anImage) {
            this.image = anImage;
        }
    }


    public class Command {
        public Command() {}
        public Command( String commandType, List results) { mCommandType = commandType; mResults = results; }
        @SuppressWarnings("unchecked")
        public Command( String commandType, Object aResult) { mCommandType = commandType; mResults = new ArrayList(); mResults.add(aResult); }
        public String mCommandType;
        public List mResults;
    }

    protected String currentCommand;

    protected int parsePosition;

    protected IDFactory mIDFactory;


    public void init(IDFactory factory) {
        mIDFactory = factory;
    }

    public Parser() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void setInput(String theInput) {
        lexer = new Lexer();
        lexer.setInput(theInput);
    }

    public Command command() throws ParserException, NoMoreIDsException {
        currentCommand = "Unknown command";
        parsePosition = 0;
        Logger log = Application.getDebugConsoleLogger();
        log.debug(lexer.mInput);

        Token firstToken = lexer.peek();
        String image = firstToken.getImage();
        if (CommandImage.BUY.image.equalsIgnoreCase(image)
            || CommandImage.SELL.image.equalsIgnoreCase(image)
            || CommandImage.SELL_SHORT.image.equalsIgnoreCase(image)
            || CommandImage.SELL_SHORT_EXEMPT.image.equalsIgnoreCase(image)) {
            return newOrderCommand();
        }
        if (CommandImage.SET.image.equalsIgnoreCase(image) ||
            CommandImage.UNSET.image.equalsIgnoreCase(image))
        {
            return newSetCommand();
        }
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

    public Command newOrderCommand() throws ParserException, NoMoreIDsException {
        currentCommand = "New Order Command";

        char side = 0;
        int quantity = 0;
        String symbol = "";
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

        Token.IntToken intToken = consumeIntToken("Expecting quantity.");
        quantity = intToken.intValue();

        token = consumeStringToken("Expecting symbol", null);
        symbol = token.getImage();

        token = consumeToken("Expecting price.", PriceImage.values());
        if (token instanceof Token.StringToken) {
            Token.StringToken mktToken = (Token.StringToken) token;
            if (!PriceImage.MKT.image.equalsIgnoreCase(mktToken.getImage())) {
                throwException("Expecting 'MKT' or price.", PriceImage.values());
            } else {
                isMarket = true;
            }
        } else if (token instanceof NumberToken)
        {
            Token.NumberToken priceToken = (Token.NumberToken)token;
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
                                                      symbol, timeInForce, accountID);
        } else {
        	aMessage = null;
        	try {
            aMessage = FIXMessageUtil.newLimitOrder(orderID, side, new BigDecimal(quantity),
                                                     symbol, new BigDecimal(priceString), timeInForce, accountID);
        	} catch (Throwable th) {
        		th.printStackTrace();
        	}
        }
        return new Command(MsgType.ORDER_SINGLE, aMessage);
    }

    public Command cancelCommand() throws ParserException, NoMoreIDsException
    {
        Collection<InternalID> cancelCollection;
        consumeToken("Expected cancel command.", null);
        cancelCollection = orderIDList();
        List<Message> messageList = new ArrayList<Message>(cancelCollection.size());
        for (InternalID id : cancelCollection){
            InternalID orderID = new InternalID(mIDFactory.getNext());
            Message message = FIXMessageUtil.newCancel(orderID, id, (char)0, BigDecimal.ZERO, "", "");
            messageList.add(message);
        }
        return new Command(MsgType.ORDER_CANCEL_REQUEST, messageList);
    }

    @SuppressWarnings("unchecked")
    public Command cancelAllCommand() throws ParserException
    {
        consumeToken("Expected cancel all command.", null);
        InternalID orderID = new InternalID(""+0);
        Message message = FIXMessageUtil.newCancel(orderID, orderID, (char)0, BigDecimal.ZERO, "", "");
        message.removeField(OrigClOrdID.FIELD);

        List aList = new ArrayList();
        aList.add(message);
        return new Command(MsgType.ORDER_CANCEL_REQUEST, aList);
    }

    Collection<InternalID> orderIDList() throws ParserException {
        String orderIDToken = consumeIntToken("Expected order ID").image;

        if (lexer.peek() == null) {
            Vector<InternalID> aVec = new Vector<InternalID>();
            aVec.addAll(Arrays.asList(new InternalID [] { new InternalID(orderIDToken) } ));
            return aVec;
        }
        Collection<InternalID> results = orderIDList();
        results.add(new InternalID(orderIDToken));
        return results;
    }

    protected Command cancelReplaceCommand() throws ParserException, NoMoreIDsException
    {
        consumeToken("Expected cancel replace command.", null);
        Token.IntToken orderIDToken = consumeIntToken("Expected order ID");
        Token.StringToken replaceTypeToken = consumeStringToken("Expected S or P", CancelReplaceTypeImage.values());

        Message cxrMessage = null;
        if (CancelReplaceTypeImage.SHARES.image.equalsIgnoreCase(replaceTypeToken.image))
        {
            Token.IntToken quantityToken = consumeIntToken("Expected quantity");
            String quantityString = quantityToken.image;
            InternalID orderID = new InternalID(mIDFactory.getNext());
            cxrMessage = FIXMessageUtil.newCancelReplaceShares(orderID, new InternalID(orderIDToken.toString()), new BigDecimal(quantityString));
        } else if (CancelReplaceTypeImage.PRICE.image.equalsIgnoreCase(replaceTypeToken.image)) {
            Token.FloatToken priceToken = consumeFloatToken("Expected quantity");
            String priceString = priceToken.image;
            InternalID orderID = new InternalID(mIDFactory.getNext());
            cxrMessage = FIXMessageUtil.newCancelReplacePrice(orderID, new InternalID(orderIDToken.toString()), new BigDecimal(priceString));
        }
        return new Command(MsgType.ORDER_CANCEL_REPLACE_REQUEST, cxrMessage);
    }

    protected void throwException(String message, Enum[] completions)
            throws ParserException {
        throw new ParserException("Error parsing '" + currentCommand + "': "
                                  + message, parsePosition, completions);
    }

    protected Command newSetCommand() throws ParserException
    {
        currentCommand = "Set command";
        Token setCommand = consumeToken("Expecting SET or UNSET command", null);
        assert(CommandImage.SET.image.equalsIgnoreCase(setCommand.image) ||
               CommandImage.UNSET.image.equalsIgnoreCase(setCommand.image));
        Token variableName = consumeToken("Expecting variable name", null);
        String variableNameString = variableName.image;

        String [] results;
        String command;
        if (CommandImage.SET.image.equalsIgnoreCase(setCommand.image))
        {
            StringBuilder builder = new StringBuilder();
            Token aToken = consumeToken("Expecting value", null);

            builder.append(aToken.image);

            while ((aToken = lexer.getNextToken())!=null)
            {
                builder.append(" ");
                builder.append(aToken.image);
            }
            results = new String [] {variableNameString, builder.toString()};
            command = CommandImage.SET.image;
        } else {
            results = new String [] {variableNameString};
            command = CommandImage.UNSET.image;
        }
        return new Command(command, Arrays.asList(results));
    }



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

    protected Token.StringToken consumeStringToken(String errorMessage,
                                                   Enum[] completions) throws ParserException {
        try {
            Token.StringToken aToken = (Token.StringToken) consumeToken(
                    errorMessage, completions);
            return aToken;
        } catch (ClassCastException cce) {
            throwException(errorMessage, completions);
        }
        return null;
    }

    protected Token.IntToken consumeIntToken(String errorMessage)
            throws ParserException {
        try {
            Token.IntToken aToken = (Token.IntToken) consumeToken(errorMessage,
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

    protected Token.FloatToken consumeFloatToken(String errorMessage)
            throws ParserException {
        try {
            Token.FloatToken aToken = (Token.FloatToken) consumeToken(
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
