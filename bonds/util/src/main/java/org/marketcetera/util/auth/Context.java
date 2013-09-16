package org.marketcetera.util.auth;

import java.io.PrintStream;
import java.util.LinkedList;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/**
 * A context, comprising a group of setters. The context provides an
 * operational context for its setters to set their respective holder
 * data. When multiple contexts attempt to set holder data in sequence
 * (via their respective, context-specific setters), then a later
 * <i>override</i> context will override prior successful settings,
 * while a non-override context will not.
 *
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class Context<T extends Setter<?>>
{

    // INSTANCE DATA.

    private I18NBoundMessage mName;
    private boolean mOverride;
    private LinkedList<T> mSetters;


    // CONSTRUCTORS.

    /**
     * Creates a new context with the given name and override role.
     *
     * @param name The context name.
     * @param override The override role.
     */

    public Context
        (I18NBoundMessage name,
         boolean override)
    {
        mName=name;
        mOverride=override;
        mSetters=new LinkedList<T>();
    }

    /**
     * Creates a new anonymous context with the given override role.
     *
     * @param override The override role.
     */

    public Context
        (boolean override)
    {
        this(null,override);
    }


    // INSTANCE METHODS.

    /**
     * Checks whether the receiver should process the specified
     * setter, that is, attempt to use it to set the holder
     * data. Override contexts will always process all setters;
     * non-override contexts will only process setters whose data is
     * not yet set.
     *
     * @param setter The setter.
     *
     * @return True if so.
     */

    public boolean shouldProcess
        (Setter<?> setter)
    {
        return (getOverride() || !setter.getHolder().isSet());
    }

    /**
     * Adds the given setter to the receiver's setters.
     *
     * @param setter The setter.
     */

    public void add
        (T setter)
    {
        mSetters.add(setter);
    }
 
    /**
     * Returns the receiver's setters.
     *
     * @return The setters.
     */

    public Iterable<T> getSetters()
    {
        return mSetters;
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name. It may be null for anonymous contexts.
     */

    public I18NBoundMessage getName()
    {
        return mName;
    }

    /**
     * Prints the receiver's usage instructions (which include its
     * name, if not anonymous) onto the given stream.
     *
     * @param stream The stream.
     */

    public void printUsage
        (PrintStream stream)
    {
        if (getName()!=null) {
            stream.print(getName().getText());
        } else {
            stream.print(Messages.CONTEXT_ANONYMOUS.getText());
        }
        if (getOverride()) {
            stream.print(' '); //$NON-NLS-1$
            stream.print(Messages.CONTEXT_OVERRIDES.getText());
        }
        stream.println();
        for (Setter<?> setter:getSetters()) {
            stream.print(' '); //$NON-NLS-1$
            stream.println(setter.getUsage().getText());
        }
    }

    /**
     * Checks whether the receiver is an override context.
     *
     * @return True iff so.
     */

    public boolean getOverride()
    {
        return mOverride;
    }

    /**
     * Sets the data of the holders associated with the receivers'
     * setters. Setters may be unable/unwilling to set their holder
     * data so, when this method returns, some holders may still have
     * unset data.
     *
     * @throws I18NException Thrown if the receiver cannot create an
     * operational context for its setters, and therefore the setters
     * cannot even attempt to set their holder data.
     */

    public abstract void setValues()
        throws I18NException;
}
