package org.marketcetera.module;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Multiple instances of this module can exist.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: MultipleModule.java 82330 2012-04-10 16:29:13Z colin $")
public class MultipleModule extends ModuleBase {
    public MultipleModule(ModuleURN inURN, boolean inAutoStart) {
        super(inURN, inAutoStart);
        sModules.put(inURN,this);
    }

    @Override
    protected void preStart() throws ModuleException {
        super.preStart();
        if(mFailStart) {
            throw new ModuleException(
                    TestMessages.TEST_START_STOP_FAILURE);
        }
    }

    @Override
    public void preStop() throws ModuleException {
        super.preStop();
        if(mFailStop) {
            throw new ModuleException(
                    TestMessages.TEST_START_STOP_FAILURE);
        }
    }

    /**
     * If the preStart() method should throw an exception.
     *
     * @param inFailStart if the preStart() method should throw an exception.
     */
    public void setFailStart(boolean inFailStart) {
        mFailStart = inFailStart;
    }

    /**
     * If the preStop() method should throw an exception.
     *
     * @param inFailStop if the preStop() method should throw an exception.
     */
    public void setFailStop(boolean inFailStop) {
        mFailStop = inFailStop;
    }

    private boolean mFailStart;
    private boolean mFailStop;
}
