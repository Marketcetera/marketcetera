package org.marketcetera.web.converters;

import java.util.Locale;

import org.marketcetera.trade.Instrument;

import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link Instrument} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class InstrumentConverter
        implements Converter<String,Instrument>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public Instrument convertToModel(String inValue,
                                     Class<? extends Instrument> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        // TODO need to link in symbol resolver from applicationContext to achieve this - is it necessary?
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(Instrument inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return inValue.getFullSymbol();
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
    public Class<Instrument> getModelType()
    {
        return Instrument.class;
    }
    /**
     * static instance for easy use
     */
    public static final InstrumentConverter instance = new InstrumentConverter();
    private static final long serialVersionUID = 5977778028835571369L;
}
