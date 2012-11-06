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

import java.io.IOException;

/**
 * Interface for systems that really like interfaces
 *
 * @see SimpleWriter
 */
public interface CSVWriter
{
    /**
     * Write a row to the CSV file
     */
    void append(Object[] fields) throws IOException;

    /**
     * Specify the character to use to seperate fields, defaults to a comma
     */
    void setSeperator(char seperator);

    /**
     * Flush after each line? Default is false
     */
    void setAutoFlush(boolean autoFlush);

    /**
     * Defaults to the system dependent newline
     */
    void setNewLine(char c);

    /**
     * Defaults to the system dependent newline
     */
    void setNewLine(char[] c);

    /**
     * Defaults to the system dependent newline
     */
    void setNewLine(String newline);

    /**
     * Append a string as a raw line, without any processing
     * <p>
     * Useful for comments, etc
     */
    void rawLine(String line) throws IOException;
}
