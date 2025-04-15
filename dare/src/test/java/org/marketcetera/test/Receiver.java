package org.marketcetera.test;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Receives messages and responds as directed.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0-Milestone3
 */
public class Receiver
        extends AbstractMockFixApplication
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        super.start();
        try {
            FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
            socketAcceptor = new quickfix.SocketAcceptor(this,
                                                         fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                         sessionSettings,
                                                         fixSettingsProvider.getLogFactory(sessionSettings),
                                                         messageFactory);
            socketAcceptor.start();
        } catch (quickfix.ConfigError e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
        SLF4JLoggerProxy.info(this,
                              "Message receiver started");
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        if(socketAcceptor != null) {
            try {
                socketAcceptor.stop(true);
            } catch (Exception ignored) {
            }
            socketAcceptor = null;
        }
    }
    /**
     * accepts incoming FIX messages
     */
    private quickfix.SocketAcceptor socketAcceptor;
}
