package org.marketcetera.quickfix.customfields;

import java.time.LocalDateTime;
import java.util.Date;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.time.DateService;

import quickfix.UtcTimeStampField;

/**
 * Implementation of a custom field 9884: DateTo.
 *
 * @author toli
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DateTo
        extends UtcTimeStampField
{
    /**
     * Create a new DateTo instance.
     */
    public DateTo()
    {
        super(FIELD,
              java.time.LocalDateTime.now());
    }
    /**
     * Create a new DateTo instance.
     *
     * @param inData a <code>LocalDateTime</code> value
     */
    public DateTo(LocalDateTime inData)
    {
        super(FIELD,
              inData);
    }
    /**
     * Create a new DateTo instance.
     *
     * @param inData a <code>Date</code> value
     * @deprecated Use {@link #DateFrom(LocalDateTime)}
     */
    @Deprecated
    public DateTo(Date inData)
    {
        super(FIELD,
              DateService.toLocalDateTime(inData));
    }
    /**
     * FIX tag to use for this field
     */
    public static final int FIELD = 9884;
    private static final long serialVersionUID = 1718387157144895999L;
}
