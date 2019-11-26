package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.TimeInForce;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link TimeInForce} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class TimeInForceConverter
        implements Converter<String,TimeInForce>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public TimeInForce convertToModel(String inValue,
                                     Class<? extends TimeInForce> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return timeInForceTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(TimeInForce inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return timeInForceTranslations.get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getPresentationType()
     */
    @Override
    public Class<String> getPresentationType()
    {
        return String.class;
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<TimeInForce> getModelType()
    {
        return TimeInForce.class;
    }
    /**
     * static instance for easy use
     */
    public static final TimeInForceConverter instance = new TimeInForceConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<TimeInForce,String> timeInForceTranslations = HashBiMap.create();
    static {
        for(TimeInForce timeInForce : TimeInForce.values()) {
            String[] timeInForceWords = PlatformServices.splitCamelCase(timeInForce.name());
            StringBuilder builder = new StringBuilder();
            for(String word : timeInForceWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            timeInForceTranslations.put(timeInForce,
                                        value);
        }
    }
    private static final long serialVersionUID = 3087292199696576480L;
}
