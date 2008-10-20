package org.marketcetera.marketdata.bogus;

import java.util.List;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.CoreException;
import org.marketcetera.quickfix.AbstractMessageTranslator;
import org.marketcetera.quickfix.IMessageTranslator;

import quickfix.Group;
import quickfix.Message;

/* $License$ */

/**
 * Bogus feed implementation of {@link IMessageTranslator}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: BogusFeedMessageTranslator.java 9456 2008-07-31 22:28:30Z klim $
 * @since 0.5.0
 */
@ClassVersion("$Id: BogusFeedMessageTranslator.java 9456 2008-07-31 22:28:30Z klim $") //$NON-NLS-1$
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
            throws CoreException
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
            throws CoreException
    {
        return inData.getAsMessage();
    }
}
