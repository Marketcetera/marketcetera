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
 * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
 * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
 * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
 * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
 * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
 * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
 * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
 * Copyright (C) 2005 David Corbin <dcorbin@users.sf.net>
 * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
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

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.jruby.runtime.Arity;
import org.jruby.runtime.Block;
import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.Frame;
import org.jruby.runtime.MethodIndex;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ObjectMarshal;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.marshal.MarshalStream;
import org.jruby.runtime.marshal.UnmarshalStream;

/**
 *
 * @author  jpetersen
 */
public class RubyException extends RubyObject {

    private Frame[] backtraceFrames;
    private IRubyObject backtrace;
    public IRubyObject message;
	public static final int TRACE_HEAD = 8;
	public static final int TRACE_TAIL = 4;
	public static final int TRACE_MAX = TRACE_HEAD + TRACE_TAIL + 6;

    protected RubyException(Ruby runtime, RubyClass rubyClass) {
        this(runtime, rubyClass, null);
    }

    public RubyException(Ruby runtime, RubyClass rubyClass, String message) {
        super(runtime, rubyClass);
        
        this.message = message == null ? runtime.getNil() : runtime.newString(message);
    }
    
    private static ObjectAllocator EXCEPTION_ALLOCATOR = new ObjectAllocator() {
        public IRubyObject allocate(Ruby runtime, RubyClass klass) {
            RubyException instance = new RubyException(runtime, klass);
            
            // for future compatibility as constructors move toward not accepting metaclass?
            instance.setMetaClass(klass);
            
            return instance;
        }
    };
    
    private static final ObjectMarshal EXCEPTION_MARSHAL = new ObjectMarshal() {
        public void marshalTo(Ruby runtime, Object obj, RubyClass type,
                              MarshalStream marshalStream) throws IOException {
            RubyException exc = (RubyException)obj;
            
            Map iVars = new HashMap(exc.getInstanceVariables());
            
            iVars.put("mesg", exc.message == null ? runtime.getNil() : exc.message);
            iVars.put("bt", exc.getBacktrace());
            
            marshalStream.dumpInstanceVars(iVars);
        }

        public Object unmarshalFrom(Ruby runtime, RubyClass type,
                                    UnmarshalStream unmarshalStream) throws IOException {
            RubyException exc = (RubyException)type.allocate();
            
            unmarshalStream.registerLinkTarget(exc);
            unmarshalStream.defaultInstanceVarsUnmarshal(exc);
            
            exc.message = exc.removeInstanceVariable("mesg");
            exc.set_backtrace(exc.removeInstanceVariable("bt"));
            
            return exc;
        }
    };

    public static RubyClass createExceptionClass(Ruby runtime) {
        RubyClass exceptionClass = runtime.defineClass("Exception", runtime.getObject(), EXCEPTION_ALLOCATOR);

        exceptionClass.setMarshal(EXCEPTION_MARSHAL);
        
        CallbackFactory callbackFactory = runtime.callbackFactory(RubyException.class);
        CallbackFactory classCB = runtime.callbackFactory(RubyClass.class);
        // TODO: could this just  be an alias for new?
        exceptionClass.getMetaClass().defineMethod("exception", classCB.getOptMethod("newInstance"));		
        exceptionClass.defineMethod("initialize", callbackFactory.getOptMethod("initialize"));
        exceptionClass.defineFastMethod("exception", callbackFactory.getFastOptMethod("exception"));
        exceptionClass.defineFastMethod("to_s", callbackFactory.getFastMethod("to_s"));
        exceptionClass.defineFastMethod("to_str", callbackFactory.getFastMethod("to_str"));
        exceptionClass.defineFastMethod("message", callbackFactory.getFastMethod("to_str"));
        exceptionClass.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
        exceptionClass.defineFastMethod("backtrace", callbackFactory.getFastMethod("backtrace"));		
        exceptionClass.defineFastMethod("set_backtrace", callbackFactory.getFastMethod("set_backtrace", RubyKernel.IRUBY_OBJECT));		

        return exceptionClass;
    }

    public static RubyException newException(Ruby runtime, RubyClass excptnClass, String msg) {
        return new RubyException(runtime, excptnClass, msg);
    }
    
    public void setBacktraceFrames(Frame[] backtraceFrames) {
        this.backtraceFrames = backtraceFrames;
    }
    
    public IRubyObject getBacktrace() {
        if (backtrace == null) {
            backtrace = backtraceFrames == null ? getRuntime().getNil() : ThreadContext.createBacktraceFromFrames(getRuntime(), backtraceFrames);
        }
        return backtrace;
    }

    public IRubyObject initialize(IRubyObject[] args, Block block) {
        if (Arity.checkArgumentCount(getRuntime(), args, 0, 1) == 1) message = args[0];
        return this;
    }

    public IRubyObject backtrace() {
        return getBacktrace(); 
    }

    public IRubyObject set_backtrace(IRubyObject obj) {
        if (obj.isNil()) {
            backtrace = null;
        } else if (!isArrayOfStrings(obj)) {
            throw getRuntime().newTypeError("backtrace must be Array of String");
        } else {
            backtrace = (RubyArray) obj;
        }
        return backtrace();
    }
    
    public RubyException exception(IRubyObject[] args) {
        switch (args.length) {
            case 0 :
                return this;
            case 1 :
                if(args[0] == this) {
                    return this;
                }
                RubyException ret = (RubyException)rbClone(Block.NULL_BLOCK);
                ret.initialize(args, Block.NULL_BLOCK); // This looks wrong, but it's the way MRI does it.
                return ret;
            default :
                throw getRuntime().newArgumentError("Wrong argument count");
        }
    }

    public IRubyObject to_s() {
        if (message.isNil()) return getRuntime().newString(getMetaClass().getName());
        message.setTaint(isTaint());
        return message;
    }

    public IRubyObject to_str() {
        return callMethod(getRuntime().getCurrentContext(), MethodIndex.TO_S, "to_s");
    }

    /** inspects an object and return a kind of debug information
     * 
     *@return A RubyString containing the debug information.
     */
    public IRubyObject inspect() {
        RubyModule rubyClass = getMetaClass();
        RubyString exception = RubyString.objAsString(this);

        if (exception.getValue().length() == 0) return getRuntime().newString(rubyClass.getName());
        StringBuffer sb = new StringBuffer("#<");
        sb.append(rubyClass.getName()).append(": ").append(exception.getValue()).append(">");
        return getRuntime().newString(sb.toString());
    }

	public void printBacktrace(PrintStream errorStream) {
	    IRubyObject backtrace = callMethod(getRuntime().getCurrentContext(), "backtrace");
	    if (!backtrace.isNil() && backtrace instanceof RubyArray) {
    		IRubyObject[] elements = ((RubyArray)backtrace.convertToArray()).toJavaArray();
	
    		for (int i = 1; i < elements.length; i++) {
    		    IRubyObject stackTraceLine = elements[i];
    			if (stackTraceLine instanceof RubyString) {
    		        printStackTraceLine(errorStream, stackTraceLine);
    		    }
	
    		    if (i == RubyException.TRACE_HEAD && elements.length > RubyException.TRACE_MAX) {
    		        int hiddenLevels = elements.length - RubyException.TRACE_HEAD - RubyException.TRACE_TAIL;
    				errorStream.print("\t ... " + hiddenLevels + " levels...\n");
    		        i = elements.length - RubyException.TRACE_TAIL;
    		    }
    		}
		}
	}

	private void printStackTraceLine(PrintStream errorStream, IRubyObject stackTraceLine) {
		errorStream.print("\tfrom " + stackTraceLine + '\n');
	}
	
    private boolean isArrayOfStrings(IRubyObject backtrace) {
        if (!(backtrace instanceof RubyArray)) return false; 
            
        IRubyObject[] elements = ((RubyArray) backtrace).toJavaArray();
        
        for (int i = 0 ; i < elements.length ; i++) {
            if (!(elements[i] instanceof RubyString)) return false;
        }
            
        return true;
    }

    protected IRubyObject doClone() {
        IRubyObject newObject = new RubyException(getRuntime(),getMetaClass().getRealClass());
        if (newObject.getType() != getMetaClass().getRealClass()) {
            throw getRuntime().newTypeError("wrong instance allocation");
        }
        return newObject;
    }
}
