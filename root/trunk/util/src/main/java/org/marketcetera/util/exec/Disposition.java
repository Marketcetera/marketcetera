package org.marketcetera.util.exec;

import org.marketcetera.util.misc.ClassVersion;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public enum Disposition
{
    MEMORY,
    STDOUT,
    STDERR;
}
