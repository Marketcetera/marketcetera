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

import java.util.ArrayList;

class LineSplitter
{
    private boolean trim = false;
    private char[] quotes = Defaults.QUOTES;
    private char seperator = Defaults.SEPERATOR;
    private char escape = Defaults.ESCAPE_CHARACTER;

    String[] split(String line)
    {
        FieldStreamGenerator gen = new FieldStreamGenerator(line, quotes, escape, seperator, trim);
        ArrayList all = new ArrayList();
        while (gen.hasNext()) all.add(gen.next());
        return (String[]) all.toArray(new String[all.size()]);
    }

    /**
     * Should whitespace be trimmed around fields?
     * <p>
     * Defualts to false
     */
    void setTrim(boolean trim)
    {
        this.trim = trim;
    }

    /**
     * Specify an array of chars that will be treated as quotes, ie, will be ignored and
     * everything between them is one field. Default is ' and "
     */
    void setQuoteCharacters(char[] quotes)
    {
        this.quotes = quotes;
    }

    /**
     * Specify the field seperator character, defaults to a comma
     */
    void setSeperator(char seperator)
    {
        this.seperator = seperator;
    }

    /**
     * Specify an escape character within a field, default is \
     */
    void setEscapeCharacter(char escape)
    {
        this.escape = escape;
    }
}
