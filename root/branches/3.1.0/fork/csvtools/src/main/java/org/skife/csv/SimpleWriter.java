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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Provides primitive support for writing CSV files.
 */
public class SimpleWriter implements CSVWriter
{
    private char[] newline = Defaults.LINE_SEPERATOR;

    private final Writer out;
    private boolean autoFlush = false;
    private final FieldMunger munger = new FieldMunger();

    /**
     * Create a CSVWriter which prints output to a <code>Writer</code>. It does not close
     * the Writer.
     */
    public SimpleWriter(Writer out)
    {
        this.out = out;
    }

    /**
     * Write a row to the CSV file
     */
    public void append(Object[] fields) throws IOException
    {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < fields.length; i++)
        {
            CharSequence field = munger.munge(String.valueOf(fields[i]));
            buffer.append(field);
            if (i < fields.length - 1) buffer.append(munger.getSeperator());
        }

        buffer.append(newline);
        out.write(buffer.toString());
        if (autoFlush) out.flush();
    }

    /**
     * Append a string as a raw line, without any processing
     * <p>
     * Useful for comments, etc
     */
    public void rawLine(String line) throws IOException
    {
        out.write(line);
        out.write(newline);
        if (autoFlush) out.flush();
    }

    /**
     * Specify the character to use to seperate fields, defaults to a comma
     */
    public void setSeperator(char seperator)
    {
        munger.setSeperator(seperator);
    }

    /**
     * Flush after each line? Default is false
     */
    public void setAutoFlush(boolean autoFlush)
    {
        this.autoFlush = autoFlush;
    }

    /**
     * Defaults to the system dependent newline
     */
    public void setNewLine(char c)
    {
        this.newline = new char[]{c};
    }

    /**
     * Defaults to the system dependent newline
     */
    public void setNewLine(char[] c)
    {
        this.newline = c;
    }

    /**
     * Defaults to the system dependent newline
     */
    public void setNewLine(String newline)
    {
        this.newline = newline.toCharArray();
    }

    /**
     * Specify an array of chars that will be treated as quotes, ie, will be ignored and
     * everything between them is one field. Default is ' and "
     */
    public void setQuoteCharacters(char[] quotes)
    {
        munger.setQuotes(quotes);
    }

    /**
     * Open a file and pass a writer to the callback which creates or overwrites that file.
     *
     * @throws RuntimeException if there is an exception during execution, flushing, or closing the file
     */
    public static void write(File file, WriterCallback callback)
    {
        withCallback(file, callback, false);
    }

    /**
     * Open a file and pass a writer to the callback which appends to that file.
     *
     * @throws RuntimeException if there is an exception during execution, flushing, or closing the file
     */
    public static void append(File file, WriterCallback callback)
    {
        withCallback(file, callback, true);
    }

    private static void withCallback(File file, WriterCallback callback, boolean append)
    {
        Writer out = null;
        try
        {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append)));
            SimpleWriter writer = new SimpleWriter(out);
            callback.withWriter(writer);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Exception thrown from callback", e);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.flush();
                    out.close();
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}