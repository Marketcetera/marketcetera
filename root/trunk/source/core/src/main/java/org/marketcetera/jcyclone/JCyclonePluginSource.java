package org.marketcetera.jcyclone;

import org.jcyclone.core.plugin.IPlugin;
import org.jcyclone.core.stage.IStageManager;
import org.jcyclone.core.internal.ISystemManager;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MessageKey;

/**
 * Abstract superclass for Jcyclone plugin sources
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$")
public abstract class JCyclonePluginSource extends JCycloneSource  implements IPlugin {
    public void initialize(IStageManager stagemgr, ISystemManager sysmgr, String pluginName) throws Exception {
        if(LoggerAdapter.isInfoEnabled(this)) {
            LoggerAdapter.info(MessageKey.JCYCLONE_PLUGIN_INIT.getLocalizedMessage(pluginName), this);
        }
        // lookup next stage. Since jcyclone doesn't provide a separate configProxy for plugins
        // we have to build the full key lookup path ourselves
        String nextStageName = stagemgr.getConfig().getString(JCycloneConstants.getPluginNextStageKey(pluginName));
        if(nextStageName!=null) {
            setNextStage(stagemgr.getStage(nextStageName).getSink(), nextStageName);
        } else {
            if(LoggerAdapter.isDebugEnabled(this)) {
                LoggerAdapter.debug("No next stage for " +pluginName, this);
            }
        }

    }
}
