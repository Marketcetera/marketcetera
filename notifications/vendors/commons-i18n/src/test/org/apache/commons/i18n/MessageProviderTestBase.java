/*
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

/**
 * This class tests that the behaviour of a <code>MessageProvider</code> implementation is correct.
 * If creating new implementations, consider subclassing this class to test the behaviour of that implementation.
 * The tests assume the provided <code>MessageProvider</code>
 * contains the following entries:
 * <p />
 * <table>
 *   <tr>
 *     <th>Language/<code>Locale</code></th>
 *     <th>ID</th>
 *     <th>Entry</th>
 *     <th>Message</th>
 *   </tr>
 *   <tr>
 *     <td>English (en)</td>
 *     <td>helloWorld</td>
 *     <td>title</td>
 *     <td>Hello World</td>
 *   <tr>
 *   <tr>
 *     <td>English (en)</td>
 *     <td>helloWorld</td>
 *     <td>text</td>
 *     <td>Hello World, we are in {0}.</td>
 *   <tr>
 *   <tr>
 *     <td>German (de)</td>
 *     <td>helloWorld</td>
 *     <td>title</td>
 *     <td>Hallo Welt</td>
 *   <tr>
 *   <tr>
 *     <td>German (de)</td>
 *     <td>helloWorld</td>
 *     <td>text</td>
 *     <td>Hallo Welt, wir sind in {0}.</td>
 *   <tr>
 *   <tr>
 *     <td colspan="4"><i>The entry below is used only if possible. If not possible, set
 *     <code>hasNonTranslatedEntry</code> to false.</i></td>
 *   <tr>
 *   <tr>
 *     <td>English (en)</td>
 *     <td>helloWorld</td>
 *     <td>helloWorld</td>
 *     <td>This entry is not translated to any other languages</td>
 *   <tr>
 * </table>
 * <p />
 * Specifically, the ID <code>nonExistentId</code> and the entry <code>nonExistentEntry</code> of ID
 * <code>helloWorld</code> must NOT be existent.
 */
public abstract class MessageProviderTestBase extends TestCase {

    protected MessageProviderTestBase() {
    }

    protected MessageProviderTestBase(String testName) {
        super(testName);
    }

    /**
     * Set English as default Locale.
     * If overridden, please remember to call <code>super.setUp()</code>
     * @throws Exception No exception is thrown, but allow for overriding methods
     * to throw exceptions.
     */
    public void setUp() throws Exception {
        /* Make sure en_US is the default Locale for tests */
        Locale.setDefault(Locale.ENGLISH);
    }

    public void tearDown() throws Exception {
        /* Uninstall resource bundles after every test */
        MessageManager.clearMessageProviders();
    }

    /**
     * Test functionality of getText() method, which should be common for all implementations of the
     * <code>MessageProvider</code> interface.
     * @param messageProvider
     */
    protected void testGetText(MessageProvider messageProvider) {
        // Explicit default locale
        assertEnglishTexts(messageProvider, Locale.ENGLISH);

        // Default locale with country
        assertEnglishTexts(messageProvider, Locale.US);

        // Default locale with variant
        assertEnglishTexts(messageProvider, new Locale("en", "", "scottish"));

        // Default locale with country and variant
        assertEnglishTexts(messageProvider, new Locale("en", "GB", "scottish"));

        // Non-default locale
        assertGermanTexts(messageProvider, Locale.GERMAN);

        // Non-default locale with country
        assertGermanTexts(messageProvider, Locale.GERMANY);

        // Non-default locale with variant
        assertGermanTexts(messageProvider, new Locale("de", "", "foo"));

        // Non-default locale with country and variant
        assertGermanTexts(messageProvider, new Locale("de", "CH", "foo"));

        // Fallback to default
        assertEnglishTexts(messageProvider, Locale.JAPANESE);

        // Non-existent entry
        // try {
        assertNull(messageProvider.getText("helloWorld", "nonExistentEntry", Locale.ENGLISH));
        // }
        // catch(MessageNotFoundException mnfex) {
        //     assertEquals("Message bundle with key helloWorld does not contain an entry with key nonExistentEntry", mnfex.getMessage());
        // }

        // Non-existent id
        try {
            assertNull(messageProvider.getText("nonExistentId", "foo", Locale.ENGLISH));
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No message entries found for bundle with key nonExistentId", mnfex.getMessage()); // TODO: JDBC
        }
    }

    private void assertEnglishTexts(MessageProvider messageProvider, Locale locale) {
        assertEquals("Locale = " + locale + ", title",
                "Hello World", messageProvider.getText("helloWorld", "title", locale));
        assertEquals("Locale = " + locale + ", text",
                "Hello World, we are in {0}.", messageProvider.getText("helloWorld", "text", locale));
    }

    private void assertGermanTexts(MessageProvider messageProvider, Locale locale) {
        assertEquals("Locale = " + locale + ", title",
                "Hallo Welt", messageProvider.getText("helloWorld", "title", locale));
        assertEquals("Locale = " + locale + ", text",
                "Hallo Welt, wir sind in {0}.", messageProvider.getText("helloWorld", "text", locale));
    }

    // TODO: Document
    protected void testGetEntries(MessageProvider messageProvider, boolean hasNonTranslatedEntry) {
        // TODO: Complete from above

        // Explicit default locale
        assertEnglishEntries(messageProvider, Locale.ENGLISH, hasNonTranslatedEntry);

        // Default locale with country
        assertEnglishEntries(messageProvider, Locale.US, hasNonTranslatedEntry);

        // Default locale with country and variant
        assertEnglishEntries(messageProvider, new Locale("en", "", "scottish"), hasNonTranslatedEntry);

        assertGermanEntries(messageProvider, Locale.GERMAN);

        // Default locale with country
        assertGermanEntries(messageProvider, Locale.GERMANY);

        // Test use of defaule
        assertEnglishEntries(messageProvider, Locale.JAPANESE, hasNonTranslatedEntry);

        // Non-existent id
        try {
            messageProvider.getEntries("nonExistentId", Locale.ENGLISH);
            fail("Non-existent ID should cause exception");
        }
        catch(MessageNotFoundException mnfex) {
            assertEquals("No message entries found for bundle with key nonExistentId", mnfex.getMessage());
        }
    }

    private static void assertEnglishEntries(MessageProvider messageProvider, Locale locale, boolean hasNonTranslatedEntry) {
        Map entries = messageProvider.getEntries("helloWorld", locale);
        if(hasNonTranslatedEntry) {
            assertEquals("Locale = " + locale + ", No of entries", 3, entries.size());
            assertEquals("Locale = " + locale, "This entry is not translated to any other languages",
                    entries.get("notTranslated"));
        }
        else
            assertEquals("Locale = " + locale + ", No of entries", 2, entries.size());
        assertEquals("Locale = " + locale, "Hello World", (String)entries.get("title"));
        assertEquals("Locale = " + locale, "Hello World, we are in {0}.", entries.get("text"));
    }

    private static void assertGermanEntries(MessageProvider messageProvider, Locale locale) {
        Map entries = messageProvider.getEntries("helloWorld", locale);

        // TODO: Consider whether all MessageProviders must behave the same way
        if(entries.size() == 3) // If non-translated entries included
            assertEquals("This entry is not translated to any other languages", entries.get("notTranslated"));
        else if(entries.size() != 2)
            fail("No of entries should be 2 or 3!");

        assertEquals("Hallo Welt", (String)entries.get("title"));
        assertEquals("Hallo Welt, wir sind in {0}.", entries.get("text"));
    }
}
