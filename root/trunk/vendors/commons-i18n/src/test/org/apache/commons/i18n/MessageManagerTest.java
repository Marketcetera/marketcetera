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

import java.util.Locale;
import java.util.Map;

/**
 * @author Mattias Jiderhamn
 */
public class MessageManagerTest extends MockProviderTestBase {

    /** Dummy to add to constructor to coverage report */
    public void testDummy() {
        new MessageManager();
    }

    public void testGetText() {
        assertEquals("Default text used", "defaultText",
                MessageManager.getText("dummyId", "dummyEntry", null, Locale.US, "defaultText"));
        assertEquals("Default text with arguments", "defaultText with value",
                MessageManager.getText("dummyId", "dummyEntry", new String[] {"with value"},
                        Locale.US, "defaultText {0}"));
        try {
            MessageManager.getText("dummyId", "dummyEntry", null, Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "No MessageProvider registered", mnfex.getMessage());
        }

        addThrowingMockProvider(); // Add mock provider always throwing exceptions

        try {
            MessageManager.getText("dummyId", "dummyEntry", null, Locale.US);
            fail("Mock provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            // TODO: What error message is most correct (here and elsewhere)?
            // assertEquals("Error text", "No message entries found for bundle with key dummyId", mnfex.getMessage());
            assertEquals("Error text", "Message bundle with key dummyId does not contain an entry with key dummyEntry", mnfex.getMessage());
        }

        addMockProvider(); // Add mock provider

        assertEquals("Throwing mock not used", "Source=mock Id=dummyId Entry=dummyEntry Locale=en_US",
                MessageManager.getText("dummyId", "dummyEntry", null, Locale.US, "defaultText"));

        removeThrowingMockProvider(); // Removing throwing mock and keep only normal mock

        assertEquals("Default text not used", "Source=mock Id=dummyId Entry=dummyEntry Locale=en_US",
                MessageManager.getText("dummyId", "dummyEntry", null, Locale.US, "defaultText"));

        assertEquals("Normal lookup", "Source=mock Id=id Entry=entry Locale=en_US",
                MessageManager.getText("id", "entry", null, Locale.US));
        assertEquals("Single argument",
                "Source=mock Id=id Entry=entry value1 Locale=en_US",
                MessageManager.getText("id", "entry {0}", new String[] {"value1"}, Locale.US));
        assertEquals("Multiple arguments",
                "Source=mock Id=id Entry=entry value0: value1 Locale=en_US",
                MessageManager.getText("id", "entry {0}: {1}", new String[] {"value0", "value1"},Locale.US));

        assertEquals("Single argument and default",
                "Source=mock Id=id Entry=entry value1 Locale=en_US",
                MessageManager.getText("id", "entry {0}", new String[] {"value1"},Locale.US, "defaultText"));

        // Named provider not found
        try {
            MessageManager.getText("mockProvider2", "dummyId", "dummyEntry", null, Locale.US);
            fail("Unknown provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Provider 'mockProvider2' not installed", mnfex.getMessage());
        }

        assertEquals("Default text used", "defaultText",
                MessageManager.getText("mockProvider2", "dummyId", "dummyEntry", null, Locale.US, "defaultText"));


        // Named provider found
        addMockProvider("mockProvider2");

        assertEquals("Default text not used, qualified lookup", "Source=mockProvider2 Id=dummyId Entry=dummyEntry Locale=en_US",
                MessageManager.getText("mockProvider2", "dummyId", "dummyEntry", null, Locale.US, "defaultText"));

        assertEquals("Normal qualified lookup", "Source=mockProvider2 Id=id Entry=entry Locale=en_US",
                MessageManager.getText("mockProvider2", "id", "entry", null, Locale.US));

        // Named provider found, but fails to find message
        addThrowingMockProvider(); // Add mock provider returning null for unknown entries
        try {
            MessageManager.getText("throwingMock", "dummyId", "dummyEntry", null, Locale.US);
            fail("Unknown provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Message bundle with key dummyId does not contain an entry with key dummyEntry", mnfex.getMessage());
        }

    }

    public void testGetEntries() {
        try {
            MessageManager.getEntries("dummyId", Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "No MessageProvider registered", mnfex.getMessage());
        }

        addThrowingMockProvider(); // Add mock provider always throwing exceptions

        try {
            MessageManager.getEntries("dummyId", Locale.US);
            fail("Mock provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Mock exception from getEntries()", mnfex.getMessage());
        }

        addMockProvider(); // Add mock provider

        Map entries = MessageManager.getEntries("dummyId", Locale.US);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Entry 1 match", "Source=mock Id=dummyId Entry=entry1 Locale=en_US", entries.get("entry1"));
        assertEquals("Entry 2 match", "Source=mock Id=dummyId Entry=entry2 Locale=en_US", entries.get("entry2"));

        removeThrowingMockProvider(); // Removing throwing mock and keep only normal mock

        addMockProvider(); // Add mock provider

        entries = MessageManager.getEntries("dummyId", Locale.US);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Entry 1 match", "Source=mock Id=dummyId Entry=entry1 Locale=en_US", entries.get("entry1"));
        assertEquals("Entry 2 match", "Source=mock Id=dummyId Entry=entry2 Locale=en_US", entries.get("entry2"));

        // Named provider not found
        try {
            MessageManager.getEntries("mockProvider2", "dummyId", Locale.US);
            fail("Unknown provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Provider 'mockProvider2' not installed", mnfex.getMessage());
        }

        // Named provider found
        addMockProvider("mockProvider2");

        entries = MessageManager.getEntries("mockProvider2", "dummyId", Locale.US);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Entry 1 match", "Source=mockProvider2 Id=dummyId Entry=entry1 Locale=en_US", entries.get("entry1"));
        assertEquals("Entry 2 match", "Source=mockProvider2 Id=dummyId Entry=entry2 Locale=en_US", entries.get("entry2"));
    }
}