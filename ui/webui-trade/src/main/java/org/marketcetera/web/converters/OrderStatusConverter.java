package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.OrderStatus;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link OrderStatus} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderStatusConverter
        implements Converter<String,OrderStatus>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public OrderStatus convertToModel(String inValue,
                                     Class<? extends OrderStatus> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return orderStatusTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(OrderStatus inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return orderStatusTranslations.get(inValue);
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
    public Class<OrderStatus> getModelType()
    {
        return OrderStatus.class;
    }
    /**
     * static instance for easy use
     */
    public static final OrderStatusConverter instance = new OrderStatusConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<OrderStatus,String> orderStatusTranslations = HashBiMap.create();
    static {
        for(OrderStatus orderStatus : OrderStatus.values()) {
            String[] orderStatusWords = PlatformServices.splitCamelCase(orderStatus.name());
            StringBuilder builder = new StringBuilder();
            for(String word : orderStatusWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            orderStatusTranslations.put(orderStatus,
                                        value);
        }
    }
    private static final long serialVersionUID = 9056150558403302763L;
}
