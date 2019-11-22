package org.marketcetera.web.converters;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.marketcetera.core.PlatformServices;
import org.marketcetera.trade.ExecutionType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link ExecutionType} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ExecutionTypeConverter
        implements Converter<String,ExecutionType>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public ExecutionType convertToModel(String inValue,
                                     Class<? extends ExecutionType> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return executionTypeTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(ExecutionType inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return executionTypeTranslations.get(inValue);
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
    public Class<ExecutionType> getModelType()
    {
        return ExecutionType.class;
    }
    /**
     * static instance for easy use
     */
    public static final ExecutionTypeConverter instance = new ExecutionTypeConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<ExecutionType,String> executionTypeTranslations = HashBiMap.create();
    static {
        for(ExecutionType executionType : ExecutionType.values()) {
            String[] executionTypeWords = PlatformServices.splitCamelCase(executionType.name());
            StringBuilder builder = new StringBuilder();
            for(String word : executionTypeWords) {
                builder.append(word).append(' ');
            }
            String value = StringUtils.trim(builder.toString());
            executionTypeTranslations.put(executionType,
                                          value);
        }
    }
    private static final long serialVersionUID = -8451895093073275101L;
}
