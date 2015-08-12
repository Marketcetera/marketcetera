package org.marketcetera.core;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;


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
        if(rawValue != null) {
            try {
                totalInstances = Integer.parseInt(rawValue);
            } catch (Exception ignored) {}
        }
        Validate.isTrue(totalInstances >= 1,
                        "Invalid instance count: " + totalInstances);
//        for(Map.Entry<Object,Object> property : System.getProperties().entrySet()) {
//            System.out.println(property.getKey() + " " + property.getValue());
//        }
//        System.out.println("\n\nArguments:\n\n");
//        for(String argument : arguments) {
//            System.out.println(argument);
//        }
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                // TODO add shutdown message
                SLF4JLoggerProxy.info(MultiInstanceApplicationContainer.class,
                                      "Shutting down now");
                try {
                    killProcesses();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        try {
            prepareHostId();
            prepareInstanceDir();
            for(int i=1;i<=totalInstances;i++) {
                launchProcess(i);
            }
            while(true) {
                // TODO monitor processes, sleepy time
                // TODO restart one if it dies?
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
    /**
     * @throws InterruptedException 
     * 
     *
     *
     */
    private static void killProcesses()
            throws InterruptedException
    {
        for(Process process : processInstances.values()) {
            try {
                process.destroy();
            } catch (Exception ignored) {}
        }
        for(Process process : processInstances.values()) {
            process.waitFor();
        }
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
    private static File getInstanceDir()
    {
        String appDir = getAppDir();
        String instanceDir = System.getProperty("org.marketcetera.instanceDir");
        File instanceDirFile = new File(appDir,
                                        instanceDir);
        return instanceDirFile;
    }
    private static String prepareHostId()
            throws IOException
    {
        // TODO write out uniquely identifying host id file if one does not exist
        // TODO lock? maybe delay a little on start for this reason
        File instanceDir = getInstanceDir();
        File hostFile = new File(instanceDir,
                                 "host.txt");
        String id = null;
        if(hostFile.exists()) {
            // this host has already been identified, return existing id
            id = StringUtils.trimToNull(FileUtils.readFileToString(hostFile,
                                                                   "UTF-8"));
        }
        if(id == null) {
            id = UUID.randomUUID().toString();
            FileUtils.write(hostFile,
                            id);
        }
        return id;
    }
    private static void prepareInstanceDir()
            throws IOException
    {
        File instanceDir = getInstanceDir();
        FileUtils.deleteDirectory(instanceDir);
        FileUtils.forceMkdir(instanceDir);
    }
    private static void writeInstanceVariable(String inName,
                                              String inValue,
                                              File inFile)
            throws IOException
    {
        FileUtils.write(inFile,
                        inName+"="+inValue+System.lineSeparator(),
                        true);
    }
    private static void buildClusterMemberList()
    {
        // TODO right now, we're coasting on multicast, but we need to identify the tcpip member list (host:port,host:port)
    }
    private static void launchProcess(int inInstanceNumber)
            throws IOException, InterruptedException
    {
        SLF4JLoggerProxy.info(MultiInstanceApplicationContainer.class,
                              "Launching instance {} of {}",
                              inInstanceNumber,
                              totalInstances);
        String javaPath = getJavaPath();
        String classpath = getClasspath();
        String appDir = getAppDir();
        String log4jConfigFile = getLog4jConfigFile();
        String hostId = prepareHostId();
        File instanceDir = new File(getInstanceDir(),
                                    "instance"+inInstanceNumber);
        File instanceConfDir = new File(instanceDir,
                                       File.separator+"conf");
        FileUtils.copyDirectory(new File(appDir+File.separator+"conf"),instanceConfDir);
        File instancePropertiesFile = new File(instanceConfDir,
                                               File.separator + "instance.properties");
        // write out instance props file
        writeInstanceVariable("metc.rpc.port",
                              String.valueOf(9000+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.ws.port",
                              String.valueOf(9100+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.sa.rpc.port",
                              String.valueOf(9200+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.sa.ws.port",
                              String.valueOf(9300+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.cluster.port",
                              String.valueOf(9400+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.stomp.port",
                              String.valueOf(9500+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.jms.port",
                              String.valueOf(9600+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.sa.jms.port",
                              String.valueOf(9700+inInstanceNumber-1),
                              instancePropertiesFile);
        writeInstanceVariable("metc.cluster.tcpip.members",
                              String.valueOf(9700+inInstanceNumber-1),
                              instancePropertiesFile);
        // TODO figure out how to build the instance env from the current env, this will qllow adding new params and such w/o code changes
        // TODO build instance dir with configs
        ProcessBuilder pb = new ProcessBuilder(javaPath,
                                               "-Xms"+getMinRam(),
                                               "-Xmx"+getMaxRam(),
                                               "-XX:MaxPermSize=1024m", // TODO this won't be needed for Java8
                                               "-cp",
                                               classpath,
                                               "-Dorg.marketcetera.appDir="+instanceDir,
                                               "-Dlog4j.configurationFile="+log4jConfigFile,
                                               "-Dmetc.instance="+String.valueOf(inInstanceNumber),
                                               "-Dmetc.max.instances="+String.valueOf(getTotalInstances()),
                                               "-Dmetc.host="+hostId,
                                               "org.marketcetera.core.ApplicationContainer");
        // TODO async thing for log4j
        // TODO cleanly transfer purt near everything to this env. this is to avoid having to make code changes to change how the instance is invoked.
//        Map<String,String> env = pb.environment();
//        System.out.println("Env: " + env);
//        env.put("VAR1", "myValue");
//        env.remove("OTHERVAR");
//        env.put("VAR2", env.get("VAR1") + "suffix");
//        pb.directory(new File("myDir"));
        // TODO fails to start?
        // TODO write log to correct directory
        File log = new File("log"+inInstanceNumber);
        pb.redirectErrorStream(true);
        pb.redirectOutput(Redirect.appendTo(log));
//        System.out.println("Starting instance " + inInstanceNumber + " " + pb.toString());
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
        Thread.sleep(1000);
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
