package org.marketcetera.util.exec;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: Disposition.java 16154 2012-07-14 16:34:05Z colin $
 */

/* $License$ */

@ClassVersion("$Id: Disposition.java 16154 2012-07-14 16:34:05Z colin $")
public enum Disposition
{
    MEMORY,
    STDOUT,
    STDERR;
}
