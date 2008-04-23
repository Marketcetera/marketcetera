package org.marketcetera.util.file;

import org.marketcetera.core.ClassVersion;

/**
 * Special names used to denote standard streams or operations on
 * regular files.
 *
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public interface SpecialNames
{
    /**
     * The standard input stream.
     */

    static final String STANDARD_INPUT=":stdin:";

    /**
     * The standard output stream.
     */

    static final String STANDARD_OUTPUT=":stdout:";

    /**
     * The standard error stream.
     */

    static final String STANDARD_ERROR=":stderr:";

    /**
     * When this prefix appears in front of an output file name, it
     * indicates that new data must be appended to the end of the
     * file, instead of overwriting existing data.
     */

    static final String PREFIX_APPEND=":append:";
}
