package org.marketcetera.webui;

import org.marketcetera.eventbus.EventBusService;
import org.marketcetera.eventbus.guava.GuavaEventBusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
@EnableAutoConfiguration
@SpringBootConfiguration
public class Application
        extends SpringBootServletInitializer
{
    /**
     * Main application entry point.
     *
     * @param inArgs a <code>String[]</code> value
     */
    public static void main(String[] inArgs)
    {
        SpringApplication.run(Application.class,
                              inArgs);
    }
    /**
     * Get the event bus service bean.
     *
     * @return an <code>EventBusService</code> value
     */
    @Bean
    public EventBusService getEventBusService()
    {
        return new GuavaEventBusService();
    }
}
