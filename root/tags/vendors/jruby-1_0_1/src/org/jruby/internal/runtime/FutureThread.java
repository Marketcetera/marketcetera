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

import org.jruby.RubyThread;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadFactory;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;
import edu.emory.mathcs.backport.java.util.concurrent.TimeoutException;

/**
 * @author cnutter
 */
public class FutureThread implements ThreadLike {
    private Future future;
    private Runnable runnable;
    public RubyThread rubyThread;
    
    private static class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            
            return thread;
        }
    }
    
    private static ExecutorService executor = Executors.newCachedThreadPool(new DaemonThreadFactory());
    
    public FutureThread(RubyThread rubyThread, RubyRunnable runnable) {
        this.rubyThread = rubyThread;
        this.runnable = runnable;
    }
    
    public void start() {
        future = executor.submit(runnable);
    }
    
    public void interrupt() {
        future.cancel(true);
    }
    
    public boolean isAlive() {
        return future != null && !future.isDone();
    }
    
    public void join() throws InterruptedException, ExecutionException {
        future.get();
    }
    
    public void join(long millis) throws InterruptedException, ExecutionException, TimeoutException {
        future.get(millis, TimeUnit.MILLISECONDS);
    }
    
    public int getPriority() {
        return 1;
    }
    
    public void setPriority(int priority) {
        //nativeThread.setPriority(priority);
    }
    
    public boolean isCurrent() {
        return rubyThread == rubyThread.getRuntime().getCurrentContext().getThread();
    }
    
    public boolean isInterrupted() {
        return future.isCancelled();
    }
}
