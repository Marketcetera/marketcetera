package org.marketcetera.util.log;

import java.util.EmptyStackException;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.Callable;
import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.misc.CollectionUtils;

/**
 * Manages the active locale.
 *
 * <p>Locale management relies on the concept of <i>scopes</i>. The
 * search for the currently active locale, effected by {@link
 * #getLocale()}, proceeds from the top-most to the bottom-most scope
 * until a non-null locale is identified.</p>
 *
 * <p>At the lowest scope resides the system default locale, which
 * cannot be altered via this class. Above it is the process-wide
 * locale (more precisely, specific to the caller's classloader),
 * managed via {@link #setProcessLocale(Locale)} and {@link
 * #getProcessLocale()}. Above it is a thread-specific stack of
 * locales that mirrors the thread's call stack. This locale stack is
 * manipulated indirectly, without direct access to stack management
 * operations. Locales are pushed and popped off the stack as code
 * blocks are executed via {@link #runWithLocale(Runnable,Locale)} or
 * {@link #runWithLocale(Callable,Locale)}. Or, the top of the stack
 * can be directly altered via {@link #setThreadLocale(Locale)} (you
 * can assume the stack has a top, and therefore use this method, even
 * if there was no explicit prior call to execute a code block).</p>
 *
 * <p>A null locale may be supplied as an argument, or be returned as
 * the result, by several methods as noted. Its intended semantics are
 * <i>skip me</i>: a null locale at a scope allows the locale at the
 * scope below (or, if null as well, the locale below it and so on) to
 * show through as the active locale.</p>
 *
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class ActiveLocale
{

    // CLASS DATA.

    private static InheritableThreadLocal<Stack<Locale>> sThreadStack=
        new InheritableThreadLocal<Stack<Locale>>() 
    {
        @Override
        protected Stack<Locale> initialValue()
        {
            Stack<Locale> stack=new Stack<Locale>();
            stack.push(null);
            return stack;
        }

        @Override
        protected Stack<Locale> childValue
            (Stack<Locale> parentStack)
        {
            Stack<Locale> stack=new Stack<Locale>();
            stack.push(CollectionUtils.getLastNonNull(parentStack));
            return stack;
        }
    };

    private static Locale sProcessLocale;


    // CLASS METHODS.

    /**
     * Clears all management settings for the calling thread. This
     * method is intended for testing purposes only.
     */

    static void clear()
    {
        Stack<Locale> stack=new Stack<Locale>();
        stack.push(null);
        sThreadStack.set(stack);
        setProcessLocale(null);
    }

    /**
     * Returns the caller's active locale.
     *
     * @return The locale.
     */

    public static Locale getLocale()
    {
        Locale locale=CollectionUtils.getLastNonNull(sThreadStack.get());
        if (locale!=null) {
            return locale;
        }
        if (getProcessLocale()!=null) {
            return getProcessLocale();
        }
        return Locale.getDefault();
    }

    /**
     * Sets the process-specific (more precisely, specific to the
     * caller's classloader) locale to the given one.
     *
     * @param locale The locale. It may be null.
     */

    public static void setProcessLocale
        (Locale locale)
    {
        sProcessLocale=locale;
    }

    /**
     * Returns the process-specific (more precisely, specific to the
     * caller's classloader) locale.
     *
     * @return The locale. It may be null.
     */

    public static Locale getProcessLocale()
    {
        return sProcessLocale;
    }

    /**
     * Sets the locale at the top of the thread-specific (that is,
     * specific to the caller's thread) locale stack to the given
     * locale.
     *
     * @param locale The locale. It may be null.
     */

    public static void setThreadLocale
        (Locale locale)
    {
        Stack<Locale> stack=sThreadStack.get();
        stack.pop();
        stack.push(locale);
    }

    /**
     * Pushes the given locale onto the thread-specific (that is,
     * specific to the caller's thread) locale stack. This call must
     * be paired up with a call to {@link #popLocale()}.
     *
     * @param locale The locale. It may be null.
     */

    public static void pushLocale
        (Locale locale)
    {
        sThreadStack.get().push(locale);
    }

    /**
     * Pop the locale at the top of the thread-specific (that is,
     * specific to the caller's thread) locale stack. This call must
     * be paired up with a call to {@link #pushLocale(Locale)}.
     *
     * @throws EmptyStackException Thrown if the locale stack is
     * empty.
     */

    public static void popLocale()
    {
        sThreadStack.get().pop();
    }

    /**
     * Initiates execution of the given runnable within the context of
     * the given locale as the active one. When execution ends, the
     * previously active locale is restored.
     *
     * @param runnable The runnable.
     * @param locale The locale. It may be null.
     */

    public static void runWithLocale
        (Runnable runnable,
         Locale locale)
    {
        pushLocale(locale);
        try {
            runnable.run();
        } finally {
            popLocale();
        }
    }

    /**
     * Initiates execution of the given callable within the context of
     * the given locale as the active one. When execution ends, the
     * previously active locale is restored, and the callable's return
     * value is returned.
     *
     * @param callable The callable.
     * @param locale The locale. It may be null.
     *
     * @return The callable's return value.
     *
     * @throws Exception Propagated from the callable's invocation.
     */

    public static <V> V runWithLocale
        (Callable<V> callable,
         Locale locale)
        throws Exception
    {
        pushLocale(locale);
        try {
            return callable.call();
        } finally {
            popLocale();
        }
    }


    // CONSTRUCTOR.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private ActiveLocale() {}
}
