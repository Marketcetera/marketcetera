package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleManagerMXBean;

/* $License$ */
/**
 * The command runner. Command runners are used to run commands specified
 * in the commands file accepted by teh Strategy Agent.
 * Each command runner type knows how to run a specific command. Sub-classes
 * of this class exist to 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
abstract class CommandRunner {
    /**
     * Runs the command.
     *
     * @param inManager   the interface to the module manager.
     * @param inCmdString the command string.
     *
     * @return the result of the command.
     *
     * @throws RuntimeException if there were errors executing the command.
     */
    abstract Object runCommand(ModuleManagerMXBean inManager,
                               String inCmdString)
            throws RuntimeException;

    /**
     * Returns the name of the command.
     *
     * @return the name of the command.
     */
    final String getName() {
        return mName;
    }

    /**
     * Creates an instance.
     *
     * @param inName the command name
     */
    protected CommandRunner(String inName) {
        mName = inName;
    }

    private final String mName;
}
