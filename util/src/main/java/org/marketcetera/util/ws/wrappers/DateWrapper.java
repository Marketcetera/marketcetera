package org.marketcetera.util.ws.wrappers;

import java.time.LocalDateTime;
import java.util.Date;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.time.DateService;

/* $License$ */

/**
 * A dual-form wrapper for marshalling a date. The raw form is {@link
 * Date}, and the marshalled form is {@link Long}. A wrapper is
 * necessary because date objects are not marshalled correctly under
 * certain time zones, including GMT.
 * 
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.5.0
 * @version $Id$
 */
@ClassVersion("$Id$")
public class DateWrapper
        extends DualWrapper<LocalDateTime,Long>
        implements Comparable<DateWrapper>
{
    /**
     * Create a new DateWrapper instance.
     *
     * @param inDate a <code>Date</code> value
     */
    public DateWrapper(Date inDate)
    {
        super(DateService.toLocalDateTime(inDate));
    }
    /**
     * Creates a new wrapper for the given date.
     *
     * @param date The date, which may be null.
     */
    public DateWrapper(LocalDateTime date)
    {
        super(date);
    }
    /**
     * Creates a new wrapper. This empty constructor is intended for
     * use by JAXB and Java serialization.
     */
    public DateWrapper() {}
    // DualWrapper.
    @Override
    protected void toRaw()
    {
        setRawOnly(DateService.toLocalDateTime(getMarshalled()));
    }
    @Override
    protected void toMarshalled()
    {
        setMarshalledOnly(DateService.toEpochMillis(getRaw()));
    }
    // Comparable.
    @Override
    public int compareTo(DateWrapper other)
    {
        if (getRaw()==null) {
            throw new NullPointerException
                (Messages.RECEIVER_WRAPS_NULL.getText());
        }
        if (other==null) {
            throw new NullPointerException
                (Messages.ARGUMENT_IS_NULL.getText());
        }
        if (other.getRaw()==null) {
            throw new NullPointerException
                (Messages.ARGUMENT_WRAPS_NULL.getText());
        }
        return getRaw().compareTo(other.getRaw());
    }
}
