package org.marketcetera.ui.service.admin;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.admin.UserAttribute;
import org.marketcetera.admin.UserAttributeType;
import org.marketcetera.core.QueueProcessor;
import org.marketcetera.core.Util;
import org.marketcetera.metrics.MetricService;
import org.marketcetera.ui.service.DisplayLayoutService;
import org.marketcetera.ui.service.ServiceManager;
import org.marketcetera.ui.service.SessionUser;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 * Provides display layout services for the admin client.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
@AutoConfiguration
public class AdminClientDisplayLayoutService
        implements DisplayLayoutService
{
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.DisplayLayoutService#getDisplayLayout()
     */
    @Override
    public Properties getDisplayLayout()
    {
        AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
        UserAttribute userAttribute = adminClientService.getUserAttribute(SessionUser.getCurrent().getUsername(),
                                                                          UserAttributeType.DISPLAY_LAYOUT);
        if(userAttribute == null) {
            return new Properties();
        } else {
            return Util.propertiesFromString(userAttribute.getAttribute());
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.service.DisplayLayoutService#setDisplayLayout(java.util.Properties)
     */
    @Override
    public void setDisplayLayout(Properties inDisplayLayout)
    {
        displayLayoutWorkQueue.add(new DisplayWorkoutUpdate(SessionUser.getCurrent().getUsername(),
                                                            UserAttributeType.DISPLAY_LAYOUT,
                                                            Util.propertiesToString(inDisplayLayout)));
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              getClass().getSimpleName());
        displayLayoutWorkQueue = new DisplayLayoutWorkQueue();
        displayLayoutWorkQueue.start();
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        SLF4JLoggerProxy.info(this,
                              "Stopping {}",
                              getClass().getSimpleName());
        try {
            displayLayoutWorkQueue.stop();
        } catch (Exception ignored) {
        } finally {
            displayLayoutWorkQueue = null;
        }
    }
    /**
     * Encapsulates the values necessary for a display update.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DisplayWorkoutUpdate
    {
        
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("DisplayWorkoutUpdate [username=").append(username).append(", userAttributeType=")
                    .append(userAttributeType).append(", value=").append(value).append("]");
            return builder.toString();
        }
        /**
         * Create a new DisplayWorkoutUpdate instance.
         *
         * @param inUsername a <code>String</code> value
         * @param inUserAttributeType a <code>UserAttributeType</code> value
         * @param inValue a <code>String</code> value
         */
        private DisplayWorkoutUpdate(String inUsername,
                                     UserAttributeType inUserAttributeType,
                                     String inValue)
        {
            username = inUsername;
            userAttributeType = inUserAttributeType;
            value = inValue;
        }
        /**
         * username which owns the attribute
         */
        private final String username;
        /**
         * attribute type
         */
        private final UserAttributeType userAttributeType;
        /**
         * attribute value
         */
        private final String value;
    }
    /**
     * Provides an async queue to process layout updates.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private class DisplayLayoutWorkQueue
            extends QueueProcessor<DisplayWorkoutUpdate>
    {
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#add(java.lang.Object)
         */
        @Override
        protected void add(DisplayWorkoutUpdate inData)
        {
            super.add(inData);
        }
        /* (non-Javadoc)
         * @see org.marketcetera.core.QueueProcessor#processData(java.lang.Object)
         */
        @Override
        protected void processData(DisplayWorkoutUpdate inData)
                throws Exception
        {
            try {
                long currentTimestamp = System.currentTimeMillis();
                long millisSinceLastUpdate = currentTimestamp - lastUpdateTimestamp;
                if(lastUpdateTimestamp != 0 && (millisSinceLastUpdate < displayLayoutUpdateDelay)) {
                    long delay = displayLayoutUpdateDelay - millisSinceLastUpdate;
                    SLF4JLoggerProxy.debug(AdminClientDisplayLayoutService.this,
                                           "Delaying next layout update {}ms as update delay is set to {}ms and it has been only {}ms since last update",
                                           delay,
                                           displayLayoutUpdateDelay,
                                           millisSinceLastUpdate);
                    Thread.sleep(delay);
                }
                DisplayWorkoutUpdate mostRecentUpdate = inData;
                if(!getQueue().isEmpty()) {
                    SLF4JLoggerProxy.debug(AdminClientDisplayLayoutService.this,
                                           "There are {} more entries in the queue",
                                           getQueue().size());
                    DisplayWorkoutUpdate updateCandidate = getQueue().poll();
                    while(updateCandidate != null) {
                        mostRecentUpdate = updateCandidate;
                        updateCandidate = getQueue().poll();
                    }
                    SLF4JLoggerProxy.debug(AdminClientDisplayLayoutService.this,
                                           "Using {} and there are {} more entries in the queue",
                                           mostRecentUpdate,
                                           getQueue().size());
                }
                // proceed to process the update only if the queue is empty, otherwise, allow ourselves to simply skip ahead to only the last entry
                AdminClientService adminClientService = serviceManager.getService(AdminClientService.class);
                adminClientService.setUserAttribute(mostRecentUpdate.username,
                                                    mostRecentUpdate.userAttributeType,
                                                    mostRecentUpdate.value);
            } catch (Exception e) {
                SLF4JLoggerProxy.warn(AdminClientDisplayLayoutService.this,
                                      e,
                                      "Skipping display layout update {}",
                                      inData);
            } finally {
                lastUpdateTimestamp = System.currentTimeMillis();
            }
        }
        /**
         * Create a new DisplayLayoutWorkQueue instance.
         */
        private DisplayLayoutWorkQueue()
        {
            super(AdminClientDisplayLayoutService.class.getSimpleName() + "-DisplayLayoutUpdateQueue");
        }
        /**
         * stores timestamp since last update
         */
        private long lastUpdateTimestamp = 0;
    }
    /**
     * forces construction of this service because the superclass of the queue needs it
     */
    @Autowired
    @SuppressWarnings("unused")
    private MetricService metricService;
    /**
     * processes display layouts asynchronously
     */
    private DisplayLayoutWorkQueue displayLayoutWorkQueue;
    /**
     * provides a configurable update minimum so that display updates will not be issued any more frequently than this value (in milliseconds), set to zero for no delay
     */
    @Value("${metc.display.layout.update.minimum:1000}")
    private long displayLayoutUpdateDelay;
    /**
     * provides access to client services
     */
    @Autowired
    private ServiceManager serviceManager;
}
