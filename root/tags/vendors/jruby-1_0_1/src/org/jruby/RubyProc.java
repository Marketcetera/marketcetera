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
 * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
 * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
 * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2002-2005 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
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
package org.jruby;

import org.jruby.exceptions.JumpException;
import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author  jpetersen
 */
public class RubyProc extends RubyObject {
    private Block block = Block.NULL_BLOCK;
    private boolean isLambda;

    public RubyProc(Ruby runtime, RubyClass rubyClass, boolean isLambda) {
        super(runtime, rubyClass);
        
        this.isLambda = isLambda;
    }
    
    private static ObjectAllocator PROC_ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
            RubyProc instance = RubyProc.newProc(runtime, false);

            instance.setMetaClass(klass);

            return instance;
        }
    };

    public static RubyClass createProcClass(Ruby runtime) {
        RubyClass procClass = runtime.defineClass("Proc", runtime.getObject(), PROC_ALLOCATOR);
        CallbackFactory callbackFactory = runtime.callbackFactory(RubyProc.class);
        
        procClass.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
        procClass.defineFastMethod("binding", callbackFactory.getFastMethod("binding"));
        procClass.defineMethod("call", callbackFactory.getOptMethod("call"));
        procClass.defineAlias("[]", "call");
        procClass.defineFastMethod("to_proc", callbackFactory.getFastMethod("to_proc"));
        procClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));

        procClass.getMetaClass().defineMethod("new", callbackFactory.getOptSingletonMethod("newInstance"));
        
        return procClass;
    }

    public Block getBlock() {
        return block;
    }

    // Proc class

    public static RubyProc newProc(Ruby runtime, boolean isLambda) {
        return new RubyProc(runtime, runtime.getClass("Proc"), isLambda);
    }
    public static RubyProc newProc(Ruby runtime, Block block, boolean isLambda) {
        RubyProc proc = new RubyProc(runtime, runtime.getClass("Proc"), isLambda);
        proc.callInit(NULL_ARRAY, block);
        
        return proc;
    }
    
    public IRubyObject initialize(IRubyObject[] args, Block procBlock) {
        Arity.checkArgumentCount(getRuntime(), args, 0, 0);
        if (procBlock == null) {
            throw getRuntime().newArgumentError("tried to create Proc object without a block");
        }
        
        if (isLambda && procBlock == null) {
            // TODO: warn "tried to create Proc object without a block"
        }
        
        block = procBlock.cloneBlock();
        block.isLambda = isLambda;
        block.setProcObject(this);

        return this;
    }
    
    protected IRubyObject doClone() {
    	RubyProc newProc = new RubyProc(getRuntime(), getRuntime().getClass("Proc"), isLambda);
    	
    	newProc.block = getBlock();
    	
    	return newProc;
    }
    
    /**
     * Create a new instance of a Proc object.  We override this method (from RubyClass)
     * since we need to deal with special case of Proc.new with no arguments or block arg.  In 
     * this case, we need to check previous frame for a block to consume.
     */
    public static IRubyObject newInstance(IRubyObject recv, IRubyObject[] args, Block block) {
        Ruby runtime = recv.getRuntime();
        IRubyObject obj = ((RubyClass) recv).allocate();
        
        // No passed in block, lets check next outer frame for one ('Proc.new')
        if (!block.isGiven()) {
            block = runtime.getCurrentContext().getPreviousFrame().getBlock();
        }
        
        obj.callMethod(runtime.getCurrentContext(), "initialize", args, block);
        return obj;
    }

    public IRubyObject binding() {
        return getRuntime().newBinding(block);
    }

    public IRubyObject call(IRubyObject[] args) {
        return call(args, null, Block.NULL_BLOCK);
    }

    // ENEBO: For method def others are Java to java versions
    public IRubyObject call(IRubyObject[] args, Block unusedBlock) {
        return call(args, null, Block.NULL_BLOCK);
    }
    
    public IRubyObject call(IRubyObject[] args, IRubyObject self, Block unusedBlock) {
        assert args != null;
        
        Ruby runtime = getRuntime();
        ThreadContext context = runtime.getCurrentContext();
        
        try {
            if (block.isLambda) {
                block.arity().checkArity(getRuntime(), args);
            }
            
            Block newBlock = block.cloneBlock();
            if (self != null) newBlock.setSelf(self);
            
            // if lambda, set new jump target in (duped) frame for returns
            if (newBlock.isLambda) newBlock.getFrame().setJumpTarget(this);
            
            return newBlock.call(context, args);
        } catch (JumpException je) {
            if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                if (block.isLambda) return (IRubyObject) je.getValue();
                
                throw runtime.newLocalJumpError("break", (IRubyObject)je.getValue(), "break from proc-closure");
            } else if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                Object target = je.getTarget();
                
                if (target == this || block.isLambda) return (IRubyObject) je.getValue();
                
                if (target == null) {
                    throw runtime.newLocalJumpError("return", (IRubyObject)je.getValue(), "unexpected return");
                }
                throw je;
            } else {
                throw je;
            }
        } 
    }

    public RubyFixnum arity() {
        return getRuntime().newFixnum(block.arity().getValue());
    }
    
    public RubyProc to_proc() {
    	return this;
    }
}
