package org.marketcetera.web.converters;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link quickfix.Message} object (in a String) for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class StringFixMessageConverter
        implements Converter<String,String>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToModel(String inValue,
                                 Class<? extends String> inTargetType,
                                 Locale inLocale)
            throws ConversionException
    {
        return inValue;
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(String inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        /*
         * Credit to Lokesh Gupta https://howtodoinjava.com/regex/java-clean-ascii-text-non-printable-chars/
         */
        // strips off all non-ASCII characters
        inValue = inValue.replaceAll("[^\\x00-\\x7F]",
                                     " ");
        // erases all the ASCII control characters
        inValue = inValue.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]",
                                     " ");
        // removes non-printable characters from Unicode
        inValue = inValue.replaceAll("\\p{C}",
                                     " ");
        return inValue.trim();
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<String> getModelType()
    {
        return String.class;
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
    public static final StringFixMessageConverter instance = new StringFixMessageConverter();
    private static final long serialVersionUID = -1583810239588477974L;
}
