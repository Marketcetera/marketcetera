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

import org.apache.commons.i18n.bundles.MessageBundle;

/**
 * @author Daniel Florey
 *
 */
public class XMLMessageProviderTest extends MessageProviderTestBase {

    public void testInstallResourceBundle() {
        MessageBundle testMessage = new MessageBundle("helloWorld");

        try {
            testMessage.getTitle(Locale.GERMAN);
            fail("XML file not installed, should throw exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }

        MessageManager.addMessageProvider("org.apache.commons-i18n.test", new XMLMessageProvider(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("testMessages.xml")));

        assertEquals("Hallo Welt", testMessage.getTitle(Locale.GERMAN));

        MessageManager.removeMessageProvider("org.apache.commons-i18n.test");

        try {
            testMessage.getTitle(Locale.GERMAN);
            fail("XML file uinstalled, should throw exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No MessageProvider registered", mnfex.getMessage());
        }

        // Try to parse non-XML file
        try {
            new XMLMessageProvider(
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("messageBundle.properties"));
            fail("Parsing non-XML file should fail");
        }
        catch(RuntimeException rtex) {
            assertEquals("Error while parsing message file", rtex.getMessage());
        }
    }
    
    public void testGetText() {
        XMLMessageProvider xmlmp = new XMLMessageProvider(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("testMessages.xml"));

        super.testGetText(xmlmp);

        // TODO: Wait for Daniels reply on whether this is intended
        // assertEquals("Fallback when only in default", "This entry is not translated to any other languages",
        //         xmlmp.getText("helloWorld", "notTranslated", Locale.GERMAN));

        // TODO: Wait for Daniels reply on whether this is intended
        /*
        try {
            String s = xmlmp.getText("helloWorld", "nonExistentEntry", Locale.US);
            fail("Entry does not exist, should throw exception. Entry was: '" + s + "'");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No message entries found for bundle with key helloWorld", mnfex.getMessage());
        }
        */
    }

    public void testGetTextVariants() {
        XMLMessageProvider xmlmp = new XMLMessageProvider(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("variantTestMessages.xml"));

        assertEquals("hello world", xmlmp.getText("variants", "theKey", Locale.ENGLISH));
        assertEquals("Botswana", "Hello Botswana", xmlmp.getText("variants", "theKey", new Locale("", "BW")));
        assertEquals("Awrite warld", xmlmp.getText("variants", "theKey", new Locale("en", "GB", "scottish")));
        assertEquals("Ga, ga, ga", xmlmp.getText("variants", "theKey", new Locale("en", "", "baby")));
    }

    public void testGetEntries() {
        final XMLMessageProvider xmlMessageProvider = new XMLMessageProvider(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("testMessages.xml"));

        super.testGetEntries(xmlMessageProvider, true);
    }

    /**
     * Constructor for MessageManagerTest.
     */
    public XMLMessageProviderTest(String testName) {
        super(testName);
    }
}
