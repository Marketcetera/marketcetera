package com.marketcetera.ors.filters;

/**
 * Indicates the operator to use in the comparison equation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: ConditionalMessageModifier.java 16529 2015-01-19 16:06:58Z colin $
 * @since 2.4.2
 */
public enum Operator
{
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
    public boolean evaluate(String inLhs,
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