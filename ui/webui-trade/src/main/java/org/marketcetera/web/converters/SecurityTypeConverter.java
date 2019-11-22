package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.SecurityType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link SecurityType} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SecurityTypeConverter
        implements Converter<String,SecurityType>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public SecurityType convertToModel(String inValue,
                                     Class<? extends SecurityType> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return securityTypeTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(SecurityType inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return securityTypeTranslations.get(inValue);
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
    public Class<SecurityType> getModelType()
    {
        return SecurityType.class;
    }
    /**
     * static instance for easy use
     */
    public static final SecurityTypeConverter instance = new SecurityTypeConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<SecurityType,String> securityTypeTranslations = HashBiMap.create();
    static {
        for(SecurityType securityType : SecurityType.values()) {
            String[] securityTypeWords = PlatformServices.splitCamelCase(securityType.name());
            StringBuilder builder = new StringBuilder();
            for(String word : securityTypeWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            securityTypeTranslations.put(securityType,
                                         value);
        }
    }
    private static final long serialVersionUID = 6108929260007287761L;
}
