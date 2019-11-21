package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.OrderType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link OrderType} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderTypeConverter
        implements Converter<String,OrderType>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public OrderType convertToModel(String inValue,
                                     Class<? extends OrderType> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return orderTypeTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(OrderType inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return orderTypeTranslations.get(inValue);
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
    public Class<OrderType> getModelType()
    {
        return OrderType.class;
    }
    /**
     * static instance for easy use
     */
    public static final OrderTypeConverter instance = new OrderTypeConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<OrderType,String> orderTypeTranslations = HashBiMap.create();
    static {
        for(OrderType OrderType : OrderType.values()) {
            String[] OrderTypeWords = PlatformServices.splitCamelCase(OrderType.name());
            StringBuilder builder = new StringBuilder();
            for(String word : OrderTypeWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            orderTypeTranslations.put(OrderType,
                                      value);
        }
    }
    private static final long serialVersionUID = 6529438901639648189L;
}
