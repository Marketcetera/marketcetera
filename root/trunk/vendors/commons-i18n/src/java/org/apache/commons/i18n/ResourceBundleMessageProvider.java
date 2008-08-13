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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The <code>ResourceBundleMessageProvider</code> deals with messages defined in 
 * resource bundles. Messages defined in resource bundles can be grouped together
 * by adding the entry key at the end of the message key separated by a dot.
 */
public class ResourceBundleMessageProvider implements MessageProvider {
    private static Logger logger = Logger.getLogger(ResourceBundleMessageProvider.class.getName());

    private final String baseName;

    /**
     *
     * @throws MessageNotFoundException Thrown if the resource bundle does not exist.
     */
    public ResourceBundleMessageProvider(String baseName) throws MessageNotFoundException {
        this.baseName = baseName;
        // Test if resource exists
        try {
            ResourceBundle.getBundle(baseName);
        }
        catch ( MissingResourceException e ) {
            String msg = MessageFormat.format(
                    I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.RESOURCE_BUNDLE_NOT_FOUND),
                    new String[]{baseName});
            logger.log(Level.WARNING, msg);
            throw new MessageNotFoundException(msg);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.i18n.MessageProvider#getText(java.lang.String, java.lang.String, java.util.Locale)
     */
    public String getText(String id, String entry, Locale locale) {
        try {
            ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
             return resourceBundle.getObject(id+"."+entry).toString();
        }
        catch ( MissingResourceException e ) {
            logger.log(
                    Level.WARNING,
                    MessageFormat.format(
                            I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.NO_MESSAGE_ENTRIES_FOUND),
                            new String[] { id }));
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.i18n.MessageProvider#getEntries(java.lang.String, java.util.Locale)
     */
    public Map getEntries(String id, Locale locale) {
        String messageIdentifier = id+".";
        Map entries = null;
        ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, locale);
        Enumeration keys = resourceBundle.getKeys();
        while ( keys.hasMoreElements() ) {
            String key = (String)keys.nextElement();
            if ( key.startsWith(messageIdentifier) ) {
                if ( entries == null ) {
                    entries = new HashMap();
                }
                entries.put(key.substring(messageIdentifier.length()), resourceBundle.getString(key));
            }
        }
        if ( entries == null ) {
            throw new MessageNotFoundException(MessageFormat.format(
                    I18nUtils.INTERNAL_MESSAGES.getString(I18nUtils.NO_MESSAGE_ENTRIES_FOUND),
                    new String[] { id })); 
        }
        return entries;
    }
}