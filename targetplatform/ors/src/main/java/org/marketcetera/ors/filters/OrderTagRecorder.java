package org.marketcetera.ors.filters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.ClOrdID;

/* $License$ */

/**
 * Tags outgoing orders with a given value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: OrderTagRecorder.java 82949 2013-03-27 00:51:36Z colin $
 * @since $Release$
 */
public class OrderTagRecorder
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
        Map<Integer,String> tags = null;
        ClOrdID orderID = new ClOrdID();
        try {
            inMessage.getField(orderID);
            for(int tag : tagsToWatch) {
                if(inMessage.isSetField(tag)) {
                    if(tags == null) {
                        tags = new HashMap<Integer,String>();
                    }
                    tags.put(tag,
                             inMessage.getString(tag));
                    if(tagsToRemove.contains(tag))
                    {
                    	inMessage.removeField(tag);
                    }
                }
            }
        } catch (FieldNotFound e) {
            throw new CoreException(e);
        }
        if(tags != null) {
            tagsByOrder.put(orderID.getValue(),
                            tags);
        }
        return false;
    }
    /**
     * Populates tags as appropriate on the given message.
     *
     * @param inMessage a <code>Message</code> value
     * @return a <code>boolean</code> value indicating if the message was modified or not
     */
    public static boolean populateTags(Message inMessage)
    {
        SLF4JLoggerProxy.debug(OrderTagRecorder.class,
                               "Populating tags on {}",
                               inMessage);
        boolean modified = false;
        if(inMessage.isSetField(ClOrdID.FIELD)) {
            ClOrdID orderID = new ClOrdID();
            try {
                inMessage.getField(orderID);
            } catch (FieldNotFound e) {
                SLF4JLoggerProxy.debug(OrderTagRecorder.class,
                                       "{} has no ClOrdID, skipping",
                                       inMessage);
                return false;
            }
            Map<Integer,String> tags = tagsByOrder.get(orderID.getValue());
            SLF4JLoggerProxy.debug(OrderTagRecorder.class,
                                   "Retrieved {} for {}",
                                   tags,
                                   orderID);
            if(tags != null) {
                for(Map.Entry<Integer,String> entry : tags.entrySet()) {
                    inMessage.setString(entry.getKey(),
                                        entry.getValue());
                    modified = true;
                }
            }
        } else {
            SLF4JLoggerProxy.debug(OrderTagRecorder.class,
                                   "{} has no ClOrdID, skipping",
                                   inMessage);
            return false;
        }
        SLF4JLoggerProxy.debug(OrderTagRecorder.class,
                               "Done populating tags on {}",
                               inMessage);
        return modified;
    }
    /**
     * Retires the given order.
     *
     * @param inOrderID a <code>String</code> value
     */
    public static void retireOrder(String inOrderID)
    {
        tagsByOrder.remove(inOrderID);
    }
    /**
     * Sets the tagsToWatch value.
     *
     * @param inTagsToWatch a <code>Set&lt;Integer&gt;</code> value
     */
    public void setTagsToWatch(Set<Integer> inTagsToWatch)
    {
        tagsToWatch = inTagsToWatch;
    }
    /**
     * Sets the tagsRemove value.
     *
     * @param inTagsToRemove a <code>Set&lt;Integer&gt;</code> value
     */   
    public void setTagsToRemove(Set<Integer> inTagsToRemove) {
		this.tagsToRemove = inTagsToRemove;
	}
	/**
     * indicates the tags for this recorder to watch
     */
    private Set<Integer> tagsToWatch = new HashSet<Integer>();
    
    /**
     * indicates the tags to be removed from order
     */
    private Set<Integer> tagsToRemove = new HashSet<Integer>();
    /**
     * tracks recorded tags for all orders
     */
    // must not traverse this collection without adding synchronization
    private final static Map<String,Map<Integer,String>> tagsByOrder = new HashMap<String,Map<Integer,String>>();
}
