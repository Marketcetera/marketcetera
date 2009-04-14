package org.marketcetera.strategyagent;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.file.Deleter;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.unicode.UnicodeFileWriter;
import org.marketcetera.module.ModuleManager;
import org.marketcetera.module.ModuleURN;
import org.marketcetera.core.LoggerConfiguration;
import org.junit.Before;
import org.junit.After;
import org.junit.BeforeClass;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import javax.management.MBeanServer;
import java.util.Properties;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.lang.management.ManagementFactory;

/* $License$ */
/**
 * StrategyAgentTestBase
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public class StrategyAgentTestBase extends TestCaseBase {
    @Before
    public void setupLogLevel() {
        Logger.getRootLogger().setLevel(Level.ERROR);
        setLevel(StrategyAgent.class.getName(), Level.INFO);
        setLevel(TestAgent.class.getName(), Level.INFO);
        setLevel(StrategyAgent.SINK_DATA, Level.INFO);
    }

    @After
    public void cleanup() throws Exception {
        if(mRunner != null) {
            ModuleManager manager = mRunner.getManager();
            if(manager != null) {
                manager.stop();
            }
            mRunner = null;
        }
        if(mFile != null) {
            Deleter.apply(mFile);
            mFile = null;
        }
    }

    protected static void savePropertiesForProvider(
            ModuleURN inURN, Properties inProperties) throws IOException {
        File conf = new File(JarClassLoaderTest.CONF_DIR, new StringBuilder().
                append(inURN.providerType()).
                append("_").append(inURN.providerName()).
                append(".properties").toString());
        FileOutputStream fos = new FileOutputStream(conf);
        inProperties.store(fos, "");
        fos.close();
        conf.deleteOnExit();
    }

    protected static MBeanServer getMBeanServer() {
        return ManagementFactory.getPlatformMBeanServer();
    }

    private static void matchMessage(I18NMessage inMessage,
                                     LoggingEvent inMsg,
                                     Object... inMsgParams) {
        int i = inMessage.getParamCount();
        Object [] msgParams;
        boolean useRegex = false;
        if(inMsgParams.length < i) {
            //Use regex for parameters that are not supplied
            msgParams = Arrays.copyOf(inMsgParams, i);
            for(i = inMsgParams.length; i < msgParams.length; i++) {
                msgParams[i] = ".+";
                useRegex = true;
            }
        } else {
            msgParams = inMsgParams;
        }
        String expectedMsg = inMessage.getMessageProvider().getText(
                inMessage, msgParams);
        String actualMsg = inMsg.getMessage().toString();
        if (useRegex) {
            Pattern p = Pattern.compile(expectedMsg);
            org.junit.Assert.assertTrue(actualMsg, p.matcher(actualMsg).matches());
        } else {
            org.junit.Assert.assertEquals(expectedMsg, actualMsg);
        }
    }

    protected File createFileWithText(String... inLines) throws IOException {
        File f = File.createTempFile("com",".txt");
        UnicodeFileWriter writer = new UnicodeFileWriter(f);
        BufferedWriter bufWriter = new BufferedWriter(writer);
        for(String line: inLines) {
            bufWriter.write(line);
            bufWriter.newLine();
        }
        bufWriter.close();
        writer.close();
        f.deleteOnExit();
        mFile = f;
        return f;
    }

    protected LoggingEvent assertLastButXEvent(int inFromEnd,
                                                    Level inLevel,
                                                    String inLogger,
                                                    I18NMessage inMessage,
                                                    Object... inMsgParams) {
        LinkedList<LoggingEvent> events = getAppender().getEvents();
        org.junit.Assert.assertTrue("" + events.size(), events.size() > inFromEnd);
        LoggingEvent event = events.get(events.size() - inFromEnd - 1);
        assertEvent(event, inLevel,  inLogger, null, null);
        matchMessage(inMessage, event, inMsgParams);
        return event;
    }

    protected static void run(TestAgent inRunner, String... args) {
        StrategyAgent.run(inRunner, args);
    }

    protected TestAgent createAgent(boolean inWaitForever) {
        mRunner = new TestAgent(inWaitForever);
        return mRunner;
    }

    /**
     * Creates a subclass of the supplied super class with the supplied name.
     * The subclass simply extends the super class and has a default constructor
     * that calls super's default constructor.
     *
     * @param inSuperClass the super class
     * @param inSubClassName the fully qualified name of the subclass.
     *
     * @return the array containing sub class byte code.
     */
    protected static byte[] generateSubclass(
            Class inSuperClass, String inSubClassName) {
        ClassWriter cw = new ClassWriter(false);
        cw.visit(org.objectweb.asm.Opcodes.V1_6,
                org.objectweb.asm.Opcodes.ACC_PUBLIC,
                transformName(inSubClassName),null,
                transformName(inSuperClass.getName()),null);
        //Generate default constructor
        MethodVisitor mv = cw.visitMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC,
                "<init>", "()V", null, null);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
        mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL,
                transformName(inSuperClass.getName()), "<init>", "()V");
        mv.visitInsn(org.objectweb.asm.Opcodes.RETURN);
        mv.visitMaxs(1,1);
        cw.visitEnd();
        return cw.toByteArray();
    }
    /**
     * Creates a subclass of the supplied super class with the supplied name.
     * The subclass extends the super class and has a constructor
     * that accepts a parameter of type {@link ModuleURN} and supplies
     * it to the super class constructor.
     *
     * @param inSuperClass the super class
     * @param inSubClassName the fully qualified name of the subclass.
     *
     * @return the array containing sub class byte code.
     */
    protected static byte[] generateSubclassURNConstructor(
            Class inSuperClass, String inSubClassName) {
        ClassWriter cw = new ClassWriter(false);
        cw.visit(org.objectweb.asm.Opcodes.V1_6,
                org.objectweb.asm.Opcodes.ACC_PUBLIC,
                transformName(inSubClassName),null,
                transformName(inSuperClass.getName()),null);
        //Generate default constructor
        MethodVisitor mv = cw.visitMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC,
                "<init>", "(Lorg/marketcetera/module/ModuleURN;)V", null, null);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
        mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 1);
        mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKESPECIAL,
                transformName(inSuperClass.getName()), "<init>",
                "(Lorg/marketcetera/module/ModuleURN;)V");
        mv.visitInsn(org.objectweb.asm.Opcodes.RETURN);
        mv.visitMaxs(2,2);
        cw.visitEnd();
        return cw.toByteArray();
    }

    protected static String transformName(String inName) {
        return inName.replace('.','/');
    }

    /**
     * A test agent that prevents the strategy agent from exiting the
     * process.
     */
    protected static class TestAgent extends StrategyAgent {
        private TestAgent(boolean inWaitForever) {
            mWaitForever = inWaitForever;
            mExitCode = NO_EXIT;
        }

        /**
         * Returns the exit code of the agent.
         *
         * @return the exit code of the agent.
         */
        public int getExitCode() {
            return mExitCode;
        }
        @Override
        void exit(int inExitCode) {
            mExitCode = inExitCode;
        }

        @Override
        public void startWaitingForever() {
            if (mWaitForever) {
                super.startWaitingForever();
            }
        }

        private final boolean mWaitForever;
        private int mExitCode;
    }

    protected TestAgent mRunner;
    private File mFile;
    protected static final int NO_EXIT = -1;
}
