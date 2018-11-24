package org.marketcetera.ors.filters;

import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.core.CoreException;
import org.marketcetera.fix.ServerFixSession;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;

/**
 * Copies the string value of one field to another.
 * Will only copy the source field if it's present.
 * This examples copies Symbol (55) to SecurityID (48):
 * <pre>
 *  &lt;bean id="fieldDuplicator" class="org.marketcetera.quickfix.messagemodifiers.FieldDuplicatorMessageModifier"&gt;
 *      &lt;constructor-arg&gt;55&lt;/constructor-arg&gt;
 *      &lt;constructor-arg&gt;48&lt;/constructor-arg&gt;
 *  &lt;/bean&gt;
 * </pre>
 *
 * Add the modifier to the list of other message modifiers in the OutgoingMessageHandler bean in ors-shared.xml:
 * <pre>
 *  &lt;bean id="outgoingMessageHandler" class="com.marketcetera.ors.OutgoingMessageHandler" scope="prototype"&gt;
 *  ...
 *      &lt;property name="orderModifiers"&gt;
 *          &lt;list>
 *              &lt;ref bean="defaultOrderModifier"/&gt;
 *              &lt;ref bean="fieldDuplicator"/&gt;
 *          &lt;/list>
 *      &lt;/property>
 *  ...
 * </pre>
 * @author toli
 * @version $Id$
 */

public class FieldDuplicatorMessageModifier
        implements MessageModifier
{
    private int destField;
    private int sourceField;

    public FieldDuplicatorMessageModifier(int sourceField, int destField) {
        this.sourceField = sourceField;
        this.destField = destField;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
     */
    @Override
    public boolean modify(ServerFixSession inServerFixSession,
                          Message message)
    {
        try {
            if(message.isSetField(sourceField)) {
                String value = message.getString(sourceField);
                message.setField(new StringField(destField, value));
                return true;
            }
            return false;
        } catch (FieldNotFound fieldNotFound) {
            throw new CoreException(fieldNotFound);
        }
    }
}
