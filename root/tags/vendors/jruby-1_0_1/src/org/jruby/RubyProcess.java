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
 * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
 * Copyright (C) 2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
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

import org.jruby.runtime.Block;
import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.MethodIndex;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;


/**
 *
 * @author  enebo
 */
public class RubyProcess {

    public static RubyModule createProcessModule(Ruby runtime) {
        RubyModule process = runtime.defineModule("Process");
        
        // TODO: NOT_ALLOCATABLE_ALLOCATOR is probably ok here. Confirm. JRUBY-415
        RubyModule process_status = process.defineClassUnder("Status", runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR); 

        CallbackFactory processCallbackFactory = runtime.callbackFactory(RubyProcess.class);
        CallbackFactory process_statusCallbackFactory = runtime.callbackFactory(RubyProcess.RubyStatus.class);

//        process.defineModuleFunction("fork", processCallbackFactory.getSingletonMethod("fork"));
//        process.defineModuleFunction("exit!", processCallbackFactory.getOptSingletonMethod("exit_bang"));
//        process.defineModuleFunction("exit", processCallbackFactory.getOptSingletonMethod("exit"));
//        process.defineModuleFunction("abort", processCallbackFactory.getOptSingletonMethod("abort"));
//        process.defineModuleFunction("kill", processCallbackFactory.getOptSingletonMethod("kill"));
//        process.defineModuleFunction("wait", processCallbackFactory.getOptSingletonMethod("wait"));
//        process.defineModuleFunction("wait2", processCallbackFactory.getOptSingletonMethod("wait2"));
//        process.defineModuleFunction("waitpid", processCallbackFactory.getOptSingletonMethod("waitpid"));
//        process.defineModuleFunction("waitpid2", processCallbackFactory.getOptSingletonMethod("waitpid2"));
//        process.defineModuleFunction("waitall", processCallbackFactory.getSingletonMethod("waitall"));
//        process.defineModuleFunction("detach", processCallbackFactory.getSingletonMethod("detach", RubyKernel.IRUBY_OBJECT));
        process.defineModuleFunction("pid", processCallbackFactory.getFastSingletonMethod("pid"));
//        process.defineModuleFunction("ppid", processCallbackFactory.getSingletonMethod("ppid"));
//
//        process.defineModuleFunction("getpgrp", processCallbackFactory.getSingletonMethod("getprgrp"));
//        process.defineModuleFunction("setpgrp", processCallbackFactory.getSingletonMethod("setpgrp"));
//        process.defineModuleFunction("getpgid", processCallbackFactory.getSingletonMethod("getpgid", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("setpgid", processCallbackFactory.getSingletonMethod("setpgid", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
//
//        process.defineModuleFunction("setsid", processCallbackFactory.getSingletonMethod("setsid"));
//
//        process.defineModuleFunction("getpriority", processCallbackFactory.getSingletonMethod("getpriority", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("setpriority", processCallbackFactory.getSingletonMethod("setpriority", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));

//    #ifdef HAVE_GETPRIORITY
//        rb_define_const(rb_mProcess, "PRIO_PROCESS", INT2FIX(PRIO_PROCESS));
//        rb_define_const(rb_mProcess, "PRIO_PGRP", INT2FIX(PRIO_PGRP));
//        rb_define_const(rb_mProcess, "PRIO_USER", INT2FIX(PRIO_USER));
//    #endif

//        process.defineModuleFunction("uid", processCallbackFactory.getSingletonMethod("uid"));
//        process.defineModuleFunction("uid=", processCallbackFactory.getSingletonMethod("uid_set", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("gid", processCallbackFactory.getSingletonMethod("gid"));
//        process.defineModuleFunction("gid=", processCallbackFactory.getSingletonMethod("gid_set", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("euid", processCallbackFactory.getSingletonMethod("euid"));
//        process.defineModuleFunction("euid=", processCallbackFactory.getSingletonMethod("euid_set", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("egid", processCallbackFactory.getSingletonMethod("egid"));
//        process.defineModuleFunction("egid=", processCallbackFactory.getSingletonMethod("egid_set", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("initgroups", processCallbackFactory.getSingletonMethod("initgroups", RubyKernel.IRUBY_OBJECT, RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("groups", processCallbackFactory.getSingletonMethod("groups"));
//        process.defineModuleFunction("groups=", processCallbackFactory.getSingletonMethod("groups_set", RubyKernel.IRUBY_OBJECT));
//        process.defineModuleFunction("maxgroups", processCallbackFactory.getSingletonMethod("maxgroups"));
//        process.defineModuleFunction("maxgroups=", processCallbackFactory.getSingletonMethod("maxgroups_set", RubyKernel.IRUBY_OBJECT));
        process.defineModuleFunction("times", processCallbackFactory.getSingletonMethod("times"));
        
        // Process::Status methods  
        process_status.defineMethod("==", process_statusCallbackFactory.getMethod("op_eq", RubyKernel.IRUBY_OBJECT));
//        process_status.defineMethod("&", process_statusCallbackFactory.getMethod("op_and"));
        process_status.defineMethod(">>", process_statusCallbackFactory.getMethod("rightshift_op", RubyKernel.IRUBY_OBJECT));
        process_status.defineMethod("to_i", process_statusCallbackFactory.getMethod("to_i"));
//        process_status.defineMethod("to_int", process_statusCallbackFactory.getMethod("to_int"));
        process_status.defineMethod("to_s", process_statusCallbackFactory.getMethod("to_s"));
        process_status.defineMethod("inspect", process_statusCallbackFactory.getMethod("inspect"));
//        process_status.defineMethod("pid", process_statusCallbackFactory.getMethod("pid"));
//        process_status.defineMethod("stopped?", process_statusCallbackFactory.getMethod("stopped_p"));
//        process_status.defineMethod("stopsig", process_statusCallbackFactory.getMethod("stopsig"));
//        process_status.defineMethod("signaled?", process_statusCallbackFactory.getMethod("signaled_p"));
//        process_status.defineMethod("termsig", process_statusCallbackFactory.getMethod("termsig"));
//        process_status.defineMethod("exited?", process_statusCallbackFactory.getMethod("exited_p"));
        process_status.defineMethod("exitstatus", process_statusCallbackFactory.getMethod("exitstatus"));
        process_status.defineMethod("success?", process_statusCallbackFactory.getMethod("success_p"));
//        process_status.defineMethod("coredump?", process_statusCallbackFactory.getMethod("coredump_p"));
        
        return process;
    }
    
    public static class RubyStatus extends RubyObject {
        private long status = 0L;
        
        private static final long EXIT_SUCCESS = 0L;
        public RubyStatus(Ruby runtime, RubyClass metaClass, long status) {
            super(runtime, metaClass);
            this.status = status;
        }
        
        public static RubyStatus newProcessStatus(Ruby runtime, long status) {
            return new RubyStatus(runtime, runtime.getModule("Process").getClass("Status"), status);
        }
        
        public IRubyObject exitstatus(Block block) {
            return getRuntime().newFixnum(status);
        }
        
        public IRubyObject rightshift_op(IRubyObject other, Block block) {
            long shiftValue = other.convertToInteger().getLongValue();
            return getRuntime().newFixnum(status >> shiftValue);
        }
        
        public IRubyObject op_eq(IRubyObject other, Block block) {
            return other.callMethod(getRuntime().getCurrentContext(), MethodIndex.EQUALEQUAL, "==", this.to_i(block));
        }

        public IRubyObject to_i(Block unusedBlock) {
            return getRuntime().newFixnum(shiftedValue());
        }
        
        public IRubyObject to_s(Block unusedBlock) {
            return getRuntime().newString(String.valueOf(shiftedValue()));
        }
        
        public IRubyObject inspect(Block unusedBlock) {
            return getRuntime().newString("#<Process::Status: pid=????,exited(" + String.valueOf(status) + ")>");
        }
        
        public IRubyObject success_p(Block unusedBlock) {
            return getRuntime().newBoolean(status == EXIT_SUCCESS);
        }
        
        private long shiftedValue() {
            return status << 8;
        }
    }
    
    public static IRubyObject times(IRubyObject recv, Block unusedBlock) {
        Ruby runtime = recv.getRuntime();
        double currentTime = System.currentTimeMillis() / 1000.0;
        double startTime = runtime.getStartTime() / 1000.0;
        RubyFloat zero = runtime.newFloat(0.0);
        return RubyStruct.newStruct(runtime.getTmsStruct(), 
                new IRubyObject[] { runtime.newFloat(currentTime - startTime), zero, zero, zero }, 
                Block.NULL_BLOCK);
    }

    public static IRubyObject pid(IRubyObject recv) {
        return recv.getRuntime().newFixnum(System.identityHashCode(recv.getRuntime()));
    }

    public static IRubyObject kill(IRubyObject recv, IRubyObject[] args) throws Exception {
        return recv.getRuntime().getNil();
    }
}
