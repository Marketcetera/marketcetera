package org.marketcetera.ors.filters;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.CoreException;
import org.marketcetera.ors.history.ReportHistoryServices;
import org.marketcetera.quickfix.messagefactory.FIXMessageAugmentor;
import org.springframework.beans.factory.InitializingBean;

import quickfix.FieldNotFound;
import quickfix.Message;

/* $License$ */

/**
 * Modifies a message if a given condition is met.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ConditionalMessageModifier
        implements MessageModifier, InitializingBean
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
        if(inMessage.isSetField(comparisonTag)) {
            try {
                String lhs = inMessage.getString(comparisonTag);
                if(operator.evaluate(lhs,
                                     comparisonValue)) {
                    inMessage.setString(tag,
                                        value);
                    modified = true;
                }
            } catch (FieldNotFound e) {
                throw new CoreException(e);
            }
        }
        return modified;
    }
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet()
            throws Exception
    {
        Validate.notNull(operator);
        Validate.notNull(comparisonValue);
        Validate.isTrue(comparisonTag > 0);
        Validate.notNull(value);
        Validate.isTrue(tag > 0);
    }
    /**
     * Get the comparisonTag value.
     *
     * @return a <code>int</code> value
     */
    public int getComparisonTag()
    {
        return comparisonTag;
    }
    /**
     * Sets the comparisonTag value.
     *
     * @param inComparisonTag an <code>int</code> value
     */
    public void setComparisonTag(int inComparisonTag)
    {
        comparisonTag = inComparisonTag;
    }
    /**
     * Get the operator value.
     *
     * @return a <code>String</code> value
     */
    public String getOperator()
    {
        return operator.name();
    }
    /**
     * Sets the operator value.
     *
     * @param inOperator a <code>String</code> value
     */
    public void setOperator(String inOperator)
    {
        operator = Operator.valueOf(inOperator.toUpperCase());
    }
    /**
     * Get the comparisonValue value.
     *
     * @return a <code>String</code> value
     */
    public String getComparisonValue()
    {
        return comparisonValue;
    }
    /**
     * Sets the comparisonValue value.
     *
     * @param inComparisonValue a <code>String</code> value
     */
    public void setComparisonValue(String inComparisonValue)
    {
        comparisonValue = inComparisonValue;
    }
    /**
     * Get the tag value.
     *
     * @return a <code>int</code> value
     */
    public int getTag()
    {
        return tag;
    }
    /**
     * Sets the tag value.
     *
     * @param inTag an <code>int</code> value
     */
    public void setTag(int inTag)
    {
        tag = inTag;
    }
    /**
     * Get the value value.
     *
     * @return a <code>String</code> value
     */
    public String getValue()
    {
        return value;
    }
    /**
     * Sets the value value.
     *
     * @param inValue a <code>String</code> value
     */
    public void setValue(String inValue)
    {
        value = inValue;
    }
    /**
     * Indicates the operator to use in the comparison equation.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private enum Operator {
        LT,
        LE,
        EQ,
        GT,
        GE,
        NE;
        /**
         * Evaluates the equation.
         *
         * @param inLhs a <code>String</code> value
         * @param inRhs a <code>String</code> value
         * @return a <code>boolean</code> value
         */
        private boolean evaluate(String inLhs,
                                 String inRhs)
        {
            int comparisonResult = Integer.MIN_VALUE;
            Integer lhValue = null;
            Integer rhValue = null;
            try {
                lhValue = Integer.parseInt(inLhs);
                rhValue = Integer.parseInt(inRhs);
            } catch (RuntimeException ignored) {}
            if(lhValue != null && rhValue != null) {
                comparisonResult = lhValue.compareTo(rhValue);
            } else {
                comparisonResult = inLhs.compareTo(inRhs);
            }
            switch(this) {
                case EQ:
                    return comparisonResult == 0;
                case GE:
                    return comparisonResult != -1;
                case GT:
                    return comparisonResult == 1;
                case LE:
                    return comparisonResult != 1;
                case LT:
                    return comparisonResult == -1;
                case NE:
                    return comparisonResult != 0;
            }
            throw new UnsupportedOperationException();
        }
    }
    /**
     * tag to examine
     */
    private int comparisonTag = Integer.MIN_VALUE;
    /**
     * operator to use in the comparison
     */
    private Operator operator;
    /**
     * value to use in the comparison
     */
    private String comparisonValue;
    /**
     * tag to set if the equation evaluates to true
     */
    private int tag = -1;
    /**
     * value to set if the equation evaluates to true
     */
    private String value;
}
