package org.marketcetera.marketdata.recorder;

import static org.marketcetera.core.time.TimeFactoryImpl.COLON;
import static org.marketcetera.core.time.TimeFactoryImpl.HOUR;
import static org.marketcetera.core.time.TimeFactoryImpl.MINUTE;
import static org.marketcetera.core.time.TimeFactoryImpl.SECOND;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
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
     * @param inTimestampGenerator a <code>TimestampGenerator</code> value
     */
    public void setTimestampGenerator(TimestampGenerator inTimestampGenerator)
    {
        timestampGenerator = inTimestampGenerator;
    }
    /**
     * Get the sessionResetTimestamp value.
     *
     * @return a <code>LocalDateTime</code> value
     */
    public LocalDateTime getSessionResetTimestamp()
    {
        if(sessionReset == null) {
            return null;
        }
        LocalDateTime tempValue = LocalDateTime.parse(sessionReset,
                                                      sessionResetFormatter);
        LocalDateTime sessionResetTimestamp = LocalDate.now().atStartOfDay().plusSeconds(tempValue.get(ChronoField.SECOND_OF_DAY));
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
     * @param inSessionReset a <code>String</code> value
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
