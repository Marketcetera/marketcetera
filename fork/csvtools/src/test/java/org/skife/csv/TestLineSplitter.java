/* Copyright 2005 Brian McCallister
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
 */
package org.skife.csv;

import junit.framework.TestCase;

public class TestLineSplitter extends TestCase
{
    private LineSplitter splitter;

    public void setUp() throws Exception
    {
        splitter = new LineSplitter();
        splitter.setTrim(true);
    }

    public void testGetFirstField() throws Exception
    {
        String line = "a, 2, E";
        String[] split = splitter.split(line);
        assertEquals(split[0], "a");
    }

    public void testGetMiddleField() throws Exception
    {
        String line = "a, 2, E";
        String[] split = splitter.split(line);
        assertEquals(split[1], "2");
    }

    public void testGetLastField() throws Exception
    {
        String line = "a, 2, E";
        String[] split = splitter.split(line);
        assertEquals(split[2], "E");
    }

    public void testSimpleQuotes() throws Exception
    {
        String line = "'a', 2, E";
        splitter.setQuoteCharacters(new char[] {'\''});
        String[] split = splitter.split(line);
        assertEquals("a", split[0]);
    }

    public void testCommaInQuote() throws Exception
    {
        String line = "'a,b', 2, E";
        splitter.setQuoteCharacters(new char[] {'\''});
        String[] split = splitter.split(line);
        assertEquals("a,b", split[0]);
    }

    public void testQuoteInMiddle() throws Exception
    {
        String line = "a','b, 2, E";
        splitter.setQuoteCharacters(new char[] {'\''});
        String[] split = splitter.split(line);
        assertEquals("a,b", split[0]);
    }

    public void testEscapeCharacter() throws Exception
    {
        String line = "a\\,b, 2, E";
        splitter.setQuoteCharacters(new char[] {'\''});
        String[] split = splitter.split(line);
        assertEquals("a,b", split[0]);
    }

    public void testEscapeQuotes() throws Exception
    {
        String line = "a\\',\\'b, 2, E";
        splitter.setQuoteCharacters(new char[] {'\''});
        String[] split = splitter.split(line);
        assertEquals("a'", split[0]);
        assertEquals("'b", split[1]);
    }
}
