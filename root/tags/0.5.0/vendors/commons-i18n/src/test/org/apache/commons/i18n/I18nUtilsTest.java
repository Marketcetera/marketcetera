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

import junit.framework.TestCase;

import java.util.Locale;

/**
 * @author Mattias Jiderhamn
 */
public class I18nUtilsTest extends TestCase {
    public void testGetParentLocale() {
        assertEquals("Language, country and variant",
                new Locale("en", "GB"),
                I18nUtils.getParentLocale(new Locale("en", "GB", "scottish")));

        assertEquals("Language and country",
                Locale.ENGLISH,
                I18nUtils.getParentLocale(new Locale("en", "GB")));

        assertEquals("Language and variant",
                Locale.ENGLISH,
                I18nUtils.getParentLocale(new Locale("en", "", "scottish")));

        assertNull("Language only", I18nUtils.getParentLocale(Locale.ENGLISH));
    }
}
