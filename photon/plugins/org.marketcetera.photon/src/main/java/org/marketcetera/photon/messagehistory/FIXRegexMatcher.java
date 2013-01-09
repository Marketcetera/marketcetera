package org.marketcetera.photon.messagehistory;

import static org.marketcetera.photon.Messages.MATCHER_FAILED;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.messagehistory.ReportHolder;

import quickfix.Message;

/* $License$ */

/**
 * {@link Matcher} implementation that matches a <code>regex</code> to a FIX message.
 * 
 * <p>The implementation of this class must be immutable to comply with the {@link Matcher}
 * contract.
 * 
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.7.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class FIXRegexMatcher
        extends FIXMatcher<String>
{
    /**
     * the compiled pattern against which to match
     */
    private final Pattern mPattern;
    /**
     * Create a new FIXRegexMatcher instance.
     *
     * @param inFixField
     * @param inValue
     */
    public FIXRegexMatcher(int inFixField,
                           String inValue)
    {
        super(inFixField,
              inValue);
        mPattern = Pattern.compile(inValue);
    }
    /**
     * Create a new FIXRegexMatcher instance.
     *
     * @param inFixField
     * @param inValue
     * @param inInclude
     */
    public FIXRegexMatcher(int inFixField,
                           String inValue,
                           boolean inInclude)
    {
        super(inFixField,
              inValue,
              inInclude);
        mPattern = Pattern.compile(inValue);
    }
    /* (non-Javadoc)
     * @see ca.odell.glazedlists.matchers.Matcher#matches(java.lang.Object)
     */
    @Override
    public boolean matches(ReportHolder inItem)
    {
        try {
            Message aMessage = inItem.getMessage();
            int matcherFIXField = getMatcherFIXField();
            String value = convertFIXValueToHumanString(getFieldValueString(aMessage,
                                                                            matcherFIXField),
                                                                            matcherFIXField);
            Matcher matcher = mPattern.matcher(value);
            return (!getShouldInclude()) ^ matcher.matches();
        } catch (Throwable t) {
            MATCHER_FAILED.warn(this);
        }
        return false;
    }
}
