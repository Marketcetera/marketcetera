package org.marketcetera.web.converters;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import org.marketcetera.core.time.TimeFactoryImpl;
import org.marketcetera.util.time.DateService;

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
        implements Converter<String,LocalDateTime>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public LocalDateTime convertToModel(String inValue,
                                        Class<? extends LocalDateTime> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return new TimeFactoryImpl().create(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(LocalDateTime inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return TimeFactoryImpl.FULL_MILLISECONDS_LOCAL.print(DateService.toEpochMillis(inValue));
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<LocalDateTime> getModelType()
    {
        return LocalDateTime.class;
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
