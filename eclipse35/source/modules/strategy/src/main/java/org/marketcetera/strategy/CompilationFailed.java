package org.marketcetera.strategy;

import static org.marketcetera.strategy.Messages.COMPILATION_FAILED;

import java.util.ArrayList;
import java.util.List;

import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Indicates that a strategy could not be compiled.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class CompilationFailed
        extends StrategyException
{
    private static final long serialVersionUID = -8620960760410053024L;
    /**
     * the list of diagnostics provided by the compiler
     */
    private final List<Diagnostic> diagnostics = new ArrayList<Diagnostic>();
    /**
     * Create a new CompilationFailed instance.
     *
     * @param inMessage
     */
    public CompilationFailed(Strategy inStrategy)
    {
        super(new I18NBoundMessage2P(COMPILATION_FAILED,
                                     inStrategy.toString(),
                                     "")); //$NON-NLS-1$
    }
    /**
     * Create a new CompilationFailed instance.
     *
     * @param inNested
     * @param inMessage
     */
    public CompilationFailed(Throwable inNested,
                             Strategy inStrategy)
    {
        super(inNested,
              new I18NBoundMessage2P(COMPILATION_FAILED,
                                     inStrategy.toString(),
                                     "")); //$NON-NLS-1$
    }
    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        for(Diagnostic diagnostic : getDiagnostics()) {
            output.append(diagnostic).append(System.getProperty("line.separator")); //$NON-NLS-1$
        }
        return output.toString();
    }
    /**
     * Gets the diagnostic information provided by the compiler.
     *
     * @return a <code>List&lt;Diagnostic&gt;</code> value
     */
    public List<Diagnostic> getDiagnostics()
    {
        return new ArrayList<Diagnostic>(diagnostics);
    }
    /**
     * Adds a piece of diagnostic information.
     *
     * @param inDiagnostic a <code>Diagnostic</code> value
     */
    void addDiagnostic(Diagnostic inDiagnostic)
    {
        diagnostics.add(inDiagnostic);
    }
    /**
     * Indicates what type of diagnostic was returned from the compiler.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    public static enum Type
    {
        /**
         * a compilation warning
         */
        WARNING,
        /**
         * a compilation failure
         */
        ERROR
    }
    /**
     * Diagnostic information provided by the compiler for a compilation error or warning.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since 1.0.0
     */
    @ClassVersion("$Id$")
    public static class Diagnostic
    {
        /**
         * Creates a warning diagnostic.
         *
         * @param inMessage a <code>String</code> value
         * @return a <code>Diagnostic</code> value
         */
        public static Diagnostic warning(String inMessage)
        {
            return new Diagnostic(Type.WARNING,
                                  inMessage);
        }
        /**
         * Creates an error diagnostic.
         *
         * @param inMessage a <code>String</code> value
         * @return a <code>Diagnostic</code> value
         */
        public static Diagnostic error(String inMessage)
        {
            return new Diagnostic(Type.ERROR,
                                  inMessage);
        }
        /**
         * the type of the diagnostic
         */
        private final Type type;
        /**
         * the message from the compiler
         */
        private final String message;
        /**
         * Create a new Diagnostic instance.
         *
         * @param inType a <code>Type</code> value
         * @param inMessage a <code>String</code> value
         */
        private Diagnostic(Type inType,
                           String inMessage)
        {
            type = inType;
            message = inMessage;
        }
        /**
         * Get the type value.
         *
         * @return a <code>Type</code> value
         */
        public final Type getType()
        {
            return type;
        }
        /**
         * Get the message value.
         *
         * @return a <code>String</code> value
         */
        public final String getMessage()
        {
            return message;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            return String.format("%s: %s", //$NON-NLS-1$
                                 type,
                                 message);
        }
    }
}
