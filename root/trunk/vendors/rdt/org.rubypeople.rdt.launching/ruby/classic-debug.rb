# Copyright (C) 2000  Network Applied Communication Laboratory, Inc.
# Copyright (C) 2000  Information-technology Promotion Agency, Japan

require 'cgi'
require 'thread'
require 'yaml'
require 'socket'
require 'tracer'
require 'xml_printer'
require 'context'

# REMOTE_DEBUG_PORT is the port on which the debugger waits for commands from a
# front-end.
#
# RDT note: if you change this, you must also change the corresponding port in
# org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy
REMOTE_DEBUG_PORT = (defined? $RemoteDebugPort) ? $RemoteDebugPort : 1098

# VERBOSE_DEBUGGER prints the communication between a front-end and ruby
# debugger on stderr. If you have started a front-end debug session (and use
# default preferences for colors of streams), the communication will be printed
# in red letters to the front-end console.
VERBOSE_DEBUGGER = defined? DebugVerbose

class Tracer
  def Tracer.trace_func(*vars)
    Single.trace_func(*vars)
  end
end

SCRIPT_LINES__ = {} unless defined? SCRIPT_LINES__

class DC_DEBUGGER__

  # type: 0 - breakpoint, 1 - watchpoint
  Breakpoint = Struct.new("Breakpoint", :valid, :type, :file, :pos)

  trap("INT") { DC_DEBUGGER__.interrupt }
  @last_thread = Thread::main
  @max_thread = 1
  @max_breakpoint_id = 0
  @thread_list = {Thread::main => 1}
  @break_points = {} # id => Breakpoint
  @display = []
  @waiting = []
  @stdout = STDOUT

  class << DC_DEBUGGER__
    def stdout
      @stdout
    end

    def stdout=(s)
      @stdout = s
    end

    def display
      @display
    end

    def break_points
      @break_points
    end

    def waiting
      @waiting
    end

    def set_trace( arg )
      Thread.critical = true
      make_thread_list
      for th in @thread_list
        context(th[0]).set_trace arg
      end
      Thread.critical = false
    end

    def set_last_thread(th)
      @last_thread = th
    end

    def suspend
      printer.debug("Suspending all")
      Thread.critical = true
      make_thread_list
      for th in @thread_list
        next if th[0] == Thread.current
        context(th[0]).set_suspend
      end
      Thread.critical = false
      # Schedule other threads to suspend as soon as possible.
      Thread.pass
    end

    def resume
      Thread.critical = true
      make_thread_list
      for th in @thread_list
        next if th[0] == Thread.current
        context(th[0]).clear_suspend
      end
      waiting.each do |th|
        th.run
      end
      waiting.clear
      Thread.critical = false
      # Schedule other threads to restart as soon as possible.
      Thread.pass
    end

    def context(thread=Thread.current)
      c = thread[:__debugger_data__]
      unless c
        thread[:__debugger_data__] = c = DC_DEBUGGER__::Context.new
      end
      c
    end

    def find_thread(context)
      for thread in Thread::list
        if context == thread[:__debugger_data__]
          return thread
        end
      end
      @@printer.debug("thread for context '#{context}' was not found")
      nil
    end

    def interrupt
      context(@last_thread).stop_next
    end

    def get_thread(num)
      th = @thread_list.index(num)
      unless th
        printer.debug("No thread ##{num}\n")
      end
      th
    end

    def get_thread_num(thread=Thread.current)
      Thread.critical = true
      make_thread_list
      n = @thread_list[thread]
      Thread.critical = false
      n
    end

    def print_thread(thread)
      num = @thread_list[thread]
      printer.print_thread(num, thread)
    end

    def thread_list_all
      printer.synchronize do
        printer.print_xml("<threads>")
        for num in @thread_list.values.sort
          printer.print_thread(num, get_thread(num))
        end
        printer.print_xml("</threads>")
      end
    end

    def make_thread_list
      hash = {}
      for th in Thread::list
        next if th == @@input_reader
        if @thread_list.key? th
          hash[th] = @thread_list[th]
        else
          @max_thread += 1
          hash[th] = @max_thread
        end
      end
      @thread_list = hash
    end

    def debug_thread_info(input, binding)
      case input
      when /^l(?:ist)?/
        make_thread_list
        thread_list_all

      when /^c(?:ur(?:rent)?)?$/
        make_thread_list
        print_thread()

      when /^(?:sw(?:itch)?\s+)?(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        else
          print_thread(th)
          context(th).stop_next
          th.run
          return
        end

      when /^stop\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        elsif th.stop?
          @stdout.print "Already stopped.\n"
        else
          print_thread(th)
          context(th).suspend
        end

      when /^resume\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        if th == Thread.current
          @stdout.print "It's the current thread.\n"
        elsif !th.stop?
          @stdout.print "Already running."
        else
          print_thread(th)
          th.run
        end

      when /^change\s+(\d+)/
        make_thread_list
        th = get_thread($1.to_i)
        print_thread(th)
        return context(th)
      end
    end

    def next_breakpoint_id
      @max_breakpoint_id += 1
    end
  end

  @@socket = nil
  @@printer = nil
  @@input_reader = nil
  @@is_started = false

  def DC_DEBUGGER__.printer
    @@printer
  end

  def DC_DEBUGGER__.socket
    @@socket
  end

  def DC_DEBUGGER__.is_started
    @@is_started
  end

  def DC_DEBUGGER__.set_started
    @@is_started = true
  end


  def DC_DEBUGGER__.input_reader
    @@input_reader
  end

  def DC_DEBUGGER__.trace_on()
    debug_commons_set_trace_func @@trace_proc
    Thread.critical = false
  end

  def DC_DEBUGGER__.trace_off()
    Thread.critical = true
    debug_commons_set_trace_func nil
  end

  def DC_DEBUGGER__.read_command_loop()
    sleep(1.0) # workaround for large files with ruby 1.6.8, otherwise parse exceptions
    loop do
      sleep(0.1)
      new_data, _, _ = IO.select( [socket], nil, nil, 0.001 )
      next unless new_data
      DC_DEBUGGER__.trace_off()
      input = new_data[0].gets
      unless input
        @@printer.debug "Socket #{socket} closed. Ending."
        break
      end
      input.chomp!
      input.strip!
      @@printer.debug("READ #{input}")
      if !DC_DEBUGGER__.is_started && input == "cont" then
        DC_DEBUGGER__.set_started()
        DC_DEBUGGER__.trace_on()
        next
      end
      input =~ /^th\s+(\d+)\s*;\s*/
      if $~ then
        @@printer.debug("Using context for thread: %s", $1.to_i)
        th = get_thread($1.to_i)
        unless th
          @@printer.debug("Cannot find thread ##{$1.to_i}. Skipping command '#{input}'")
          DC_DEBUGGER__.trace_on()
          next
        end
        context = DC_DEBUGGER__.context(th)
        input = input[$~[0].length..input.length]
      else
        @@printer.debug("Using context for main thread : %s", Thread.main)
        context = DC_DEBUGGER__.context(Thread.main)
      end
      context.process_input(input)
      if context.should_resume then
        thread_to_resume = DC_DEBUGGER__.find_thread(context)
      end
      DC_DEBUGGER__.trace_on()
      next unless thread_to_resume
      thread_to_resume.run()
    end
  end

  # use 127.0.0.1 instead of localhost because OSX 10.4
  server = TCPServer.new('127.0.0.1', REMOTE_DEBUG_PORT)
  puts "ruby #{RUBY_VERSION} debugger listens on port #{REMOTE_DEBUG_PORT}"
  $stdout.flush
  STDERR.sync = true
  @@socket = server.accept
  @@printer = DC_DEBUGGER__::XmlPrinter.new(@@socket)
  @@printer.debug("Socket connection established.")

  @@printer.debug("Starting command reader loop.")
  @@input_reader = Thread.new {
    begin
      DC_DEBUGGER__.read_command_loop()
    rescue ScriptError, StandardError => error
      y error
      y error.backtrace
    end
  }

  @@trace_proc = proc { |event, file, line, id, binding, klass, *rest|
    #@@printer.debug("trace %s, %s:%s", event, file, line)
    next if Thread.current == DC_DEBUGGER__.input_reader
    next if file =~ /classic-debug.rb$/
    #@@printer.debug("trace %s, %s:%s", event, file, line)
    while (!DC_DEBUGGER__.is_started) do
      @@printer.debug("Debugging not yet started.")
      sleep(1)
    end
    DC_DEBUGGER__.context.trace_func(event, file, line, id, binding, klass)
  }

  @@printer.debug("Setting trace func: %s", @@trace_proc)
  Kernel.module_eval(<<-"end;"
      alias_method(:debug_commons_set_trace_func, :set_trace_func)
      def set_trace_func(proc)
        raise "Cannot call 'set_trace_func' method during debugging session."
      end
    end;
  )
  debug_commons_set_trace_func @@trace_proc

end
