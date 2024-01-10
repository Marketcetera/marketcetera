package org.marketcetera.test;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.marketcetera.fix.FixSettingsProvider;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Sends messages and listens for responses.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Sender
        extends AbstractMockFixApplication
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        super.start();
        initiators.clear();
        try {
            FixSettingsProvider fixSettingsProvider = fixSettingsProviderFactory.create();
            quickfix.ThreadedSocketInitiator initiator = new quickfix.ThreadedSocketInitiator(this,
                                                                                              fixSettingsProvider.getMessageStoreFactory(sessionSettings),
                                                                                              sessionSettings,
                                                                                              fixSettingsProvider.getLogFactory(sessionSettings),
                                                                                              messageFactory);
            initiator.start();
            initiators.add(initiator);
        } catch (quickfix.ConfigError e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            throw new RuntimeException(e);
        }
        SLF4JLoggerProxy.info(this,
                              "Message sender started");
    }
    /**
     * Stops the object.
     */
    @PreDestroy
    public void stop()
    {
        try {
            for(quickfix.ThreadedSocketInitiator initiator : initiators) {
                initiator.stop(true);
            }
        } catch (Exception ignored) {
        }
        initiators.clear();
    }
    /**
     * active initiators
     */
    private final List<quickfix.ThreadedSocketInitiator> initiators = new ArrayList<>();
}
