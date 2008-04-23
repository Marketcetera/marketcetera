package org.marketcetera.util.exec;

import org.marketcetera.core.ClassVersion;

/**
 * The allowed destinations for the interleaved standard output and
 * error streams of a process.
 *
 * @author tlerios
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
