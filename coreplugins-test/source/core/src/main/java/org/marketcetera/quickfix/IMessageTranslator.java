package org.marketcetera.quickfix;

import org.marketcetera.core.CoreException;

import quickfix.Message;

/**
 * Translates between the specified external data type <code>T</code> and {@link Message} format.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
public interface IMessageTranslator<T>
{
    /**
     * Translate from <code>FIX</code> to an external data format. 
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>T</code> value
     * @throws IllegalArgumentException if the message type is not handled by the translater
     * @throws CoreException if an error occurs during otherwise valid message translation 
     */
    public T translate(Message inMessage)
        throws CoreException;
    
    /**
     * Translate from an external data type to <code>FIX</code> format.
     *
     * @param inData an <code>T</code> value
     * @return a <code>Message</code> value
     * @throws CoreException if the message cannot be translated
     */
    public Message asMessage(T inData)
        throws CoreException;
}
