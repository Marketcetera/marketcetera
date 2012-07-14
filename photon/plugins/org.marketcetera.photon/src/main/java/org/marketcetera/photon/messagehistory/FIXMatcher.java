package org.marketcetera.photon.messagehistory;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;
import org.marketcetera.photon.FIXFieldLocalizer;
import org.marketcetera.quickfix.CurrentFIXDataDictionary;

import quickfix.DataDictionary;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.Side;
import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * <code>Matcher</code> implementation for FIX messages.
 * 
 * <p>This class is immutable as suggested by {@link Matcher}.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public abstract class FIXMatcher<T>
        implements Matcher<ReportHolder>
{
    /**
     * collection of converters keyed by FIX field
     */
    private static final Map<Integer, FIXConverter> sFIXConverters;
    /**
     * Initializes the converters.
     * 
     * <p>Note, extra converters should be added to this class if new groups of messages are added
     * to <code>photon_fix_messages.properties</code>
     */
    static {
        // initialize the converter collection here in order to guarantee the order of the two static constructors (the variable and population)
        sFIXConverters = new HashMap<Integer, FIXConverter>();
        // create converters for FIX fields that use codes to represent longer values
        // FIX field 35 - MsgType
        sFIXConverters.put(MsgType.FIELD,
                           new FIXConverter("MsgType", //$NON-NLS-1$
                                            MsgType.FIELD));
        // FIX field 39 - OrdStatus
        sFIXConverters.put(OrdStatus.FIELD,
                           new FIXConverter("OrdStatus", //$NON-NLS-1$
                                            OrdStatus.FIELD));
        // FIX field 40 - OrdType
        sFIXConverters.put(OrdType.FIELD,
                           new FIXConverter("OrdType", //$NON-NLS-1$
                                            OrdType.FIELD));
        // FIX field 54 - Side
        sFIXConverters.put(Side.FIELD,
                           new FIXConverter("Side", //$NON-NLS-1$
                                            Side.FIELD));
    }
    /**
     * the field against which to match
     */
    private final int mMatcherFIXField;
    /**
     * the value against which to match
     */
    private final T mMatcherValue;
    /**
     * indicates whether to include or exclude the match criteria
     */
    private final boolean mShouldInclude;
    /**
     * Create a new FIXMatcher instance.
     *
     * @param inFixField an <code>int</code> value containing the FIX field tag
     * @param inValue a <code>T</code> value containing the FIX value
     * @throws IllegalArgumentException if <code>inFixField</code> is less than or equal to zero
     * @throws NullPointerException if <code>inValue</code> is null
     */
    protected FIXMatcher(int inFixField,
                         T inValue)
    {
        this(inFixField,
             inValue,
             true);
    }
    /**
     * Create a new FIXMatcher instance.
     *
     * @param inFixField an <code>int</code> value containing the FIX field tag
     * @param inValue a <code>T</code> value containing the FIX value
     * @param inShouldInclude a <code>boolean</code> value indicating whether the matcher should include or exclude
     *   the given criteria
     * @throws IllegalArgumentException if <code>inFixField</code> is less than or equal to zero
     * @throws NullPointerException if <code>inValue</code> is null
     */
    protected FIXMatcher(int inFixField,
                         T inValue,
                         boolean inShouldInclude)
    {
        validateField(inFixField);
        if(inValue == null) {
            throw new NullPointerException();
        }
        mMatcherFIXField = inFixField;
        mMatcherValue = inValue;
        mShouldInclude = inShouldInclude;
    }
    /**
     * Retrieves the specified field from the given FIX message as a <code>String</code>.
     *
     * @param inMessage a <code>Message</code> value
     * @param inFieldNum an <code>int</code> value
     * @return a <code>String</code> value
     * @throws FieldNotFound if the given <code>Message</code> does not contain the specified field
     */
    protected static String getFieldValueString(Message inMessage,
                                                int inFieldNum)
            throws FieldNotFound
    {
        validateMessage(inMessage);
        validateField(inFieldNum);
        DataDictionary dictionary = CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getDictionary();
        if (dictionary.isHeaderField(inFieldNum)) {
            return inMessage.getHeader().getString(inFieldNum);
        } else if (dictionary.isTrailerField(inFieldNum)) {
            return inMessage.getTrailer().getString(inFieldNum);
        } else {
            return inMessage.getString(inFieldNum);
        }
    }
    /**
     * Get the matcherFIXField value.
     * 
     * @return a <code>FIXMatcher</code> value
     */
    protected final int getMatcherFIXField()
    {
        return mMatcherFIXField;
    }
    /**
     * Get the matcherValue value.
     * 
     * @return a <code>FIXMatcher</code> value
     */
    protected final T getMatcherValue()
    {
        return mMatcherValue;
    }
    /**
     * Get the shouldInclude value.
     * 
     * @return a <code>FIXMatcher</code> value
     */
    protected final boolean getShouldInclude()
    {
        return mShouldInclude;
    }
    /**
     * Converts the given FIX-encoded value to a human-readable value, if appropriate.
     * 
     * <p>If the given FIX field is one of the fields for which Photon stores human-readable
     * translations, this method will return the human-readable translation for the given
     * value.  Note that the value returned is the corresponding entry in <code>photon_fix_messages.properties</code>.
     * 
     * @param inValue a <code>String</code> value containing the FIX value to convert
     * @param inFIXField an <code>int</code> value containing the FIX field corresponding to the given value
     * @return a <code>String</code> value containing the converted value 
     */
    protected static String convertFIXValueToHumanString(String inValue,
                                                         int inFIXField)
    {
        if(inValue == null) {
            throw new NullPointerException();
        }
        validateField(inFIXField);
        // check to see if there is a converter for the FIX field
        FIXConverter converter = sFIXConverters.get(inFIXField);
        if (converter == null) {
            // no converter is available, return the field as it was passed in
            return inValue;
        }
        // there *is* a converter available, so return the converted value
        return converter.convert(inValue);
    }
    /**
     * Validation routine for a <code>Message</code> parameter.
     *
     * @param inMessage a <code>Message</code> value
     * @throws NullPointerException if <code>inMessage</code> is null
     */
    private static void validateMessage(Message inMessage)
    {
        if(inMessage == null) {
            throw new NullPointerException();
        }
    }
    /**
     * Validation routine for an <code>int</code> parameter used as a FIX field.
     *
     * @param inFixField an <code>int</code> value
     * @throws IllegalArgumentException if <code>inFixField</code> is not valid
     */
    private static void validateField(int inFixField)
    {
        if(inFixField <= 0) {
            throw new IllegalArgumentException();
        }
    }
    /**
     * Converts a value to the short FIX value defined in the Photon FIX message catalog. 
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 0.7.0
     */
    @ClassVersion("$Id$")//$NON-NLS-1$
    private static class FIXConverter
    {
        /**
         * the name of the field - this is not localizable, but is merely the key in photon's FIX message catalog
         */
        private final String mFieldName;
        /**
         * the FIX field tag
         */
        private final int mFixField;
        /**
         * Create a new FIXConverter instance.
         *
         * @param inFieldName a <code>String</code> value
         * @param inFIXField an <code>int</code> value
         */
        private FIXConverter(String inFieldName,
                             int inFIXField)
        {
            mFieldName = inFieldName;
            mFixField = inFIXField;
        }
        /**
         * Converts the given value to the short FIX value, if possible.
         *
         * @param inValue a <code>String</code> value containing a FIX value
         * @return a <code>String</code> value
         */
        private String convert(String inValue)
        {
            String conversion = FIXFieldLocalizer.getLocalizedFIXValueName(mFieldName,
            		CurrentFIXDataDictionary.getCurrentFIXDataDictionary().getHumanFieldValue(mFixField,
                                                                                                                                                     inValue));
            // if the conversion comes up empty, that means that the converter exists, but there is no translation
            //  for this particular value.  that's ok, just return the passed value (this is what Photon would display)
            if(conversion == null) {
                return inValue;
            }
            return conversion;
        }
    }
}
