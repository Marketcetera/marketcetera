package org.marketcetera.core;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Launches multiple {@link ApplicationContainer} instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MultiInstanceApplicationContainer
{
    /**
     * Main application routine.
     *
     * @param inArguments a <code>String[]</code> value
     */
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
     * Kills all launched processes.
     *
     * @throws InterruptedException if the method is interrupted while waiting for the processes to end
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
    /**
     * Gets the Java executable path to use.
     *
     * @return a <code>String</code> value
     */
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
    /**
     * Gets the Java classpath to use.
     *
     * @return a <code>String</code> value
     */
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
        return System.getProperty(ApplicationBase.APP_DIR_PROP);
    }
    private static String getLog4jConfigFile()
    {
        return System.getProperty("log4j.configurationFile");
    }
    private static String getLogDir()
    {
        return System.getProperty("metc.logdir");
    }
    private static String getLogName()
    {
        return System.getProperty("metc.logname");
    }
    private static File getInstanceDir()
    {
        String appDir = getAppDir();
        String instanceDir = System.getProperty("org.marketcetera.instanceDir");
        File instanceDirFile = new File(appDir,
                                        instanceDir);
        return instanceDirFile;
    }
    /**
     * Prepares and retrieves the host id for this host.
     * 
     * <p>Caller must guarantee that two instances do not call this method at the same time.
     *
     * @return a <code>String</code> value
     * @throws IOException if an error occurs creating or retrieving the host id
     */
    private static String prepareHostId()
            throws IOException
    {
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
        SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                               "Writing {}={} to {}",
                               inName,
                               inValue,
                               inFile);
        FileUtils.write(inFile,
                        inName+"="+inValue+System.lineSeparator(),
                        true);
    }
    private static String buildClusterMemberList()
    {
        StringBuilder memberlist = new StringBuilder();
        String rawMemberList = StringUtils.trimToNull(System.getProperty("metc.cluster.tcpip.members"));
        if(rawMemberList != null) {
            String[] members = rawMemberList.split(",");
            String rawClusterPort = System.getProperty("metc.port.metc.cluster.port");
            if(rawClusterPort == null) {
                rawClusterPort = "9400";
            }
            int clusterPort = Integer.parseInt(rawClusterPort);
            for(String member : members) {
                for(int index=0;index<totalInstances;index++) {
                    if(memberlist.length() != 0) {
                        memberlist.append(',');
                    }
                    memberlist.append(member).append(':').append(clusterPort+index);
                }
            }
        }
        return memberlist.toString();
    }
    /**
     * Launches the process with the given instance number.
     *
     * @param inInstanceNumber an <code>int</code> value
     * @throws IOException if an error occurs launching the process
     * @throws InterruptedException if the method is interrupted
     */
    private static void launchProcess(int inInstanceNumber)
            throws IOException, InterruptedException
    {
        String appDir = getAppDir();
        File instanceDir = new File(getInstanceDir(),
                                    "instance"+inInstanceNumber);
        File instanceConfDir = new File(instanceDir,
                                       File.separator+"conf");
        FileUtils.copyDirectory(new File(appDir+File.separator+"conf"),instanceConfDir);
        File instancePropertiesFile = new File(instanceConfDir,
                                               File.separator + "instance.properties");
        // write out instance props file
        for(Map.Entry<Object,Object> entry : System.getProperties().entrySet()) {
            String key = String.valueOf(entry.getKey());
            if(key.startsWith("metc.port.")) {
                String value = String.valueOf(entry.getValue());
                key = key.substring("metc.port.".length());
                writeInstanceVariable(key,
                                      String.valueOf(Integer.parseInt(value)+inInstanceNumber-1),
                                      instancePropertiesFile);
            }
        }
        writeInstanceVariable("metc.cluster.tcpip.members",
                              buildClusterMemberList(),
                              instancePropertiesFile);
        String[] arguments = builderProcessArgumentList(inInstanceNumber,
                                                        instanceDir.getAbsolutePath());
        SLF4JLoggerProxy.info(STARTUP_CATEGORY,
                              "Launching instance {} of {}",
                              inInstanceNumber,
                              totalInstances);
        SLF4JLoggerProxy.info(MultiInstanceApplicationContainer.class,
                              "Launching instance {} of {} with {}",
                              inInstanceNumber,
                              totalInstances,
                              Arrays.toString(arguments));
        ProcessBuilder pb = new ProcessBuilder(arguments);
        // TODO fails to start?
        File log = new File(getLogDir(),
                            getLogName()+inInstanceNumber+".log");
        pb.redirectErrorStream(true);
        pb.redirectOutput(Redirect.appendTo(log));
        Process p = pb.start();
        assert pb.redirectInput() == Redirect.PIPE;
        assert pb.redirectOutput().file() == log;
        assert p.getInputStream().read() == -1;
        processInstances.put(inInstanceNumber,
                             p);
        Thread.sleep(1000);
    }
    /**
     *
     *
     * @param inInstanceDir 
     * @return
     * @throws IOException 
     */
    private static String[] builderProcessArgumentList(int inInstanceNumber,
                                                       String inInstanceDirName)
            throws IOException
    {
        List<String> arguments = new ArrayList<>();
        arguments.add(getJavaPath());
        arguments.add("-classpath");
        arguments.add(getClasspath());
        for(Map.Entry<Object,Object> entry : System.getProperties().entrySet()) {
            String key = String.valueOf(entry.getKey());
            if(key.startsWith("metc.instanceport.")) {
                String value = String.valueOf(entry.getValue());
                key = key.substring("metc.instanceport.".length());
                value = String.valueOf(Integer.parseInt(value) + inInstanceNumber -1);
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "Adding -D{}={}",
                                       key,
                                       value);
                arguments.add("-D" + key + "=" + value);
            }
            if(key.startsWith("metc.instance.")) {
                String value = String.valueOf(entry.getValue());
                key = key.substring("metc.instance.".length());
                if(key.startsWith("X")) {
                    key = key.substring(1);
                    SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                           "Adding -X{}={}",
                                           key,
                                           value);
                    arguments.add("-X" + key + value);
                } else {
                    SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                           "Adding -D{}={}",
                                           key,
                                           value);
                    arguments.add("-D" + key + "=" + value);
                }
            }
        }
        arguments.add("-Dmetc.instance=" + inInstanceNumber);
        arguments.add("-Dmetc.max.instances="+String.valueOf(getTotalInstances()));
        arguments.add("-Dmetc.host="+prepareHostId());
        arguments.add("-D"+ApplicationBase.APP_DIR_PROP+"="+inInstanceDirName);
        arguments.add("-Dlog4j.configurationFile=" + getLog4jConfigFile());
        arguments.add(ApplicationContainer.class.getCanonicalName());
        return arguments.toArray(new String[arguments.size()]);
    }
    /**
     * 
     */
    private static Map<Integer,Process> processInstances = new HashMap<>();
    /**
     * 
     */
    private static int totalInstances = 1;
    /**
     * arguments passed to the cmd line
     */
    @SuppressWarnings("unused")
    private static String[] arguments;
    /**
     * 
     */
    public static final String STARTUP_CATEGORY = "metc.startup";
}
