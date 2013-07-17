package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.ModuleManagerMXBean;

/* $License$ */
/**
 * The create module command. This command creates a new
 * command instance.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
final class CreateModule extends CommandRunner {
    /**
     * Creates an instance.
     */
    protected CreateModule() {
        super("createModule");  //$NON-NLS-1$
    }

    @Override
    final Object runCommand(ModuleManagerMXBean inManager, String inCmdString) {
        int idx = inCmdString.indexOf(';');  //$NON-NLS-1$
        if (idx >= 0) {
            return inManager.createModule(inCmdString.substring(0, idx),
                    inCmdString.substring(++idx));
        } else {
            throw new IllegalArgumentException(
                    Messages.CREATE_MODULE_INVALID_SYNTAX.getText(inCmdString));
        }
    }
}
