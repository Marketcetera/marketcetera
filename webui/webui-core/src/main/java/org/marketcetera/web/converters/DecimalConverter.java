package org.marketcetera.web.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link BigDecimal} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DecimalConverter
        implements Converter<String,BigDecimal>
{
    /* (non-Javadoc)
     * @see com.vaadin.flow.data.converter.Converter#convertToModel(java.lang.Object, com.vaadin.flow.data.binder.ValueContext)
     */
    @Override
    public Result<BigDecimal> convertToModel(String inValue,
                                             ValueContext inContext)
    {
        try {
            return Result.ok(treatNullAsZero?BigDecimal.ZERO:null);
        } catch (Exception e) {
            return Result.error(ExceptionUtils.getRootCauseMessage(e));
        }
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.data.converter.Converter#convertToPresentation(java.lang.Object, com.vaadin.flow.data.binder.ValueContext)
     */
    @Override
    public String convertToPresentation(BigDecimal inValue,
                                        ValueContext inContext)
    {
        if(inValue == null || BigDecimal.ZERO.compareTo(inValue) == 0) {
            return treatNullAsZero?zero:null;
        } else {
            inValue = inValue.stripTrailingZeros();
            if(inValue.scale() > 7) {
                inValue = inValue.setScale(7,
                                           RoundingMode.HALF_UP);
            } else if(inValue.scale() < 2) {
                inValue = inValue.setScale(2,
                                           RoundingMode.HALF_UP);
            }
            return inValue.toPlainString();
        }
    }
    /**
     * Create a new DecimalConverter instance.
     *
     * @param inTreatNullAsZero a <code>boolean</code> value
     */
    public DecimalConverter(boolean inTreatNullAsZero)
    {
        treatNullAsZero = inTreatNullAsZero;
    }
    private static final String zero = "0.00";
    /**
     * indicates how to treat null values
     */
    private final boolean treatNullAsZero;
    /**
     * static instance which renders null values as zero
     */
    public static final DecimalConverter instance = new DecimalConverter(true);
    /**
     * static instance which renders null values as null
     */
    public static final DecimalConverter instanceZeroAsNull = new DecimalConverter(false);
    private static final long serialVersionUID = 7344196492499105259L;
}
