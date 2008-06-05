package org.marketcetera.bogusfeed;

import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.AbstractMessageTranslator;
import org.marketcetera.quickfix.IMessageTranslator;

import quickfix.Group;
import quickfix.Message;

/* $License$ */

/**
 * Bogus feed implementation of {@link IMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
public class BogusFeedMessageTranslator
        extends AbstractMessageTranslator<BogusMessage>
{
    /**
     * static instance
     */
    private static final BogusFeedMessageTranslator sInstance = new BogusFeedMessageTranslator();
    /**
     * Gets a <code>BogusFeedMessageTranslator</code> instance.
     * 
     * @return a <code>BogusFeedMessageTranslator</code> value
     */
    static BogusFeedMessageTranslator getInstance()
    {
        return sInstance;
    }
    /**
     * Create a new BogusFeedMessageTranslator instance.
     *
     */
    private BogusFeedMessageTranslator()
    {
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(quickfix.Message)
     */
    public BogusMessage translate(Message inMessage)
            throws MarketceteraException
    {
        BogusMessage message = new BogusMessage(inMessage);
        List<Group> groups = getGroups(inMessage);
        for(Group group : groups) {
            // the symbol is the instrument for which the group is defined            
            message.addSymbol(getSymbol(group));
        }
        return message;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.quickfix.IMessageTranslator#translate(java.lang.Object)
     */
    public Message asMessage(BogusMessage inData)
            throws MarketceteraException
    {
        return inData.getAsMessage();
    }
}
