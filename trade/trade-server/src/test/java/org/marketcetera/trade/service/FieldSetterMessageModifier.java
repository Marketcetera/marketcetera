package org.marketcetera.trade.service;

import org.marketcetera.brokers.MessageModifier;
import org.marketcetera.fix.ServerFixSession;

import quickfix.Message;

/* $License$ */

/**
 * Sets the given field to the given value on handled messages.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FieldSetterMessageModifier
        implements MessageModifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.brokers.MessageModifier#modify(org.marketcetera.fix.ServerFixSession, quickfix.Message)
     */
    @Override
    public boolean modify(ServerFixSession inServerFixSession,
                          Message inMessage)
    {
        inMessage.setString(field,
                            value);
        return true;
    }
    /**
     * Get the field value.
     *
     * @return an <code>int</code> value
     */
    public int getField()
    {
        return field;
    }
    /**
     * Sets the field value.
     *
     * @param inField an <code>int</code> value
     */
    public void setField(int inField)
    {
        field = inField;
    }
    /**
     * Get the value value.
     *
     * @return a <code>String</code> value
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        value = inValue;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FieldSetterMessageModifier [field=").append(field).append(", value=").append(value).append("]");
        return builder.toString();
    }
    /**
     * field on the message to set
     */
    private int field;
    /**
     * value to set on the message
     */
    private String value;
}
