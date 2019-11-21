package org.marketcetera.web.converters;

import java.util.Locale;

import org.marketcetera.trade.OrderID;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link OrderID} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderIdConverter
        implements Converter<String,OrderID>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public OrderID convertToModel(String inValue,
                                  Class<? extends OrderID> inTargetType,
                                  Locale inLocale)
            throws ConversionException
    {
        if(inValue == null) {
            return null;
        }
        return new OrderID(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(OrderID inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        if(inValue == null) {
            return null;
        }
        return inValue.toString();
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
    public Class<OrderID> getModelType()
    {
        return OrderID.class;
    }
    /**
     * static instance for easy use
     */
    public static final OrderIdConverter instance = new OrderIdConverter();
    private static final long serialVersionUID = -2476201426228436499L;
}
