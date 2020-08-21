package org.marketcetera.quickfix.customfields;

import java.time.LocalDateTime;
import java.util.Date;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.time.DateService;

import quickfix.UtcTimeStampField;

/**
 * Implementation of a custom field 9883: DateFrom.
 * 
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DateFrom
        extends UtcTimeStampField
{
    /**
     * Create a new DateFrom instance.
     */
    public DateFrom()
    {
        super(FIELD,
              LocalDateTime.now());
    }
    /**
     * Create a new DateFrom instance.
     *
     * @param inDate a <code>LocalDateTime</code> value
     */
    public DateFrom(LocalDateTime inDate)
    {
        super(FIELD,
              inDate);
    }
    /**
     * Create a new DateFrom instance.
     *
     * @param inDate a <code>Date</code> value
     * @deprecated Use {@link #DateFrom(LocalDateTime)}
     */
    @Deprecated
    public DateFrom(Date inDate)
    {
        super(FIELD,
              DateService.toLocalDateTime(inDate));
    }
    /**
     * FIX tag to use for this field
     */
    public static final int FIELD = 9883;
    private static final long serialVersionUID = -1637032148167115887L;
}
