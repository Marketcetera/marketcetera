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

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class TestCSVReader extends TestCase
{
    private CSVReader reader;
    private InputStream in;

    public void setUp() throws Exception
    {
        URL url = this.getClass().getClassLoader().getResource("sample.csv");
        in = url.openStream();
        reader = new SimpleReader();
    }

    public void tearDown() throws Exception
    {
        in.close();
    }

    public void testExample1() throws Exception
    {
        CSVReader reader = new SimpleReader();

        URL url = this.getClass().getClassLoader().getResource("sample.csv");
        InputStream in = url.openStream();

        List items = reader.parse(in);
        String[] first = (String[]) items.get(0);
        assertEquals("Brian McCallister", first[0]);
        assertEquals("(302) 994-8629", first[1]);

        String[] second = (String[]) items.get(1);
        assertEquals("Eric McCallister", second[0]);
        assertEquals("(302) 994-8991", second[1]);

        String[] third = (String[]) items.get(2);
        assertEquals("Keith McCallister", third[0]);
        assertEquals("(302) 994-8761", third[1]);

        in.close();
    }

    public void testExample2() throws Exception
    {
        CSVReader reader = new SimpleReader();

        URL url = this.getClass().getClassLoader().getResource("sample.csv");
        InputStream in = url.openStream();

        final int[] count = {0};
        reader.parse(in, new ReaderCallback()
        {
            public void onRow(String[] fields)
            {
                count[0]++;
                switch (count[0])
                {
                    case 1:
                        assertEquals("Brian McCallister", fields[0]);
                        assertEquals("(302) 994-8629", fields[1]);
                        break;
                    case 2:
                        assertEquals("Eric McCallister", fields[0]);
                        assertEquals("(302) 994-8991", fields[1]);
                        break;
                    case 3:
                        assertEquals("Keith McCallister", fields[0]);
                        assertEquals("(302) 994-8761", fields[1]);
                        break;
                }
            }
        });
        assertEquals(3, count[0]);
        in.close();
    }

    public void testNotEmpty() throws Exception
    {
        List items = reader.parse(in);
        assertFalse(items.isEmpty());
    }

    public void testCount() throws Exception
    {
        List items = reader.parse(in);
        assertEquals(3, items.size());
    }

    public void testContentType() throws Exception
    {
        List items = reader.parse(in);
        assertEquals((new String[]{}).getClass(), items.get(0).getClass());
    }

    public void testCallbacks() throws Exception
    {
        final int count[] = {0};

        reader.parse(in, new ReaderCallback()
        {
            public void onRow(String[] fields)
            {
                count[0]++;
                if (count[0] == 1)
                {
                    assertEquals("Brian McCallister", fields[0]);
                }
                else if (count[0] == 2)
                {
                    assertEquals("Eric McCallister", fields[0]);
                }
                else
                {
                    assertEquals("Keith McCallister", fields[0]);
                }
            }
        });
        assertEquals(3, count[0]);
    }

    public void testRawStringUNIXEOL() throws Exception
    {
        String raw = "brian,1\neric,2";
        List rows = reader.parse(raw);
        assertEquals(2, rows.size());
        assertEquals("1", ((String[]) rows.get(0))[1]);
    }

    public void testRawStringWindowsEOL() throws Exception
    {
        String raw = "brian,1\r\neric,2";
        List rows = reader.parse(raw);
        assertEquals(2, rows.size());
        assertEquals("1", ((String[]) rows.get(0))[1]);
        assertEquals("eric", ((String[]) rows.get(1))[0]);
    }

    public void testComments() throws Exception
    {
        reader.setLineCommentIndicator("#");
        String raw = "brian,1\n# a comment\neric,2";
        List rows = reader.parse(raw);
        assertEquals(2, rows.size());
    }
}
