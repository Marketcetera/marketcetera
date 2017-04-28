package org.marketcetera.trade;

import java.util.Map;

/* $License$ */

/**
 * Indicates that the implementor has custom fields.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasCustomFields
{
    /**
     * Get the custom fields specified along with this order.
     * 
     * <p>Custom fields may be optionally specified to specify order fields that
     * are not supported by this type.
     * 
     * <p>For example, extra FIX field values may be supplied when sending
     * an order. When sending FIX field values, the map key should be
     * the integer field tag value and the map value should be the string
     * value of that field. 
     *
     * @return a <code>Map&lt;String,String&gt;</code> value or <code>null</code>
     */
    Map<String,String> getCustomFields();
    /**
     * Set the custom fields for this order.
     *
     * @param inCustomFields a <code>Map&lt;String,String&gt;</code> value or <code>null</code>
     * @see #getCustomFields() 
     */
    void setCustomFields(Map<String,String> inCustomFields);
}
