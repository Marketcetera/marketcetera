package org.marketcetera.jcyclone;

/**
 * Collection of constants for JCyclone words
 * example: nextStage, ports, etc
 *
 * @author Toli Kuznets
 * @version $Id$
 */
public class JCycloneConstants {

    public static final String NEXT_STAGE = "nextStage";
    private static final String PLUGIN_PREFIX = "plugins.";
    private static final String INITARGS_SUFFIX = ".initargs.";

    public static String getPluginNextStageKey(String pluginName) 
    {
        return PLUGIN_PREFIX+pluginName+INITARGS_SUFFIX + NEXT_STAGE;
    }
}
