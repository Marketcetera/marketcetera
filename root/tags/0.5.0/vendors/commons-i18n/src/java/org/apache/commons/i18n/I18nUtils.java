/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class holds utility methods useful when working with i18n.
 * @author Mattias Jiderhamn
 */
public class I18nUtils {

    static final String INTERNAL_MESSAGE_NOT_FOUND = "Internal I18n error: Message not found";
    static final String MESSAGE_NOT_FOUND = "messageNotFound";
    static final String NO_MESSAGE_ENTRIES_FOUND = "noMessageEntriesFound";
    static final String MESSAGE_ENTRY_NOT_FOUND = "messageEntryNotFound";
    static final String RESOURCE_BUNDLE_NOT_FOUND = "resourceBundleNotFound";
    public static final String MESSAGE_PARSING_ERROR = "messageParsingError";

    public static final ResourceBundle INTERNAL_MESSAGES = ResourceBundle.getBundle("i18n-messages", Locale.getDefault());

    private I18nUtils() {
    }

    public static Locale getParentLocale (Locale locale) {
        if(locale.getVariant().length() != 0)
          return new Locale(locale.getLanguage(), locale.getCountry());
        else if(locale.getCountry().length() != 0)
            return new Locale(locale.getLanguage());
        else // Locale with only language have no parent
            return null;
    }
}
