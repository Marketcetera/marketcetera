package org.marketcetera.web.converters;

import java.util.Locale;

import org.marketcetera.admin.User;
import org.marketcetera.trade.ReportType;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vaadin.data.util.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link User} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class ReportTypeConverter
        implements Converter<String,ReportType>
{
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public ReportType convertToModel(String inValue,
                                     Class<? extends ReportType> inTargetType,
                                     Locale inLocale)
            throws ConversionException
    {
        return reportTypeTranslations.inverse().get(inValue);
    }
    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(ReportType inValue,
                                        Class<? extends String> inTargetType,
                                        Locale inLocale)
            throws ConversionException
    {
        return reportTypeTranslations.get(inValue);
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
    public Class<ReportType> getModelType()
    {
        return ReportType.class;
    }
    /**
     * static instance for easy use
     */
    public static final ReportTypeConverter instance = new ReportTypeConverter();
    /**
     * human-readable translations to use
     */
    private static final BiMap<ReportType,String> reportTypeTranslations = HashBiMap.create();
    static {
        reportTypeTranslations.put(ReportType.CancelReject,
                                   "Cancel Reject");
        reportTypeTranslations.put(ReportType.ExecutionReport,
                                   "Execution Report");
    }
    private static final long serialVersionUID = -8063815694993601603L;
}
