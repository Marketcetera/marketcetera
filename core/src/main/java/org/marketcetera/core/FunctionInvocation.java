package org.marketcetera.core;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.marketcetera.util.misc.ClassVersion;

import com.google.common.collect.Lists;

/* $License$ */

/**
 * Represents a function invocation.
 * 
 * <p>This class is useful when parsing function invocations like <code>search(1,2,3)</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
@ClassVersion("$Id$")
public class FunctionInvocation
{
    /**
     * Parses the given input into a function invocation.
     * 
     * <p>Valid functions are of the form: <code>name(arg1...,argN)</code>. Empty
     * invocations are allowed, but the parens are required: <code>name()</code>.
     *
     * @param inInput a <code>String</code> value
     * @return a <code>FunctionInvocation</code> value
     * @throws IllegalArgumentException if the input cannot be parsed
     */
    public static FunctionInvocation parse(String inInput)
    {
        inInput = StringUtils.trimToNull(inInput);
        Validate.notNull(inInput);
        Validate.isTrue(invocation.matcher(inInput).matches());
        String name = StringUtils.trim(inInput.split("\\(")[0]); //$NON-NLS-1$
        String argumentInvocation = StringUtils.trim(inInput.substring(inInput.indexOf('(')+1,
                                                                       inInput.indexOf(')')));
        List<String> argumentList = Lists.newArrayList();
        if(!argumentInvocation.isEmpty()) {
            String[] arguments = argumentInvocation.split(","); //$NON-NLS-1$
            for(String argument : arguments) {
                argumentList.add(StringUtils.trim(argument));
            }
        }
        return new FunctionInvocation(name,
                                      argumentList.toArray(new String[argumentList.size()]));
    }
    /**
     * Get the functionName value.
     *
     * @return a <code>String</code> value
     */
    public String getFunctionName()
    {
        return functionName;
    }
    /**
     * Get the arguments value.
     *
     * @return a <code>String[]</code> value
     */
    public String[] getArguments()
    {
        return arguments;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(functionName).append(Arrays.toString(arguments));
        return builder.toString();
    }
    /**
     * Create a new FunctionInvocation instance.
     *
     * @param inFunctionName a <code>String</code> value
     * @param inArguments a <code>String...</code> value
     */
    private FunctionInvocation(String inFunctionName,
                               String...inArguments)
    {
        functionName = inFunctionName;
        arguments = inArguments;
    }
    /**
     * function name value
     */
    private final String functionName;
    /**
     * function arguments value, may be <code>null</code> or empty
     */
    private final String[] arguments;
    /**
     * pattern used to verify function invocation
     */
    private static final Pattern invocation = Pattern.compile("^( )*[A-Za-z0-9-_]+( )*\\(( )*(([A-Za-z(((\\-|\\+)?[0-9]+(.[0-9]+)?)?)]+)( )*(,( )*[A-Za-z(((\\-|\\+)?[0-9]+(.[0-9]+)?)?)]+)*( )*)?\\)( )*$"); //$NON-NLS-1$
}

