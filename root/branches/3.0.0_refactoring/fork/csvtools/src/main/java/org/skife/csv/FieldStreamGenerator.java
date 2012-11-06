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

import java.util.Iterator;

class FieldStreamGenerator implements Iterator
{
    private final CharSequence line;
    private final char[] quotes;
    private final char seperator;
    private final boolean trim;
    private final char escape;

    private int location = 0;
    private char quoteChar = 0;
    private boolean inQuote = false;
    private boolean escapeNext = false;

    FieldStreamGenerator(CharSequence line, char[] quotes, char escape, char seperator, boolean trim)
    {
        this.line = line;
        this.quotes = quotes;
        this.seperator = seperator;
        this.trim = trim;
        this.escape = escape;
    }

    public boolean hasNext()
    {
        return location < line.length();
    }

    /**
     * @return a String
     */
    public Object next()
    {
        StringBuffer buffer = new StringBuffer();
        while (location < line.length()
               && (line.charAt(location) != seperator || inQuote || escapeNext))
        {
            char c = line.charAt(location);

            if (escapeNext)
            {
                buffer.append(c);
                escapeNext = false;
            }
            else if (c == escape)
            {
                escapeNext = true;
            }
            else if (inQuote)
            {
                if (c == quoteChar)
                {
                    inQuote = false;
                }
                else
                {
                    buffer.append(c);
                }
            }
            else
            {
                if (isQuoteChar(c))
                {
                    inQuote = true;
                    quoteChar = c;
                }
                else
                {
                    buffer.append(c);
                }
            }
            location++;
        }
        location++;
        return trim ? buffer.toString().trim() : buffer.toString();
    }

    private boolean isQuoteChar(char c)
    {
        for (int i = 0; i < quotes.length; i++)
        {
            char quote = quotes[i];
            if (c == quote) return true;
        }
        return false;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
