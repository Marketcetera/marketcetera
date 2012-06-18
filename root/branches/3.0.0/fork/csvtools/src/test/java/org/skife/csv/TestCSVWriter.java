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

import java.io.File;
import java.io.StringWriter;
import java.util.List;

public class TestCSVWriter extends TestCase
{
    private StringWriter buffer;
    private CSVWriter writer;

    public void setUp() throws Exception
    {
        buffer = new StringWriter();
        writer = new SimpleWriter(buffer);

    }

    public void testBasics() throws Exception
    {
        writer.setNewLine('\n');
        writer.append(new Object[]{"brian", "1"});
        writer.append(new Object[]{"eric", "2"});
        String file = buffer.getBuffer().toString();

        assertEquals("brian,1\neric,2\n", file);
    }

    public void testPlatformNewline() throws Exception
    {
        String newline = System.getProperty("line.separator");
        writer.append(new Object[]{"brian", "1"});
        writer.append(new Object[]{"eric", "2"});
        String file = buffer.getBuffer().toString();

        assertEquals("brian,1" + newline + "eric,2" + newline, file);
    }

    public void testEscapeSeperator() throws Exception
    {
        writer.append(new Object[]{"bri,an", "1"});
        String file = buffer.getBuffer().toString();

        assertEquals("bri\\,an,1".trim(), file.trim());
    }

    public void testEscapeSeperatorAtEnd() throws Exception
    {
        writer.append(new Object[]{"brian,", "1"});
        String file = buffer.getBuffer().toString();

        assertEquals("brian\\,,1".trim(), file.trim());
    }

    public void testEscapeQuote() throws Exception
    {
        writer.append(new Object[]{"bri'an", "1"});
        String file = buffer.getBuffer().toString();

        assertEquals("bri\\'an,1".trim(), file.trim());
    }

    public void testEscapeQuoteAtEnd() throws Exception
    {
        writer.append(new Object[]{"brian'", "1"});
        String file = buffer.getBuffer().toString();

        assertEquals("brian\\',1".trim(), file.trim());

    }

    public void testExample1() throws Exception
    {
        StringWriter buffer = new StringWriter();
        CSVWriter writer = new SimpleWriter(buffer);

        writer.append(new Object[]{"brian", "1"});
        writer.rawLine("# some comment");
        writer.append(new Object[]{"eric", "2"});
        String file = buffer.getBuffer().toString();

        assertEquals("brian,1\n# some comment\neric,2\n", file);
    }

    public void testCallback() throws Exception
    {
        File temp = File.createTempFile("test", ".csv");
        temp.deleteOnExit();

        SimpleWriter.write(temp, new WriterCallback()
        {
            public void withWriter(SimpleWriter writer) throws Exception
            {
                writer.append(new Object[]{"brian", "1"});
            }
        });

        List lines = new SimpleReader().parse(temp);
        assertEquals(1, lines.size());
        String[] fields = (String[]) lines.get(0);
        assertEquals("brian", fields[0]);
        assertEquals("1", fields[1]);
    }
}
