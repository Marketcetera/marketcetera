package org.marketcetera.ors.filters;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.Message;
import quickfix.StringField;


/* $License$ */

/**
 * Sets the value of the given fields regardless of the original value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FieldOverrideMessageModifier
        implements MessageModifier
{
	
    /* (non-Javadoc)
     * @see org.marketcetera.ors.filters.MessageModifier#modifyMessage(quickfix.Message, org.marketcetera.ors.history.ReportHistoryServices, org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor)
     */
    @Override
    public boolean modifyMessage(Message inMessage,
                                 ReportHistoryServices inHistoryServices,
                                 FIXMessageAugmentor inAugmentor)
            throws CoreException
    {
        boolean modified = false;
        if(!msgfields.isEmpty()) {
            modified = true;
            for(Map.Entry<Integer,String> entry : msgfields.entrySet()) {
                inMessage.setField(new StringField(entry.getKey(),
                                                   entry.getValue()));
            }
        }
        if(!headerFields.isEmpty()) {
            modified = true;
            for(Map.Entry<Integer,String> entry : headerFields.entrySet()) {
                inMessage.getHeader().setField(new StringField(entry.getKey(),
                                                               entry.getValue()));
            }
        }
        if(!trailerFields.isEmpty()) {
            modified = true;
            for(Map.Entry<Integer,String> entry : trailerFields.entrySet()) {
                inMessage.getTrailer().setField(new StringField(entry.getKey(),
                                                                entry.getValue()));
            }
        }
        if(modified) {
            String newMessageContents = inMessage.toString(); // necessary to recalculate checksum on message
            SLF4JLoggerProxy.debug(this,
                                   "Message modified to {}",
                                   newMessageContents);
        }
        return modified;
    }
    /**
     * Sets body field values.
     *
     * @param inFields a <code>Map&lt;Integer,String&gt;</code> value
     */
    public void setMsgFields(Map<Integer,String> inFields)
    {
        msgfields.clear();
        if(inFields != null) {
            msgfields.putAll(inFields);
        }
    }
    /**
     * Sets header field values.
     *
     * @param inFields a <code>Map&lt;Integer,String&gt;</code> value
     */
    public void setHeaderFields(Map<Integer,String> inFields)
    {
        headerFields.clear();
        if(inFields != null) {
            headerFields.putAll(inFields);
        }
    }
    /**
     * Sets trailer field values.
     *
     * @param inFields a <code>Map&lt;Integer,String&gt;</code> value
     */
    public void setTrailerFields(Map<Integer,String> inFields)
    {
        trailerFields.clear();
        if(inFields != null) {
            trailerFields.putAll(inFields);
        }
    }
    
    
    
    
    
    /**
     * body fields to set
     */
    private final Map<Integer,String> msgfields = new HashMap<Integer,String>();
    /**
     * header fields to set
     */
    private final Map<Integer,String> headerFields = new HashMap<Integer,String>();
    /**
     * trailer fields to set
     */
    private final Map<Integer,String> trailerFields = new HashMap<Integer,String>();
}
