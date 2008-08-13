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

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The <code>XMLMessageProvider</code> provides messages defined in an XML format.
 *  
 */
public class XMLMessageProvider implements MessageProvider {
    private static final Logger logger = Logger.getLogger(XMLMessageProvider.class.getName());

    private static SAXParserFactory factory = SAXParserFactory.newInstance();
    
    private Map messages = new HashMap();

    public XMLMessageProvider(InputStream inputStream) {
        try {
            Map applicationMessages = new HashMap();
            SAXParser parser = factory.newSAXParser();
            ConfigurationHandler handler = new ConfigurationHandler();
            parser.parse(new InputSource(inputStream), handler);
            Map parsedMessages = handler.getMessages();
            applicationMessages.putAll(parsedMessages);
            messages.putAll(applicationMessages);
        } catch (Exception exception) {
            String msg = I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.MESSAGE_PARSING_ERROR);
            logger.log(Level.SEVERE,msg,exception);
            throw new RuntimeException(msg, exception);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.i18n.MessageProvider#getText(java.lang.String, java.lang.String, java.util.Locale)
     */
    public String getText(String id, String entry, Locale locale) throws MessageNotFoundException {
        Message message = findMessage(id, locale);
        return message.getEntry(entry);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.i18n.MessageProvider#getEntries(java.lang.String, java.util.Locale)
     */
    public Map getEntries(String id, Locale locale) throws MessageNotFoundException {
        Message message = findMessage(id, locale);
        return message.getEntries();
    }

    private Message findMessage(String id, Locale locale) {
        Message message = lookupMessage(id, locale);
        if (message == null) {
            message = lookupMessage(id, Locale.getDefault());
        }
        if (message == null ) throw new MessageNotFoundException(
                MessageFormat.format(
                        I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.NO_MESSAGE_ENTRIES_FOUND),
                        new String[] { id }));
        return message;
    }

    private Message lookupMessage(String id, Locale locale) {
        String key = id + '_' + locale.toString();
        if (messages.containsKey(key)) return (Message)messages.get(key);
        locale = I18nUtils.getParentLocale(locale);
        while (locale != null) {
            key = id + '_' + locale.toString();
            if (messages.containsKey(key)) return (Message)messages.get(key);
            locale = I18nUtils.getParentLocale(locale);
        }
        return null;
    }

    class ConfigurationHandler extends DefaultHandler {
        private String id, key;
        private Message message;
        private StringBuffer cData;

        public void startElement(String namespaceUri, String localeName, String qName, Attributes attributes)  {
            if (qName.matches("message")) {
                id = attributes.getValue("id");
            } else if (qName.matches("locale")) {
                message = new Message(id);
                message.setLanguage(attributes.getValue("language"));
                message.setCountry(attributes.getValue("country"));
                message.setVariant(attributes.getValue("variant"));
            } else if (qName.matches("entry")) {
                key = attributes.getValue("key");
                cData = new StringBuffer();
            }
        }
        public void characters(char[] ch,
                int start,
                int length) {
            if ( message != null && key != null && length > 0 ) {
                cData.append(ch, start, length);
            }
        }
        
        public void endElement(String namespaceUri, String localeName, String qName) {
            if (qName.matches("locale")) {
                messages.put(message.getKey(), message);
            } else if (qName.matches("entry")) {
                message.addEntry(key, cData.toString());
                key = null;
            }
        }
        
        Map getMessages() {
            return messages;
        }
    }

    static class Message {
        private final String id;
        private String language, country, variant;
        private Map entries = new HashMap();

        public Message(String id) {
            this.id = id;
        }

        public void addEntry(String key, String value) {
            entries.put(key, value);
        }

        public String getEntry(String key) {
            return (String)entries.get(key);
        }

        public Map getEntries() {
            return entries;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setVariant(String variant) {
            this.variant = variant;
        }

        public String getKey() {
            return id + '_' + new Locale((language != null) ? language : "",
                    (country != null) ? country : "",
                    (variant != null) ? variant : "").toString();
        }
    }
}