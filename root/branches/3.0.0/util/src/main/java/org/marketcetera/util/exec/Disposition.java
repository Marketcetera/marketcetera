package org.marketcetera.util.exec;

import org.marketcetera.core.attributes.ClassVersion;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: Disposition.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@ClassVersion("$Id: Disposition.java 16063 2012-01-31 18:21:55Z colin $")
public enum Disposition
{
    MEMORY,
    STDOUT,
    STDERR;
}
