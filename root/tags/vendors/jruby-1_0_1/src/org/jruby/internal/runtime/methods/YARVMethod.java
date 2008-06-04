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
 * Copyright (C) 2007 Ola Bini <ola@ologix.com>
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
package org.jruby.internal.runtime.methods;

import java.util.ArrayList;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyBinding;
import org.jruby.RubyModule;
import org.jruby.exceptions.JumpException;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.StaticScope;
import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.DynamicScope;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.Visibility;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.collections.SinglyLinkedList;
import org.jruby.ast.executable.YARVMachine;
import org.jruby.ast.executable.ISeqPosition;
import org.jruby.runtime.EventHook;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class YARVMethod extends DynamicMethod {
    private SinglyLinkedList cref;
    private YARVMachine.InstructionSequence iseq;
    private StaticScope staticScope;
    private Arity arity;

    public YARVMethod(RubyModule implementationClass, YARVMachine.InstructionSequence iseq, StaticScope staticScope, Visibility visibility, SinglyLinkedList cref) {
        super(implementationClass, visibility);
        this.staticScope = staticScope;
        this.iseq = iseq;
		this.cref = cref;

        boolean opts = iseq.args_arg_opts > 0 || iseq.args_rest > 0;
        boolean req = iseq.args_argc > 0;
        if(!req && !opts) {
            this.arity = Arity.noArguments();
        } else if(req && !opts) {
            this.arity = Arity.fixed(iseq.args_argc);
        } else if(opts && !req) {
            this.arity = Arity.optional();
        } else {
            this.arity = Arity.required(iseq.args_argc);
        }
    }
    
    public void preMethod(ThreadContext context, RubyModule clazz, IRubyObject self, String name, 
            IRubyObject[] args, boolean noSuper, Block block) {
        context.preDefMethodInternalCall(clazz, name, self, args, arity.required(), block, noSuper, cref, staticScope, this);
    }
    
    public void postMethod(ThreadContext context) {
        context.postDefMethodInternalCall();
    }

    public IRubyObject internalCall(ThreadContext context, RubyModule klazz, IRubyObject self, String name, IRubyObject[] args, boolean noSuper, Block block) {
    	assert args != null;
        
        Ruby runtime = context.getRuntime();
        
        try {
            prepareArguments(context, runtime, args);
            getArity().checkArity(runtime, args);

            if (runtime.hasEventHooks()) {
                traceCall(context, runtime, name);
            }

            DynamicScope sc = new DynamicScope(staticScope);
            for(int i = 0; i<args.length; i++) {
                sc.setValue(i,args[i],0);
            }

            return YARVMachine.INSTANCE.exec(context, self, sc, iseq.body);
        } catch (JumpException je) {
        	if (je.getJumpType() == JumpException.JumpType.ReturnJump && je.getTarget() == this) {
	                return (IRubyObject) je.getValue();
        	}
            
       		throw je;
        } finally {
            if (runtime.hasEventHooks()) {
                traceReturn(context, runtime, name);
            }
        }
    }

    private void prepareArguments(ThreadContext context, Ruby runtime, IRubyObject[] args) {
        context.setPosition(new ISeqPosition(iseq));

        int expectedArgsCount = iseq.args_argc;
        int restArg = iseq.args_rest;
        boolean hasOptArgs = iseq.args_arg_opts > 0;

        if (expectedArgsCount > args.length) {
            throw runtime.newArgumentError("Wrong # of arguments(" + args.length + " for " + expectedArgsCount + ")");
        }

        // optArgs and restArgs require more work, so isolate them and ArrayList creation here
        if (hasOptArgs || restArg != -1) {
            args = prepareOptOrRestArgs(context, runtime, args, expectedArgsCount, restArg, hasOptArgs);
        }
        
        context.setFrameArgs(args);
    }

    private IRubyObject[] prepareOptOrRestArgs(ThreadContext context, Ruby runtime, IRubyObject[] args, int expectedArgsCount, int restArg, boolean hasOptArgs) {
        if (restArg == 0 && hasOptArgs) {
            int opt = expectedArgsCount + iseq.args_arg_opts;

            if (opt < args.length) {
                throw runtime.newArgumentError("wrong # of arguments(" + args.length + " for " + opt + ")");
            }
        }
        
        int count = expectedArgsCount + iseq.args_arg_opts + iseq.args_rest;

        ArrayList allArgs = new ArrayList();
        
        // Combine static and optional args into a single list allArgs
        for (int i = 0; i < count && i < args.length; i++) {
            allArgs.add(args[i]);
        }

        if (restArg != 0) {
            for (int i = expectedArgsCount; i < args.length; i++) {
                allArgs.add(args[i]);
            }

            // only set in scope if named
            if (restArg >= 0) {
                RubyArray array = runtime.newArray(args.length - expectedArgsCount);
                for (int i = expectedArgsCount; i < args.length; i++) {
                    array.append(args[i]);
                }

                context.getCurrentScope().setValue(restArg, array, 0);
            }
        }
        
        args = (IRubyObject[])allArgs.toArray(new IRubyObject[allArgs.size()]);
        return args;
    }
    
    private void traceReturn(ThreadContext context, Ruby runtime, String name) {
        if (!runtime.hasEventHooks()) {
            return;
        }
        
        ISourcePosition position = context.getPreviousFramePosition();
        runtime.callEventHooks(context, EventHook.RUBY_EVENT_RETURN, position.getFile(), position.getStartLine(), name, getImplementationClass());
    }
    
    private void traceCall(ThreadContext context, Ruby runtime, String name) {
        if (!runtime.hasEventHooks()) {
            return;
        }
        
        ISourcePosition position = context.getPosition();
        
        runtime.callEventHooks(context, EventHook.RUBY_EVENT_CALL, position.getFile(), position.getStartLine(), name, getImplementationClass());
    }
    
    public Arity getArity() {
        return this.arity;
    }
    
    public DynamicMethod dup() {
        return new YARVMethod(getImplementationClass(), iseq, staticScope, getVisibility(), cref);
    }	
}// YARVMethod
