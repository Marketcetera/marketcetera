/*
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

import junit.framework.TestCase;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;

/**
 * The <code>MockProviderTestBase</code> class serves as a base class for test cases using a mock
 * <code>MessageProvider</code>. After every test, it will remove the mock message provider to prepare
 * for other tests.
 * @author Mattias Jiderhamn
 */
public abstract class MockProviderTestBase extends TestCase {

    public void tearDown() {
        /* Remove mock provider after each test, to allow for MessageNotFoundExceptions */
        MessageManager.clearMessageProviders();
    }

    /**
     * Add mock provider to <code>MessageManager</code>.
     */
    protected void addMockProvider() {
        addMockProvider("mock");
    }

    /**
     * Add mock provider to <code>MessageManager</code>.
     */
    protected void addMockProvider(final String providerId) {
         //  Mock message provider that returns a string made up of the arguments passed to it.
        MessageProvider mockMessageProvider = new MessageProvider() {
            public String getText(String id, String entry, Locale locale) throws MessageNotFoundException {
                return MockProviderTestBase.getMockString(providerId, id, entry, locale);
            }

            public Map getEntries(String id, Locale locale) throws MessageNotFoundException {
                Map output = new HashMap();
                output.put("entry1", MockProviderTestBase.getMockString(providerId,id,"entry1",locale));
                output.put("entry2", MockProviderTestBase.getMockString(providerId,id,"entry2",locale));
                return output;
            }
        };

        MessageManager.addMessageProvider(providerId, mockMessageProvider);
    }

    /**
     * Add provider that always throws error to <code>MessageManager</code>.
     */
    protected void addThrowingMockProvider() {
        MessageManager.addMessageProvider("throwingMock", new MessageProvider() {
            public String getText(String id, String entry, Locale locale) throws MessageNotFoundException {
                return null;
            }

            public Map getEntries(String id, Locale locale) throws MessageNotFoundException {
                throw new MessageNotFoundException("Mock exception from getEntries()");
            }
        });
    }

    protected void removeThrowingMockProvider() {
        MessageManager.removeMessageProvider("throwingMock");
    }

    ////////////////////////////////////////////////////////////////////////
    // Utility methods
    ////////////////////////////////////////////////////////////////////////

    public static String getMockString(String providerId, String id, String entry, Locale locale) throws MessageNotFoundException {
        return ((providerId != null) ? "Source=" + providerId + " " : "") +
                "Id=" + id + " Entry=" + entry + " Locale=" + locale + "";
    }

    public static String getMockString(String id, String entry, Locale locale) throws MessageNotFoundException {
        return getMockString("mock", id, entry, locale);
    }

    public static String getFormattedMockString(String providerId, String id, String entry, String[] arguments, Locale locale) {
        return MessageFormat.format(getMockString(providerId, id, entry, locale), arguments);
    }

    public static String getFormattedMockString(String id, String entry, String[] arguments, Locale locale) {
        return MessageFormat.format(getMockString(id, entry, locale), arguments);
    }
}
