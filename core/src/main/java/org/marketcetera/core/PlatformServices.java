package org.marketcetera.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.marketcetera.symbol.SymbolResolverService;
import org.marketcetera.trade.Instrument;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.CellStyle.HorizontalAlign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstanceNotActiveException;

/* $License$ */

/**
 * Provides platform services for private/enterprise.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class PlatformServices
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        instance = this;
        SLF4JLoggerProxy.info(this,
                              "Starting {}",
                              getServiceName(getClass()));
    }
    public static String getFileChecksum(File inFile)
            throws NoSuchAlgorithmException, IOException
    {
        return getFileChecksum(MessageDigest.getInstance("SHA-256"),
                               inFile);
    }
    public static String getFileChecksum(MessageDigest inDigest,
                                         File inFile)
            throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(inFile);
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0; 
        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            inDigest.update(byteArray, 0, bytesCount);
        };
        //close the stream; We don't need it now.
        fis.close();
        //Get the hash's bytes
        byte[] bytes = inDigest.digest();
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        //return complete hash
        return sb.toString();
    }
    /**
     * Split the given value into its components where each component starts with a capital letter.
     *
     * @param inValue a <code>String</code> value
     * @return a <code>String[]</code> value
     */
    public static String[] splitCamelCase(String inValue)
    {
        if(inValue == null) {
            return new String[0];
        }
        return inValue.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
    }
    /**
     * Get a human-readable description of the given class.
     *
     * @param inClass a <code>Class&lt;?&gt;</code> value
     * @return a <code>String</code> value
     */
    public static String getServiceName(Class<?> inClass)
    {
        return Arrays.toString(StringUtils.splitByCharacterTypeCamelCase(inClass.getSimpleName())).replaceAll("\\[|\\]|,|(Impl)","");
    }
    /**
     * Determines if the given exception indicates a shutdown.
     *
     * @param inThrowable a <code>Throwable</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isShutdown(Throwable inThrowable)
    {
        if(inThrowable instanceof HazelcastInstanceNotActiveException) {
            return true;
        }
        if(inThrowable instanceof InterruptedException) {
            return true;
        }
        if(ExceptionUtils.getRootCause(inThrowable) instanceof HazelcastInstanceNotActiveException) {
            return true;
        }
        return ExceptionUtils.getFullStackTrace(inThrowable).contains(hazelcastInstanceIsNotActive);
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
     * Get the hostname value.
     *
     * @return a <code>String</code> value
     */
    public static String getHostname()
    {
        if(hostname == null) {
            hostname = "unknown host";
            try {
                hostname =InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ignored) {}
        }
        return hostname;
    }
    /**
     * Create a new EnterprisePlatformServices instance.
     * Get the instrument for the given full symbol.
     *
     * @param inFullSymbol a <code>String</code> value
     * @return an <code>Instrument</code> value
     */
    public static Instrument getInstrument(String inFullSymbol)
    {
        Validate.notNull(instance,
                         "Platform services must be initialized before use");
        return instance.symbolResolverService.resolveSymbol(inFullSymbol);
    }
    /**
     * provides symbol resolver services
     */
    @Autowired
    private SymbolResolverService symbolResolverService;
    /**
     * static instance
     */
    private static PlatformServices instance;
    /**
     * indicates that hazelcast is not active
     */
    private static final String hazelcastInstanceIsNotActive = "Hazelcast instance is not active"; //$NON-NLS-1$
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
    /**
     * hostname value
     */
    private static String hostname;
    public static final CellStyle cellStyleRightAlign = new CellStyle(HorizontalAlign.right);
    public static final CellStyle cellStyleLeftAlign = new CellStyle(HorizontalAlign.left);
    public static final CellStyle cellStyleCenterAlign = new CellStyle(HorizontalAlign.center);
}
