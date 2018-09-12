package com.marketcetera.quickfix;

import org.apache.commons.lang.Validate;

import quickfix.Log;
import quickfix.LogFactory;
import quickfix.SLF4JLogFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;

/* $License$ */

/**
 * Creates {@link RecordingLog} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class RecordingLogFactory
        implements LogFactory
{
    /* (non-Javadoc)
     * @see quickfix.LogFactory#create()
     */
    @Override
    public Log create()
    {
        // not supported
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see quickfix.LogFactory#create(quickfix.SessionID)
     */
    @Override
    public Log create(SessionID inSessionId)
    {
        synchronized(this) {
            if(configuration == null) {
                configuration = RecordingLogConfiguration.instance;
                Validate.notNull(configuration);
            }
        }
        configuration.setSessionSettings(sessionSettings);
        return new RecordingLog(inSessionId,
                                configuration,
                                embeddedLogFactory);
    }
    /**
     * Create a new RecordingLogFactory instance.
     *
     * @param inSessionSettings a <code>SessionSettings</code> value
     */
    public RecordingLogFactory(SessionSettings inSessionSettings)
    {
        sessionSettings = inSessionSettings;
        embeddedLogFactory = new SLF4JLogFactory(inSessionSettings);
    }
    /**
     * constructs an additional log factory to be used as a mix-in
     */
    private final SLF4JLogFactory embeddedLogFactory;
    /**
     * log configuration value
     */
    private RecordingLogConfiguration configuration;
    /**
     * session settings provided to construct FIX sessions
     */
    private final SessionSettings sessionSettings;
}
