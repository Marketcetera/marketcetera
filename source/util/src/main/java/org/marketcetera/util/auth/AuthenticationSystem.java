package org.marketcetera.util.auth;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedList;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An authentication system. It is responsible for using a series of
 * authentication contexts to set the data of a group of data
 * holders. Roughly speaking, for each holder and context pair, a
 * setter is responsible for determining whether the context can (or
 * should) supply the holder's data. This conceptual design does not
 * directly translate to the class API; instead, expected usage takes
 * the following form:
 *
 * <ol>
 *
 * <li>The contexts and holders are created.</li>
 *
 * <li>For each context:
 *
 * <ol>
 *
 * <li> A setter is created for each holder, and added to the
 * context.</li>
 *
 * <li>The context is added to the authentication system.</li>
 *
 * </ol></li>
 *
 * <li> When {@link #setValues()} is called, the system dynamically
 * determines its set of holders by retrieving the union of all
 * holders from all setters from all its contexts.</li>
 *
 * </ol>
 *
 * Note that this API makes it possible for a context to have setters
 * for only a subset of the system holders. This is acceptable.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AuthenticationSystem
{

    // INSTANCE DATA.

    private LinkedList<Context<?>> mContexts;


    // CONSTRUCTORS.

    /**
     * Creates a new authentication system.
     */

    public AuthenticationSystem()
    {
        mContexts=new LinkedList<Context<?>>();
    }


    // INSTANCE METHODS.

    /**
     * Adds the given context to the receiver's contexts.
     *
     * @param context The context.
     */

    public void add
        (Context<?> context)
    {
        mContexts.add(context);
    }

    /**
     * Prints the receiver's usage instructions onto the given
     * stream. This is a compilation of the usage instructions of the
     * receiver's contexts.
     *
     * @param stream The stream.
     */

    public void printUsage
        (PrintStream stream)
    {
        for (Context<?> context:mContexts) {
            context.printUsage(stream);
            stream.println();
        }
    }

    /**
     * Sets the data of the receiver's holders. Once done, it checks
     * whether all holders with required data have had their data set.
     *
     * @return True if so.
     */

    public boolean setValues()
    {
        HashSet<Holder<?>> holders=new HashSet<Holder<?>>();
        for (Context<?> context:mContexts) {
            boolean needProcess=false;
            for (Setter<?> setter:context.getSetters()) {
                holders.add(setter.getHolder());
                needProcess=(needProcess || context.shouldProcess(setter));
            }
            if (!needProcess) {
                continue;
            }
            try {
                context.setValues();
            } catch (I18NException ex) {
                ExceptUtils.swallow(ex,this,Messages.CONTEXT_FAILED);
            }
        }
        boolean allSet=true;
        for (Holder<?> holder:holders) {
            if (!holder.isSet() && (holder.getMessage()!=null)) {
                holder.getMessage().error(this);
                allSet=false;
            }
        }
        return allSet;
    }
}
