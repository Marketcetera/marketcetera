package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.Side;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link Side} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SideConverter
        implements Converter<String,Side>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public Side convertToModel(String inValue,
                                     Class<? extends Side> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return sideTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(Side inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return sideTranslations.get(inValue);
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
    public Class<Side> getModelType()
    {
        return Side.class;
    }
    /**
     * static instance for easy use
     */
    public static final SideConverter instance = new SideConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<Side,String> sideTranslations = HashBiMap.create();
    static {
        for(Side side : Side.values()) {
            String[] sideWords = PlatformServices.splitCamelCase(side.name());
            StringBuilder builder = new StringBuilder();
            for(String word : sideWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            sideTranslations.put(side,
                                 value);
        }
    }
    private static final long serialVersionUID = -8451895093073275101L;
}
