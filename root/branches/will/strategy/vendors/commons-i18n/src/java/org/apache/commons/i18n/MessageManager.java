/*
 *
 * ====================================================================
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.commons.i18n;

import java.text.MessageFormat;
import java.util.*;

/**
 * The <code>MessageManager</code> provides methods for retrieving localized
 * messages and adding custom message providers. 
 * This class should not be called directly for other purposes than registering a custom
 * {@link MessageProvider} or retrieving information about available
 * message entries.
 * <p>
 * To access localized messages a subclass of the {@link LocalizedBundle} class
 * such as <code>LocalizedText </code> should be used:
 * 
 * <pre>
 * LocalizedText welcome = new LocalizedText(&quot;welcome&quot;); 
 * // Get the german translacion of the retrieved welcome text 
 * System.out.println(welcome.getText(Locale.GERMAN));       
 * </pre>
 * 
 * <p>
 * You can call {@link MessageManager#getText(String,String,Object[],Locale) getText} directly,
 * but if you do so, you have to ensure that the given entry key really
 * exists and to deal with the {@link MessageNotFoundException} exception that will
 * be thrown if you try to access a not existing entry.</p>
 * 
 */
public class MessageManager {

    private static Map messageProviders = new LinkedHashMap();

    /**
     * Add a custom <code>{@link MessageProvider}</code> to the
     * <code>MessageManager</code>. It will be incorporated in later calls of
     * the {@link MessageManager#getText(String,String,Object[],Locale) getText}
     * or {@link #getEntries(String,Locale) getEntries}methods.
     *
     * @param providerId Id of the provider used for uninstallation and
     *          qualified naming.
     * @param messageProvider
     *            The <code>MessageProvider</code> to be added.
     */
    public static void addMessageProvider(String providerId, MessageProvider messageProvider) {
        messageProviders.put(providerId, messageProvider);
    }

    /**
     * Remove all <code>{@link MessageProvider}</code>s from the
     * <code>MessageManager</code>. Used for tearing down unit tests.
     */
    static void clearMessageProviders() {
        messageProviders.clear();
    }

    /**
     * Remove custom <code>{@link MessageProvider}</code> from the
     * <code>MessageManager</code>. Used for tearing down unit tests.
     *
     * @param providerId The ID of the provider to remove.
     */
    static void removeMessageProvider(String providerId) {
        messageProviders.remove(providerId);
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * entry in the requested message bundle.
     * 
     * @param id
     *            The identifier that will be used to retrieve the message
     *            bundle
     * @param entry
     *            The desired message entry
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @exception MessageNotFoundException
     *                Will be thrown if no message bundle can be found for the
     *                given id or if the desired message entry is missing in the
     *                retrieved bundle
     * @return The localized text
     */
    public static String getText(String id, String entry, Object[] arguments,
            Locale locale) throws MessageNotFoundException {
        if(messageProviders.isEmpty())
            throw new MessageNotFoundException("No MessageProvider registered");
        for (Iterator i = messageProviders.values().iterator(); i.hasNext();) {
            String text = ((MessageProvider) i.next()).getText(id, entry,
                    locale);
            if(text != null)
                return (arguments != null && arguments.length > 0) ?
                        formatMessage(text, arguments, locale) : text;
        }
        throw new MessageNotFoundException(MessageFormat.format(
                I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.MESSAGE_ENTRY_NOT_FOUND),
                new String[] { id, entry }));
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * entry in the requested message bundle.
     * 
     * @param id
     *            The identifier that will be used to retrieve the message
     *            bundle
     * @param entry
     *            The desired message entry
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @param defaultText
     *            If no message bundle or message entry could be found for the
     *            specified parameters, the default text will be returned.
     * @return The localized text or the default text if the message could not
     *         be found
     */
    public static String getText(String id, String entry, Object[] arguments,
            Locale locale, String defaultText) {
        try {
            return getText(id, entry, arguments, locale);
        } catch (MessageNotFoundException e) {
            return (arguments != null && arguments.length > 0) ?
                    formatMessage(defaultText, arguments, locale) : defaultText;
        }
    }

    /**
     * Tries to find the desired entry in the named message provider.
     * @param providerId The name of the message provider (i.e. source) to use for the message
     * @param id
     *            The identifier that will be used to retrieve the message
     *            bundle
     * @param entry
     *            The desired message entry
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @exception MessageNotFoundException
     *                Will be thrown if the requested message provider cannot be found or
     *                no message bundle can be found for the given id or if the desired
     *                message entry is missing in the retrieved bundle
     * @return The localized text
     */
    public static String getText(String providerId, String id, String entry, Object[] arguments,
            Locale locale) throws MessageNotFoundException {
        MessageProvider provider = (MessageProvider) messageProviders.get(providerId);
        if(provider == null)
            throw new MessageNotFoundException("Provider '" + providerId + "' not installed");
        String text = provider.getText(id, entry, locale);
        if(text != null)
            return (arguments != null && arguments.length > 0) ?
                    formatMessage(text, arguments, locale) : text;
        else
            throw new MessageNotFoundException(MessageFormat.format(
                    I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.MESSAGE_ENTRY_NOT_FOUND),
                    new String[] { id, entry }));
    }

    /**
     * Iterates over all registered message providers in order to find the given
     * entry in the requested message bundle.
     *
     * @param providerId The name of the message provider (i.e. source) to use for the message
     * @param id
     *            The identifier that will be used to retrieve the message
     *            bundle
     * @param entry
     *            The desired message entry
     * @param arguments
     *            The dynamic parts of the message that will be evaluated using
     *            the standard java text formatting abilities.
     * @param locale
     *            The locale in which the message will be printed
     * @param defaultText
     *            If no message bundle or message entry could be found for the
     *            specified parameters, the default text will be returned.
     * @return The localized text or the default text if the message could not
     *         be found
     */
    public static String getText(String providerId, String id, String entry, Object[] arguments,
            Locale locale, String defaultText) {
        try {
            return getText(providerId, id, entry, arguments, locale);
        } catch (MessageNotFoundException e) {
            return (arguments != null && arguments.length > 0) ?
                    formatMessage(defaultText, arguments, locale) : defaultText;
        }
    }

    /**
     * Returns a map containing all available message entries for the given
     * locale. The map contains keys of type {@link String}containing the keys
     * of the available message entries and values of type {@link String}
     * containing the localized message entries.
     */
    public static Map getEntries(String id, Locale locale)
            throws MessageNotFoundException {
        if(messageProviders.isEmpty())
            throw new MessageNotFoundException("No MessageProvider registered");
        MessageNotFoundException exception = null;
        for (Iterator i = messageProviders.values().iterator(); i.hasNext();) {
            try {
                return ((MessageProvider) i.next()).getEntries(id, locale);
            } catch (MessageNotFoundException e) {
                exception = e;
            }
        }
        throw exception;
    }

    /**
     * Formats the supplied message ensuring that the arguments are formatted
     * using formatters setup with the supplied locale.
     *
     * @param text
     *          the message text
     * @param arguments
     *          message arguments
     * @param locale
     *          the locale for message arguments
     * 
     * @return the localized text
     */
    private static String formatMessage(String text, Object[] arguments, Locale locale) {
        MessageFormat mf = new MessageFormat(text,locale);
        return mf.format(arguments);
    }

  /**
   * Returns a map containing all available message entries for the given
   * locale. The map contains keys of type {@link String}containing the keys
   * of the available message entries and values of type {@link String}
   * containing the localized message entries.
   */
  public static Map getEntries(String providerId, String id, Locale locale)
          throws MessageNotFoundException {
      MessageProvider provider = (MessageProvider) messageProviders.get(providerId);
      if(provider == null)
          throw new MessageNotFoundException("Provider '" + providerId + "' not installed");
      return provider.getEntries(id, locale);
  }
}