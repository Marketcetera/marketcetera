package org.marketcetera.modules.object;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.module.DisplayName;
import org.marketcetera.module.DataFlowID;

import javax.management.MXBean;
import java.io.File;

/* $License$ */
/**
 * Management interface for Recorder Module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
@MXBean(true)
@DisplayName("Management Inteface for Recorder Module")
public interface ObjectRecorderMXBean {
    @DisplayName("The directory for storing data flow output")
    String getOutputDirectory();

    @DisplayName("The directory for storing data flow output")
    void setOutputDirectory(
            @DisplayName("The directory for storing data flow output")
            String inOutputDirectory);

    @DisplayName("The list of data flow IDs currently being saved by the module")
    DataFlowID[] getSavedFlows();
}
