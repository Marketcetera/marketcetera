#
#   tracer.rb - 
#   	$Release Version: 0.2$
#   	$Revision: 2062 $
#   	$Date: 2006-06-10 14:14:15 -0500 (Sat, 10 Jun 2006) $
#   	by Keiju ISHITSUKA(Nippon Rational Inc.)
#
# --
#
#   
#

#
# tracer main class
#
class Tracer
  @RCS_ID='-$Id: tracer.rb 2062 2006-06-10 19:14:15Z headius $-'

  @stdout = STDOUT
  @verbose = false
  class << self
    attr :verbose, true
    alias verbose? verbose
    attr :stdout, true
  end
  
  MY_FILE_NAME = caller(0)[0].scan(/^(.*):[0-9]+$/)[0][0]
  
  EVENT_SYMBOL = {
    "line" => "-",
    "call" => ">",
    "return" => "<",
    "class" => "C",
    "end" => "E",
    "c-call" => ">",
    "c-return" => "<",
  }
  
  def initialize
    @threads = Hash.new
    if defined? Thread.main
      @threads[Thread.main.object_id] = 0
    else
      @threads[Thread.current.object_id] = 0
    end

    @get_line_procs = {}

    @filters = []
  end
  
  def stdout
    Tracer.stdout
  end

  def on
    if block_given?
      on
      begin
	yield
      ensure
	off
      end
    else
      set_trace_func method(:trace_func).to_proc
      stdout.print "Trace on\n" if Tracer.verbose?
    end
  end
  
  def off
    set_trace_func nil
    stdout.print "Trace off\n" if Tracer.verbose?
  end

  def add_filter(p = proc)
    @filters.push p
  end

  def set_get_line_procs(file, p = proc)
    @get_line_procs[file] = p
  end
  
  def get_line(file, line)
    if p = @get_line_procs[file]
      return p.call(line)
    end

    unless list = SCRIPT_LINES__[file]
      begin
	f = open(file)
	begin 
	  SCRIPT_LINES__[file] = list = f.readlines
	ensure
	  f.close
	end
      rescue
	SCRIPT_LINES__[file] = list = []
      end
    end
    if l = list[line - 1]
      l
    else
      "-\n"
    end
  end
  
  def get_thread_no
    if no = @threads[Thread.current.object_id]
      no
    else
      @threads[Thread.current.object_id] = @threads.size
    end
  end
  
  def trace_func(event, file, line, id, binding, klass, *)
    return if file == MY_FILE_NAME
    
    for p in @filters
      return unless p.call event, file, line, id, binding, klass
    end
    
    saved_crit = Thread.critical
    Thread.critical = true
    stdout.printf("#%d:%s:%d:%s:%s: %s",
      get_thread_no,
      file,
      line,
      klass || '',
      EVENT_SYMBOL[event],
      get_line(file, line))
    Thread.critical = saved_crit
  end

  Single = new
  def Tracer.on
    if block_given?
      Single.on{yield}
    else
      Single.on
    end
  end
  
  def Tracer.off
    Single.off
  end
  
  def Tracer.set_get_line_procs(file_name, p = proc)
    Single.set_get_line_procs(file_name, p)
  end

  def Tracer.add_filter(p = proc)
    Single.add_filter(p)
  end
  
end

SCRIPT_LINES__ = {} unless defined? SCRIPT_LINES__

if caller(0).size == 1
  if $0 == Tracer::MY_FILE_NAME
    # direct call
    
    $0 = ARGV[0]
    ARGV.shift
    Tracer.on
    require $0
  else
    Tracer.on
  end
end
