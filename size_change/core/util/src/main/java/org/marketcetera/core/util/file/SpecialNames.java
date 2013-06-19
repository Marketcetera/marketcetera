package org.marketcetera.core.util.file;

/**
 * Special names used to denote standard streams or operations on
 * regular files.
 *
 * @since 0.5.0
 * @version $Id: SpecialNames.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public interface SpecialNames
{
    /**
     * The standard input stream.
     */

    static final String STANDARD_INPUT=
        ":stdin:"; //$NON-NLS-1$

    /**
     * The standard output stream.
     */

    static final String STANDARD_OUTPUT=
        ":stdout:"; //$NON-NLS-1$

    /**
     * The standard error stream.
     */

    static final String STANDARD_ERROR=
        ":stderr:"; //$NON-NLS-1$

    /**
     * When this prefix appears in front of an output file name, it
     * indicates that new data must be appended to the end of the
     * file, instead of overwriting existing data.
     */

    static final String PREFIX_APPEND=
        ":append:"; //$NON-NLS-1$
}
