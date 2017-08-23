package org.marketcetera;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import quickfix.MessageFactory;

/* $License$ */

/**
 * Provides test configuration for FIX server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ComponentScan
@SpringBootConfiguration
public class FixServerTestConfiguration
{
    /**
     * Get the message factory value.
     *
     * @return a <code>MessageFactory</code> value
     */
    @Bean
    public static MessageFactory getMessageFactory()
    {
        return new quickfix.DefaultMessageFactory();
    }
}
