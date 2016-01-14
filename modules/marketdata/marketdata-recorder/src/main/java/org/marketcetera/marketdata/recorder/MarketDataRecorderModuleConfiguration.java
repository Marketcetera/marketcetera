package org.marketcetera.marketdata.recorder;

import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.marketcetera.event.TimestampGenerator;

/* $License$ */

/**
 * Provides configuration for {@link MarketDataRecorderModule}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MarketDataRecorderModuleConfiguration
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(timestampGenerator,
                         Messages.TIMESTAMP_GENERATOR_REQUIRED.getText());
        try {
            getSessionResetTimestamp();
        } catch (RuntimeException e) {
            throw new IllegalArgumentException(Messages.SESSION_RESET_REQUIRED.getText(),
                                               e);
        }
    }
    /**
     * Get the timestampGenerator value.
     *
     * @return a <code>TimestampGenerator</code> value
     */
    public TimestampGenerator getTimestampGenerator()
    {
        return timestampGenerator;
    }
    /**
     * Sets the timestampGenerator value.
     *
     * @param a <code>TimestampGenerator</code> value
     */
    public void setTimestampGenerator(TimestampGenerator inTimestampGenerator)
    {
        timestampGenerator = inTimestampGenerator;
    }
    /**
     * Get the sessionResetTimestamp value.
     *
     * @return a <code>DateTime</code> value
     */
    public DateTime getSessionResetTimestamp()
    {
        if(sessionReset == null) {
            return null;
        }
        DateTime tempValue = sessionResetFormatter.parseDateTime(sessionReset);
        DateTime sessionResetTimestamp = new DateTime().withTimeAtStartOfDay().plusMillis(tempValue.getMillisOfDay());
        return sessionResetTimestamp;
    }
    /**
     * Get the sessionReset value.
     *
     * @return a <code>String</code> value
     */
    public String getSessionReset()
    {
        return sessionReset;
    }
    /**
     * Sets the sessionReset value.
     *
     * @param a <code>String</code> value
     */
    public void setSessionReset(String inSessionReset)
    {
        sessionReset = inSessionReset;
    }
    /**
     * generates timestamps
     */
    private TimestampGenerator timestampGenerator;
    /**
     * indicates the time time the session should reset
     */
    private String sessionReset;
    /**
     * used to parse the {@link #sessionReset} value
     */
    private static final DateTimeFormatter sessionResetFormatter = new DateTimeFormatterBuilder().append(HOUR).append(COLON).append(MINUTE).append(COLON).append(SECOND).toFormatter();
}
