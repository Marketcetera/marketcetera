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

class FieldMunger
{
    private char escapeCharacter = Defaults.ESCAPE_CHARACTER;
    private char seperator = Defaults.SEPERATOR;
    private char[] quotes = Defaults.QUOTES;

    CharSequence munge(CharSequence field)
    {
        final StringBuffer buffer = new StringBuffer();
        char c = 0;
        for (int i = 0; i < field.length(); i++)
        {
            c = field.charAt(i);
            if (isQuote(c) || c == seperator)
            {
                buffer.append(escapeCharacter);
                buffer.append(c);
            }
            else
            {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    char getSeperator()
    {
        return seperator;
    }

    void setSeperator(char seperator)
    {
        this.seperator = seperator;
    }

    void setEscapeCharacter(char escapeCharacter)
    {
        this.escapeCharacter = escapeCharacter;
    }

    void setQuotes(char[] quotes)
    {
        this.quotes = quotes;
    }

    private boolean isQuote(char c)
    {
        for (int i = 0; i < quotes.length; i++)
        {
            if (quotes[i] == c) return true;
        }
        return false;
    }
}
