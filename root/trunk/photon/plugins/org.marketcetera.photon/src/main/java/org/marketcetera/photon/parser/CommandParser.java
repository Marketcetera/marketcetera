package org.marketcetera.photon.parser;

import java.util.List;

import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.pattern.CharPredicates;
import org.codehaus.jparsec.pattern.Patterns;
import org.marketcetera.photon.IBrokerIdValidator;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Parses a command string.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class CommandParser {

    /**
     * A typical word is a sequence of non-whitespace characters.
     */
    private static final Parser<String> WORD = Scanners.pattern(
            Patterns.regex("\\S*"), "part").source(); //$NON-NLS-1$ //$NON-NLS-2$

    private static final Parser<List<String>> CANCEL_PARSER = Parsers.sequence(
            Scanners.isChar(CharPredicates.among("cC")), Scanners.WHITESPACES, //$NON-NLS-1$
            WORD.sepBy(Scanners.WHITESPACES));

    private final Parser<Object> mCommandParser;

    /**
     * Constructor.
     * 
     * @param brokerIdValidator
     *            validates broker id's on order commands, can be null
     */
    public CommandParser(IBrokerIdValidator brokerIdValidator) {
        mCommandParser = Parsers.or(CANCEL_PARSER, Parsers.sequence(Scanners
                .isChar(CharPredicates.among("oO")), Scanners.WHITESPACES, //$NON-NLS-1$
                new OrderSingleParser(brokerIdValidator).getParser()));
    }

    /**
     * Parse a command into a string. Currently two commands are supported:
     * <ol>
     * <li>Order - "o b 10 METC 10", a new OrderSingle object will be returned</li>
     * <li>Cancel - "c 1001 1002", a list of strings (the order id's) will be
     * returned</li>
     * </ol>
     * 
     * @param string
     *            the command string
     * @return either {@link OrderSingle} or {@link List<String>}
     */
    public Object parseCommand(String string) {
        return mCommandParser.parse(string);
    }
}
