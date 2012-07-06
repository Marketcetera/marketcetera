package org.marketcetera.core.ws.wrappers;

import java.util.Date;
import org.marketcetera.core.attributes.ClassVersion;

/**
 * A dual-form wrapper for marshalling a date. The raw form is {@link
 * Date}, and the marshalled form is {@link Long}. A wrapper is
 * necessary because date objects are not marshalled correctly under
 * certain time zones, including GMT.
 * 
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id: DateWrapper.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

@ClassVersion("$Id: DateWrapper.java 82324 2012-04-09 20:56:08Z colin $")
public class DateWrapper
    extends DualWrapper<Date,Long>
    implements Comparable<DateWrapper>
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;


    // CONSTRUCTORS.

    /**
     * Creates a new wrapper for the given date.
     *
     * @param date The date, which may be null.
     */

    public DateWrapper
        (Date date)
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
        setRawOnly(new Date(getMarshalled()));
    }
    
    @Override
    protected void toMarshalled()
    {
        setMarshalledOnly(getRaw().getTime());
    }


    // Comparable.

    @Override
    public int compareTo
        (DateWrapper other)
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
