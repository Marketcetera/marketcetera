package org.marketcetera.ors.filters;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;

/* $License$ */

/**
 * Removes fields based on conditions.
 *
 */
@ClassVersion("$Id$")
public class ConditionalFieldRemoverMessageModifier
        implements MessageModifier
{
	private int conditionalField;
	
	private List<String> matchCriteria;
	
    /**
     * Create a new ConditionalFieldRemoverMessageModifier instance.
     *
     */
    public ConditionalFieldRemoverMessageModifier(String skipField)
    {
        skipField = StringUtils.trimToNull(skipField);
        if(skipField == null) {
            field = -1;
            msgType = null;
        } else {
            Validate.isTrue(messageDescriptor.matcher(skipField).matches(),
                            Messages.NON_CONFORMING_FIELD_SPECIFICATION.getText());
            int splitIndex = skipField.indexOf('(');
            // the field to remove is everything left of splitIndex
            field = Integer.parseInt(skipField.substring(0,
                                                       splitIndex));
            msgType = skipField.substring(splitIndex+1,
                                        skipField.length()-1);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.MessageModifier#modifyMessage(quickfix.Message, org.marketcetera.ors.history.ReportHistoryServices, org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor)
     */
    @Override
    public boolean modifyMessage(Message inMessage,
                                 ReportHistoryServices inHistoryServices,
                                 FIXMessageAugmentor inAugmentor)
            throws CoreException
    {
    	boolean isModified = false;
        boolean matchFlag = false;
        
        if(inMessage.isSetField(conditionalField))
        {
        	String matcherValue = null;
        	try {
				matcherValue = inMessage.getString(conditionalField);
			} catch (FieldNotFound e) {
				e.printStackTrace();
			}
        	for(String value: matchCriteria)
        	{
        		if(value.equals(matcherValue)){
        			matchFlag = true;
        			break;
        		}
        	}
        }
        
        if(matchFlag){
	        if(msgType != null &&
	           inMessage.isSetField(field)) {
	            SLF4JLoggerProxy.debug(ConditionalFieldRemoverMessageModifier.class,
	                                   "Message contains field {}", //$NON-NLS-1$
	                                   field);
	            if(msgType.equals(allMessageIndicator)) {
	                SLF4JLoggerProxy.debug(ConditionalFieldRemoverMessageModifier.class,
	                                       "Message type specifier is 'all messages', removing field"); //$NON-NLS-1$
	                inMessage.removeField(field);
	                isModified = true;
	            } else {
	                MsgType thisMessageType = new MsgType();
	                try {
	                    inMessage.getHeader().getField(thisMessageType);
	                } catch (FieldNotFound e) {
	                    throw new CoreException(e);
	                }
	                if(thisMessageType.valueEquals(msgType)) {
	                    SLF4JLoggerProxy.debug(ConditionalFieldRemoverMessageModifier.class,
	                                           "Message type specified matches message, removing field"); //$NON-NLS-1$
	                    inMessage.removeField(field);
	                    isModified = true;
	                }
	            }
	        }
        }
        if(isModified) {
            SLF4JLoggerProxy.debug(ConditionalFieldRemoverMessageModifier.class,
                                   "Modified message is {}", //$NON-NLS-1$
                                   inMessage);
        }
        return isModified;
    }
    /**
     * Get the msgType value.
     *
     * @return a <code>String</code> value
     */
    public String getMsgType()
    {
        return msgType;
    }
    /**
     * Get the field value.
     *
     * @return a <code>int</code> value
     */
    public int getField()
    {
        return field;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("MsgType", //$NON-NLS-1$
                                                msgType).append("Field", //$NON-NLS-1$
                                                                field).toString();
    }
    

	public void setConditionalField(int conditionalField) {
		this.conditionalField = conditionalField;
	}
	public void setMatchCriteria(List<String> matchCriteria) {
		this.matchCriteria = matchCriteria;
	}

	/**
     * FIX message code of the messages to modify
     */
    private final String msgType;
    /**
     * FIX message specifier of the field to remove
     */
    private final int field;
    /**
     * indicates that the field should be removed from all messages
     */
    private static final String allMessageIndicator = "*"; //$NON-NLS-1$
    /**
     * pattern used to identify conforming specifications
     */
    private static final Pattern messageDescriptor = Pattern.compile("[0-9]{1,5}\\((\\" + allMessageIndicator + "?|[A-Z]{1,3})\\)"); //$NON-NLS-1$ //$NON-NLS-2$
}
