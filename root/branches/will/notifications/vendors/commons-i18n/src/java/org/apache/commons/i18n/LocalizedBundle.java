/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//i18n/src/java/org/apache/commons/i18n/LocalizedBundle.java,v 1.3 2004/12/29 17:03:55 dflorey Exp $
 * $Revision$
 * $Date: 2006-11-29 01:00:46 -0800 (Wed, 29 Nov 2006) $
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

import java.io.Serializable;
import java.util.Locale;

/**
 * 
 * <p>The <code>LocalizedBundle</code> class represents a bundle of localized messages that
 * belong together.</p>
 * <p> The <code>LocalizedBundle</code> class itself contains the message id and the arguments
 * that might be used to include dynamic values into the message text.
 * The message id specifies the base name of the message bundle.
 * Single entries of the message bundle can be retrieved using the <code>getText()</code> method by passing
 * the key of the desired message entry.</p>  
 * This class should not be used directly in order to retrieve entries of a message bundle. It is recommended
 * to subclass the <code>LocalizedBundle</code> class in order to define a specific localized bundle. 
 * @see org.apache.commons.i18n.bundles.TextBundle, MessageBundle, ErrorBundle
 */
public class LocalizedBundle implements Serializable {
    public final static String ID = "id";

    protected String providerId;
    protected String id;
    protected Object[] arguments;

  /**
   * @param providerId The name of the message provider (i.e. source) to use for the message
   * @param messageId The messageId refers the corresponding bundle in the file containing
   * the localized messages.
   */
  public LocalizedBundle(String providerId, String messageId) {
      this(providerId, messageId, new Object[0]);
  }

    /**
     * @param messageId The messageId refers the corresponding bundle in the file containing
     * the localized messages.
     */
    public LocalizedBundle(String messageId) {
        this(messageId, new Object[0]);
    }

    /**
     * @param messageId The messageId refers the corresponding bundle in the file containing
     * the localized messages.
     * @param arguments An array of objects containing arguments for the messages. These arguments
     * are used to insert dynamic values into the localized messages.
     */
    public LocalizedBundle(String messageId, Object[] arguments) {
        this.id = messageId;
        this.arguments = arguments;
    }

  /**
   * @param providerId The name of the message provider (i.e. source) to use for the message
   * @param messageId The messageId refers the corresponding bundle in the file containing
   * the localized messages.
   * @param arguments An array of objects containing arguments for the messages. These arguments
   * are used to insert dynamic values into the localized messages.
   */
  public LocalizedBundle(String providerId, String messageId, Object[] arguments) {
    this.providerId = providerId;
    this.id = messageId;
      this.arguments = arguments;
  }

    /**
     * @return returns the id of this bundle
     */
    public String getId() {
        return id;
    }

    /**
     * @return returns the arguments associated with this message bundle
     */
    public Object[] getArguments() {
    	return arguments;
    }

    /**
     * @return The name of the message provider (i.e. source) to use for the message  
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * @param key the key of the specific message entry in the message bundle
     * @param locale the locale for that this message should be rendered
     * @return returns the text of the desired message entry for the given locale  
     * @throws MessageNotFoundException if an entry with the given key can not be found
     * in this bundle
     */
    public String getEntry(String key, Locale locale) throws MessageNotFoundException {
        return providerId != null ?
            MessageManager.getText(providerId, id, key, arguments, locale) :
            MessageManager.getText(id, key, arguments, locale);
    }

    /**
     * @param key the key of the specific message entry in the message bundle
     * @param locale the locale for that this message should be rendered
     * @param defaultText the text to be returned if no entry was found for the given key
     * @return returns the text of the desired message entry for the given locale  
     */
    public String getEntry(String key, Locale locale, String defaultText) {
        return providerId != null ?
            MessageManager.getText(providerId, id, key, arguments, locale, defaultText) :
            MessageManager.getText(id, key, arguments, locale, defaultText);
    }
}