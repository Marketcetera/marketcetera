package org.marketcetera.core.util.exec;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @since 0.5.0
 * @version $Id: Disposition.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public enum Disposition
{
    MEMORY,
    STDOUT,
    STDERR;
}
