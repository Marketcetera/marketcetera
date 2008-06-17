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

import java.util.Locale;
import java.util.Map;

import org.apache.commons.i18n.bundles.MessageBundle;

/**
 * @author Daniel Florey
 *
 */
public class ResourceBundleMessageProviderTest extends MessageProviderTestBase {
    public ResourceBundleMessageProviderTest(String testName) {
        super(testName);
    }

    public void testInstallResourceBundle() {
        MessageBundle testMessage = new MessageBundle("helloWorld");

        try {
            testMessage.getTitle(Locale.GERMAN);
            fail("ResourceBundle not installed, should throw exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }

        MessageManager.addMessageProvider("messageBundle", new ResourceBundleMessageProvider("messageBundle"));

        assertEquals("Hallo Welt", testMessage.getTitle(Locale.GERMAN));

        MessageManager.removeMessageProvider("messageBundle");

        try {
            testMessage.getTitle(Locale.GERMAN);
            fail("ResourceBundle uinstalled, should throw exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }
    }

    public void testGetText() {
        ResourceBundleMessageProvider rbmp = new ResourceBundleMessageProvider("messageBundle");

        super.testGetText(rbmp);

        // TODO: Wait for Daniels reply on whether this is intended
        assertEquals("Fallback when only in default", "This entry is not translated to any other languages",
                rbmp.getText("helloWorld", "notTranslated", Locale.GERMAN));

        // Test with list resource bundle
        ResourceBundleMessageProvider listResourceBundleProvider =
                new ResourceBundleMessageProvider("org.apache.commons.i18n.MyListResourceBundle"); // Install ListResourceBundle
        assertEquals("Value from ListResourceBundle", "listResourceValue", listResourceBundleProvider.getText("helloWorld", "title", Locale.US));
        assertEquals("Value from ListResourceBundle", "1", listResourceBundleProvider.getText("helloWorld", "text", Locale.US));

        try {
            new ResourceBundleMessageProvider("nonExistentBundle");
            fail("Bundle does not exist and should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Could not find resource bundle with base name nonExistentBundle, uninstalling it", mnfex.getMessage());
        }
    }

    public void testGetEntries() {
//        ResourceBundleMessageProvider.install("messageBundle");
        Map usEntries = new ResourceBundleMessageProvider("messageBundle").getEntries("helloWorld", Locale.US);
        assertEquals("Default locale, no of entries", 3, usEntries.size());
        assertEquals("Default locale, titel", "Hello World", usEntries.get("title"));
        assertEquals("Default locale, text", "Hello World, we are in {0}.", usEntries.get("text"));
        assertEquals("This entry is not translated to any other languages", usEntries.get("notTranslated"));

        Map germanEntries = new ResourceBundleMessageProvider("messageBundle").getEntries("helloWorld", Locale.GERMAN);
        assertEquals("No of entries", 3, germanEntries.size());
        assertEquals("Hallo Welt", germanEntries.get("title"));
        assertEquals("Hallo Welt, wir sind in {0}.", germanEntries.get("text"));
        assertEquals("This entry is not translated to any other languages", germanEntries.get("notTranslated"));

        Map frenchEntries = new ResourceBundleMessageProvider("messageBundle").getEntries("helloWorld", Locale.FRENCH);
        assertEquals("Fallback locale, no of entries", 3, frenchEntries.size());
        assertEquals("Fallback locale, titel", "Hello World", frenchEntries.get("title"));
        assertEquals("Fallback locale, text", "Hello World, we are in {0}.", frenchEntries.get("text"));
        assertEquals("This entry is not translated to any other languages", frenchEntries.get("notTranslated"));

        try {
            ResourceBundleMessageProvider provider = new ResourceBundleMessageProvider("messageBundle");
            provider.getEntries("fooBar", Locale.GERMAN);
            fail("Bundle does not exist and should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No message entries found for bundle with key fooBar", mnfex.getMessage());
        }

        try {
            new ResourceBundleMessageProvider("nonExistentBundle");
            fail("Bundle does not exist and should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("Could not find resource bundle with base name nonExistentBundle, uninstalling it", mnfex.getMessage());
        }
    }
}