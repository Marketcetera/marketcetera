package org.marketcetera.util.exec;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.except.I18NInterruptedException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A simple process executor. The executed process requires no input,
 * and the interleaved mix of its standard output and error streams is
 * directed to either the JVM's standard output or error stream, or to
 * an in-memory array.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public final class Exec
{
 
    // CLASS METHODS.

    /**
     * Executes the process with the given arguments and returns the
     * execution result. The first argument must be the required
     * executable/binary command. The working directory of the process
     * is set to the given one. The interleaved output and error
     * streams of the process are redirected/captured as per the given
     * setting.
     *
     * @param directory The working directory. Use null for the
     * JVM's current working directory.
     * @param disposition The disposition of the output.
     * @param args The process arguments.
     *
     * @return The execution result.
     *
     * @throws I18NInterruptedException Thrown if process execution
     * fails due to an interruption.
     * @throws I18NException Thrown if process execution fails for any
     * other reason.
     */

    public static ExecResult run
        (File directory,
         Disposition disposition,
         String... args)
        throws I18NException
    {
        String command=args[0];
        Process process;
        InputThread consumer=null;
        OutputStream out;
        try {

            // Configure process builder.

            ProcessBuilder builder=new ProcessBuilder(args);
            if (directory!=null) {
                builder.directory(directory);
            }
            builder.redirectErrorStream(true);

            // Choose output stream.

            boolean closeOut=false;
            if (disposition==Disposition.MEMORY) {
                out=new ByteArrayOutputStream();
                closeOut=true;
            } else if (disposition==Disposition.STDOUT) {
                out=System.out;
            } else {
                out=System.err;
            }

            // Start process and input consumer.

            process=builder.start();
            consumer=new InputThread
                (command,process.getInputStream(),out,closeOut);
            consumer.start();
        } catch (Throwable t) {
            if (consumer!=null) {
                consumer.interrupt();
            }
            throw ExceptUtils.wrap(t,new I18NBoundMessage1P
                                   (Messages.CANNOT_EXECUTE,command));
        }
        /* EXTREME TEST 1: uncomment this comment.
        try {
            Thread.sleep(1000);
            consumer.interrupt();
            process.getInputStream().close();
        } catch (Throwable t) {}
        */
        try {

            // Wait for process to end, and retain its exit code.

            process.waitFor();
            int exitValue=process.exitValue();

            // Wait for input consumer to end, and retain the captured
            // output.

            consumer.join();
            consumer=null;
            byte[] capture=null;
            if (disposition==Disposition.MEMORY) {
                capture=((ByteArrayOutputStream)out).toByteArray();
            }

            // Clean up.

            return new ExecResult(exitValue,capture);
        } catch (Throwable t) {
            if (consumer!=null) {
                consumer.interrupt();
            }
            throw ExceptUtils.wrap(t,new I18NBoundMessage1P
                                   (Messages.UNEXPECTED_TERMINATION,command));
        } finally {
            process.destroy();
        }
    }

    /**
     * Executes the process with the given arguments and returns the
     * execution result. The first argument must be the required
     * executable/binary command. The name of the working directory of
     * the process is set to the given one. The interleaved output and
     * error streams of the process are redirected/captured as per the
     * given setting.
     *
     * @param name The name of the working directory. Use null for the
     * JVM's current working directory.
     * @param disposition The disposition of the output.
     * @param args The process arguments.
     *
     * @return The execution result.
     *
     * @throws I18NException Thrown if process execution fails.
     */

    public static ExecResult run
        (String name,
         Disposition disposition,
         String... args)
        throws I18NException
    {
        File file=null;
        if (name!=null) {
            file=new File(name);
        }
        return run(file,disposition,args);
    }
}
