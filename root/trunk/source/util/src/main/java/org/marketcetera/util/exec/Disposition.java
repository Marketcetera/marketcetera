package org.marketcetera.util.exec;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import org.marketcetera.core.ClassVersion;

@ClassVersion("$Id$")
public enum Disposition
{
    MEMORY,
    STDOUT,
    STDERR;
}
