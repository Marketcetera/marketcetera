package org.marketcetera.core;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

/* $License$ */

/**
 * Provides platform services for private/enterprise.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class PlatformServices
{
    /**
     * Determines if the given exception indicates a shutdown.
     *
     * @param inThrowable a <code>Throwable</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isShutdown(Throwable inThrowable)
    {
        if(inThrowable instanceof InterruptedException) {
            return true;
        }
        return false;
    }
    /**
     * Get a human-readable message describing the root cause of the given exception.
     *
     * @param inException a <code>Throwable</code> value
     * @return a <code>String</code> value
     */
    public static String getMessage(Throwable inException)
    {
        String message = ExceptionUtils.getRootCauseMessage(inException);
        if(message != null && message.contains(":")) {
            message = StringUtils.trimToNull(message.substring(message.indexOf(':')+1));
        }
        return message;
    }
    /**
     * Handle the given exception in a standardized fashion.
     *
     * @param inLoggingCategory an <code>Object</code> value
     * @param inMessage a <code>String</code> value describing what process was underway when the exception occurred
     * @param inException a <code>Throwable</code> value
     */
    public static void handleException(Object inLoggingCategory,
                                       String inMessage,
                                       Throwable inException)
    {
        if(isShutdown(inException)) {
            return;
        } else {
            String errorMessage = getMessage(inException);
            inMessage = inMessage + ": {}";
            if(SLF4JLoggerProxy.isDebugEnabled(inLoggingCategory)) {
                SLF4JLoggerProxy.warn(inLoggingCategory,
                                      inException,
                                      inMessage,
                                      errorMessage);
            } else {
                SLF4JLoggerProxy.warn(inLoggingCategory,
                                      inMessage,
                                      errorMessage);
            }
        }
    }
    /**
     * Generates a unique id.
     *
     * @return a <code>String</code> value
     */
    public static String generateId()
    {
        return UUID.randomUUID().toString();
    }
    /**
     * Autowires the given object with the default application context.
     * 
     * @param inTarget an <code>Object</code> value
     * @throws RuntimeException if the object cannot be autowired
     */
    public static void autowire(Object inTarget)
    {
        autowire(inTarget,
                 ApplicationContextProvider.getInstance().getApplicationContext());
    }
    /**
     * Autowires the given object.
     * 
     * @param inTarget an <code>Object</code> value
     * @param inApplicationContext an <code>ApplicationContext</code> value
     * @throws RuntimeException if the object cannot be autowired
     */
    public static void autowire(Object inTarget,
                                ApplicationContext inApplicationContext)
    {
        if(inApplicationContext == null) {
            throw new IllegalArgumentException("No application context");
        }
        AutowireCapableBeanFactory beanFactory = inApplicationContext.getAutowireCapableBeanFactory();
        SLF4JLoggerProxy.debug(PlatformServices.class,
                               "Autowiring {}",
                               inTarget);
        beanFactory.autowireBeanProperties(inTarget,
                                           AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,
                                           false);
        beanFactory.autowireBean(inTarget);
        SLF4JLoggerProxy.debug(PlatformServices.class,
                               "Autowiring {} complete",
                               inTarget);
    }
    /**
     * Create a new EnterprisePlatformServices instance.
     */
    private PlatformServices()
    {
        throw new UnsupportedOperationException();
    }
    /**
     * describes the style of the table cell
     */
    public static final CellStyle cellStyle = new CellStyle(HorizontalAlign.center);
    /**
     * rounding mode used in the platform
     */
    public static final RoundingMode roundingMode = RoundingMode.HALF_UP;
    /**
     * math context used for math operations
     */
    public static final MathContext divisionContext = new MathContext(6,
                                                                      roundingMode);
    /**
     * The scale used for storing all decimal values.
     */
    public static final int DECIMAL_SCALE = 7;
    /**
     * The precision used for storing all decimal values.
     */
    public static final int DECIMAL_PRECISION = 17;
    /**
     * constant representing one penny
     */
    public static final BigDecimal ONE_PENNY = new BigDecimal("0.01");
}
