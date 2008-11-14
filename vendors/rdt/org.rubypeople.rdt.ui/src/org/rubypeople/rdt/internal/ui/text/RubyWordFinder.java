/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;

public class RubyWordFinder {
    /**
     * The characters which mark the end of a "word" in Ruby. Essentially these
     * are what break up the tokens for double-clicking and for hovers.
     */
    private static final char[] BOUNDARIES = { ' ', '\n', '\t', '\r', '.', '(', ')', '{', '}', '[',
            ']', '=', '*', '+', '-', '"', '\'', '#', ',', '|', '>', '%'};
    public static IRegion findWord(IDocument document, int offset) {
        int start = -1;
        int end = -1;

        try {
            int pos = offset;
            char c;

            while (pos >= 0) {
                c = document.getChar(pos);
                if (!isRubyWordPart(c)) break;
                --pos;
            }

            start = pos;

            pos = offset;
            int length = document.getLength();

            while (pos < length) {
                c = document.getChar(pos);
                if (!isRubyWordPart(c)) break;
                ++pos;
            }

            end = pos;

        } catch (BadLocationException x) {
	    return null;
        }

        if (start >= -1 && end > -1) {
            if (start == offset && end == offset)
                return new Region(offset, 0);
            else if (start == offset)
                return new Region(start, end - start);
            else
                return new Region(start + 1, end - start - 1);
        }

        return null;
    }

    private static boolean isRubyWordPart(char c) {
        return !isBoundary(c);
    }

    private static boolean isBoundary(char c) {
        return contains(BOUNDARIES, c);
    }

    private static boolean contains(char[] boundaries2, char c) {
        if (boundaries2 == null || boundaries2.length == 0) return false;
        for (int i = 0; i < boundaries2.length; i++) {
            if (boundaries2[i] == c) return true;
        }
        return false;
    }
}
