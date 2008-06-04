/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Charles O Nutter <headius@headius.com>
 *  
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.jruby.compiler;

import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.Arity;
import org.jruby.runtime.CallType;
import org.jruby.util.ByteList;

/**
 * Compiler represents the current state of a compiler and all appropriate
 * transitions and modifications that can be made within it. The methods here begin
 * and end a class for a given compile run, begin and end methods for the script being
 * compiled, set line number information, and generate code for all the basic
 * operations necessary for a script to run.
 * 
 * The intent of this interface is to provide a library-neutral set of functions for
 * compiling a given script using any backend or any output format.
 */
public interface Compiler {
    /**
     * Begin compilation for a script, preparing all necessary context and code
     * to support this script's compiled representation.
     */
    public void startScript();
    
    /**
     * End compilation for the current script, closing all context and structures
     * used for the compilation.
     */
    public void endScript();
    
    /**
     * Begin compilation for a method that has the specified number of local variables.
     * The returned value is a token that can be used to end the method later.
     * 
     * @param friendlyName The outward user-readable name of the method. A unique name will be generated based on this.
     * @param arity The arity of the method's argument list
     * @param localVarCount The number of local variables that will be used by the method.
     * @return An Object that represents the method within this compiler. Used in calls to
     * endMethod once compilation for this method is completed.
     */
    public Object beginMethod(String friendlyName, ClosureCallback argsHandler);
    
    /**
     * End compilation for the method associated with the specified token. This should
     * close out all structures created for compilation of the method.
     * 
     * @param token A token identifying the method to be terminated.
     */
    public void endMethod(Object token);
    
    /**
     * As code executes, values are assumed to be "generated", often by being pushed
     * on to some execution stack. Generally, these values are consumed by other
     * methods on the context, but occasionally a value must be "thrown out". This method
     * provides a way to discard the previous value generated by some other call(s).
     */
    public void consumeCurrentValue();
    
    /**
     * Push a copy the topmost value on the stack.
     */
    public void duplicateCurrentValue();
    
    /**
     * Swap the top and second values on the stack.
     */
    public void swapValues();
    
    /**
     * This method provides a way to specify a line number for the current piece of code
     * being compiled. The compiler may use this information to create debugging
     * information in a bytecode-format-dependent way.
     * 
     * @param position The ISourcePosition information to use.
     */
    public void lineNumber(ISourcePosition position);
    
    /**
     * Invoke the named method as a "function", i.e. as a method on the current "self"
     * object, using the specified argument count. It is expected that previous calls
     * to the compiler has prepared the exact number of argument values necessary for this
     * call. Those values will be consumed, and the result of the call will be generated.
     */
    public void invokeDynamic(String name, boolean hasReceiver, boolean hasArgs, CallType callType, ClosureCallback closureArg, boolean attrAssign);
    
    /**
     * Attr assign calls have slightly different semantics that normal calls, so this method handles those additional semantics.
     */
    public void invokeAttrAssign(String name);
    
    /**
     * Invoke the block passed into this method, or throw an error if no block is present.
     * If arguments have been prepared for the block, specify true. Otherwise the default
     * empty args will be used.
     */
    public void yield(boolean hasArgs);
    
    /**
     * Assigns the value on top of the stack to a local variable at the specified index, consuming
     * that value in the process. This assumes a lexical scoping depth of 0.
     * 
     * @param index The index of the local variable to which to assign the value.
     */
    public void assignLocalVariable(int index);
    
    /**
     * Assigns the special "last line" variable $_ in the outermost local scope.
     */
    public void assignLastLine();
    
    /**
     * Assigns the value from incoming block args to a local variable at the specified index, consuming
     * that value in the process. This assumes a lexical scoping depth of 0.
     * 
     * @param index The index of the local variable to which to assign the value.
     */
    public void assignLocalVariableBlockArg(int argIndex, int varIndex);
    
    /**
     * Retrieve the local variable at the specified index to the top of the stack, using whatever local variable store is appropriate.
     * This assumes the local variable in question should be present at the current lexical scoping depth (0).
     * 
     * @param index The index of the local variable to retrieve
     */
    public void retrieveLocalVariable(int index);
    
    /**
     * Retrieve the special "last line" variable $_ from the outermost local scope.
     */
    public void retrieveLastLine();
    
    /**
     * Retrieve the special "back ref" variable $~ from the outermost local scope.
     */
    public void retrieveBackRef();
    
    /**
     * Assign the value on top of the stack to a local variable at the specified index and
     * lexical scoping depth (0 = current scope), consuming that value in the process.
     * 
     * @param index The index in which to store the local variable
     * @param depth The lexical scoping depth in which to store the variable
     */
    public void assignLocalVariable(int index, int depth);
    
    /**
     * Assign the value from incoming block args to a local variable at the specified index and
     * lexical scoping depth (0 = current scope), consuming that value in the process.
     * 
     * @param index The index in which to store the local variable
     * @param depth The lexical scoping depth in which to store the variable
     */
    public void assignLocalVariableBlockArg(int argIndex, int varIndex, int depth);
    
    /**
     * Retrieve the local variable as the specified index and lexical scoping depth to the top of the stack,
     * using whatever local variable store is appropriate.
     * 
     * @param index The index of the local variable to retrieve
     * @param depth The lexical scoping depth from which to retrieve the variable
     */
    public void retrieveLocalVariable(int index, int depth);
    
    public void assignOptionalArgs(Object object, int expectedArgsCount, int size, ArrayCallback optEval);
    
    /**
     * Retrieve the current "self" and put a reference on top of the stack.
     */
    public void retrieveSelf();
    
    /**
     * Retrieve the current "self" object's metaclass and put a reference on top of the stack
     */
    public void retrieveSelfClass();
    
    public void retrieveClassVariable(String name);
    
    public void assignClassVariable(String name);
    
    /**
     * Generate a new "Fixnum" value.
     */
    public void createNewFixnum(long value);

    /**
     * Generate a new "Float" value.
     */
    public void createNewFloat(double value);

    /**
     * Generate a new "Bignum" value.
     */
    public void createNewBignum(java.math.BigInteger value);
    
    /**
     * Generate a new "String" value.
     */
    public void createNewString(ByteList value);

    /**
     * Generate a new dynamic "String" value.
     */
    public void createNewString(ArrayCallback callback, int count);

    /**
     * Generate a new "Symbol" value (or fetch the existing one).
     */
    public void createNewSymbol(String name);
    
    public void createObjectArray(Object[] elementArray, ArrayCallback callback);

    /**
     * Combine the top <pre>elementCount</pre> elements into a single element, generally
     * an array or similar construct. The specified number of elements are consumed and
     * an aggregate element remains.
     * 
     * @param elementCount The number of elements to consume
     */
    public void createObjectArray(int elementCount);

    /**
     * Given an aggregated set of objects (likely created through a call to createObjectArray)
     * create a Ruby array object.
     */
    public void createNewArray();

    /**
     * Create an empty Ruby array
     */
    public void createEmptyArray();
    
    /**
     * Create an empty Ruby Hash object and put a reference on top of the stack.
     */
    public void createEmptyHash();
    
    /**
     * Create a new hash by calling back to the specified ArrayCallback. It is expected that the keyCount
     * will be the actual count of key/value pairs, and the caller will handle passing an appropriate elements
     * collection in and dealing with the sequential indices passed to the callback.
     * 
     * @param elements An object holding the elements from which to create the Hash.
     * @param callback An ArrayCallback implementation to which the elements array and iteration counts
     * are passed in sequence.
     * @param keyCount the total count of key-value pairs to be constructed from the elements collection.
     */
    public void createNewHash(Object elements, ArrayCallback callback, int keyCount);
    
    /**
     * Create a new range. It is expected that the stack will contain the end and begin values for the range as
     * its topmost and second topmost elements.
     * 
     * @param isExclusive Whether the range is exclusive or not (inclusive)
     */
    public void createNewRange(boolean isExclusive);
    
    /**
     * Perform a boolean branch operation based on the Ruby "true" value of the top value
     * on the stack. If Ruby "true", invoke the true branch callback. Otherwise, invoke the false branch callback.
     * 
     * @param trueBranch The callback for generating code for the "true" condition
     * @param falseBranch The callback for generating code for the "false" condition
     */
    public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    /**
     * Perform a logical short-circuited Ruby "and" operation, using Ruby notions of true and false.
     * If the value on top of the stack is false, it remains and the branch is not executed. If it is true,
     * the top of the stack is replaced with the result of the branch.
     * 
     * @param longBranch The branch to execute if the "and" operation does not short-circuit.
     */
    public void performLogicalAnd(BranchCallback longBranch);
    
    
    /**
     * Perform a logical short-circuited Ruby "or" operation, using Ruby notions of true and false.
     * If the value on top of the stack is true, it remains and the branch is not executed. If it is false,
     * the top of the stack is replaced with the result of the branch.
     * 
     * @param longBranch The branch to execute if the "or" operation does not short-circuit.
     */
    public void performLogicalOr(BranchCallback longBranch);
    
    /**
     * Perform a boolean loop using the given condition-calculating branch and body branch. For
     * while loops, pass true for checkFirst. For statement-modifier while loops, pass false. For
     * unless loops, reverse the result of the condition after calculating it.
     * 
     * @param condition The code to execute for calculating the loop condition. A Ruby true result will
     * cause the body to be executed again.
     * @param body The body to executed for the loop.
     * @param checkFirst whether to check the condition the first time through or not.
     */
    public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst);
    
    /**
     * Return the current value on the top of the stack, taking into consideration surrounding blocks.
     */
    public void performReturn();
    
    /**
     * Create a new closure (block) using the given lexical scope information, call arity, and
     * body generated by the body callback. The closure will capture containing scopes and related information.
     * 
     * @param scope The static scoping information
     * @param arity The arity of the block's argument list
     * @param body The callback which will generate the closure's body
     */
    public void createNewClosure(StaticScope scope, int arity, ClosureCallback body, ClosureCallback args);
    
    /**
     * Define a new method with the given name, arity, local variable count, and body callback.
     * This will create a new compiled method and bind it to the given name at this point in
     * the program's execution.
     * 
     * @param name The name to which to bind the resulting method.
     * @param arity The arity of the method's argument list
     * @param localVarCount The number of local variables within the method
     * @param body The callback which will generate the method's body.
     */
    public void defineNewMethod(String name, StaticScope scope, ClosureCallback body, ClosureCallback args);
    
    public void processRequiredArgs(Arity arity, int totalArgs);
    
    /**
     * Define an alias for a new name to an existing oldName'd method.
     * 
     * @param newName The new alias to create
     * @param oldName The name of the existing method or alias
     */
    public void defineAlias(String newName, String oldName);
    
    public void assignConstantInCurrent(String name);
    
    public void assignConstantInModule(String name);
    
    public void assignConstantInObject(String name);
    
    /**
     * Retrieve the constant with the specified name available at the current point in the
     * program's execution.
     * 
     * @param name The name of the constant
     */
    public void retrieveConstant(String name);

    /**
     * Retreive a named constant from the RubyModule/RubyClass that's just been pushed.
     * 
     * @param name The name of the constant
     */
    public void retrieveConstantFromModule(String name);
    
    /**
     * Load a Ruby "false" value on top of the stack.
     */
    public void loadFalse();
    
    /**
     * Load a Ruby "true" value on top of the stack.
     */
    public void loadTrue();
    
    /**
     * Load a Ruby "nil" value on top of the stack.
     */
    public void loadNil();
    
    /**
     * Load the given string as a symbol on to the top of the stack.
     * 
     * @param symbol The symbol to load.
     */
    public void loadSymbol(String symbol);
    
    /**
     * Load the Object class
     */
    public void loadObject();
    
    /**
     * Retrieve the instance variable with the given name, based on the current "self".
     * 
     * @param name The name of the instance variable to retrieve.
     */
    public void retrieveInstanceVariable(String name);
    
    /**
     * Assign the value on top of the stack to the instance variable with the specified name
     * on the current "self". The value is consumed.
     * 
     * @param name The name of the value to assign.
     */
    public void assignInstanceVariable(String name);
    
    /**
     * Assign the value from incoming block args instance variable with the specified name
     * on the current "self".
     * 
     * @param index The index in the incoming arguments from which to get the ivar value
     * @param name The name of the ivar to assign.
     */
    public void assignInstanceVariableBlockArg(int index, String name);
    
    /**
     * Assign the top of the stack to the global variable with the specified name.
     * 
     * @param name The name of the global variable.
     */
    public void assignGlobalVariable(String name);
    
    /**
     * Assign the value from incoming block args to the global variable with the specified name.
     * 
     * @param index The index in the incoming arguments from which to get the gvar value
     * @param name The name of the global variable.
     */
    public void assignGlobalVariableBlockArg(int index, String name);
    
    /**
     * Retrieve the global variable with the specified name to the top of the stack.
     * 
     * @param name The name of the global variable.
     */
    public void retrieveGlobalVariable(String name);
    
    /**
     * Perform a logical Ruby "not" operation on the value on top of the stack, leaving the
     * negated result.
     */
    public void negateCurrentValue();
    
    /**
     * Convert the current value into a "splatted value" suitable for passing as
     * method arguments or disassembling into multiple variables.
     */
    public void splatCurrentValue();
    
    /**
     * Given a splatted value, extract a single value. If no splat or length is
     * zero, use nil
     */
    public void singlifySplattedValue();
    
    /**
     * Given an IRubyObject[] on the stack (or otherwise available as the present object)
     * call back to the provided ArrayCallback 'callback' for 'count' elements, starting with 'start'.
     */
    public void forEachInValueArray(int count, int start, Object source, ArrayCallback callback);
    
    /**
     * Ensures that the present value is an IRubyObject[] by wrapping it with one if it is not.
     */
    public void ensureRubyArray();
    
    /**
     * Load an integer value suitable for numeric comparisons
     */
    public void loadInteger(int value);
    
    /**
     * Perform a greater-than-or-equal test and branch, given the provided true and false branches.
     */
    public void performGEBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    /**
     * Perform a greater-than test and branch, given the provided true and false branches.
     */
    public void performGTBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    /**
     * Perform a greater-than-or-equal test and branch, given the provided true and false branches.
     */
    public void performLEBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    /**
     * Perform a greater-than test and branch, given the provided true and false branches.
     */
    public void performLTBranch(BranchCallback trueBranch, BranchCallback falseBranch);
    
    public void loadRubyArraySize();
    
    public void issueBreakEvent();

    public void asString();

    public void nthRef(int match);

    public void match();

    public void match2();

    public void match3();

    public void createNewRegexp(ByteList value, int options, String lang);
    
    public void defineClass(String name, StaticScope staticScope, ClosureCallback superCallback, ClosureCallback pathCallback, ClosureCallback bodyCallback);
    
    public void defineModule(String name, StaticScope staticScope, ClosureCallback pathCallback, ClosureCallback bodyCallback);
    
    public void pollThreadEvents();

    public void branchIfModule(BranchCallback moduleCallback, BranchCallback notModuleCallback);
}
