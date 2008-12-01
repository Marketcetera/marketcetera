package org.marketcetera.photon.messagehistory;

import static org.marketcetera.photon.Messages.MATCHER_FAILED;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;

import quickfix.FieldNotFound;
import quickfix.Message;
import ca.odell.glazedlists.matchers.Matcher;

/* $License$ */

/**
 * {@link Matcher} implementation that matches a <code>String</code> to a FIX message.
 * 
 * <p>The implementation of this class must be immutable to comply with the {@link Matcher}
 * contract.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class FIXStringMatcher
        extends FIXMatcher<String>
{
    /**
     * Create a new FIXStringMatcher instance.
     *
     * @param inFixField an <code>int</code> value containing the FIX field against which to match
     * @param inValue a <code>String</code> value containing the value to match
     */
    public FIXStringMatcher(int inFixField,
                            String inValue)
    {
        super(inFixField,
              inValue);
    }
    /**
     * Create a new FIXStringMatcher instance.
     *
     * @param inFixField an <code>int</code> value containing the FIX field against which to match
     * @param inValue a <code>String</code> value containing the value to match
     * @param inShouldInclude a <code>boolean</code> value indicating whether to match using logical
     *   set membership or its inverse
     */
    public FIXStringMatcher(int inFixField,
                            String inValue,
                            boolean inShouldInclude)
    {
        super(inFixField,
              inValue,
              inShouldInclude);
    }
    /* (non-Javadoc)
     * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
     */
    @Override
    public boolean matches(ReportHolder inEntry)
    {
        try {
            Message message = inEntry.getMessage();
            int matcherFIXField = getMatcherFIXField();
            String value = convertFIXValueToHumanString(getFieldValueString(message,
                                                                            matcherFIXField),
                                                                            matcherFIXField);
            return (!getShouldInclude()) ^ value.equals(getMatcherValue().toString());
        } catch (FieldNotFound e) {
            // do nothing here - it's ok if the field is not present            
        } catch (Throwable t) {
            MATCHER_FAILED.warn(this);
        }
        return false;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new StringBuffer().append("FIXStringMatcher matches field ").append(getMatcherFIXField()).append(" to ").append(getMatcherValue()).toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
