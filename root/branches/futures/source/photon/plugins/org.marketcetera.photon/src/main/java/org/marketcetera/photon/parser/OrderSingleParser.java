package org.marketcetera.photon.parser;

import java.math.BigDecimal;
import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.functors.Map;
import org.codehaus.jparsec.functors.Map4;
import org.codehaus.jparsec.functors.Map5;
import org.codehaus.jparsec.pattern.CharPredicates;
import org.codehaus.jparsec.pattern.Patterns;
import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.trade.BrokerID;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Instrument;
import org.marketcetera.trade.Option;
import org.marketcetera.trade.OptionType;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Parses an order from a string.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class OrderSingleParser {

    /**
     * A typical word is a sequence of non-whitespace characters.
     */
    private static final Parser<String> WORD = Scanners.pattern(
            Patterns.regex("\\S*"), "part").source(); //$NON-NLS-1$ //$NON-NLS-2$

    private static final Parser<Side> SIDE_PARSER = Terminals.Identifier.PARSER
            .map(new Map<String, Side>() {
                @Override
                public Side map(String from) {
                    if (from.equalsIgnoreCase("b")) { //$NON-NLS-1$
                        return Side.Buy;
                    } else if (from.equalsIgnoreCase("s")) { //$NON-NLS-1$
                        return Side.Sell;
                    } else if (from.equalsIgnoreCase("ss")) { //$NON-NLS-1$
                        return Side.SellShort;
                    }
                    throw new IllegalArgumentException(
                            Messages.ORDER_SINGLE_PARSER_INVALID_SIDE
                                    .getText(from));
                }
            });

    private static final Parser<Instrument> OPTION_PARSER = Parsers.sequence(
            Scanners.pattern(Patterns.regex("\\D+"), "symbol").source(), //$NON-NLS-1$ //$NON-NLS-2$
            Scanners.INTEGER, Scanners.isChar(CharPredicates.among("cCpP")) //$NON-NLS-1$
                    .source(), Scanners.DECIMAL,
            new Map4<String, String, String, String, Instrument>() {
                @Override
                public Instrument map(String symbol, String expiry,
                        String typeString, String strikeString) {
                    OptionType type;
                    if (typeString.equalsIgnoreCase("c")) { //$NON-NLS-1$
                        type = OptionType.Call;
                    } else if (typeString.equalsIgnoreCase("p")) { //$NON-NLS-1$
                        type = OptionType.Put;
                    } else {
                        throw new IllegalArgumentException();
                    }
                    return new Option(symbol, expiry, new BigDecimal(
                            strikeString), type);
                }
            });

    private static final Parser<Instrument> EQUITY_PARSER = WORD
            .map(new Map<String, Instrument>() {
                @Override
                public Instrument map(String symbol) {
                    return new Equity(symbol);
                }
            });

    private static final Parser<Instrument> INSTRUMENT_PARSER = Terminals.Identifier.PARSER
            .map(new Map<String, Instrument>() {

                @Override
                public Instrument map(String from) {
                    return OPTION_PARSER.or(EQUITY_PARSER).parse(from);
                }
            });

    private static final Parser<BigDecimal> BIG_DECIMAL_PARSER = Terminals.Identifier.PARSER
            .map(new Map<String, BigDecimal>() {
                @Override
                public BigDecimal map(String from) {
                    try {
                        return new BigDecimal(from);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException(
                                Messages.ORDER_SINGLE_PARSER_NOT_A_DECIMAL
                                        .getText(from));
                    }
                }
            });

    /**
     * Optional parts have an id followed by a colon, followed by a value. The
     * value can be quoted if spaces are desired.
     */
    private static final Parser<String> OPTIONAL_PART = Scanners.pattern(
            Patterns.regex("\\S*:((\".*\")|(\\S*))"), "optional part").source(); //$NON-NLS-1$ //$NON-NLS-2$
    private static final Parser<?> TOKENS = Terminals.caseInsensitive(
            OPTIONAL_PART.or(WORD), new String[] {}, new String[] {})
            .tokenizer();

    private final Parser<OrderSingle> ORDER_PARSER = Parsers
            .sequence(
                    SIDE_PARSER,
                    BIG_DECIMAL_PARSER,
                    INSTRUMENT_PARSER,
                    Terminals.Identifier.PARSER,
                    Terminals.Identifier.PARSER.many(),
                    new Map5<Side, BigDecimal, Instrument, String, List<String>, OrderSingle>() {
                        @Override
                        public OrderSingle map(Side side, BigDecimal quantity,
                                Instrument instrument, String price,
                                List<String> optionalField) {
                            OrderSingle order = Factory.getInstance()
                                    .createOrderSingle();
                            order.setSide(side);
                            order.setQuantity(quantity);
                            if (price.equalsIgnoreCase("mkt")) { //$NON-NLS-1$
                                order.setOrderType(OrderType.Market);
                            } else {
                                order.setOrderType(OrderType.Limit);
                                try {
                                    order.setPrice(new BigDecimal(price));
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException(
                                            Messages.ORDER_SINGLE_PARSER_INVALID_PRICE
                                                    .getText(price));
                                }
                            }
                            order.setInstrument(instrument);
                            for (String string : optionalField) {
                                applyOptionalField(string, order);
                            }
                            return order;
                        }

                        private void applyOptionalField(String string,
                                OrderSingle order) {
                            if (string.isEmpty()) {
                                return;
                            }
                            String[] split = string.split(":"); //$NON-NLS-1$
                            if (split.length == 1) {
                                throw new IllegalArgumentException(
                                        Messages.ORDER_SINGLE_PARSER_NO_VALUE_FOR_OPTIONAL_FIELD
                                                .getText(split[0]));
                            } else if (split.length != 2) {
                                throw new IllegalArgumentException(
                                        Messages.ORDER_SINGLE_PARSER_INVALID_OPTIONAL_FIELD
                                                .getText(string));
                            }
                            String keyword = split[0];
                            String value = split[1];
                            if (keyword.equalsIgnoreCase("acc")) { //$NON-NLS-1$
                                if (value.startsWith("\"")) { //$NON-NLS-1$
                                    value = Terminals.StringLiteral.DOUBLE_QUOTE_TOKENIZER
                                            .parse(value);
                                }
                                order.setAccount(value);
                            } else if (keyword.equalsIgnoreCase("b")) { //$NON-NLS-1$
                                if (mBrokerIdValidator != null
                                        && !mBrokerIdValidator.isValid(value)) {
                                    throw new IllegalArgumentException(
                                            Messages.ORDER_SINGLE_PARSER_INVALID_BROKER_ID
                                                    .getText(value));
                                } else {
                                    order.setBrokerID(new BrokerID(value));
                                }
                            } else if (keyword.equalsIgnoreCase("tif")) { //$NON-NLS-1$
                                if (value.equalsIgnoreCase("day")) { //$NON-NLS-1$
                                    order.setTimeInForce(TimeInForce.Day);
                                } else if (value.equalsIgnoreCase("gtc")) { //$NON-NLS-1$
                                    order
                                            .setTimeInForce(TimeInForce.GoodTillCancel);
                                } else if (value.equalsIgnoreCase("fok")) { //$NON-NLS-1$
                                    order
                                            .setTimeInForce(TimeInForce.FillOrKill);
                                } else if (value.equalsIgnoreCase("clo")) { //$NON-NLS-1$
                                    order
                                            .setTimeInForce(TimeInForce.AtTheClose);
                                } else if (value.equalsIgnoreCase("opg")) { //$NON-NLS-1$
                                    order
                                            .setTimeInForce(TimeInForce.AtTheOpening);
                                } else if (value.equalsIgnoreCase("ioc")) { //$NON-NLS-1$
                                    order
                                            .setTimeInForce(TimeInForce.ImmediateOrCancel);
                                } else {
                                    throw new IllegalArgumentException(
                                            Messages.ORDER_SINGLE_PARSER_INVALID_TIF
                                                    .getText(value));
                                }
                            } else {
                                throw new IllegalArgumentException(
                                        Messages.ORDER_SINGLE_PARSER_INVALID_OPTIONAL_FIELD
                                                .getText(string));
                            }
                        }
                    }).from(TOKENS, Scanners.WHITESPACES);

    private final IBrokerIdValidator mBrokerIdValidator;

    /**
     * Constructor.
     * 
     * @param brokerIdValidator
     *            validates broker id's on order commands, can be null
     */
    public OrderSingleParser(IBrokerIdValidator brokerIdValidator) {
        mBrokerIdValidator = brokerIdValidator;
    }

    /**
     * Returns a parser that parses a string into an order.
     * 
     * @return the parser
     */
    public Parser<OrderSingle> getParser() {
        return ORDER_PARSER;
    }

}
