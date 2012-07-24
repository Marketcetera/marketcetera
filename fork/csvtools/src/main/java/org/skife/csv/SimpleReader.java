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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a means to read CSV files
 */
public class SimpleReader implements CSVReader
{
    private LineSplitter splitter = new LineSplitter();
    private String commentToken = null;

    /**
     * Calls onRow for each row in the raw (including line breaks)
     * content of the csv, passed as a String
     */
    public void parse(String raw, ReaderCallback callback)
    {
        StringReader reader = new StringReader(raw);
        try
        {
            parse(reader, callback);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Somehow got an IOException reading from a String!", e);
        }
    }

    /**
     * Returns a List of String[] where it is passed the raw (including line breaks)
     * content of the csv
     */
    public List parse(String raw)
    {
        StringReader reader = new StringReader(raw);
        try
        {
            return parse(reader);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Somehow got an IOException reading from a String!", e);
        }
    }

    /**
     * Opens, parses, and closes a file
     *
     * @throws java.io.IOException           if there is an error reading
     * @throws java.io.FileNotFoundException if the file does not exist
     */
    public List parse(File in) throws IOException
    {
        FileInputStream file = new FileInputStream(in);
        try
        {
            return parse(file);
        }
        finally
        {
            file.close();
        }
    }

    /**
     * Opens, parses, and closes a file
     *
     * @throws java.io.IOException           if there is an error reading
     * @throws java.io.FileNotFoundException if the file does not exist
     */
    public void parse(File in, ReaderCallback callback) throws IOException
    {
        FileInputStream file = new FileInputStream(in);
        try
        {
            parse(file, callback);
        }
        finally
        {
            file.close();
        }
    }

    /**
     * Returns a List of String[]
     *
     * @param in will not be closed by the reader
     */
    public List parse(Reader in) throws IOException
    {
        final ArrayList list = new ArrayList();
        parse(in, new ReaderCallback()
        {
            public void onRow(String[] fields)
            {
                list.add(fields);
            }
        });
        return list;
    }

    /**
     * Returns a List of String[]
     *
     * @param in will not be closed by the reader
     */
    public List parse(InputStream in) throws IOException
    {
        return parse(new InputStreamReader(in));
    }

    /**
     * Invoke the callback for each row of the CSV, passing in the fields.
     *
     * @param in will not be closed by the reader
     */
    public void parse(InputStream in, ReaderCallback callback) throws IOException
    {
        parse(new InputStreamReader(in), callback);
    }

    /**
     * Invoke the callback for each row of the CSV, passing in the fields.
     *
     * @param in will not be closed by the reader
     */
    public void parse(Reader in, ReaderCallback callback) throws IOException
    {
        BufferedReader reader = new BufferedReader(in);
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            if (commentToken == null || !line.startsWith(commentToken))
            {
                callback.onRow(splitter.split(line));
            }
        }
    }

    /**
     * Specify an escape character within a field, default is \
     */
    public void setEscapeCharacter(char escape)
    {
        splitter.setEscapeCharacter(escape);
    }

    /**
     * Specify the field seperator character, defaults to a comma
     */
    public void setSeperator(char seperator)
    {
        splitter.setSeperator(seperator);
    }

    /**
     * Specify an array of chars that will be treated as quotes, ie, will be ignored and
     * everything between them is one field. Default is ' and "
     */
    public void setQuoteCharacters(char[] quotes)
    {
        splitter.setQuoteCharacters(quotes);
    }

    /**
     * Trim whitespace around fields, defaults to false
     * @param b
     */
    public void setTrim(boolean b)
    {
        splitter.setTrim(b);
    }

    /**
     * Specify a string that is used to indicate that a line should be passed over
     * without processing. When the passed String is the very first thing on the line,
     * the whole line will be skipped.
     * <p/>
     * Passing null indicates all lines should be processed
     * <p/>
     * Processing all lines is the default behavior
     */
    public void setLineCommentIndicator(String token)
    {
        this.commentToken = token;
    }
}
