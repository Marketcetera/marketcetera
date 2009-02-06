package org.marketcetera.quickfix;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.core.CoreException;
import org.marketcetera.trade.MSymbol;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

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
 * Common routines for {@link IMessageTranslator} implementations.
 * 
 * <p>This class hierarchy translates <em>QuickFIX</em> messages to 
 * other formats.  This class specifically provides common routines to
 * parse a <em>QuickFIX</em> message.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public abstract class AbstractMessageTranslator<T>
{
    /**
     * Indicates what FIX version to use
     */
    public static final FIXVersion sMessageVersion = FIXVersion.FIX44;
    /**
     * Indicates what FIX factory to use
     */
    public static final FIXMessageFactory sMessageFactory = sMessageVersion.getMessageFactory();

    /**
     * Gets the <code>Symbol</code> specified in the given <code>Group</code>.
     *
     * @param inGroup a <code>Group</code> value
     * @return a <code>MSymbol</code> value containing the symbol
     * @throws CoreException if the symbol could not be extracted
     */
    public static MSymbol getSymbol(Group inGroup) 
        throws CoreException
    {
        String securityType;
        try {
            securityType = inGroup.getString(SecurityType.FIELD);
        } catch (FieldNotFound e) {
            securityType = SecurityType.COMMON_STOCK;
        }
        MSymbol symbol;
        try {
            if(SecurityType.OPTION.equals(securityType) && 
               inGroup.isSetField(NoUnderlyings.FIELD)) {
                Group underlyingGroup = sMessageFactory.createGroup(MsgType.MARKET_DATA_REQUEST, 
                                                                    NoUnderlyings.FIELD);
                inGroup.getGroup(1, 
                                 underlyingGroup);
                symbol = new MSymbol(underlyingGroup.getString(UnderlyingSymbol.FIELD));
            } else {
                symbol = new MSymbol(inGroup.getString(Symbol.FIELD));
            }
        } catch (FieldNotFound e) {
            throw new CoreException(e, 
                                    org.marketcetera.marketdata.Messages.ERROR_MARKET_DATA_FEED_CANNOT_FIND_SYMBOL);
        }
        return symbol;
    }
    
    /**
     * Get all the {@link Group} objects associated with the given <code>Message</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>List&lt;Group&gt;</code> value
     * @throws CoreException 
     */
    public static List<Group> getGroups(Message inMessage) 
        throws CoreException
    {
        int totalSymbols = AbstractMessageTranslator.determineTotalSymbols(inMessage);
        List<Group> groups = new ArrayList<Group>();
        // the message header is valid, make a best effort to parse each group, discarding as few as possible
        for (int symbolCounter=1;symbolCounter<=totalSymbols;symbolCounter++) {
            try {
                Group relatedSymGroup = sMessageFactory.createGroup(MsgType.MARKET_DATA_REQUEST, 
                                                                    NoRelatedSym.FIELD);
                inMessage.getGroup(symbolCounter, 
                                   relatedSymGroup);
                // relatedSymGroup is a chunk in the message that encapsulates the Symbol
                // securityType now holds a description of the symbol (fix tag 167)
                groups.add(relatedSymGroup);
            } catch (Throwable e) {
                SLF4JLoggerProxy.debug(AbstractMessageTranslator.class, e, Messages.GROUP_NUMBER_COULD_NOT_TRANSLATE.getText(symbolCounter));
            }
        }            
        
        return groups;
    }

    /**
     * Retrieves the subscription type from the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>char</code> value
     */
    public static char determineSubscriptionRequestType(Message inMessage) 
    {
        try {
            return inMessage.getChar(SubscriptionRequestType.FIELD);
        } catch (FieldNotFound e) {
            org.marketcetera.marketdata.Messages.WARNING_MARKET_DATA_FEED_CANNOT_DETERMINE_SUBSCRIPTION.warn(AbstractMessageTranslator.class, e);
            return SubscriptionRequestType.SNAPSHOT;
        }
    }

    /**
     * Retrieves the total number of symbols in the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return an <code>int</code> value
     * @throws CoreException if the number of symbols could not be determined
     */
    public static int determineTotalSymbols(Message inMessage) 
        throws CoreException 
    {
        try {
            return inMessage.getInt(NoRelatedSym.FIELD);
        } catch (FieldNotFound e) {
            throw new CoreException(e);
        }
    }
}
