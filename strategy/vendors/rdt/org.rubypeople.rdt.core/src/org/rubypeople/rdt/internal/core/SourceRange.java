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
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.ISourceRange;

/**
 * @see ISourceRange
 */
public class SourceRange implements ISourceRange {

    private int offset, length;

    public SourceRange(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    /**
     * @see ISourceRange
     */
    public int getLength() {
        return this.length;
    }

    /**
     * @see ISourceRange
     */
    public int getOffset() {
        return this.offset;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[offset="); //$NON-NLS-1$
        buffer.append(this.offset);
        buffer.append(", length="); //$NON-NLS-1$
        buffer.append(this.length);
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }
}
