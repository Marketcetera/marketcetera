package org.marketcetera.core;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;


/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MultiInstanceApplicationContainer
{
    public static void main(String[] inArguments)
    {
        arguments = inArguments;
        String rawValue = StringUtils.trimToNull(System.getProperty("metc.total.instances"));
        if(rawValue == null) {
            try {
                totalInstances = Integer.parseInt(rawValue);
            } catch (Exception ignored) {}
        }
        Validate.isTrue(totalInstances >= 1,
                        "Invalid instance count: " + totalInstances);
        for(Map.Entry<Object,Object> property : System.getProperties().entrySet()) {
            System.out.println(property.getKey() + " " + property.getValue());
        }
        System.out.println("\n\nArguments:\n\n");
        for(String argument : arguments) {
            System.out.println(argument);
        }
        try {
            for(int i=1;i<=totalInstances;i++) {
                launchProcess(i);
            }
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis()<start+5000) {
                System.out.println(processInstances + " live instance(s)");
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * Get the totalInstances value.
     *
     * @return an <code>int</code> value
     */
    public static int getTotalInstances()
    {
        return totalInstances;
    }
    private static String getJavaPath()
    {
        String javaHome = StringUtils.trimToNull(System.getProperty("java.home"));
        Validate.notNull(javaHome,
                         "JAVA_HOME must be defined"); // TODO
        StringBuilder javaPath = new StringBuilder();
        javaPath.append(javaHome);
        javaPath.append(File.separator);
        javaPath.append("bin");
        javaPath.append(File.separator);
        if(System.getProperty("os.name").startsWith("Win")) {
            javaPath.append("java.exe");
        } else {
            javaPath.append("java");
        }
        File javaExec = new File(javaPath.toString());
        Validate.isTrue(javaExec.exists() && javaExec.canExecute(),
                        javaPath + " must exist and be executable");
        return javaPath.toString();
    }
    private static String getClasspath()
    {
        StringBuilder classpath = new StringBuilder();
        boolean separatorNeeded = false;
        String separator = System.getProperty("path.separator");
        for(URL url : ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs()) {
            if(separatorNeeded) {
                classpath.append(separator);
            }
            classpath.append(url.getPath());
            separatorNeeded = true;
        }
        return classpath.toString();
    }
    private static String getAppDir()
    {
        return System.getProperty("org.marketcetera.appDir");
    }
    private static String getLog4jConfigFile()
    {
        return System.getProperty("log4j.configurationFile");
    }
    private static String getMinRam()
    {
        return System.getProperty("metc.ms");
    }
    private static String getMaxRam()
    {
        return System.getProperty("metc.mx");
    }
    private static void launchProcess(int inInstanceNumber)
            throws IOException
    {
        String javaPath = getJavaPath();
        String classpath = getClasspath();
        String appDir = getAppDir();
        String log4jConfigFile = getLog4jConfigFile();
        ProcessBuilder pb = new ProcessBuilder(javaPath,
                                               "-Xms"+getMinRam(),
                                               "-Xmx"+getMaxRam(),
                                               "-XX:MaxPermSize=1024m", // TODO this won't be needed for Java8
                                               "-cp",
                                               classpath,
                                               "-Dorg.marketcetera.appDir="+appDir,
                                               "-Dlog4j.configurationFile="+log4jConfigFile,
                                               "-Dmetc.instance="+String.valueOf(inInstanceNumber),
                                               "org.marketcetera.core.ApplicationContainer");
        // TODO async thing for log4j
        // TODO cleanly transfer purt near everything to this env. this is to avoid having to make code changes to change how the instance is invoked.
        Map<String,String> env = pb.environment();
        System.out.println("Env: " + env);
//        env.put("VAR1", "myValue");
//        env.remove("OTHERVAR");
//        env.put("VAR2", env.get("VAR1") + "suffix");
//        pb.directory(new File("myDir"));
        // TODO fails to start?
        File log = new File("log"+inInstanceNumber);
        pb.redirectErrorStream(true);
        pb.redirectOutput(Redirect.appendTo(log));
        System.out.println("Starting instance " + inInstanceNumber + " " + pb.toString());
        Process p = pb.start();
        assert pb.redirectInput() == Redirect.PIPE;
        assert pb.redirectOutput().file() == log;
        assert p.getInputStream().read() == -1;
        processInstances.put(inInstanceNumber,
                             p);
/*
java -Xms384m -Xmx4096m -XX:MaxPermSize=1024m -Xloggc:dare_gc.out -server -Dorg.marketcetera.appDir=${METC_HOME}/${APPLICATION_DIR}\
 -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods\
 -Dlog4j.configurationFile=${METC_HOME}/${APPLICATION_DIR}/conf/log4j2.xml\
 -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector\
 -cp "${THE_CLASSPATH}"\
 org.marketcetera.core.ApplicationContainer $* &
 */
    }
    private static Map<Integer,Process> processInstances = new HashMap<>();
    /**
     * 
     */
    private static int totalInstances = 1;
    /**
     * arguments passed to the cmd line
     */
    private static String[] arguments;
}
