package org.marketcetera.web.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

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
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public BigDecimal convertToModel(String inValue,
                                     Class<? extends BigDecimal> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return new BigDecimal(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(BigDecimal inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        if(BigDecimal.ZERO.compareTo(inValue) == 0) {
            return "0.00";
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
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<BigDecimal> getModelType()
    {
        return BigDecimal.class;
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getPresentationType()
     */
    @Override
    public Class<String> getPresentationType()
    {
        return String.class;
    }
    /**
     * static instance for easy use
     */
    public static final DecimalConverter instance = new DecimalConverter();
    private static final long serialVersionUID = 7344196492499105259L;
}
