package org.marketcetera.web.converters;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link quickfix.SessionID} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionIdConverter
        implements Converter<String,quickfix.SessionID>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public quickfix.SessionID convertToModel(String inValue,
                                             Class<? extends quickfix.SessionID> inTargetType,
                                             Locale inLocale)
            throws ConversionException
    {
        return new quickfix.SessionID(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(quickfix.SessionID inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
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
    public Class<quickfix.SessionID> getModelType()
    {
        return quickfix.SessionID.class;
    }
    /**
     * static instance for easy use
     */
    public static final SessionIdConverter instance = new SessionIdConverter();
    private static final long serialVersionUID = -1946823978104255352L;
}
