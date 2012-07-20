package org.marketcetera.util.exec;

import org.marketcetera.util.misc.ClassVersion;

/**
 * A container for the result of a process execution. This comprises
 * an exit code and (if {@link Disposition#MEMORY} is elected) the
 * captured contents of the interleaved standard output and error
 * streams.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ExecResult
{

    // INSTANCE DATA.

    private int mExitCode;
    private byte[] mOutput;


    // CONSTRUCTORS.

    /**
     * Creates a new container for the given exit code and captured
     * output.
     *
     * @param exitCode The exit code.
     * @param output The captured output. It may be null if {@link
     * Disposition#MEMORY} is not elected.
     */

    public ExecResult
        (int exitCode,
         byte[] output)
    {
        mExitCode=exitCode;
        mOutput=output;
    }


    // INSTANCE METHODS.

    /**
     * Returns the exit code.
     *
     * @return The code.
     */

    public int getExitCode()
    {
        return mExitCode;
    }

    /**
     * Returns the captured output.
     *
     * @return The output. It may be null if {@link
     * Disposition#MEMORY} is not elected.
     */

    public byte[] getOutput()
    {
        return mOutput;
    }
}
