package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An abstraction to represent a command specified by the user in
 * the command file.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
final class Command {
    /**
     * Creates a command instance.
     *
     * @param inRunner    the command runner that runs this command.
     * @param inParameter the parameter to this command runner.
     * @param inLineNum   the line number at which this command is
     *                    specified in the commands file.
     */
    Command(CommandRunner inRunner, String inParameter,
            int inLineNum) {
        mRunner = inRunner;
        mParameter = inParameter;
        mLineNum = inLineNum;
    }

    /**
     * The command runner for this command.
     *
     * @return the command runner.
     */
    public CommandRunner getRunner() {
        return mRunner;
    }

    /**
     * The parameter for this command.
     *
     * @return the command parameter
     */
    public String getParameter() {
        return mParameter;
    }

    /**
     * The line number at which this command appears in
     * the commands file.
     *
     * @return the line number at which this command appears in the
     *         commands file.
     */
    public int getLineNum() {
        return mLineNum;
    }

    private final CommandRunner mRunner;
    private final String mParameter;
    private final int mLineNum;
}
