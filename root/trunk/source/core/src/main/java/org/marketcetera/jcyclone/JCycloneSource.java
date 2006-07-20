package org.marketcetera.jcyclone;

import org.jcyclone.core.queue.ISink;
import org.marketcetera.core.LoggerAdapter;

/**
 * Abstract superclass for something that acts as a "source" in JCyclone -
 * basically can send output to the next stage
 * @author Toli Kuznets
 * @version $Id$
 */
public abstract class JCycloneSource {

    private ISink nextStage;

    public JCycloneSource() {
        // jcyclone constructor
    }

    /** to be overridden by subclasses for unit testing */
    protected void setNextStage(ISink inSink, String nextStageName) {
        nextStage = inSink;
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Setting next stage to be ["+nextStageName+"]", this);
        }
    }

    public ISink getNextStage() {
        return nextStage;
    }

    // used by stages and plugins
    public void destroy() throws Exception {
        if(LoggerAdapter.isDebugEnabled(this)) { LoggerAdapter.debug("Getting Destroyed", this); }
    }
}
