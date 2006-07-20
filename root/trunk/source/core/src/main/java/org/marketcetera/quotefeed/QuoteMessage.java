package org.marketcetera.quotefeed;

public interface QuoteMessage extends FeedMessage {

    /**
     * Set the value of the field in this quote
     * @param fieldNum The FIX protocol field number for the field to set
     * @param fieldValue  The value
     */
    void setField(int fieldNum, Object fieldValue);

    /**
     * Get the value of a field in a quote
     * @param fieldNum The FIX protocol field number for the field to get
     * @return The value corresponding to the field
     */
    Object getField(int fieldNum);

    /**
     * Determine whether this field could possibly be present in this message
     * @param fieldNum
     * @return true if this field is a valid field for this message type, false otherwise
     */
    boolean isValidField(int fieldNum);

}
