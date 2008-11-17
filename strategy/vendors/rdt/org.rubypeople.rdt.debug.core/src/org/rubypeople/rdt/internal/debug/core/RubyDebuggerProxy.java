package org.rubypeople.rdt.internal.debug.core;

import java.io.IOException;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.debug.core.RubyLineBreakpoint;
import org.rubypeople.rdt.internal.debug.core.commands.AbstractDebuggerConnection;
import org.rubypeople.rdt.internal.debug.core.commands.BreakpointCommand;
import org.rubypeople.rdt.internal.debug.core.commands.ClassicDebuggerConnection;
import org.rubypeople.rdt.internal.debug.core.commands.GenericCommand;
import org.rubypeople.rdt.internal.debug.core.commands.RubyDebugConnection;
import org.rubypeople.rdt.internal.debug.core.model.IEvaluationResult;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyEvaluationResult;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.ErrorReader;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.LoadResultReader;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;

public class RubyDebuggerProxy {

	public final static String DEBUGGER_ACTIVE_KEY = "org.rubypeople.rdt.debug.ui.debuggerActive";
	private AbstractDebuggerConnection debuggerConnection;
	private IRubyDebugTarget debugTarget;
	private RubyLoop rubyLoop;
	private ICommandFactory commandFactory;
	private Thread threadUpdater;
	private Thread errorReader;
	private boolean isLoopFinished ;

	public RubyDebuggerProxy(IRubyDebugTarget debugTarget, boolean isRubyDebug) {
		this.debugTarget = debugTarget;
		debugTarget.setRubyDebuggerProxy(this);
		commandFactory = isRubyDebug ? new RubyDebugCommandFactory() : new ClassicDebuggerCommandFactory();
		debuggerConnection = isRubyDebug ? new RubyDebugConnection(debugTarget.getPort()) : new ClassicDebuggerConnection(debugTarget.getPort());
	}

	public boolean checkConnection() {
		return debuggerConnection.isCommandPortConnected();
	}

	public void start() throws RubyProcessingException, IOException {
			isLoopFinished = false ;
			debuggerConnection.connect();
			this.setBreakPoints();
			this.startRubyLoop();
	}

	public void stop() {
		if (rubyLoop == null) {
			// only in tests, where no real connection is established
			return;
		}
		rubyLoop.setShouldStop();
		rubyLoop.interrupt();
	}

	protected void setBreakPoints() throws IOException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IRubyDebugTarget.MODEL_IDENTIFIER);
		for (int i = 0; i < breakpoints.length; i++) {
			this.addBreakpoint(breakpoints[i]);
		}
	}

	public void addBreakpoint(IBreakpoint breakpoint) {
		try {
			if (breakpoint.isEnabled()) {
				if (breakpoint instanceof RubyExceptionBreakpoint) {
					// TODO: check result
					String command = commandFactory.createCatchOn(breakpoint);
					new BreakpointCommand(command).execute(debuggerConnection);
				} else if (breakpoint instanceof RubyLineBreakpoint) {
					RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
					String command = commandFactory.createAddBreakpoint(rubyLineBreakpoint.getFileName(), rubyLineBreakpoint.getLineNumber());
					int index = new BreakpointCommand(command).executeWithResult(debuggerConnection);
					rubyLineBreakpoint.setIndex(index);
				}
			}
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		} catch (CoreException e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void removeBreakpoint(IBreakpoint breakpoint) {
		try {
			if (breakpoint instanceof RubyExceptionBreakpoint) {
				// so far we allow only one catch exception
				// catch off must be set in the case that the enablement has
				// changed to disabled
				String command = commandFactory.createCatchOff();
				new BreakpointCommand(command).execute(debuggerConnection) ;
			} else if (breakpoint instanceof RubyLineBreakpoint) {
				RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
				if (rubyLineBreakpoint.getIndex() != -1) {
					String command = commandFactory.createRemoveBreakpoint(rubyLineBreakpoint.getIndex());
					// TODO: check for errors
					int deletedIndex = new BreakpointCommand(command).executeWithResult(debuggerConnection);
					rubyLineBreakpoint.setIndex(-1);
				}
			}
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		}

	}

	public void updateBreakpoint(IBreakpoint breakpoint, IMarkerDelta markerDelta) {		
		this.removeBreakpoint(breakpoint);
		this.addBreakpoint(breakpoint);
	}

	public void startRubyLoop() throws DebuggerNotFoundException, IOException {
		debuggerConnection.start();
		rubyLoop = new RubyLoop();
		rubyLoop.start();
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					RdtDebugCorePlugin.debug("Command Connection error handler started.") ;
					while (debuggerConnection.getCommandReadStrategy().isConnected()) {
						// The read strategy resumes read() after the connection to the debugger
						// has been dropped
						new ErrorReader(debuggerConnection.getCommandReadStrategy()).read();
					}
				} catch (Exception e) {
					RdtDebugCorePlugin.log(e);
				} finally {
					RdtDebugCorePlugin.debug("Command Connection error handler finished.") ;
				}
			};
		};
		errorReader = new Thread(runnable, "Error Reader");
		errorReader.start();
		// TODO: Check if it would not be better if the ruby part created the threadinfos
		// only after a change to the thread status has occurred
		Runnable threadListener = new Runnable() {
			public void run() {
				try {
					RdtDebugCorePlugin.debug("Thread updater started.") ;
					Thread.sleep(2000) ;
					GenericCommand cmd = null ;
					while (cmd == null || (cmd != null && cmd.getReadStrategy().isConnected())) {
						if (!getDebugTarget().isSuspended()) {
							String command = commandFactory.createReadThreads() ;
							cmd = new GenericCommand(command, true /* isControl */) ;
							cmd.execute(debuggerConnection);
							ThreadInfo[] threadInfos = new ThreadInfoReader(cmd.getReadStrategy()).readThreads() ;
							((RubyDebugTarget)getDebugTarget()).updateThreads(threadInfos) ;
						}
						Thread.sleep(2000) ;
					}
				} catch (Exception e) {
					RdtDebugCorePlugin.log(e);
				} finally {
					RdtDebugCorePlugin.debug("Thread updater finished.") ;
				}
			};
		};
		threadUpdater = new Thread(threadListener, "Ruby Thread Updater");
		threadUpdater.start();
	}

	public void resume(RubyThread thread) {
		try {
			println(commandFactory.createResume(thread));
		} catch (IOException e) {
			// terminate ?
		}
	}

	protected void println(String s) throws IOException {
		try {
			// TOOD: GenericCommand is only temporary solution
			new GenericCommand(s, false /* isControl */).execute(debuggerConnection);
		} catch (IOException e) {
			RdtDebugCorePlugin.debug("Could not send to debugger. Exception occured.", e);
			throw e;
		}
	}

	protected IRubyDebugTarget getDebugTarget() {
		return debugTarget;
	}

	public RubyVariable[] readVariables(RubyStackFrame frame) {
		try {
			this.println(commandFactory.createReadLocalVariables(frame));
			return new VariableReader(getMultiReaderStrategy()).readVariables(frame);
		} catch (Exception ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable[] readInstanceVariables(RubyVariable variable) {
		try {
			this.println(commandFactory.createReadInstanceVariable(variable));
			return new VariableReader(getMultiReaderStrategy()).readVariables(variable);
		} catch (Exception ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable readInspectExpression(RubyStackFrame frame, String expression) throws RubyProcessingException {
		try {			
			expression = expression.replaceAll("\\n", "\\\\n");
			RubyEvaluationResult result = new RubyEvaluationResult(expression, frame.getThread());
			this.println(commandFactory.createInspect(frame, expression));
			RubyVariable[] variables = new VariableReader(getMultiReaderStrategy()).readVariables(frame);
			if (variables.length == 0) {
				return null;
			} else {
				result.setValue(variables[0].getValue());
				return variables[0];
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}
	
	public IEvaluationResult evaluate(RubyStackFrame frame, String expression) {
		expression = expression.replaceAll("\\r\\n", "\n");
		expression = expression.replaceAll("\\n", "; ");
		expression = expression.trim();
		RubyEvaluationResult result = new RubyEvaluationResult(expression, frame.getThread());
		try {						
			this.println(commandFactory.createInspect(frame, expression));
			RubyVariable[] variables = new VariableReader(getMultiReaderStrategy()).readVariables(frame);
			if (variables.length > 0) {
				result.setValue(variables[0].getValue());
			}
		} catch (IOException ioex) {
			DebugException ex = new DebugException(new Status(IStatus.ERROR, RdtDebugCorePlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR, ioex.getMessage(), ioex));
			result.setException(ex);
		} catch (RubyProcessingException e) {
			DebugException ex = new DebugException(new Status(IStatus.ERROR, RdtDebugCorePlugin.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED, e.getMessage(), e));
			result.setException(ex);
		}
		return result;
	}

	public void sendStepOverEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepOver(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);

		}
	}

	public void sendStepReturnEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepReturn(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void sendStepIntoEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepInto(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}
	
	public void sendThreadStop(RubyThread thread) {
		try {
			String command = commandFactory.createThreadStop(thread) ; 
			new GenericCommand(command, true /* isControl */).execute(debuggerConnection);
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public RubyStackFrame[] readFrames(RubyThread thread) {
		try {
			this.println(commandFactory.createReadFrames(thread));
			return new FramesReader(getMultiReaderStrategy()).readFrames(thread);
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}

	}

	public ThreadInfo[] readThreads() {
		try {
			String command = commandFactory.createReadThreads() ;
			new GenericCommand(command, true /* isControl */).execute(debuggerConnection);
			return new ThreadInfoReader(getMultiReaderStrategy()).readThreads();
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}

	public LoadResultReader.LoadResult readLoadResult(String filename) {
		try {
			this.println(commandFactory.createLoad(filename));
			return new LoadResultReader(getMultiReaderStrategy()).readLoadResult();
		} catch (Exception e) {
			return null;
		}
	}

	public void closeConnection() throws IOException {
		debuggerConnection.exit();
	}

	private AbstractReadStrategy getMultiReaderStrategy() {
		return debuggerConnection.getCommandReadStrategy();
	}

	class RubyLoop extends Thread {

		public RubyLoop() {
			this.setName("RubyDebuggerLoop");
		}

		public void setShouldStop() {}

		public void run() {
			try {
				System.setProperty(DEBUGGER_ACTIVE_KEY, "true");
				
				RdtDebugCorePlugin.debug("Waiting for breakpoints.");
				while (true) {
					final SuspensionPoint hit = new SuspensionReader(getMultiReaderStrategy()).readSuspension();
					if (hit == null) {
						break;
					}
					RdtDebugCorePlugin.debug(hit);
					// TODO: should this be using the JOB API?
					new Thread() {

						public void run() {
							getDebugTarget().suspensionOccurred(hit);
						}
					}.start();
				}
			} catch (DebuggerNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				RdtDebugCorePlugin.debug("Exception in socket reader loop.", ex);
			} finally {
				System.setProperty(DEBUGGER_ACTIVE_KEY, "false");
				getDebugTarget().terminate();
				try {
					closeConnection();
				} catch (IOException e) {
					RdtDebugCorePlugin.log(e);
				}
				RdtDebugCorePlugin.debug("Socket reader loop finished.");
			}
		}
	}



}
