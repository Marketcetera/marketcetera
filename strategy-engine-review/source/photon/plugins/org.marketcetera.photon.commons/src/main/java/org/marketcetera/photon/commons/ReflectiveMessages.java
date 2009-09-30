package org.marketcetera.photon.commons;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.marketcetera.util.log.I18NLoggerProxy;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage1P;
import org.marketcetera.util.log.I18NMessage2P;
import org.marketcetera.util.log.I18NMessage3P;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Experimental utility to aid construction of message constants. A typical
 * usage would be
 * 
 * <pre>
 * /**
 *  * Messages for this package.
 *  &#42;/
 * &#064;ClassVersion(&quot;&#36;Id$&quot;)
 * final class Messages {
 * 
 *     static I18NMessage0P MY_MESSAGE;
 *     static I18NMessage1P MY_OTHER_MESSAGE;
 * 
 *     static {
 *         ReflectiveMessages.init(Messages.class);
 *     }
 * }
 * </pre>
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public final class ReflectiveMessages {

    private static final String MESSAGE_ENTRY_SEPARATOR = "__"; //$NON-NLS-1$
    private static final String EXTENSION_METHOD_NAME = "initReflectiveMessages"; //$NON-NLS-1$

    /**
     * Initializes message constants for a class. This assumes a properties file
     * named "_messages.properties" in the same package as clazz. For each
     * static {@link I18NMessage} field in clazz, this method instantiates a
     * suitable instance. The message id and entry id is determined by the
     * following procedure:
     * <ul>
     * <li>convert the field name to lower case</li>
     * <li>if the result ends with "__xyz", use the string before it as the
     * message id, and "xyz" as the entry id</li>
     * <li>otherwise use the entire string as the messageId and
     * {@link I18NMessage#UNKNOWN_ENTRY_ID} as the entry id</li>
     * </ul>
     * <p>
     * This method also supports extension. If a field has a type Type that is
     * not assignable to {@link I18NMessage}, this method will attempt to
     * reflect on Type for a static "initReflectiveMessages" method with the
     * signature:
     * 
     * <pre>
     * Type initReflectiveMessages(String, I18NLoggerProxy)
     * </pre>
     * 
     * If found, it will invoke this method and set the field to the returned
     * Type object. Otherwise, the field will be ignored. An example of
     * extension can be found in
     * org.marketcetera.photon.commons.ui.LocalizedLabel.
     * 
     * @param clazz
     *            the class to initialize
     */
    public static void init(Class<?> clazz) {
        final String className = clazz.getName();
        try {
            String id = clazz.getPackage().getName().replaceAll("\\.", "/") + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            final I18NMessageProvider provider = new I18NMessageProvider(id,
                    clazz.getClassLoader());
            final I18NLoggerProxy logger = new I18NLoggerProxy(provider);
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                final String fieldName = field.getName();
                if ((field.getModifiers() & Modifier.STATIC) == 0) {
                    Messages.NONSTATIC_FIELD_IGNORED.info(
                            ReflectiveMessages.class, fieldName, className);
                    continue;
                }
                Class<?> type = field.getType();
                if (I18NMessage.class.isAssignableFrom(type)) {
                    String entryId = I18NMessage.UNKNOWN_ENTRY_ID;
                    String messageId = fieldName.toLowerCase();
                    String[] split = messageId.split(MESSAGE_ENTRY_SEPARATOR);
                    if (split.length > 1) {
                        String suffix = split[split.length - 1];
                        if (!suffix.isEmpty()) {
                            entryId = suffix;
                            messageId = messageId.substring(0, messageId
                                    .lastIndexOf(MESSAGE_ENTRY_SEPARATOR));
                        }
                    }
                    try {
                        Constructor<?> constructor = type.getConstructor(
                                I18NLoggerProxy.class, String.class,
                                String.class);
                        Object instance = constructor.newInstance(logger,
                                messageId, entryId);
                        field.set(null, instance);
                    } catch (Exception e) {
                        Messages.FAILED_TO_INITIALIZE_FIELD.error(
                                ReflectiveMessages.class, e, fieldName,
                                className);
                    } catch (ExceptionInInitializerError e) {
                        Messages.FAILED_TO_INITIALIZE_FIELD.error(
                                ReflectiveMessages.class, e, fieldName,
                                className);
                    }
                } else {
                    try {
                        Method initMethod = type.getDeclaredMethod(
                                EXTENSION_METHOD_NAME, String.class,
                                I18NLoggerProxy.class);
                        initMethod.setAccessible(true);
                        try {
                            Object instance = initMethod.invoke(null,
                                    fieldName, logger);
                            field.set(null, instance);
                        } catch (Exception e) {
                            Messages.FAILED_TO_INITIALIZE_FIELD.error(
                                    ReflectiveMessages.class, e, fieldName,
                                    className);
                        } catch (ExceptionInInitializerError e) {
                            Messages.FAILED_TO_INITIALIZE_FIELD.error(
                                    ReflectiveMessages.class, e, fieldName,
                                    className);
                        }
                    } catch (NoSuchMethodException e) {
                        Messages.UNSUPPORTED_FIELD_IGNORED.info(
                                ReflectiveMessages.class, e, type.getName(),
                                fieldName, className);
                    }
                }
            }
        } catch (Exception e) {
            Messages.FAILED_TO_INITIALIZE_CLASS.error(ReflectiveMessages.class,
                    e, className);
        }
    }

    private ReflectiveMessages() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }

    /**
     * Message constants used by {@link ReflectiveMessages}.
     */
    @ClassVersion("$Id$")
    private static class Messages {

        /**
         * The message provider
         */
        static final I18NMessageProvider PROVIDER = new I18NMessageProvider(
                "reflective_messages", Messages.class.getClassLoader()); //$NON-NLS-1$

        /**
         * The message logger.
         */
        static final I18NLoggerProxy LOGGER = new I18NLoggerProxy(PROVIDER);

        /**
         * The messages.
         */
        static final I18NMessage2P NONSTATIC_FIELD_IGNORED = new I18NMessage2P(
                LOGGER, "nonstatic_field_ignored"); //$NON-NLS-1$
        static final I18NMessage3P UNSUPPORTED_FIELD_IGNORED = new I18NMessage3P(
                LOGGER, "unsupported_field_ignored"); //$NON-NLS-1$
        static final I18NMessage2P FAILED_TO_INITIALIZE_FIELD = new I18NMessage2P(
                LOGGER, "failed_to_initialize_field"); //$NON-NLS-1$
        static final I18NMessage1P FAILED_TO_INITIALIZE_CLASS = new I18NMessage1P(
                LOGGER, "failed_to_initialize_class"); //$NON-NLS-1$
    }
}
