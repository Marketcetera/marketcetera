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
 * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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
package org.jruby.internal.runtime;

import org.jruby.Ruby;
import org.jruby.RubyProc;
import org.jruby.RubyThread;
import org.jruby.RubyThreadGroup;
import org.jruby.exceptions.RaiseException;
import org.jruby.exceptions.ThreadKill;
import org.jruby.runtime.Block;
import org.jruby.runtime.Frame;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyRunnable implements Runnable {
    private Ruby runtime;
    private Frame currentFrame;
    private RubyProc proc;
    private IRubyObject[] arguments;
    private RubyThread rubyThread;
    
    public RubyRunnable(RubyThread rubyThread, IRubyObject[] args, Block currentBlock) {
        this.rubyThread = rubyThread;
        this.runtime = rubyThread.getRuntime();
        ThreadContext tc = runtime.getCurrentContext();
        
        proc = runtime.newProc(false, currentBlock);
        currentFrame = tc.getCurrentFrame();
        this.arguments = args;
    }
    
    public RubyThread getRubyThread() {
        return rubyThread;
    }
    
    public void run() {
        runtime.getThreadService().registerNewThread(rubyThread);
        ThreadContext context = runtime.getCurrentContext();
        
        context.preRunThread(currentFrame);
        
        // Call the thread's code
        try {
            IRubyObject result = proc.call(arguments);
            rubyThread.cleanTerminate(result);
        } catch (ThreadKill tk) {
            // notify any killer waiting on our thread that we're going bye-bye
            synchronized (rubyThread.killLock) {
                rubyThread.killLock.notifyAll();
            }
        } catch (RaiseException e) {
            rubyThread.exceptionRaised(e);
        } finally {
            runtime.getThreadService().setCritical(false);
            runtime.getThreadService().unregisterThread(rubyThread);
            ((RubyThreadGroup)rubyThread.group()).remove(rubyThread);
        }
    }
}
