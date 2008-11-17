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
import java.util.Properties;
import java.util.HashMap;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

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

    /**
     * Tests resource bundle loading with a specific classloader.
     */
    public void testClassLoader() {
        //Test that bundle is not available in the default classloader
        String bundleName="classBundle";
        try {
            new ResourceBundleMessageProvider(bundleName);
            fail("Should fail with bundle not exist error");
        } catch (MessageNotFoundException e) {
            assertEquals("Could not find resource bundle with base name classBundle, uninstalling it", e.getMessage());
        }
        try {
            new ResourceBundleMessageProvider(bundleName, null);
            fail("Should fail with bundle not exist error");
        } catch (MessageNotFoundException e) {
            assertEquals("Could not find resource bundle with base name classBundle, uninstalling it", e.getMessage());
        }
        //Create a new classloader with the bundle.
        Properties p = new Properties();
        p.put("helloWorld.title","Hello World");
        p.put("helloWorld.text","Hello World, we are in {0}.");
        p.put("helloWorld.notTranslated","This entry is not translated to any other languages");
        Properties pDE = new Properties();
        pDE.put("helloWorld.title","Hallo Welt");
        pDE.put("helloWorld.text","Hallo Welt, wir sind in {0}.");
        DynamicResourceLoader loader = new DynamicResourceLoader();
        loader.addResource(bundleName + ".properties", p);
        loader.addResource(bundleName + "_de.properties", pDE);
        //Create a provider with the classloader
        ResourceBundleMessageProvider provider =
                new ResourceBundleMessageProvider(bundleName, loader);
        //Test get text for messages
        super.testGetText(provider);
        //Test Entries
        Map usEntries = provider.getEntries("helloWorld", Locale.US);
        assertEquals("Default locale, no of entries", 3, usEntries.size());
        assertEquals("Default locale, titel", "Hello World", usEntries.get("title"));
        assertEquals("Default locale, text", "Hello World, we are in {0}.", usEntries.get("text"));
        assertEquals("This entry is not translated to any other languages", usEntries.get("notTranslated"));

        Map germanEntries = provider.getEntries("helloWorld", Locale.GERMAN);
        assertEquals("No of entries", 3, germanEntries.size());
        assertEquals("Hallo Welt", germanEntries.get("title"));
        assertEquals("Hallo Welt, wir sind in {0}.", germanEntries.get("text"));
        assertEquals("This entry is not translated to any other languages", germanEntries.get("notTranslated"));
        try {
            provider.getEntries("fooBar", Locale.GERMAN);
            fail("Bundle does not exist and should cause error");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No message entries found for bundle with key fooBar", mnfex.getMessage());
        }
    }

    /**
     * A Test classloader to dynamically load Properties instances as
     * properties files.
     */
    private static class DynamicResourceLoader extends ClassLoader {
        public InputStream getResourceAsStream(String name) {
            try {
                if(mResources.containsKey(name)) {
                    Properties p = (Properties) mResources.get(name);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    p.store(baos,"");
                    baos.close();
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            } catch (IOException ignore) {
            }
            return super.getResourceAsStream(name);
        }

        /**
         * Adds the property file with the specified name and contents to
         * the set of resources that should be returned by this class loader.
         *
         * @param inName the resource name.
         * @param inProperties the property value.
         */
        public void addResource(String inName, Properties inProperties) {
            mResources.put(inName, inProperties);
        }

        private HashMap mResources = new HashMap();
    }
}