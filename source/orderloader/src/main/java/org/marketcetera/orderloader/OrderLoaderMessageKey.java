package org.marketcetera.orderloader;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LocalizedMessage;
import org.marketcetera.core.MessageKey;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public enum OrderLoaderMessageKey implements LocalizedMessage {
    PARSING_ORDER_GEN_ERROR("orderloader.error_order_gen"),
    PARSING_PRICE_VALID_NUM("orderloader.parsing.price_valid_num"),
    PARSING_PRICE_POSITIVE("orderloader.parsing.price_positive"),
    PARSING_QTY_INT("orderloader.parsing.quantity_int"),
    PARSING_QTY_POS_INT("orderloader.parsing.quantity_pos_int"),
    PARSING_FIELD_NOT_IN_DICT("orderloader.parsing.field_not_in_dict"),
    PARSING_WRONG_NUM_FIELDS("orderloader.parsing.wrong_num_fields");

    private OrderLoaderMessageKey(String inKey) {
        key = inKey;
    }

    private final String key;

    public String toString() {
        return key;
    }

    public String getLocalizedMessage()
    {
        return MessageKey.getMessageString(toString());
    }

    public String getLocalizedMessage(Object... args)
    {
        return MessageKey.getMessageString(toString(), args);
    }
}
