package com.marketcetera.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* $License$ */

/**
 * Application entry point for the web UI.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootApplication
public class WebuiApplication
{
    /**
     * Main application entry.
     *
     * @param inArgs
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(WebuiApplication.class,
                              inArgs);
    }
}
