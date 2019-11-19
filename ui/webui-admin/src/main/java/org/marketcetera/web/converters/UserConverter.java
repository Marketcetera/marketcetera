package org.marketcetera.web.converters;

import java.util.Locale;

import org.marketcetera.admin.User;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link User} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserConverter
        implements Converter<String,User>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public User convertToModel(String inValue,
                               Class<? extends User> inTargetType,
                               Locale inLocale)
            throws ConversionException
    {
        throw new UnsupportedOperationException(); // TODO
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(User inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return inValue.getName();
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<User> getModelType()
    {
        return User.class;
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
    public static final UserConverter instance = new UserConverter();
    private static final long serialVersionUID = -5955230114553357716L;
}
