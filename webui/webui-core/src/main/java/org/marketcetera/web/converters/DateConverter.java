package org.marketcetera.web.converters;

import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.marketcetera.core.time.TimeFactoryImpl;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/* $License$ */

/**
 * Converts columns with a {@link Date} object for display.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DateConverter
        implements Converter<String,Date>
{
    /* (non-Javadoc)
     * @see com.vaadin.flow.data.converter.Converter#convertToModel(java.lang.Object, com.vaadin.flow.data.binder.ValueContext)
     */
    @Override
    public Result<Date> convertToModel(String inValue,
                                       ValueContext inContext)
    {
        try {
            return Result.ok(new TimeFactoryImpl().create(inValue).toDate());
        } catch (Exception e) {
            return Result.error(ExceptionUtils.getRootCauseMessage(e));
        }
    }
    /* (non-Javadoc)
     * @see com.vaadin.flow.data.converter.Converter#convertToPresentation(java.lang.Object, com.vaadin.flow.data.binder.ValueContext)
     */
    @Override
    public String convertToPresentation(Date inValue,
                                        ValueContext inContext)
    {
        return TimeFactoryImpl.FULL_MILLISECONDS_LOCAL.print(new DateTime(inValue.getTime()));
    }
    /**
     * static instance for easy use
     */
    public static final DateConverter instance = new DateConverter();
    private static final long serialVersionUID = -8354098730515883172L;
}
