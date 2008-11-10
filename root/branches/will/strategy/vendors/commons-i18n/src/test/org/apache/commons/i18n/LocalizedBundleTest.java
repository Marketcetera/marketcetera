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

/**
 * @author Mattias Jiderhamn
 */
public class LocalizedBundleTest extends MockProviderTestBase {
    public void testConstructors() {
        LocalizedBundle lb = new LocalizedBundle("dummyId1");
        assertNull("No provider", lb.getProviderId());
        assertEquals("Id set", "dummyId1", lb.getId());
        assertNotNull("Arguments not null", lb.getArguments());
        assertEquals("No arguments", 0, lb.getArguments().length);

        LocalizedBundle lbProvider = new LocalizedBundle("dummyProvider2", "dummyId2");
        assertEquals("Provider set", "dummyProvider2", lbProvider.getProviderId());
        assertEquals("Id set", "dummyId2", lbProvider.getId());
        assertNotNull("Arguments not null", lbProvider.getArguments());
        assertEquals("No arguments", 0, lbProvider.getArguments().length);

        String[] arguments = new String[]{"arg1", "arg2"};
        LocalizedBundle lbArgs = new LocalizedBundle("dummyId3", arguments);
        assertNull("No provider", lbArgs.getProviderId());
        assertEquals("Id set", "dummyId3", lbArgs.getId());
        assertNotNull("Arguments not null", lbArgs.getArguments());
        assertEquals("No of arguments", 2, lbArgs.getArguments().length);
        assertEquals("Arguments", arguments, lbArgs.getArguments());

        LocalizedBundle lbProviderArgs = new LocalizedBundle("dummyProvider4", "dummyId4", arguments);
        assertEquals("Provider set", "dummyProvider4", lbProviderArgs.getProviderId());
        assertEquals("Id set", "dummyId4", lbProviderArgs.getId());
        assertNotNull("Arguments not null", lbProviderArgs.getArguments());
        assertEquals("No of arguments", 2, lbProviderArgs.getArguments().length);
        assertEquals("Arguments", arguments, lbProviderArgs.getArguments());
    }

    public void testGetEntry() {
        LocalizedBundle lb = new LocalizedBundle("dummyId1");
        LocalizedBundle lbProvider = new LocalizedBundle("dummyProvider2", "dummyId2");
        LocalizedBundle lbArgs = new LocalizedBundle("dummyId3", new String[] {"arg1", "arg2"});
        LocalizedBundle lbProviderArgs = new LocalizedBundle("dummyProvider4", "dummyId4", new String[] {"arg1", "arg2"});

        // Test errors
        try {
            lb.getEntry("dummyEntry", Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "No MessageProvider registered", mnfex.getMessage());
        }
        try {
            lbArgs.getEntry("dummyEntry", Locale.US);
            fail("Entry not found should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "No MessageProvider registered", mnfex.getMessage());
        }

        // Test default texts
        assertEquals("Default text", "defaultText", lb.getEntry("dummyEntry", Locale.US, "defaultText"));
        assertEquals("Default text with arguments", "defaultText with arg1 arg2", lbArgs.getEntry("dummyEntry",
                        Locale.US, "defaultText with {0} {1}"));

        addMockProvider(); // Add mock provider

        assertEquals("Default text not used", getMockString("dummyId1", "dummyEntry", Locale.US),
                lb.getEntry("dummyEntry", Locale.US, "defaltText"));

        assertEquals("Normal lookup", getMockString("dummyId1", "entry", Locale.US), lb.getEntry("entry", Locale.US));
        assertEquals("Arguments missing", "Source=mock Id=dummyId1 Entry=entry {0} Locale=en_US",
                lb.getEntry("entry {0}", Locale.US));
        assertEquals("Argument", "Source=mock Id=dummyId3 Entry=entry arg1 arg2 Locale=en_US",
                lbArgs.getEntry("entry {0} {1}", Locale.US));
        assertEquals("Arguments and default", "Source=mock Id=dummyId3 Entry=entry arg1 arg2 Locale=en_US",
                lbArgs.getEntry("entry {0} {1}", Locale.US, "defaultText"));

        // Named provider not found
        try {
            lbProvider.getEntry("dummyEntry", Locale.US);
            fail("Unknown provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Provider 'dummyProvider2' not installed", mnfex.getMessage());
        }

        try {
            lbProviderArgs.getEntry("dummyEntry", Locale.US);
            fail("Unknown provider should throw Exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Error text", "Provider 'dummyProvider4' not installed", mnfex.getMessage());
        }

        assertEquals("Default text", "defaultText", lbProvider.getEntry("dummyEntry", Locale.US, "defaultText"));
        assertEquals("Default text", "defaultText", lbProviderArgs.getEntry("dummyEntry", Locale.US, "defaultText"));

        // Named provider found
        addMockProvider("dummyProvider2");
        addMockProvider("dummyProvider4");

        assertEquals("Named provider: Default text not used",
                getMockString("dummyProvider2", "dummyId2", "dummyEntry", Locale.US),
                lbProvider.getEntry("dummyEntry", Locale.US, "defaltText"));

        assertEquals("Named provider: Normal lookup", getMockString("dummyProvider2", "dummyId2", "entry", Locale.US),
                lbProvider.getEntry("entry", Locale.US));
        assertEquals("Named provider: Arguments missing", "Source=dummyProvider2 Id=dummyId2 Entry=entry {0} Locale=en_US",
                lbProvider.getEntry("entry {0}", Locale.US));
        assertEquals("Named provider: Argument", "Source=dummyProvider4 Id=dummyId4 Entry=entry arg1 arg2 Locale=en_US",
                lbProviderArgs.getEntry("entry {0} {1}", Locale.US));
        assertEquals("Named provider: Arguments and default",
                "Source=dummyProvider4 Id=dummyId4 Entry=entry arg1 arg2 Locale=en_US",
                lbProviderArgs.getEntry("entry {0} {1}", Locale.US, "defaultText"));
    }
}
