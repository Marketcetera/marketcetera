/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons-sandbox//i18n/src/java/org/apache/commons/i18n/MessageProvider.java,v 1.2 2004/12/29 17:03:55 dflorey Exp $
 * $Revision$
 * $Date: 2006-11-29 03:00:46 -0600 (Wed, 29 Nov 2006) $
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

import java.util.Locale;
import java.util.Map;

/**
 * The <code>MessageProvider</code> interface specifies the methods that
 * must be implemented by each message provider in order to be pluggable 
 * into the <code>MessageManager</code>.
 *
 */
public interface MessageProvider {
    /**
     * @param id unique id that specifies a particular message  
     * @param entry specifies a particular entry in the specified message 
     * @param locale the locale for which this text should be provided
     * @return returns the localized message entry matching the given message id, entry key and locale. If
     * no match is found for the given locale, the parent locale is used, and finally the default. If the
     * id is found but the entry is not, null is returned. 
     * @throws MessageNotFoundException thrown if no message exists matching the given id
     */
    public String getText(String id, String entry, Locale locale);
    
    /**
     * @param id unique id that specifies a particular message  
     * @param locale the locale for which to return the entries
     * @return returns a map <code>&lt;entry(String) -> localized text(String)<code> of 
     * message entries matching the given message id and locale 
     * @throws MessageNotFoundException thrown if no message could be found matching the given message id
     */
    public Map getEntries(String id, Locale locale) throws MessageNotFoundException;
}