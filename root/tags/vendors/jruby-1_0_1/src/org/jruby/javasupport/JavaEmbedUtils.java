package org.jruby.javasupport;

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
 * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
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

import java.util.List;

import org.jruby.Ruby;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Utility functions to help embedders out.   These function consolidate logic that is
 * used between BSF and JSR 223.  People who are embedding JRuby 'raw' should use these
 * as well.  If at a later date, we discover a flaw or change how we do things, this
 * utility class should provide some insulation.
 * 
 */
public class JavaEmbedUtils {
	/**
	 * Get an instance of a JRuby runtime.  Provide any loadpaths you want used at startup.
	 * 
	 * @param loadPaths to specify where to look for Ruby modules. 
	 * @return an instance
	 */
	public static Ruby initialize(List loadPaths) {
        Ruby runtime = Ruby.getDefaultInstance();
        runtime.getLoadService().init(loadPaths);
        runtime.getLoadService().require("java");
        
        return runtime;
	}

	/**
	 * Dispose of the runtime you initialized.
	 * 
	 * @param runtime to be disposed of
	 */
	public static void terminate(Ruby runtime) {
        runtime.tearDown();
        runtime.getThreadService().disposeCurrentThread();
	}
	
	/**
	 * Convenience function for embedders
	 * 
	 * @param runtime environment where the invoke will occur
	 * @param receiver is the instance that will receive the method call
	 * @param method is method to be called
	 * @param args are the arguments to the method
	 * @param returnType is the type we want it to conform to
	 * @return the result of the invocation.
	 */
	public static Object invokeMethod(Ruby runtime, Object receiver, String method, Object[] args,
			Class returnType) {
        IRubyObject rubyReceiver = receiver != null ? 
        		JavaUtil.convertJavaToRuby(runtime, receiver) : runtime.getTopSelf();

        IRubyObject[] rubyArgs = JavaUtil.convertJavaArrayToRuby(runtime, args);

        // Create Ruby proxies for any input arguments that are not primitives.
        IRubyObject javaUtilities = runtime.getObject().getConstant("JavaUtilities");
        ThreadContext context = runtime.getCurrentContext();
        for (int i = 0; i < rubyArgs.length; i++) {
            IRubyObject obj = rubyArgs[i];

            if (obj instanceof JavaObject) {
                rubyArgs[i] = javaUtilities.callMethod(context, "wrap", obj);
            }
        }

        IRubyObject result = rubyReceiver.callMethod(context, method, rubyArgs);

        return rubyToJava(runtime, result, returnType);
	}
	
	/**
	 * Convert a Ruby object to a Java object.
	 * 
	 */
	public static Object rubyToJava(Ruby runtime, IRubyObject value, Class type) {
        return JavaUtil.convertArgument(Java.ruby_to_java(runtime.getObject(), value, Block.NULL_BLOCK), type);
    }

	/**
	 *  Convert a java object to a Ruby object.
	 */
    public static IRubyObject javaToRuby(Ruby runtime, Object value) {
        if (value instanceof IRubyObject) {
            return (IRubyObject) value;
        }
        IRubyObject result = JavaUtil.convertJavaToRuby(runtime, value);
        if (result instanceof JavaObject) {
            return runtime.getModule("JavaUtilities").callMethod(runtime.getCurrentContext(), "wrap", result);
        }
        return result;
    }   

    public static IRubyObject javaToRuby(Ruby runtime, boolean value) {
        return javaToRuby(runtime, value ? Boolean.TRUE : Boolean.FALSE);
    }
    public static IRubyObject javaToRuby(Ruby runtime, byte value) {
        return javaToRuby(runtime, new Byte(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, char value) {
        return javaToRuby(runtime, new Character(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, double value) {
        return javaToRuby(runtime, new Double(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, float value) {
        return javaToRuby(runtime, new Float(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, int value) {
        return javaToRuby(runtime, new Integer(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, long value) {
        return javaToRuby(runtime, new Long(value));
    }
    public static IRubyObject javaToRuby(Ruby runtime, short value) {
        return javaToRuby(runtime, new Short(value));
    }
}
