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

package org.jruby.compiler.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import jregex.Pattern;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBignum;
import org.jruby.RubyBoolean;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyFloat;
import org.jruby.RubyHash;
import org.jruby.RubyModule;
import org.jruby.RubyRange;
import org.jruby.RubyRegexp;
import org.jruby.RubyString;
import org.jruby.RubySymbol;
import org.jruby.ast.Node;
import org.jruby.ast.executable.Script;
import org.jruby.compiler.ArrayCallback;
import org.jruby.compiler.BranchCallback;
import org.jruby.compiler.ClosureCallback;
import org.jruby.compiler.Compiler;
import org.jruby.compiler.NotCompilableException;
import org.jruby.evaluator.EvaluationState;
import org.jruby.exceptions.JumpException;
import org.jruby.internal.runtime.GlobalVariables;
import org.jruby.javasupport.util.CompilerHelpers;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.ReOptions;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.CallType;
import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.CompiledBlock;
import org.jruby.runtime.CompiledBlockCallback;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.MethodIndex;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.Visibility;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;
import org.jruby.util.CodegenUtils;
import org.jruby.util.JRubyClassLoader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author headius
 */
public class StandardASMCompiler implements Compiler, Opcodes {
    private static final CodegenUtils cg = CodegenUtils.cg;
    private static final String THREADCONTEXT = cg.p(ThreadContext.class);
    private static final String RUBY = cg.p(Ruby.class);
    private static final String IRUBYOBJECT = cg.p(IRubyObject.class);
    
    private static final String METHOD_SIGNATURE =
            cg.sig(IRubyObject.class, new Class[] { ThreadContext.class, IRubyObject.class, IRubyObject[].class, Block.class });
    private static final String CLOSURE_SIGNATURE =
        cg.sig(IRubyObject.class, new Class[] { ThreadContext.class, IRubyObject.class, IRubyObject[].class });
    
    private static final int THREADCONTEXT_INDEX = 0;
    private static final int SELF_INDEX = 1;
    private static final int ARGS_INDEX = 2;
    private static final int CLOSURE_INDEX = 3;
    private static final int SCOPE_INDEX = 4;
    private static final int RUNTIME_INDEX = 5;
    private static final int VISIBILITY_INDEX = 6;
    private static final int LOCAL_VARS_INDEX = 7;
    
    private Stack SkinnyMethodAdapters = new Stack();
    private Stack arities = new Stack();
    private Stack scopeStarts = new Stack();
    
    private String classname;
    private String sourcename;
    
    //Map classWriters = new HashMacg.p();
    private ClassWriter classWriter;
    ClassWriter currentMultiStub = null;
    int methodIndex = -1;
    int multiStubCount = -1;
    int innerIndex = -1;
    
    int lastLine = -1;
    
    /**
     * Used to make determinations about non-local returns and similar flow control
     */
    boolean isCompilingClosure;
    
    /** Creates a new instance of StandardCompilerContext */
    public StandardASMCompiler(String classname, String sourcename) {
        this.classname = classname;
        this.sourcename = sourcename;
    }
    
    public StandardASMCompiler(Node node) {
        // determine new class name based on filename of incoming node
        // must generate unique classnames for evals, since they could be generated many times in a given run
        classname = "EVAL" + hashCode();
        sourcename = "EVAL" + hashCode();
    }
    
    public Class loadClass(JRubyClassLoader classLoader) throws ClassNotFoundException {
        classLoader.defineClass(cg.c(classname), classWriter.toByteArray());
        
        return classLoader.loadClass(cg.c(classname));
    }
    
    public void writeClass(File destination) throws IOException {
        writeClass(classname, destination, classWriter);
    }
    
    private void writeClass(String classname, File destination, ClassWriter writer) throws IOException {
        String fullname = classname + ".class";
        String filename = null;
        String path = null;
        if (fullname.lastIndexOf("/") == -1) {
            filename = fullname;
            path = "";
        } else {
            filename = fullname.substring(fullname.lastIndexOf("/") + 1);
            path = fullname.substring(0, fullname.lastIndexOf("/"));
        }
        // create dir if necessary
        File pathfile = new File(destination, path);
        pathfile.mkdirs();
        
        FileOutputStream out = new FileOutputStream(new File(pathfile, filename));
        
        out.write(writer.toByteArray());
    }
    
    public String getClassname() {
        return classname;
    }
    
    public String getSourcename() {
        return sourcename;
    }
    
    public ClassVisitor getClassVisitor() {
        return classWriter;
    }
    
    public SkinnyMethodAdapter getMethodAdapter() {
        return (SkinnyMethodAdapter)SkinnyMethodAdapters.peek();
    }
    
    public SkinnyMethodAdapter popMethodAdapter() {
        return (SkinnyMethodAdapter)SkinnyMethodAdapters.pop();
    }
    
    public void pushMethodAdapter(SkinnyMethodAdapter mv) {
        SkinnyMethodAdapters.push(mv);
    }
    
    public int getArity() {
        return ((Integer)arities.peek()).intValue();
    }
    
    public void pushArity(Arity arity) {
        arities.push(arity);
    }
    
    public Arity popArity() {
        return (Arity)arities.pop();
    }
    
    public void pushScopeStart(Label start) {
        scopeStarts.push(start);
    }
    
    public Label popScopeStart() {
        return (Label)scopeStarts.pop();
    }
    
    public void startScript() {
        classWriter = new ClassWriter(true);
        
        // Create the class with the appropriate class name and source file
        classWriter.visit(V1_4, ACC_PUBLIC + ACC_SUPER, classname, null, cg.p(Object.class), new String[] {cg.p(Script.class)});
        classWriter.visitSource(sourcename, null);
        
        createClassInit();
        createConstructor();
    }
    
    public void endScript() {
        // add Script#run impl, used for running this script with a specified threadcontext and self
        // root method of a script is always in __load__ method
        String methodName = "__file__";
        SkinnyMethodAdapter mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC, "run", METHOD_SIGNATURE, null, null));
        mv.start();
        
        // invoke __file__ with threadcontext, self, args (null), and block (null)
        // These are all +1 because run is an instance method where others are static
        mv.aload(THREADCONTEXT_INDEX + 1);
        mv.aload(SELF_INDEX + 1);
        mv.aload(ARGS_INDEX + 1);
        mv.aload(CLOSURE_INDEX + 1);
        
        mv.invokestatic(classname, methodName, METHOD_SIGNATURE);
        mv.areturn();
        mv.end();
        
        // add main impl, used for detached or command-line execution of this script with a new runtime
        // root method of a script is always in stub0, method0
        mv = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, "main", cg.sig(Void.TYPE, cg.params(String[].class)), null, null));
        mv.start();
        
        // new instance to invoke run against
        mv.newobj(classname);
        mv.dup();
        mv.invokespecial(classname, "<init>", cg.sig(Void.TYPE));
        
        // invoke run with threadcontext and topself
        mv.invokestatic(cg.p(Ruby.class), "getDefaultInstance", cg.sig(Ruby.class));
        mv.dup();
        
        mv.invokevirtual(RUBY, "getCurrentContext", cg.sig(ThreadContext.class));
        mv.swap();
        mv.invokevirtual(RUBY, "getTopSelf", cg.sig(IRubyObject.class));
        mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
        mv.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
        
        mv.invokevirtual(classname, "run", METHOD_SIGNATURE);
        mv.voidreturn();
        mv.end();
    }
    
    private void createConstructor() {
        ClassVisitor cv = getClassVisitor();
        
        SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<init>", cg.sig(Void.TYPE), null, null));
        mv.start();
        mv.aload(0);
        mv.invokespecial(cg.p(Object.class), "<init>",
                cg.sig(Void.TYPE));
        mv.voidreturn();
        mv.end();
    }
    
    private void createClassInit() {
        ClassVisitor cv = getClassVisitor();

        cv.visitField(ACC_STATIC | ACC_PRIVATE | ACC_FINAL, "$isClassLoaded", cg.ci(Boolean.TYPE), null, Boolean.FALSE);
        cv.visitField(ACC_STATIC | ACC_PRIVATE | ACC_FINAL, "$class", cg.ci(Class.class), null, null);
        
        SkinnyMethodAdapter mv = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC, "<clinit>", cg.sig(Void.TYPE), null, null));
        mv.start();
        
        // This is a little hacky...since clinit recurses, set a boolean so we don't continue trying to load class
        mv.getstatic(classname, "$isClassLoaded", cg.ci(Boolean.TYPE));
        Label doNotLoadClass = new Label();
        mv.ifne(doNotLoadClass);
        
        mv.ldc(Boolean.TRUE);
        mv.putstatic(classname, "$isClassLoaded", cg.ci(Boolean.TYPE));
        mv.ldc(cg.c(classname));
        mv.invokestatic(cg.p(Class.class), "forName", cg.sig(Class.class, cg.params(String.class)));
        mv.putstatic(classname, "$class", cg.ci(Class.class));
        
        mv.label(doNotLoadClass);
        mv.voidreturn();
        mv.end();
    }
    
    public Object beginMethod(String friendlyName, ClosureCallback args) {
        SkinnyMethodAdapter newMethod = new SkinnyMethodAdapter(getClassVisitor().visitMethod(ACC_PUBLIC | ACC_STATIC, friendlyName, METHOD_SIGNATURE, null, null));
        pushMethodAdapter(newMethod);
        
        newMethod.start();
        
        // set up a local IRuby variable
        newMethod.aload(THREADCONTEXT_INDEX);
        invokeThreadContext("getRuntime", cg.sig(Ruby.class));
        newMethod.astore(RUNTIME_INDEX);
        
        // set visibility
        newMethod.getstatic(cg.p(Visibility.class), "PRIVATE", cg.ci(Visibility.class));
        newMethod.astore(VISIBILITY_INDEX);
        
        // store the local vars in a local variable
        loadThreadContext();
        invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
        newMethod.dup();
        newMethod.astore(LOCAL_VARS_INDEX);
        newMethod.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
        newMethod.astore(SCOPE_INDEX);
        
        if (args != null) {
            args.compile(this);
        } else {
            pushArity(null);
        }
        
        // visit a label to start scoping for local vars in this method
        Label start = new Label();
        newMethod.label(start);
        pushScopeStart(start);
        
        return newMethod;
    }
    
    public void endMethod(Object token) {
        assert token instanceof SkinnyMethodAdapter;
        
        SkinnyMethodAdapter mv = (SkinnyMethodAdapter)token;
        // return last value from execution
        mv.areturn();
        
        // end of variable scope
        Label end = new Label();
        mv.label(end);
        
        // local variable for lvars array
        mv.visitLocalVariable("lvars", cg.ci(IRubyObject[].class), null, popScopeStart(), end, LOCAL_VARS_INDEX);
        
        mv.end();
        
        popMethodAdapter();
        popArity();
    }
    
    public void lineNumber(ISourcePosition position) {
        if (lastLine == (lastLine = position.getEndLine())) return; // did not change lines for this node, don't bother relabeling
        
        Label l = new Label();
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.label(l);
        // line numbers are zero-based; add one for them to make sense in an editor
        mv.visitLineNumber(position.getStartLine() + 1, l);
    }
    
    public void invokeAttrAssign(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();

        // start with [recv, args]
        
        // get args[length - 1] and stuff it under the receiver
        // dup args * 2
        mv.dup(); // [recv, args, args]
        mv.dup(); // [recv, args, args, args]
        mv.arraylength(); // [recv, args, args, len]
        mv.iconst_1(); // [recv, args, args, len, 1]
        mv.isub(); // [recv, args, args, len-1]
        // load from array
        mv.arrayload(); // [recv, args, val]
        mv.dup_x2(); // [val, recv, args, val]
        mv.pop(); // [val, recv, args]
        
        invokeDynamic(name, true, true, CallType.NORMAL, null, true); // [val, result]
        
        // pop result, use args[length - 1] captured above
        mv.pop(); // [val]
    }
    
    public void invokeDynamic(String name, boolean hasReceiver, boolean hasArgs, CallType callType, ClosureCallback closureArg, boolean attrAssign) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        String callSig = cg.sig(IRubyObject.class, cg.params(IRubyObject.class, IRubyObject[].class, ThreadContext.class, String.class, IRubyObject.class, CallType.class, Block.class));
        String callSigIndexed = cg.sig(IRubyObject.class, cg.params(IRubyObject.class, IRubyObject[].class, ThreadContext.class, Byte.TYPE, String.class, IRubyObject.class, CallType.class, Block.class));
        
        int index = MethodIndex.getIndex(name);
        
        if (hasArgs) {
            if (hasReceiver) {
                // Call with args
                // receiver already present
            } else {
                // FCall
                // no receiver present, use self
                loadSelf();
                // put self under args
                mv.swap();
            }
        } else {
            if (hasReceiver) {
                // receiver already present
                // empty args list
                mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
                
            } else {
                // VCall
                // no receiver present, use self
                loadSelf();
                
                // empty args list
                mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
            }
        }

        loadThreadContext();

        if (index != 0) {
            // load method index
            mv.ldc(new Integer(index));
        }

        mv.ldc(name);
        
        // load self for visibility checks
        loadSelf();
        
        mv.getstatic(cg.p(CallType.class), callType.toString(), cg.ci(CallType.class));
        
        if (closureArg == null) {
            mv.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
        } else {
            closureArg.compile(this);
        }
        
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        if (closureArg != null) {
            // wrap with try/catch for block flow-control exceptions
            // FIXME: for flow-control from containing blocks, but it's not working right;
            // stack is not as expected for invoke calls below...
            //mv.trycatch(tryBegin, tryEnd, tryCatch, cg.p(JumpException.class));

            mv.label(tryBegin);
        }
        
        if (attrAssign) {
            if (index != 0) {
                invokeUtilityMethod("doAttrAssignIndexed", callSigIndexed);
            } else {
                invokeUtilityMethod("doAttrAssign", callSig);
            }
        } else {
            if (index != 0) {
                invokeUtilityMethod("doInvokeDynamicIndexed", callSigIndexed);
            } else {
                invokeUtilityMethod("doInvokeDynamic", callSig);
            }
        }
        
        if (closureArg != null) {
            mv.label(tryEnd);

            // no physical break, terminate loop and skip catch block
            Label normalEnd = new Label();
            mv.go_to(normalEnd);

            mv.label(tryCatch);
            {
                loadClosure();
                invokeUtilityMethod("handleJumpException", cg.sig(IRubyObject.class, cg.params(JumpException.class, Block.class)));
            }

            mv.label(normalEnd);
        }
    }
    
    public void yield(boolean hasArgs) {
        loadClosure();
        
        SkinnyMethodAdapter method = getMethodAdapter();
        
        if (hasArgs) {
            method.swap();
            
            loadThreadContext();
            method.swap();
            
            // args now here
        } else {
            loadThreadContext();
            
            // empty args
            method.aconst_null();
        }
        
        method.aconst_null();
        method.aconst_null();
        method.ldc(Boolean.FALSE);
        
        method.invokevirtual(cg.p(Block.class), "yield", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, IRubyObject.class, IRubyObject.class, RubyModule.class, Boolean.TYPE)));
    }
    
    private void invokeIRubyObject(String methodName, String signature) {
        getMethodAdapter().invokeinterface(IRUBYOBJECT, methodName, signature);
    }
    
    public void loadThreadContext() {
        getMethodAdapter().aload(THREADCONTEXT_INDEX);
    }
    
    public void loadClosure() {
        loadThreadContext();
        invokeThreadContext("getFrameBlock", cg.sig(Block.class));
    }
    
    public void loadSelf() {
        getMethodAdapter().aload(SELF_INDEX);
    }
    
    public void loadRuntime() {
        getMethodAdapter().aload(RUNTIME_INDEX);
    }
    
    public void loadVisibility() {
        getMethodAdapter().aload(VISIBILITY_INDEX);
    }
    
    public void loadNil() {
        loadRuntime();
        invokeIRuby("getNil", cg.sig(IRubyObject.class));
    }
    
    public void loadSymbol(String symbol) {
        loadRuntime();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.ldc(symbol);
        
        invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
    }
    
    public void loadObject() {
        loadRuntime();
        
        invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
    }
    
    public void consumeCurrentValue() {
        getMethodAdapter().pop();
    }
    
    public void duplicateCurrentValue() {
        getMethodAdapter().dup();
    }
    
    public void swapValues() {
        getMethodAdapter().swap();
    }
    
    public void retrieveSelf() {
        loadSelf();
    }
    
    public void retrieveSelfClass() {
        loadSelf();
        invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
    }
    
    public void assignLocalVariable(int index) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.dup();

        mv.aload(SCOPE_INDEX);
        mv.swap();
        mv.ldc(new Integer(index));
        mv.swap();
        mv.arraystore();
    }
    
    public void assignLastLine() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.dup();

        loadThreadContext();
        invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
        mv.swap();
        mv.invokevirtual(cg.p(DynamicScope.class), "setLastLine", cg.sig(Void.TYPE, cg.params(IRubyObject.class)));
    }
    
    public void assignLocalVariableBlockArg(int argIndex, int varIndex) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // this is copying values, but it would be more efficient to just use the args in-place
        mv.aload(LOCAL_VARS_INDEX);
        mv.ldc(new Integer(varIndex));
        mv.aload(ARGS_INDEX);
        mv.ldc(new Integer(argIndex));
        mv.arrayload();
        mv.iconst_0();
        mv.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
    }
    
    public void retrieveLocalVariable(int index) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.aload(SCOPE_INDEX);
        mv.ldc(new Integer(index));
        mv.arrayload();
        nullToNil();
    }
    
    public void retrieveLastLine() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadThreadContext();
        invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
        mv.invokevirtual(cg.p(DynamicScope.class), "getLastLine", cg.sig(IRubyObject.class));
        nullToNil();
    }
    
    public void retrieveBackRef() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadThreadContext();
        invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
        mv.invokevirtual(cg.p(DynamicScope.class), "getBackRef", cg.sig(IRubyObject.class));
        nullToNil();
    }
    
    public void assignLocalVariable(int index, int depth) {
        if (depth == 0) {
            assignLocalVariable(index);
            return;
        }

        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.dup();

        mv.aload(LOCAL_VARS_INDEX);
        mv.swap();
        mv.ldc(new Integer(index));
        mv.swap();
        mv.ldc(new Integer(depth));
        mv.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
    }
    
    public void assignLocalVariableBlockArg(int argIndex, int varIndex, int depth) {
        if (depth == 0) {
            assignLocalVariableBlockArg(argIndex, varIndex);
            return;
        }

        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.aload(LOCAL_VARS_INDEX);
        mv.ldc(new Integer(varIndex));
        mv.aload(ARGS_INDEX);
        mv.ldc(new Integer(argIndex));
        mv.arrayload();
        mv.ldc(new Integer(depth));
        mv.invokevirtual(cg.p(DynamicScope.class), "setValue", cg.sig(Void.TYPE, cg.params(Integer.TYPE, IRubyObject.class, Integer.TYPE)));
    }
    
    public void retrieveLocalVariable(int index, int depth) {
        if (depth == 0) {
            retrieveLocalVariable(index);
            return;
        }
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.aload(LOCAL_VARS_INDEX);
        mv.ldc(new Integer(index));
        mv.ldc(new Integer(depth));
        mv.invokevirtual(cg.p(DynamicScope.class), "getValue", cg.sig(IRubyObject.class, cg.params(Integer.TYPE, Integer.TYPE)));
        nullToNil();
    }
    
    public void assignConstantInCurrent(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadThreadContext();
        mv.ldc(name);
        mv.dup2_x1();
        mv.pop2();
        invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
    }
    
    public void assignConstantInModule(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadThreadContext();
        mv.ldc(name);
        mv.swap2();
        invokeThreadContext("setConstantInCurrent", cg.sig(IRubyObject.class, cg.params(String.class, RubyModule.class, IRubyObject.class)));
    }
    
    public void assignConstantInObject(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // load Object under value
        loadRuntime();
        invokeIRuby("getObject", cg.sig(RubyClass.class, cg.params()));
        mv.swap();
        
        assignConstantInModule(name);
    }
    
    public void retrieveConstant(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadThreadContext();
        mv.ldc(name);
        invokeThreadContext("getConstant", cg.sig(IRubyObject.class, cg.params(String.class)));
    }

    public void retrieveConstantFromModule(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.visitTypeInsn(CHECKCAST, cg.p(RubyModule.class));
        mv.ldc(name);
        mv.invokevirtual(cg.p(RubyModule.class), "getConstantFrom", cg.sig(IRubyObject.class, cg.params(String.class)));
    }
    
    public void retrieveClassVariable(String name) {
        loadThreadContext();
        loadRuntime();
        loadSelf();
        getMethodAdapter().ldc(name);
        
        invokeUtilityMethod("fetchClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class)));
    }
    
    public void assignClassVariable(String name) {
        SkinnyMethodAdapter method = getMethodAdapter();
        
        loadThreadContext();
        method.swap();
        loadRuntime();
        method.swap();
        loadSelf();
        method.swap();
        getMethodAdapter().ldc(name);
        method.swap();
        
        invokeUtilityMethod("setClassVariable", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Ruby.class, IRubyObject.class, String.class, IRubyObject.class)));
    }
    
    private void loadScope(int depth) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        // get the appropriate array out of the scopes
        mv.aload(SCOPE_INDEX);
        mv.ldc(new Integer(depth - 1));
        mv.arrayload();
    }
    
    public void createNewFloat(double value) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        mv.ldc(new Double(value));
        
        invokeIRuby("newFloat", cg.sig(RubyFloat.class, cg.params(Double.TYPE)));
    }

    public void createNewFixnum(long value) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        mv.ldc(new Long(value));
        
        invokeIRuby("newFixnum", cg.sig(RubyFixnum.class, cg.params(Long.TYPE)));
    }
    
    public void createNewBignum(BigInteger value) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        mv.ldc(value.toString());
        
        mv.invokestatic(cg.p(RubyBignum.class) , "newBignum", cg.sig(RubyBignum.class,cg.params(Ruby.class,String.class)));
    }
    
    public void createNewString(ArrayCallback callback, int count) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        loadRuntime();
        invokeIRuby("newString", cg.sig(RubyString.class, cg.params()));
        for(int i = 0; i < count; i++) {
            callback.nextValue(this, null, i);
            mv.invokevirtual(cg.p(RubyString.class), "append", cg.sig(RubyString.class, cg.params(IRubyObject.class)));
        }
    }

    public void createNewString(ByteList value) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // FIXME: this is sub-optimal, storing string value in a java.lang.String again
        loadRuntime();
        mv.ldc(value.toString());
        
        invokeIRuby("newString", cg.sig(RubyString.class, cg.params(String.class)));
    }
    
    public void createNewSymbol(String name) {
        loadRuntime();
        getMethodAdapter().ldc(name);
        invokeIRuby("newSymbol", cg.sig(RubySymbol.class, cg.params(String.class)));
    }
    
    public void createNewArray() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        // put under object array already present
        mv.swap();
        
        invokeIRuby("newArrayNoCopy", cg.sig(RubyArray.class, cg.params(IRubyObject[].class)));
    }
    
    public void createEmptyArray() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        
        invokeIRuby("newArray", cg.sig(RubyArray.class, cg.params()));
    }
    
    public void createObjectArray(Object[] sourceArray, ArrayCallback callback) {
        buildObjectArray(IRUBYOBJECT, sourceArray, callback);
    }
    
    public void createObjectArray(int elementCount) {
        SkinnyMethodAdapter method = getMethodAdapter();
        
        // create the array
        method.ldc(new Integer(elementCount));
        method.anewarray(cg.p(IRubyObject.class));
        
        // for each element, swap with array and insert
        for (int i = 0; i < elementCount; i++) {
            method.dup_x1();
            method.dup_x1();
            method.pop();
            
            method.ldc(new Integer(elementCount - 1 - i));
            method.swap();
            
            method.arraystore();
        }
    }
    
    private void buildObjectArray(String type, Object[] sourceArray, ArrayCallback callback) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.ldc(new Integer(sourceArray.length));
        mv.anewarray(type);
        
        for (int i = 0; i < sourceArray.length; i++) {
            mv.dup();
            mv.ldc(new Integer(i));
            
            callback.nextValue(this, sourceArray, i);
            
            mv.arraystore();
        }
    }
    
    public void createEmptyHash() {
        SkinnyMethodAdapter mv = getMethodAdapter();

        loadRuntime();
        
        mv.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class)));
    }
    
    public void createNewHash(Object elements, ArrayCallback callback, int keyCount) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        
        // create a new hashmap
        mv.newobj(cg.p(HashMap.class));
        mv.dup();       
        mv.invokespecial(cg.p(HashMap.class), "<init>", cg.sig(Void.TYPE));
        
        for (int i = 0; i < keyCount; i++) {
            mv.dup();       
            callback.nextValue(this, elements, i);
            mv.invokevirtual(cg.p(HashMap.class), "put", cg.sig(Object.class, cg.params(Object.class, Object.class)));
            mv.pop();
        }
        
        loadNil();
        mv.invokestatic(cg.p(RubyHash.class), "newHash", cg.sig(RubyHash.class, cg.params(Ruby.class, Map.class, IRubyObject.class)));
    }
    
    public void createNewRange(boolean isExclusive) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadRuntime();
        
        mv.dup_x2();
        mv.pop();

        mv.ldc(new Boolean(isExclusive));
        
        mv.invokestatic(cg.p(RubyRange.class), "newRange", cg.sig(RubyRange.class, cg.params(Ruby.class, IRubyObject.class, IRubyObject.class, Boolean.TYPE)));
    }
    
    /**
     * Invoke IRubyObject.isTrue
     */
    private void isTrue() {
        invokeIRubyObject("isTrue", cg.sig(Boolean.TYPE));
    }
    
    public void performBooleanBranch(BranchCallback trueBranch, BranchCallback falseBranch) {
        Label afterJmp = new Label();
        Label falseJmp = new Label();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // call isTrue on the result
        isTrue();
        
        mv.ifeq(falseJmp); // EQ == 0 (i.e. false)
        trueBranch.branch(this);
        mv.go_to(afterJmp);
        
        // FIXME: optimize for cases where we have no false branch
        mv.label(falseJmp);
        falseBranch.branch(this);
        
        mv.label(afterJmp);
    }
    
    public void performLogicalAnd(BranchCallback longBranch) {
        Label afterJmp = new Label();
        Label falseJmp = new Label();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // dup it since we need to return appropriately if it's false
        mv.dup();
        
        // call isTrue on the result
        isTrue();
        
        mv.ifeq(falseJmp); // EQ == 0 (i.e. false)
        // pop the extra result and replace with the send part of the AND
        mv.pop();
        longBranch.branch(this);
        mv.label(falseJmp);
    }
    
    public void performLogicalOr(BranchCallback longBranch) {
        Label afterJmp = new Label();
        Label falseJmp = new Label();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // dup it since we need to return appropriately if it's false
        mv.dup();
        
        // call isTrue on the result
        isTrue();
        
        mv.ifne(falseJmp); // EQ == 0 (i.e. false)
        // pop the extra result and replace with the send part of the AND
        mv.pop();
        longBranch.branch(this);
        mv.label(falseJmp);
    }
    
    public void performBooleanLoop(BranchCallback condition, BranchCallback body, boolean checkFirst) {
        // FIXME: handle next/continue, break, etc
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        Label tryBegin = new Label();
        Label tryEnd = new Label();
        Label tryCatch = new Label();
        
        mv.trycatch(tryBegin, tryEnd, tryCatch, cg.p(JumpException.class));
        
        mv.label(tryBegin);
        {
            Label endJmp = new Label();
            if (checkFirst) {
                // calculate condition
                condition.branch(this);
                // call isTrue on the result
                isTrue();

                mv.ifeq(endJmp); // EQ == 0 (i.e. false)
            }

            Label topJmp = new Label();

            mv.label(topJmp);

            body.branch(this);

            // clear result after each loop
            mv.pop();

            // calculate condition
            condition.branch(this);
            // call isTrue on the result
            isTrue();

            mv.ifne(topJmp); // NE == nonzero (i.e. true)

            if (checkFirst) {
                mv.label(endJmp);
            }
        }
        
        mv.label(tryEnd);
        
        // no physical break, terminate loop and skip catch block
        Label normalBreak = new Label();
        mv.go_to(normalBreak);
        
        mv.label(tryCatch);
        {
            mv.dup();
            mv.invokevirtual(cg.p(JumpException.class), "getJumpType", cg.sig(JumpException.JumpType.class));
            mv.invokevirtual(cg.p(JumpException.JumpType.class), "getTypeId", cg.sig(Integer.TYPE));

            Label tryDefault = new Label();
            Label breakLabel = new Label();

            mv.lookupswitch(tryDefault, new int[] {JumpException.JumpType.BREAK}, new Label[] {breakLabel});

            // default is to just re-throw unhandled exception
            mv.label(tryDefault);
            mv.athrow();

            // break just terminates the loop normally, unless it's a block break...
            mv.label(breakLabel);
            
            // JRUBY-530 behavior
            mv.dup();
            mv.invokevirtual(cg.p(JumpException.class), "getTarget", cg.sig(Object.class));
            loadClosure();
            Label notBlockBreak = new Label();
            mv.if_acmpne(notBlockBreak);
            mv.dup();
            mv.aconst_null();
            mv.invokevirtual(cg.p(JumpException.class), "setTarget", cg.sig(Void.TYPE, cg.params(Object.class)));
            mv.athrow();

            mv.label(notBlockBreak);
            // target is not == closure, normal loop exit, pop remaining exception object
            mv.pop();
        }
        
        mv.label(normalBreak);
        loadNil();
    }
    
    public void performReturn() {
        if (isCompilingClosure) {
            throw new NotCompilableException("Can't compile non-local return");
        }
        
        // otherwise, just do a local return
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.areturn();
    }
    
    public void createNewClosure(StaticScope scope, int arity, ClosureCallback body, ClosureCallback args) {
        // FIXME: This isn't quite done yet; waiting to have full support for passing closures so we can test it
        ClassVisitor cv = getClassVisitor();
        SkinnyMethodAdapter method;
        
        String closureMethodName = "closure" + ++innerIndex;
        String closureFieldName = "_" + closureMethodName;
        
        // declare the field
        cv.visitField(ACC_PRIVATE | ACC_STATIC, closureFieldName, cg.ci(CompiledBlockCallback.class), null, null);
        
        ////////////////////////////
        // closure implementation
        method = new SkinnyMethodAdapter(cv.visitMethod(ACC_PUBLIC | ACC_STATIC, closureMethodName, CLOSURE_SIGNATURE, null, null));
     
        // FIXME: I don't like this pre/post state management.
        boolean previousIsCompilingClosure = isCompilingClosure;
        isCompilingClosure = true;
        
        pushMethodAdapter(method);
        
        method.start();
        
        // store the local vars in a local variable
        loadThreadContext();
        invokeThreadContext("getCurrentScope", cg.sig(DynamicScope.class));
        method.dup();
        method.astore(LOCAL_VARS_INDEX);
        method.invokevirtual(cg.p(DynamicScope.class), "getValues", cg.sig(IRubyObject[].class));
        method.astore(SCOPE_INDEX);
        
        // set up a local IRuby variable
        method.aload(THREADCONTEXT_INDEX);
        invokeThreadContext("getRuntime", cg.sig(Ruby.class));
        method.astore(RUNTIME_INDEX);
        
        // set up block arguments
        args.compile(this);
        
        // start of scoping for closure's vars
        Label start = new Label();
        method.label(start);
        
        // visit the body of the closure
        body.compile(this);
        
        method.areturn();
        
        // end of scoping for closure's vars
        Label end = new Label();
        method.label(end);
        method.end();
        
        popMethodAdapter();
        
        // FIXME: I don't like this pre/post state management.
        isCompilingClosure = previousIsCompilingClosure;
        
        method = getMethodAdapter();
        
        // Done with closure compilation
        /////////////////////////////////////////////////////////////////////////////
        
        // Now, store a compiled block object somewhere we can access it in the future
        
        // in current method, load the field to see if we've created a BlockCallback yet
        method.getstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
        Label alreadyCreated = new Label();
        method.ifnonnull(alreadyCreated);
        
        // no callback, construct it
        getCallbackFactory();
        
        method.ldc(closureMethodName);
        method.invokevirtual(cg.p(CallbackFactory.class), "getBlockCallback", cg.sig(CompiledBlockCallback.class, cg.params(String.class)));
        method.putstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
        
        method.label(alreadyCreated);
        
        // Construct the block for passing to the target method
        loadThreadContext();
        loadSelf();
        method.ldc(new Integer(arity));
        
        buildStaticScopeNames(method, scope);
        
        method.getstatic(classname, closureFieldName, cg.ci(CompiledBlockCallback.class));
        
        invokeUtilityMethod("createBlock", cg.sig(CompiledBlock.class, cg.params(ThreadContext.class, IRubyObject.class, Integer.TYPE, String[].class, CompiledBlockCallback.class)));
    }
    
    private void buildStaticScopeNames(SkinnyMethodAdapter method, StaticScope scope) {
        // construct static scope list of names
        method.ldc(new Integer(scope.getNumberOfVariables()));
        method.anewarray(cg.p(String.class));
        for (int i = 0; i < scope.getNumberOfVariables(); i++) {
            method.dup();
            method.ldc(new Integer(i));
            method.ldc(scope.getVariables()[i]);
            method.arraystore();
        }
    }
    
    /**
     * This is for utility methods used by the compiler, to reduce the amount of code generation 
     * necessary.  All of these live in CompilerHelpers.
     */
    private void invokeUtilityMethod(String methodName, String signature) {
        getMethodAdapter().invokestatic(cg.p(CompilerHelpers.class), methodName, signature);
    }
    
    private void invokeThreadContext(String methodName, String signature) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.invokevirtual(THREADCONTEXT, methodName, signature);
    }
    
    private void invokeIRuby(String methodName, String signature) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.invokevirtual(RUBY, methodName, signature);
    }
    
    private void getCallbackFactory() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        loadRuntime();
        getCompiledClass();
        mv.dup();
        mv.invokevirtual(cg.p(Class.class), "getClassLoader", cg.sig(ClassLoader.class));
        mv.invokestatic(cg.p(CallbackFactory.class), "createFactory", cg.sig(CallbackFactory.class, cg.params(Ruby.class, Class.class, ClassLoader.class)));
    }
    
    private void getCompiledClass() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.getstatic(classname, "$class", cg.ci(Class.class));
    }
    
    private void getRubyClass() {
        loadSelf();
        // FIXME: This doesn't seem *quite* right. If actually within a class...end, is self.getMetaClass the correct class? should be self, no?
        invokeIRubyObject("getMetaClass", cg.sig(RubyClass.class));
    }
    
    private void println() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.dup();
        mv.getstatic(cg.p(System.class), "out", cg.ci(PrintStream.class));
        mv.swap();
        
        mv.invokevirtual(cg.p(PrintStream.class), "println", cg.sig(Void.TYPE, cg.params(Object.class)));
    }
    
    public void defineAlias(String newName, String oldName) {
        getRubyClass();
        getMethodAdapter().ldc(newName);
        getMethodAdapter().ldc(oldName);
        getMethodAdapter().invokevirtual(cg.p(RubyModule.class), "defineAlias", cg.sig(Void.TYPE,cg.params(String.class,String.class)));
        loadNil();
        // TODO: should call method_added, and possibly push nil.
    }
    
    public void defineNewMethod(String name, StaticScope scope, ClosureCallback body, ClosureCallback args) {
        if (isCompilingClosure) {
            throw new NotCompilableException("Can't compile def within closure yet");
        }
        
        // TODO: build arg list based on number of args, optionals, etc
        ++methodIndex;
        String methodName = cg.cleanJavaIdentifier(name) + "__" + methodIndex;
        
        beginMethod(methodName, args);
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // callbacks to fill in method body
        body.compile(this);
        
        endMethod(mv);
        
        // return to previous method
        mv = getMethodAdapter();
        
        // prepare to call "def" utility method to handle def logic
        loadThreadContext();
        
        loadVisibility();
        
        loadSelf();
        
        // load the class we're creating, for binding purposes
        getCompiledClass();
        
        mv.ldc(name);
        
        mv.ldc(methodName);
        
        buildStaticScopeNames(mv, scope);
        
        mv.ldc(new Integer(0));
        
        invokeUtilityMethod("def",
                cg.sig(IRubyObject.class, cg.params(ThreadContext.class, Visibility.class, IRubyObject.class, Class.class, String.class, String.class, String[].class, Integer.TYPE)));
    }
    
    public void processRequiredArgs(Arity arity, int totalArgs) {
        SkinnyMethodAdapter newMethod = getMethodAdapter();
        
        // check arity
        newMethod.ldc(new Integer(arity.getValue()));
        newMethod.invokestatic(cg.p(Arity.class), "createArity", cg.sig(Arity.class, cg.params(Integer.TYPE)));
        loadRuntime();
        newMethod.aload(ARGS_INDEX);
        newMethod.invokevirtual(cg.p(Arity.class), "checkArity", cg.sig(Void.TYPE, cg.params(Ruby.class, IRubyObject[].class)));
        
        // optional has different checks for size
        if (!arity.isFixed()) {
            loadRuntime();
            newMethod.aload(ARGS_INDEX);
            newMethod.arraylength();
            newMethod.ldc(new Integer(totalArgs));
            invokeUtilityMethod("raiseArgumentError", cg.sig(Void.TYPE, cg.params(Ruby.class, Integer.TYPE, Integer.TYPE)));
        }
        
        // arraycopy all arguments into local vars array
        Label noArgs = new Label();
        newMethod.aload(ARGS_INDEX);
        newMethod.ifnull(noArgs);
        newMethod.aload(ARGS_INDEX);
        newMethod.arraylength();
        newMethod.ifeq(noArgs);
        newMethod.aload(LOCAL_VARS_INDEX);
        newMethod.aload(ARGS_INDEX);
        newMethod.dup();
        newMethod.arraylength();
        newMethod.invokevirtual(cg.p(DynamicScope.class), "setArgValues", cg.sig(Void.TYPE, cg.params(IRubyObject[].class, Integer.TYPE)));
        newMethod.label(noArgs);
        
        // push down the argument count of this method
        pushArity(arity);
    }
    
    public void assignOptionalArgs(Object object, int expectedArgsCount, int size, ArrayCallback optEval) {
        SkinnyMethodAdapter newMethod = getMethodAdapter();
        
        // NOTE: By the time we're here, arity should have already been checked. We proceed without boundschecking.
        
        // opt args are handled with a switch; the key is how many args we have coming in, and the cases are
        // each opt arg index. The cases fall-through, so remaining opt args are handled.
        newMethod.aload(ARGS_INDEX);
        newMethod.arraylength();
        
        Label defaultLabel = new Label();
        Label[] labels = new Label[size];
        
        for (int i = 0; i < size; i++) {
            labels[i] = new Label();
        }
        
        newMethod.tableswitch(expectedArgsCount, expectedArgsCount + size - 1, defaultLabel, labels);
        
        for (int i = 0; i < size; i++) {
            newMethod.label(labels[i]);
            optEval.nextValue(this, object, i);
            newMethod.pop();
        }
        
        newMethod.label(defaultLabel);
    }
    
    public void loadFalse() {
        loadRuntime();
        invokeIRuby("getFalse", cg.sig(RubyBoolean.class));
    }
    
    public void loadTrue() {
        loadRuntime();
        invokeIRuby("getTrue", cg.sig(RubyBoolean.class));
    }
    
    public void retrieveInstanceVariable(String name) {
        loadSelf();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.ldc(name);
        invokeIRubyObject("getInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class)));
        
        // check if it's null; if so, load nil
        mv.dup();
        Label notNull = new Label();
        mv.ifnonnull(notNull);
        
        // pop the dup'ed null
        mv.pop();
        // replace it with nil
        loadNil();
        
        mv.label(notNull);
    }
    
    public void assignInstanceVariable(String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadSelf();
        mv.swap();
        
        mv.ldc(name);
        mv.swap();
        
        invokeIRubyObject("setInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
    }
    
    public void assignInstanceVariableBlockArg(int argIndex, String name) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        loadSelf();
        mv.ldc(name);
        
        mv.aload(ARGS_INDEX);
        mv.ldc(new Integer(argIndex));
        mv.arrayload();
        
        invokeIRubyObject("setInstanceVariable", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
    }
    
    public void retrieveGlobalVariable(String name) {
        loadRuntime();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
        mv.ldc(name);
        mv.invokevirtual(cg.p(GlobalVariables.class), "get", cg.sig(IRubyObject.class, cg.params(String.class)));
    }
    
    public void assignGlobalVariable(String name) {
        loadRuntime();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
        mv.swap();
        mv.ldc(name);
        mv.swap();
        mv.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
    }
    
    public void assignGlobalVariableBlockArg(int argIndex, String name) {
        loadRuntime();
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        invokeIRuby("getGlobalVariables", cg.sig(GlobalVariables.class));
        mv.ldc(name);
        
        mv.aload(ARGS_INDEX);
        mv.ldc(new Integer(argIndex));
        mv.arrayload();
        
        mv.invokevirtual(cg.p(GlobalVariables.class), "set", cg.sig(IRubyObject.class, cg.params(String.class, IRubyObject.class)));
    }
    
    public void negateCurrentValue() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        isTrue();
        Label isTrue = new Label();
        Label end = new Label();
        mv.ifne(isTrue);
        loadTrue();
        mv.go_to(end);
        mv.label(isTrue);
        loadFalse();
        mv.label(end);
    }
    
    public void splatCurrentValue() {
        SkinnyMethodAdapter method = getMethodAdapter();
        
        method.invokestatic(cg.p(EvaluationState.class), "splatValue", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
    }
    
    public void singlifySplattedValue() {
        SkinnyMethodAdapter method = getMethodAdapter();
        method.invokestatic(cg.p(EvaluationState.class), "aValueSplat", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
    }
    
    public void ensureRubyArray() {
        invokeUtilityMethod("ensureRubyArray", cg.sig(RubyArray.class, cg.params(IRubyObject.class)));
    }
    
    public void forEachInValueArray(int start, int count, Object source, ArrayCallback callback) {
        SkinnyMethodAdapter method = getMethodAdapter();
        
        Label noMoreArrayElements = new Label();
        for (; start < count; start++) {
            // confirm we're not past the end of the array
            method.dup(); // dup the original array object
            method.invokevirtual(cg.p(RubyArray.class), "getLength", cg.sig(Integer.TYPE, cg.params()));
            method.ldc(new Integer(start));
            method.ifle(noMoreArrayElements); // if length <= start, end loop
            
            // extract item from array
            method.dup(); // dup the original array object
            method.ldc(new Integer(start)); // index for the item
            method.invokevirtual(cg.p(RubyArray.class), "entry",
                    cg.sig(IRubyObject.class, cg.params(Long.TYPE))); // extract item
            callback.nextValue(this, source, start);
        }
        method.label(noMoreArrayElements);
    }

    public void loadInteger(int value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performGEBranch(BranchCallback trueBranch,
                                BranchCallback falseBranch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performGTBranch(BranchCallback trueBranch,
                                BranchCallback falseBranch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performLEBranch(BranchCallback trueBranch,
                                BranchCallback falseBranch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void performLTBranch(BranchCallback trueBranch,
                                BranchCallback falseBranch) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void loadRubyArraySize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public void issueBreakEvent() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        mv.newobj(cg.p(JumpException.class));
        mv.dup();
        mv.getstatic(cg.p(JumpException.JumpType.class), "BreakJump", cg.ci(JumpException.JumpType.class));
        mv.invokespecial(cg.p(JumpException.class), "<init>", cg.sig(Void.TYPE, cg.params(JumpException.JumpType.class)));
        
        // set result into jump exception
        mv.dup_x1();
        mv.swap();
        mv.invokevirtual(cg.p(JumpException.class), "setValue", cg.sig(Void.TYPE, cg.params(Object.class)));
        
        mv.athrow();
    }

    public void asString() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.invokeinterface(cg.p(IRubyObject.class), "asString", cg.sig(RubyString.class, cg.params()));
    }

    public void nthRef(int match) {
        SkinnyMethodAdapter mv = getMethodAdapter();

        mv.ldc(new Integer(match));
        loadThreadContext();
        invokeThreadContext("getBackref", cg.sig(IRubyObject.class, cg.params()));
        mv.invokestatic(cg.p(RubyRegexp.class), "nth_match", cg.sig(IRubyObject.class, cg.params(Integer.TYPE,IRubyObject.class)));
    }

    public void match() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.invokevirtual(cg.p(RubyRegexp.class), "match2", cg.sig(IRubyObject.class, cg.params()));
    }

    public void match2() {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));
    }

    public void match3() {
        SkinnyMethodAdapter mv = getMethodAdapter();

        mv.dup();
        mv.visitTypeInsn(INSTANCEOF, cg.p(RubyString.class));

        Label l0 = new Label();
        mv.visitJumpInsn(IFEQ, l0);

        mv.invokevirtual(cg.p(RubyRegexp.class), "match", cg.sig(IRubyObject.class, cg.params(IRubyObject.class)));

        Label l1 = new Label();
        mv.visitJumpInsn(GOTO, l1);
        mv.visitLabel(l0);

        mv.swap();
        loadThreadContext();
        mv.swap();
        mv.ldc("=~");
        mv.swap();

        mv.invokeinterface(cg.p(IRubyObject.class), "callMethod", cg.sig(IRubyObject.class, cg.params(ThreadContext.class, String.class, IRubyObject.class)));
        mv.visitLabel(l1);
    }

    private int constants = 0;
    private String getNewConstant(String type, String name_prefix) {
        ClassVisitor cv = getClassVisitor();

        String realName;
        synchronized(this) {
            realName = name_prefix + constants++;
        }

        // declare the field
        cv.visitField(ACC_PRIVATE|ACC_STATIC, realName, type, null, null).visitEnd();
        return realName;
    }

    public void createNewRegexp(final ByteList value, final int options, final String lang) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        String name = getNewConstant(cg.ci(Pattern.class),"literal_re_");
        String name_flags = getNewConstant(cg.ci(Integer.TYPE),"literal_re_flags_");

        loadRuntime();

        // load string, for Regexp#source and Regexp#inspect
        String regexpString = null;
        if ((options & ReOptions.RE_UNICODE) > 0) {
            regexpString = value.toUtf8String();
        } else {
            regexpString = value.toString();
        }
        mv.ldc(regexpString);

        // in current method, load the field to see if we've created a Pattern yet

        mv.visitFieldInsn(GETSTATIC, classname, name, cg.ci(Pattern.class));
        mv.dup();

        Label alreadyCreated = new Label();
        mv.ifnonnull(alreadyCreated);
        mv.pop();
        mv.ldc(new Integer(options));
        invokeUtilityMethod("regexpLiteralFlags",cg.sig(Integer.TYPE,cg.params(Integer.TYPE)));
        mv.visitFieldInsn(PUTSTATIC, classname, name_flags, cg.ci(Integer.TYPE));
        
        loadRuntime();
        mv.ldc(regexpString);
        mv.ldc(new Integer(options));
        invokeUtilityMethod("regexpLiteral",cg.sig(Pattern.class,cg.params(Ruby.class,String.class,Integer.TYPE)));
        mv.dup();

        mv.visitFieldInsn(PUTSTATIC, classname, name, cg.ci(Pattern.class));

        mv.label(alreadyCreated);
        
        mv.visitFieldInsn(GETSTATIC, classname, name_flags, cg.ci(Integer.TYPE));
        if(null == lang) {
            mv.aconst_null();
        } else {
            mv.ldc(lang);
        }

        mv.invokestatic(cg.p(RubyRegexp.class), "newRegexp", cg.sig(RubyRegexp.class, cg.params(Ruby.class, String.class, Pattern.class, Integer.TYPE, String.class)));
    }
    
    public void defineClass(String name, StaticScope staticScope, ClosureCallback superCallback, ClosureCallback pathCallback, ClosureCallback bodyCallback) {
        // TODO: build arg list based on number of args, optionals, etc
        ++methodIndex;
        String methodName = "rubyclass__" + cg.cleanJavaIdentifier(name) + "__" + methodIndex;
        
        beginMethod(methodName, null);
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // class def bodies default to public visibility
        mv.getstatic(cg.p(Visibility.class), "PUBLIC", cg.ci(Visibility.class));
        mv.astore(VISIBILITY_INDEX);
        
        // Here starts the logic for the class definition
        loadRuntime();
        
        superCallback.compile(this);
        
        invokeUtilityMethod("prepareSuperClass", cg.sig(RubyClass.class, cg.params(Ruby.class, IRubyObject.class)));
        
        loadThreadContext();
        
        pathCallback.compile(this);
        
        invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
        
        mv.swap();
        
        mv.ldc(name);
        
        mv.swap();
        
        mv.invokevirtual(cg.p(RubyModule.class), "defineOrGetClassUnder", cg.sig(RubyClass.class, cg.params(String.class, RubyClass.class)));
        
        // set self to the class
        mv.dup();
        mv.astore(SELF_INDEX);
        
        // CLASS BODY
        loadThreadContext();
        mv.swap();
        
        // FIXME: this should be in a try/finally
        invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class)));
        
        bodyCallback.compile(this);
        
        loadThreadContext();
        invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
        
        endMethod(mv);
        
        // return to previous method
        mv = getMethodAdapter();
        
        // prepare to call class definition method
        loadThreadContext();
        loadSelf();
        mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
        mv.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
        
        mv.invokestatic(classname, methodName, METHOD_SIGNATURE);
    }
    
    public void defineModule(String name, StaticScope staticScope, ClosureCallback pathCallback, ClosureCallback bodyCallback) {
        // TODO: build arg list based on number of args, optionals, etc
        ++methodIndex;
        String methodName = "rubymodule__" + cg.cleanJavaIdentifier(name) + "__" + methodIndex;
        
        beginMethod(methodName, null);
        
        SkinnyMethodAdapter mv = getMethodAdapter();
        
        // module def bodies default to public visibility
        mv.getstatic(cg.p(Visibility.class), "PUBLIC", cg.ci(Visibility.class));
        mv.astore(VISIBILITY_INDEX);
        
        // Here starts the logic for the module definition
        loadThreadContext();
        
        pathCallback.compile(this);
        
        invokeUtilityMethod("prepareClassNamespace", cg.sig(RubyModule.class, cg.params(ThreadContext.class, IRubyObject.class)));
        
        mv.ldc(name);
        
        mv.invokevirtual(cg.p(RubyModule.class), "defineModuleUnder", cg.sig(RubyModule.class, cg.params(String.class)));
        
        // set self to the module
        mv.dup();
        mv.astore(SELF_INDEX);
        
        // MODULE BODY
        loadThreadContext();
        mv.swap();
        
        // FIXME: this should be in a try/finally
        invokeThreadContext("preCompiledClass", cg.sig(Void.TYPE, cg.params(RubyModule.class)));
        
        bodyCallback.compile(this);
        
        loadThreadContext();
        invokeThreadContext("postCompiledClass", cg.sig(Void.TYPE, cg.params()));
        
        endMethod(mv);
        
        // return to previous method
        mv = getMethodAdapter();
        
        // prepare to call class definition method
        loadThreadContext();
        loadSelf();
        mv.getstatic(cg.p(IRubyObject.class), "NULL_ARRAY", cg.ci(IRubyObject[].class));
        mv.getstatic(cg.p(Block.class), "NULL_BLOCK", cg.ci(Block.class));
        
        mv.invokestatic(classname, methodName, METHOD_SIGNATURE);
    }
    
    public void pollThreadEvents() {
        loadThreadContext();
        invokeThreadContext("pollThreadEvents", cg.sig(Void.TYPE));
    }
    
    private void nullToNil() {
        loadRuntime();
        invokeUtilityMethod("nullToNil", cg.sig(IRubyObject.class, cg.params(IRubyObject.class, Ruby.class)));
    }

    public void branchIfModule(BranchCallback moduleCallback, BranchCallback notModuleCallback) {
        SkinnyMethodAdapter mv = getMethodAdapter();
        mv.dup();
        mv.visitTypeInsn(INSTANCEOF, cg.p(RubyModule.class));
        
        Label falseJmp = new Label();
        Label afterJmp = new Label();

        mv.ifeq(falseJmp); // EQ == 0 (i.e. false)

        moduleCallback.branch(this);

        mv.go_to(afterJmp);
        mv.label(falseJmp);

        notModuleCallback.branch(this);

        mv.label(afterJmp);
    }
}
