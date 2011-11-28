package org.marketcetera.orderloader.system;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.orderloader.OrderParsingException;
import org.marketcetera.orderloader.Messages;
import org.marketcetera.core.Pair;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * A field processor that extracts custom fields from an order row
 * and sets it on the supplied order instance.
*
* @author anshul@marketcetera.com
* @version $Id$
* @since 1.0.0
*/
@ClassVersion("$Id$")
final class CustomFieldProcessor implements FieldProcessor {
    @Override
    public void apply(String[] inRow, OrderSingle inOrder)
            throws OrderParsingException {
        Map<String,String> customs = new HashMap<String, String>();
        for(Pair<Integer,String> custom: mCustomFields) {
            customs.put(custom.getSecondMember(),
                    inRow[custom.getFirstMember()]);
        }
        inOrder.setCustomFields(customs);
    }

    /**
     * Add the custom field to the processor.
     *
     * @param inIndex the column index of the custom field.
     * @param inHeader the column header value.
     *
     * @throws OrderParsingException if the header value is not numeric or
     * if a duplicate header was specified.
     */
    public void addField(int inIndex, String inHeader)
            throws OrderParsingException {
        try {
            int header = Integer.parseInt(inHeader);
            Integer oldIndex = mHeaders.put(header, inIndex);
            mCustomFields.add(new Pair<Integer,String>(inIndex, inHeader));
            if (oldIndex != null) {
                //We have a duplicate
                throw new OrderParsingException(new I18NBoundMessage3P(
                        Messages.DUPLICATE_HEADER, inHeader,
                        oldIndex, inIndex));
            }
        } catch (NumberFormatException e) {
            throw new OrderParsingException(e, new I18NBoundMessage2P(
                    Messages.INVALID_CUSTOM_HEADER, inHeader, inIndex));
        }
    }

    /**
     * If no custom fields will be processed by this processor.
     *
     * @return true if no custom fields will be processed by this processor.
     */
    public boolean isEmpty() {
        return mCustomFields.isEmpty();
    }
    private final List<Pair<Integer,String>> mCustomFields =
            new LinkedList<Pair<Integer, String>>();
    private final Map<Integer,Integer> mHeaders =
            new HashMap<Integer,Integer>();
}
