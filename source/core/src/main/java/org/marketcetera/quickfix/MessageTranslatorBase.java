package org.marketcetera.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.MarketceteraException;

import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.NoRelatedSym;
import quickfix.field.NoUnderlyings;
import quickfix.field.SecurityType;
import quickfix.field.SubscriptionRequestType;
import quickfix.field.Symbol;
import quickfix.field.UnderlyingSymbol;

/**
 * Common routines for {@link MessageTranslator} implementations.
 * 
 * <p>This class hierarch translates <em>QuickFIX</em> messages to 
 * other formats.  This class specifically provides common routines to
 * parse a <em>QuickFIX</em> message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public abstract class MessageTranslatorBase
        implements MessageTranslator
{
    /**
     * the subscription type
     */
    private final char mSubscriptionRequestType;
    /**
     * the total number of symbols in the FIX message
     */
    private final int mTotalSymbols;
    /**
     * the message itself
     */
    private final Message mMessage;
    
    /**
     * Create a new MessageTranslatorBase instance.
     *
     * @param inMessage a <code>Message</code> value
     * @throws MarketceteraException if an error occurs while parsing the message
     */
    protected MessageTranslatorBase(Message inMessage) 
        throws MarketceteraException
    {
        mMessage = inMessage;
        mSubscriptionRequestType = determineSubscriptionRequestType(inMessage);
        mTotalSymbols = determineTotalSymbols(inMessage);
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug(String.format("Translating market data request for %d total symbols and subscription type %c",
                                              getTotalSymbols(),
                                              getSubscriptionRequestType()),
                                this);
        }
    }
    
    /**
     * Gets the <code>Symbol</code> specified in the given <code>Group</code>.
     *
     * @param inGroup a <code>Group</code> value
     * @return a <code>String</code> value containing the symbol
     * @throws MarketceteraException
     */
    protected static String getSymbol(Group inGroup) 
        throws MarketceteraException
    {
        // TODO this return type should be non-String
        String securityType;
        try {
            securityType = inGroup.getString(SecurityType.FIELD);
        } catch (FieldNotFound e) {
            securityType = SecurityType.COMMON_STOCK;
        }
        String symbol;
        try {
            if(SecurityType.OPTION.equals(securityType) && 
               inGroup.isSetField(NoUnderlyings.FIELD)) {
                Group underlyingGroup = sMessageFactory.createGroup(MsgType.MARKET_DATA_REQUEST, 
                                                                    NoUnderlyings.FIELD);
                inGroup.getGroup(1, 
                                 underlyingGroup);
                symbol = underlyingGroup.getString(UnderlyingSymbol.FIELD);
            } else {
                symbol = inGroup.getString(Symbol.FIELD);
            }
        } catch (FieldNotFound e) {
            // TODO add a message here
            throw new MarketceteraException(e);
        }
        return symbol;
    }
    
    /**
     * Get all the {@link Group} objects associated with the <code>Message</code>.
     *
     * @return a <code>List&lt;Group&gt;</code> value
     */
    protected List<Group> getGroups()
    {
        List<Group> groups = new ArrayList<Group>();
        // the message header is valid, make a best effort to parse each group, discarding as few as possible
        for (int symbolCounter=1;symbolCounter<=getTotalSymbols();symbolCounter++) {
            try {
                Group relatedSymGroup = sMessageFactory.createGroup(MsgType.MARKET_DATA_REQUEST, 
                                                                    NoRelatedSym.FIELD);
                getMessage().getGroup(symbolCounter, 
                                      relatedSymGroup);
                // relatedSymGroup is a chunk in the message that encapsulates the Symbol
                // securityType now holds a description of the symbol (fix tag 167)
                groups.add(relatedSymGroup);
            } catch (Throwable e) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug(String.format("Group number %d could not be translated, skipping",
                                                      symbolCounter),
                                        e,
                                        this);
                }
            }
        }            
        
        return groups;
    }

    /**
     * Retrieves the subscription type from the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>char</code> value
     * @throws MarketceteraException if the subscription type could not be determined
     */
    protected static char determineSubscriptionRequestType(Message inMessage) 
        throws MarketceteraException 
    {
        try {
            return inMessage.getChar(SubscriptionRequestType.FIELD);
        } catch (FieldNotFound e) {
            throw new MarketceteraException(e);
        }
    }

    /**
     * Retrieves the total number of symbols in the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return an <code>int</code> value
     * @throws MarketceteraException if the number of symbols could not be determined
     */
    protected static int determineTotalSymbols(Message inMessage) 
        throws MarketceteraException 
    {
        try {
            return inMessage.getInt(NoRelatedSym.FIELD);
        } catch (FieldNotFound e) {
            throw new MarketceteraException(e);
        }
    }

    /**
     * Get the subscriptionRequestType value.
     *
     * @return a <code>MessageTranslatorBase</code> value
     */
    protected char getSubscriptionRequestType()
    {
        return mSubscriptionRequestType;
    }

    /**
     * Get the totalSymbols value.
     *
     * @return a <code>MessageTranslatorBase</code> value
     */
    protected int getTotalSymbols()
    {
        return mTotalSymbols;
    }

    /**
     * Get the message value.
     *
     * @return a <code>MessageTranslatorBase</code> value
     */
    protected Message getMessage()
    {
        return mMessage;
    }
}
