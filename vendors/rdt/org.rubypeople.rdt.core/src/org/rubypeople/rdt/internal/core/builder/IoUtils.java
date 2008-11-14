/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.core.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.rubypeople.rdt.core.RubyCore;

public class IoUtils {

    public static void closeQuietly(Reader reader) {
        try {
            reader.close();
        } catch (IOException e) {
            RubyCore.log(e);
        }
    }

    public static void closeQuietly(InputStream contents) {
        try {
            if (contents != null)
                contents.close();
        } catch (IOException e) {
            RubyCore.log(e);
        }
    }

    public static String readAll(Reader reader) throws IOException {
        StringBuffer result = new StringBuffer();
        char[] buffer = new char[1024];
        while (true) {
            int bytesRead = reader.read(buffer);
            if (bytesRead <= 0)
                return result.toString();
            result.append(buffer, 0, bytesRead);
        }
    }

    public static String readAllQuietly(Reader reader) {
        try {
            return readAll(reader);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}
