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
package org.apache.commons.i18n.bundles;

import org.apache.commons.i18n.MockProviderTestBase;
import org.apache.commons.i18n.MessageNotFoundException;

import java.util.Locale;

/**
 * @author Mattias Jiderhamn
 */
public class MessageBundleTest extends MockProviderTestBase {
    public void testWithoutArguments() {
        MessageBundle mb = new MessageBundle("dummyId");
        try {
            mb.getText(Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }
        assertEquals("Default used", "defaultText", mb.getText(Locale.US, "defaultText"));

        addMockProvider();

        assertEquals("Normal use", getMockString("dummyId", MessageBundle.TEXT, Locale.US), mb.getText(Locale.US));
        assertEquals("Normal use", getMockString("dummyId", MessageBundle.TITLE, Locale.US), mb.getTitle(Locale.US));
        assertEquals("Default not used", getMockString("dummyId", MessageBundle.TEXT, Locale.US),
                mb.getText(Locale.US, "defaultText"));
        assertEquals("Default not used", getMockString("dummyId", MessageBundle.TITLE, Locale.US),
                mb.getTitle(Locale.US, "defaultText"));
    }

    public void testWithProvider() {
        addMockProvider(); // Add wrong provider
        MessageBundle mb = new MessageBundle("dummyProvider", "dummyId");
        try {
            mb.getText(Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Provider 'dummyProvider' not installed", mnfex.getMessage());
        }
        assertEquals("Default used", "defaultText", mb.getText(Locale.US, "defaultText"));

        addMockProvider("dummyProvider");

        assertEquals("Normal use", getMockString("dummyProvider", "dummyId", MessageBundle.TEXT, Locale.US),
                mb.getText(Locale.US));
        assertEquals("Normal use", getMockString("dummyProvider", "dummyId", MessageBundle.TITLE, Locale.US),
                mb.getTitle(Locale.US));
        assertEquals("Default not used", getMockString("dummyProvider", "dummyId", MessageBundle.TEXT, Locale.US),
                mb.getText(Locale.US, "defaultText"));
        assertEquals("Default not used", getMockString("dummyProvider", "dummyId", MessageBundle.TITLE, Locale.US),
                mb.getTitle(Locale.US, "defaultText"));
    }

    public void testWithArguments() {
        String[] arguments = new String[]{"arg1", "arg2"};
        MessageBundle mb = new MessageBundle("dummyId", arguments);
        try {
            mb.getText(Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }
        assertEquals("Default used", "defaultText arg1 arg2", mb.getText(Locale.US, "defaultText {0} {1}"));

        addMockProvider();

        assertEquals("Normal use", getFormattedMockString("dummyId", MessageBundle.TEXT, arguments, Locale.US),
                mb.getText(Locale.US));
        assertEquals("Normal use", getFormattedMockString("dummyId", MessageBundle.TITLE, arguments, Locale.US),
                mb.getTitle(Locale.US));
        assertEquals("Default not used", getFormattedMockString("dummyId", MessageBundle.TEXT, arguments, Locale.US),
                mb.getText(Locale.US, "defaultText"));
        assertEquals("Default not used", getFormattedMockString("dummyId", MessageBundle.TITLE, arguments, Locale.US),
                mb.getTitle(Locale.US, "defaultText"));
    }

    public void testWithProviderAndArguments() {
        addMockProvider(); // Add wrong provider
        String[] arguments = new String[]{"arg1", "arg2"};
        MessageBundle mb = new MessageBundle("dummyProvider", "dummyId", arguments);
        try {
            mb.getText(Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Provider 'dummyProvider' not installed", mnfex.getMessage());
        }
        assertEquals("Default used", "defaultText arg1 arg2", mb.getText(Locale.US, "defaultText {0} {1}"));

        addMockProvider("dummyProvider");

        assertEquals("Normal use",
                getFormattedMockString("dummyProvider", "dummyId", MessageBundle.TEXT, arguments, Locale.US),
                mb.getText(Locale.US));
        assertEquals("Normal use",
                getFormattedMockString("dummyProvider", "dummyId", MessageBundle.TITLE, arguments, Locale.US),
                mb.getTitle(Locale.US));
        assertEquals("Default not used",
                getFormattedMockString("dummyProvider", "dummyId", MessageBundle.TEXT, arguments, Locale.US),
                mb.getText(Locale.US, "defaultText"));
        assertEquals("Default not used",
                getFormattedMockString("dummyProvider", "dummyId", MessageBundle.TITLE, arguments, Locale.US),
                mb.getTitle(Locale.US, "defaultText"));
    }
}
