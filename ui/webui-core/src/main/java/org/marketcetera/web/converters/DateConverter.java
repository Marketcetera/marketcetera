package org.marketcetera.web.converters;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.marketcetera.core.time.TimeFactoryImpl;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link Date} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DateConverter
        implements Converter<String,Date>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public Date convertToModel(String inValue,
                               Class<? extends Date> inTargetType,
                               Locale inLocale)
            throws ConversionException
    {
        return new TimeFactoryImpl().create(inValue).toDate();
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(Date inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return TimeFactoryImpl.FULL_MILLISECONDS_LOCAL.print(new DateTime(inValue.getTime()));
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<Date> getModelType()
    {
        return Date.class;
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
    public static final DateConverter instance = new DateConverter();
    private static final long serialVersionUID = -8354098730515883172L;
}
