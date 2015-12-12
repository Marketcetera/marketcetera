package org.marketcetera.core;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.OsProcess;
import com.jezhumble.javasysmon.ProcessVisitor;

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
        totalInstances = getSystemPropertyAsInt(PARAM_METC_INSTANCES,
                                                totalInstances);
        // TODO message
        Validate.isTrue(totalInstances >= 1,
                        "Invalid instance count: " + totalInstances);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run()
            {
                // TODO message
                SLF4JLoggerProxy.info(STARTUP_CATEGORY,
                                      "Shutting down");
                try {
                    killProcesses();
                } catch (InterruptedException ignored) {}
            }
        });
        try {
            prepareHostId();
            prepareInstanceDir();
            for(int i=1;i<=totalInstances;i++) {
                launchProcess(i);
            }
            // keep alive forever so we can kill off the child processes when we die
            while(true) {
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            SLF4JLoggerProxy.warn(MultiInstanceApplicationContainer.class,
                                  e);
        }
    }
    /**
     * Kills all launched processes.
     *
     * @throws InterruptedException if the method is interrupted while waiting for the processes to end
     */
    private static void killProcesses()
            throws InterruptedException
    {
        synchronized(spawnProcessMutex) {
            for(Process process : processInstances.values()) {
                try {
                    process.destroy();
                } catch (Exception ignored) {}
            }
            for(Process process : processInstances.values()) {
                process.waitFor();
            }
        }
    }
    /**
     * Gets the Java executable path to use.
     *
     * @return a <code>String</code> value
     */
    private static String getJavaPath()
    {
        String javaHome = getAndValidateSystemProperty(PARAM_JAVA_HOME);
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
        String separator = getSystemProperty(PATH_SEPARATOR);
        for(URL url : ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs()) {
            if(separatorNeeded) {
                classpath.append(separator);
            }
            classpath.append(url.getPath());
            separatorNeeded = true;
        }
        return classpath.toString();
    }
    /**
     * Gets the system property with the given key as an integer.
     *
     * @param inKey a <code>String</code> value
     * @param inDefaultValue an <code>int</code> value
     * @return an <code>int</code> value
     * @throws NumberFormatException if the system property cannot be resolved as an integer
     */
    private static int getSystemPropertyAsInt(String inKey,
                                              int inDefaultValue)
    {
        String rawValue = getSystemProperty(inKey);
        if(rawValue == null) {
            return inDefaultValue;
        }
        return Integer.parseInt(rawValue);
    }
    /**
     * Gets the system property with the given key.
     *
     * @param inKey a <code>String</code> value
     * @param inDefaultValue an <code>Object</code> value
     * @return a <code>String</code> value or the value of the given default
     */
    @SuppressWarnings("unused")
    private static String getSystemProperty(String inKey,
                                            Object inDefaultValue)
    {
        String rawValue = getSystemProperty(inKey);
        if(rawValue == null) {
            return String.valueOf(inDefaultValue);
        }
        return rawValue;
    }
    /**
     * Gets the system property with the given key.
     *
     * @param inKey a <code>String</code> value
     * @return a <code>String</code> value or <code>null</code>
     */
    private static String getSystemProperty(String inKey)
    {
        return StringUtils.trimToNull(System.getProperty(inKey));
    }
    /**
     * Gets the system property with the given key.
     *
     * @param inKey a <code>String</code> value
     * @return a <code>String</code> value
     * @throws IllegalArgumentException if the given key cannot be resolved to a system property
     */
    private static String getAndValidateSystemProperty(String inKey)
    {
        String rawValue = getSystemProperty(inKey);
        // TODO message
        Validate.notNull(rawValue,
                         inKey + " must be defined");
        return rawValue;
    }
    /**
     * Gets the <code>appDir</code> value.
     *
     * @return a <code>String</code> value
     */
    private static String getAppDir()
    {
        return getAndValidateSystemProperty(ApplicationBase.APP_DIR_PROP);
    }
    /**
     * Gets the log4j configuration file path.
     *
     * @return a <code>String</code> value
     */
    private static String getLog4jConfigFile()
    {
        File log4jConfigFile = new File(getAndValidateSystemProperty(PARAM_LOG4J_CONFIGURATION_FILE));
        return log4jConfigFile.getAbsolutePath();
    }
    /**
     * Gets the output log directory.
     *
     * @return a <code>String</code> value
     */
    private static String getLogDir()
    {
        File logDir = new File(getAndValidateSystemProperty(PARAM_METC_LOG_DIR));
        return logDir.getAbsolutePath();
    }
    /**
     * Gets the stdout/stderr base log file name.
     *
     * @return a <code>String</code> value
     */
    private static String getLogName()
    {
        return getAndValidateSystemProperty(PARAM_METC_LOG_NAME);
    }
    /**
     * Gets the instance directory.
     *
     * @return a <code>File</code> value
     */
    private static File getInstanceDir()
    {
        String appDir = getAppDir();
        String instanceDir = getAndValidateSystemProperty(PARAM_INSTANCE_DIR);
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
        File hostFile = new File(getAppDir(),
                                 HOST_ID_NAME);
        String id = null;
        if(hostFile.exists()) {
            // this host has already been identified, return existing id
            id = StringUtils.trimToNull(FileUtils.readFileToString(hostFile,
                                                                   StandardCharsets.UTF_8));
            SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                   "Host file: {} exists, existing id is {}",
                                   hostFile.getAbsolutePath(),
                                   id);
        } else {
            SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                   "Host file: {} does not exist",
                                   hostFile.getAbsolutePath());
        }
        if(id == null) {
            id = UUID.randomUUID().toString();
            FileUtils.write(hostFile,
                            id);
        }
        return id;
    }
    /**
     * Prepares the instance directory.
     *
     * @throws IOException if an error occurs preparing the instance directory
     */
    private static void prepareInstanceDir()
            throws IOException
    {
        File instanceDir = getInstanceDir();
        FileUtils.deleteDirectory(instanceDir);
        FileUtils.forceMkdir(instanceDir);
    }
    /**
     * Writes the given name/value to the given file.
     *
     * @param inName a <code>String</code> value
     * @param inValue a <code>String</code> value
     * @param inFile a <code>File</code> value
     * @throws IOException if an error occurs writing the given name and value to the given file
     */
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
    /**
     * Generates the cluster member list for all instances.
     *
     * @return a <code>String</code> value
     */
    private static String buildClusterMemberList()
    {
        StringBuilder memberlist = new StringBuilder();
        String rawMemberList = getAndValidateSystemProperty(PARAM_METC_CLUSTER_TCPIP_MEMBERS);
        String[] members = rawMemberList.split(",");
        int clusterPort = getSystemPropertyAsInt(PARAM_METC_CLUSTER_PORT,
                                                 9400);
        for(String member : members) {
            for(int index=0;index<totalInstances;index++) {
                if(memberlist.length() != 0) {
                    memberlist.append(',');
                }
                memberlist.append(member).append(':').append(clusterPort+index);
            }
        }
        return memberlist.toString();
    }
    /**
     * Gets the amount of time to wait in ms between starting instances.
     *
     * @return a <code>long</code> value
     */
    private static long getInstanceStartDelay()
    {
        long result = defaultInstanceStartDelay;
        String rawValue = getSystemProperty(PARAM_METC_INSTANCE_START_DELAY);
        if(rawValue != null) {
            try {
                result = Long.parseLong(rawValue);
            } catch (Exception ignored) {}
        }
        return result;
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
                                    INSTANCE_DIR_NAME+inInstanceNumber);
        File instanceConfDir = new File(instanceDir,
                                       File.separator+CONF_DIR_NAME);
        FileUtils.copyDirectory(new File(appDir+File.separator+CONF_DIR_NAME),
                                instanceConfDir);
        File instancePropertiesFile = new File(instanceConfDir,
                                               File.separator + INSTANCE_PROPERTIES_FILE);
        // write out instance props file
        for(Map.Entry<Object,Object> entry : System.getProperties().entrySet()) {
            String key = String.valueOf(entry.getKey());
            if(key.startsWith(PARAM_METC_PORT)) {
                String value = String.valueOf(entry.getValue());
                key = key.substring(PARAM_METC_PORT.length());
                writeInstanceVariable(key,
                                      String.valueOf(Integer.parseInt(value)+inInstanceNumber-1),
                                      instancePropertiesFile);
            }
        }
        writeInstanceVariable(PARAM_METC_CLUSTER_TCPIP_MEMBERS,
                              buildClusterMemberList(),
                              instancePropertiesFile);
        String[] arguments = buildProcessArgumentList(inInstanceNumber,
                                                      instanceDir.getAbsolutePath());
        // TODO message
        SLF4JLoggerProxy.info(STARTUP_CATEGORY,
                              "Launching instance {} of {}",
                              inInstanceNumber,
                              totalInstances);
        SLF4JLoggerProxy.info(MultiInstanceApplicationContainer.class,
                              "Launching instance {} of {} with {}",
                              inInstanceNumber,
                              totalInstances,
                              Arrays.toString(arguments));
        // write execution to a start script
        // TODO windows version
        File startScript = new File(instanceDir+File.separator+"bin",
                                    "start_dare.sh");
        File stopScript = new File(instanceDir+File.separator+"bin",
                                   "stop_dare.sh");
        File darePid = new File(instanceDir+File.separator+"bin",
                                "dare.pid");
        // add "go to instance dir" to start script
        FileUtils.write(startScript,
                        "cd " + instanceDir.getAbsolutePath()+System.lineSeparator());
        int lineCount = arguments.length;
        int lineNumber = 1;
        for(String entry : arguments) {
            // TODO is this line different for windows?
            if(lineNumber++ == lineCount) {
                FileUtils.write(startScript,
                                entry+" &"+System.lineSeparator(),
                                true);
            } else {
                FileUtils.write(startScript,
                                entry+" \\"+System.lineSeparator(),
                                true);
            }
        }
        FileUtils.write(startScript,
                        "retval=$?"+System.lineSeparator(),
                        true);
        FileUtils.write(startScript,
                        "pid=$!"+System.lineSeparator(),
                        true);
        FileUtils.write(startScript,
                        "[ ${retval} -eq 0 ] && [ ${pid} -eq ${pid} ] && echo ${pid} > " + instanceDir.getAbsolutePath()+File.separator+"bin"+File.separator+"dare.pid"+System.lineSeparator(),
                        true);
        File log = new File(getLogDir(),
                            getLogName()+inInstanceNumber+".log");
        ProcessBuilder pb = new ProcessBuilder(arguments);
        pb.redirectErrorStream(true);
        pb.redirectOutput(Redirect.appendTo(log));
        int pid = spawnInstance(pb,
                                inInstanceNumber);
        FileUtils.write(darePid,
                        pid + System.lineSeparator());
        FileUtils.write(stopScript,
                        "cd " + instanceDir.getAbsolutePath()+File.separator+"bin"+System.lineSeparator());
        FileUtils.write(stopScript,
                        "if [ -f dare.pid ]" + System.lineSeparator(),
                        true);
        FileUtils.write(stopScript,
                        "then" + System.lineSeparator(),
                        true);
        FileUtils.write(stopScript,
                        "    kill `cat dare.pid`" + System.lineSeparator(),
                        true);
        FileUtils.write(stopScript,
                        "else" + System.lineSeparator(),
                        true);
        FileUtils.write(stopScript,
                        "    kill `ps -ef | grep metc.instance="+ inInstanceNumber +" | grep java | awk '{print $2}'`"+System.lineSeparator(),
                        true);
        FileUtils.write(stopScript,
                        "fi" + System.lineSeparator(),
                        true);
        // sleep to generate separation between the instances to help clearly identify the order of instances on the host
        Thread.sleep(getInstanceStartDelay());
    }
    /**
     * Spawns the instance described by the given arguments.
     *
     * @param inProcBuilder a <code>ProcessBuilder</code> value
     * @param inInstanceNumber an <code>int</code> value
     * @return an <code>int</code> value containing the spawned PID
     * @throws IOException if the instance could not be spawned
     */
    private static int spawnInstance(ProcessBuilder inProcBuilder,
                                     int inInstanceNumber)
            throws IOException
    {
        synchronized (spawnProcessMutex) {
            JavaSysMon monitor = new JavaSysMon();
            DirectChildProcessVisitor beforeVisitor = new DirectChildProcessVisitor(monitor);
            monitor.visitProcessTree(monitor.currentPid(),
                                     beforeVisitor);
            Set<Integer> alreadySpawnedProcesses = beforeVisitor.getPids();
            Process p = inProcBuilder.start();
            processInstances.put(inInstanceNumber,
                                 p);
            DirectChildProcessVisitor afterVisitor = new DirectChildProcessVisitor(monitor);
            monitor.visitProcessTree(monitor.currentPid(),
                                     afterVisitor);
            Set<Integer> newProcesses = afterVisitor.getPids();
            newProcesses.removeAll(alreadySpawnedProcesses);
            if(newProcesses.isEmpty()){
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "There is no new instance PID");
            } else if(newProcesses.size() > 1){
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "There are multiple new instance PIDs: {}",
                                       newProcesses);
            } else {
                int newPid = newProcesses.iterator().next();
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "New PID: {}",
                                       newPid);
                return newPid;
            }
            return -1;
        }
    }
    /**
     * Builds the process argument list.
     *
     * @param inInstanceDir an <code>int</code> value
     * @return a <code>String[]</code> value
     * @throws IOException if an error occurs building the process argument list
     */
    private static String[] buildProcessArgumentList(int inInstanceNumber,
                                                     String inInstanceDirName)
            throws IOException
    {
        List<String> arguments = new ArrayList<>();
        arguments.add(getJavaPath());
        arguments.add(DASH_D+"metc.instance=" + inInstanceNumber);
        arguments.add("-classpath");
        arguments.add(getClasspath());
        for(Map.Entry<Object,Object> entry : System.getProperties().entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = entry.getValue() == null ? null : StringUtils.trimToNull((String)entry.getValue());
            StringBuilder statement = new StringBuilder();
            if(key.startsWith(PARAM_METC_INSTANCEPORT)) {
                key = key.substring(PARAM_METC_INSTANCEPORT.length());
                statement.append(DASH_D).append(key);
                if(value != null) {
                    value = String.valueOf(Integer.parseInt(value) + inInstanceNumber -1);
                    statement.append('=').append(value);
                }
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "Adding {}",
                                       statement);
                arguments.add(statement.toString());
                continue;
            }
            if(key.startsWith(PARAM_METC_INSTANCE)) {
                key = key.substring(PARAM_METC_INSTANCE.length());
                if(key.startsWith("X")) {
                    key = key.substring(1);
                    statement.append(DASH_X).append(key);
                    if(value != null) {
                        statement.append('=').append(value);
                    }
                    SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                           "Adding {}",
                                           statement);
                    arguments.add(statement.toString());
                } else {
                    statement.append(DASH_D).append(key);
                    if(value != null) {
                        statement.append('=').append(value);
                    }
                    SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                           "Adding {}",
                                           statement);
                    arguments.add(statement.toString());
                }
            } else if(key.startsWith(PARAM_METC_SYSTEM)) {
                key = key.substring(PARAM_METC_SYSTEM.length());
                statement.append('-').append(key);
                if(value != null) {
                    statement.append('=').append(value);
                }
                SLF4JLoggerProxy.debug(MultiInstanceApplicationContainer.class,
                                       "Adding {}",
                                       statement);
                arguments.add(statement.toString());
            }
        }
        arguments.add(DASH_D+"metc.max.instances="+String.valueOf(totalInstances));
        arguments.add(DASH_D+PARAM_METC_HOST+"="+prepareHostId());
        arguments.add(DASH_D+ApplicationBase.APP_DIR_PROP+"="+inInstanceDirName);
        arguments.add(DASH_D+PARAM_LOG4J_CONFIGURATION_FILE+"="+getLog4jConfigFile());
        arguments.add(ApplicationContainer.class.getCanonicalName());
        return arguments.toArray(new String[arguments.size()]);
    }
    /**
     * Visits running processes.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class DirectChildProcessVisitor
            implements ProcessVisitor
    {
        /* (non-Javadoc)
         * @see com.jezhumble.javasysmon.ProcessVisitor#visit(com.jezhumble.javasysmon.OsProcess, int)
         */
        @Override
        public boolean visit(OsProcess inOsProcess,
                             int inChildIndex)
        {
            int currentPid = parent.currentPid();
            if(inOsProcess.processInfo().getParentPid() == currentPid) {
                newPids.add(inOsProcess.processInfo().getPid());
            }
            return false;
        }
        /**
         * Gets the new pids discovered for this parent.
         *
         * @return a <code>Set&lt;Integer&gt;</code> value
         */
        private Set<Integer> getPids()
        {
            return newPids;
        }
        /**
         * Create a new DirectChildProcessVisitor instance.
         *
         * @param inParent a <code>JavaSysMon</code> value
         */
        private DirectChildProcessVisitor(JavaSysMon inParent)
        {
            parent = inParent;
        }
        /**
         * current process (parent of vistor processes)
         */
        private final JavaSysMon parent;
        /**
         * query result, holds process ids identified
         */
        private Set<Integer> newPids = new HashSet<Integer>();
    }
    /**
     * tracks child instance processes
     */
    private static Map<Integer,Process> processInstances = new HashMap<>();
    /**
     * holds the number of instances to launch
     */
    private static int totalInstances = 1;
    /**
     * logger category for startup messages
     */
    public static final String STARTUP_CATEGORY = "metc.startup";
    /**
     * parameter which indicates the number of instances to launch
     */
    public static final String PARAM_METC_INSTANCES = "metc.total.instances";
    /**
     * java home parameter
     */
    public static final String PARAM_JAVA_HOME = "java.home";
    /**
     * used to indicate X params
     */
    public static final String DASH_X = "-X";
    /**
     * used to indicate D params
     */
    public static final String DASH_D = "-D";
    /**
     * config file name
     */
    public static final String CONF_DIR_NAME = "conf";
    /**
     * instance directory name
     */
    public static final String INSTANCE_DIR_NAME = "instance";
    /**
     * instance properties file name
     */
    public static final String INSTANCE_PROPERTIES_FILE = "instance.properties";
    /**
     * name of the host id file
     */
    public static final String HOST_ID_NAME = ".metc_host.txt";
    /**
     * path separator constant name
     */
    public static final String PATH_SEPARATOR = "path.separator";
    /**
     * log4j configuration file param name
     */
    public static final String PARAM_LOG4J_CONFIGURATION_FILE = "log4j.configurationFile";
    /**
     * metc-specific log dir name
     */
    public static final String PARAM_METC_LOG_DIR = "metc.logdir";
    /**
     * metc-specific log name param name
     */
    public static final String PARAM_METC_LOG_NAME = "metc.logname";
    /**
     * metc-specific instance dir param name
     */
    public static final String PARAM_INSTANCE_DIR = "org.marketcetera.instanceDir";
    /**
     * metc-specific cluster members param name
     */
    public static final String PARAM_METC_CLUSTER_TCPIP_MEMBERS = "metc.cluster.tcpip.members";
    /**
     * metc-specific cluster port param name
     */
    public static final String PARAM_METC_CLUSTER_PORT = "metc.port.metc.cluster.port";
    /**
     * metc-specific port prefix param name
     */
    public static final String PARAM_METC_PORT = "metc.port.";
    /**
     * metc-specific instance param name
     */
    public static final String PARAM_METC_INSTANCE = "metc.instance.";
    /**
     * metc-specific instance/port param name
     */
    public static final String PARAM_METC_INSTANCEPORT = "metc.instanceport.";
    /**
     * metc-specific host param name
     */
    public static final String PARAM_METC_HOST = "metc.host";
    /**
     * metc-specific system param
     */
    public static final String PARAM_METC_SYSTEM = "metc.system.";
    /**
     * metc-specific instance start delay param
     */
    public static final String PARAM_METC_INSTANCE_START_DELAY = "metc.start.delay";
    /**
     * guards access to process-specific stats
     */
    private static final Object spawnProcessMutex = new Object();
    /**
     * default delay between start of each instance
     */
    private static final long defaultInstanceStartDelay = 1000;
}
