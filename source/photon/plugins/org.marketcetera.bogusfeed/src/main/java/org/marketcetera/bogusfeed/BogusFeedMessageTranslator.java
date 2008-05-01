package org.marketcetera.bogusfeed;

import java.util.List;

import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.AbstractMessageTranslator;

import quickfix.Group;
import quickfix.Message;

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class BogusFeedMessageTranslator
        extends AbstractMessageTranslator<BogusMessage>
{
    /**
     * Create a new BogusFeedMessageTranslator instance.
     *
     */
    public BogusFeedMessageTranslator()
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
    public Message translate(BogusMessage inData)
            throws MarketceteraException
    {
        return inData.getAsMessage();
    }
}
