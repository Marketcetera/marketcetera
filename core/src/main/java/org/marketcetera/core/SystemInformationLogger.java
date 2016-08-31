package org.marketcetera.core;

import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.ws.stateful.PortDescriptor;
import org.marketcetera.util.ws.stateful.UsesPort;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Polls and logs system information.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SystemInformationLogger
        implements ApplicationContextAware
{
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inApplicationContext)
            throws BeansException
    {
        applicationContext = inApplicationContext;
    }
    /**
     * Get the pollingInterval value.
     *
     * @return a <code>long</code> value
     */
    public long getPollingInterval()
    {
        return pollingInterval;
    }
    /**
     * Sets the pollingInterval value.
     *
     * @param inPollingInterval a <code>long</code> value
     */
    public void setPollingInterval(long inPollingInterval)
    {
        pollingInterval = inPollingInterval;
    }
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        pollingExecutor = Executors.newSingleThreadScheduledExecutor();
        pollingExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    evaluate();
                } catch (Exception e) {
                    SLF4JLoggerProxy.warn(SystemInformationLogger.this,
                                          e);
                }
            }
        },pollingInterval,pollingInterval,TimeUnit.MILLISECONDS);
    }
    /**
     * Stop the object.
     */
    @PreDestroy
    public void stop()
    {
        if(pollingExecutor != null) {
            try {
                pollingExecutor.shutdownNow();
            } catch (Exception ignored) {}
            pollingExecutor = null;
        }
    }
    /**
     * Determines if system information has changed and publishes, if necessary.
     */
    private void evaluate()
    {
        if(applicationContext == null) {
            return;
        }
        Map<String,UsesPort> portUsersMap = applicationContext.getBeansOfType(UsesPort.class);
        SortedSet<PortDescriptor> newPortUsers = Sets.newTreeSet();
        for(Map.Entry<String,UsesPort> entry : portUsersMap.entrySet()) {
            UsesPort portUser = entry.getValue();
            newPortUsers.addAll(portUser.getPortDescriptors());
        }
        synchronized(portUsers) {
            if(portUsers.equals(newPortUsers)) {
                SLF4JLoggerProxy.debug(this,
                                       "No change in port users"); //$NON-NLS-1$
            } else {
                portUsers.clear();
                portUsers.addAll(newPortUsers);
                logPortUsers();
            }
        }
    }
    /**
     * Log port user information.
     */
    private void logPortUsers()
    {
        Table table = new Table(2,
                                BorderStyle.CLASSIC_COMPATIBLE_WIDE,
                                ShownBorders.ALL,
                                false);
        table.addCell(Messages.PORTS_IN_USE.getText(),
                      cellStyle,
                      2);
        table.addCell(Messages.PORT.getText(),
                      cellStyle);
        table.addCell(Messages.DESCRIPTION.getText(),
                      cellStyle);
        for(PortDescriptor portDescriptor : portUsers) {
            table.addCell(String.valueOf(portDescriptor.getPort()),
                          cellStyle);
            table.addCell(portDescriptor.getDescription(),
                          cellStyle);
        }
        SLF4JLoggerProxy.info(this,
                              "{}{}", //$NON-NLS-1$
                              System.lineSeparator(),
                              table.render());
    }
    /**
     * 
     */
    private final SortedSet<PortDescriptor> portUsers = Sets.newTreeSet();
    /**
     * service used to poll for ports
     */
    private ScheduledExecutorService pollingExecutor;
    /**
     * polling interval value
     */
    private long pollingInterval = 10000;
    /**
     * application context value
     */
    private ApplicationContext applicationContext;
    /**
     * describes the style of the table cell
     */
    private static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
}
