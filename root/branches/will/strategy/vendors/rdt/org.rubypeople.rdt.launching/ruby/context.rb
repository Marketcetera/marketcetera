class DC_DEBUGGER__

  class Context

    attr_reader  :should_resume

    def initialize
      @printer = DC_DEBUGGER__.printer
      @socket = DC_DEBUGGER__.socket
      @stop_next = 0
      @last_file = nil
      @file = nil
      @line = nil
      @no_step = nil
      @frames = []
      @current_frame = -1
      @finish_pos = 0
      @trace = false
      @catch = nil # "StandardError"
      @suspend_next = false
      @should_resume = false
    end

    def stop_next(n=1)
      @stop_next = n
    end

    def set_suspend
      @suspend_next = true
    end

    def clear_suspend
      @suspend_next = false
    end

    def stop_current_thread
      @printer.debug("Suspending : %s", Thread.current)
      Thread.stop()
      @printer.debug("Resumed : %s", Thread.current)
    end

    def trace?
      @trace
    end

    def set_trace(arg)
      @trace = arg
    end

    def stdout
      if @socket then
        @socket
      else
        DC_DEBUGGER__.stdout
      end
    end

    def break_points
      DC_DEBUGGER__.break_points
    end

    def display
      DC_DEBUGGER__.display
    end

    def context(th)
      DC_DEBUGGER__.context(th)
    end

    def set_trace_all(arg)
      DC_DEBUGGER__.set_trace(arg)
    end

    def set_last_thread(th)
      DC_DEBUGGER__.set_last_thread(th)
    end

    def debug_eval_private(str, binding)
      # evaluates str like "var.@instance_var.@instance_var"
      # and "var.private_method"
      # return value might be nil
      # the scan must detect array and hash accesses, which might
      # themselves contain point separated expressions, e.g.
      # array[Test.getX].y
      names  = str.scan(/[^\[\]]*(?=\.)|.*\[.*\](?=\.)|.*$/)
      # names can contain empty strings
      obj = eval(names[0], binding)
       (1..names.length-1).each { |i|
        if names[i] != "" then
          if names[i].length > 2 && names[i][0..1] == '@@' then
            @printer.debug("Evaluating (class_var): %s on %s", names[i], obj )
            obj = obj.class.class_eval("#{names[i]}")
          else
            @printer.debug("Evaluating (instance_var): %s on %s", names[i], obj )
            obj = obj.instance_eval("#{names[i]}")
          end
        end
      }
      return obj
    end

    def debug_eval(str, binding, no_private_instance_vars = false)
      begin
        if no_private_instance_vars then
          val = eval(str, binding)
        else
          val = debug_eval_private(str, binding)
        end
        val
      rescue ScriptError, StandardError => error
        if error.to_s =~ /private method .* called for/ then
          return debug_eval(str, binding, true)
        end
        @printer.debug("Error in debug_eval (no_private_instancs_vars=%s): %s", no_private_instance_vars, error)
        raise error
      end
    end

    def debug_silent_eval(str, binding)
      begin
        val = eval(str, binding)
        val
      rescue StandardError, ScriptError
        nil
      end
    end

    def var_list(ary, binding, kind)
      ary.sort! do |v1, v2|
        if v1 == 'self'
          result = -1
        elsif v2 == 'self'
          result = 1
        else
          result = v1 <=> v2
        end
        result
      end
      @printer.synchronize do
        @printer.print_xml("<variables>")
        for v in ary
          @printer.print_variable(v,binding, kind)
        end
        @printer.print_xml("</variables>")
      end
    end

    def print_array_elements(array)
      index = 0
      array.each do |e|
        @printer.print_variable_value('[' + index.to_s + ']', e, 'instance')
        index += 1
      end
    end

    def print_hash_elements(hash)
      hash.keys.each { | k |
        if k.class.name == "String"
          name = '\'' + k + '\''
        else
          name = k.to_s
        end
        @printer.print_variable_value(name, hash[k], 'instance')
      }
    end

    def get_constants_in_class(a_class)
      constants = a_class.constants() - Object.constants
      return constants.delete_if { |c| ! a_class.const_defined? c }
    end

    def get_binding(pos)
      # returns frame info of frame pos, if pos is within bound,  nil otherwise
      pos = @current_frame unless pos
      pos = pos.to_i - 1
      if pos < 0 then
        @printer.debug("ERROR: illegal stack frame, using top frame.")
        pos = 0
      end
      if pos >= @frames.size then
        @printer.debug("ERROR: given stack frame number %i too high.", pos + 1)
        pos = @frames.size - 1
      end
      @printer.debug("Using frame %s (1-based) for evaluation of variable.", pos + 1)
      return @frames[pos][0]
    end

    def debug_variable_info(input, binding)

      case input
      when /^\s*g(?:lobal)?$/
        var_list(global_variables, binding, 'global')

      when /^\s*l(?:ocal)?\s*(\d+)?$/
        @printer.debug("Getting binding for frame #{$1}")
        new_binding = get_binding($1)
        if new_binding then
          binding = new_binding
        end
        begin
          #@printer.debug("binding=%s", binding)
          local_vars = eval("local_variables", binding)
          # v 1.8.1 dies in eval @printer.debug("HERE")
          if eval('self.to_s', binding) !~ /main/ then
            local_vars << "self"
          end
          var_list(local_vars, binding, 'local')
        rescue StandardError => bang
          @printer.debug("Exception while evaluating local_variables: %s", bang)
          var_list([], binding, 'local')
        end

      when /^\s*i(?:nstance)?\s*(\d+)?\s+((?:[\\+-]0x)[\dabcdef]+)?/
        new_binding = get_binding($1)
        if new_binding then
          binding = new_binding
        end
        @printer.synchronize do
          begin
            @printer.print_xml("<variables>")
            if $2 then
              obj = ObjectSpace._id2ref($2.hex)
              if (!obj) then
                @printer.debug("unknown object id : %s", $2)
              end
            else
              obj = debug_eval($', binding)
            end
            if (obj.class.name == "Array") then
              print_array_elements(obj)
              return
            end
            if (obj.class.name == "Hash") then
              print_hash_elements(obj)
              return
            end
            @printer.debug("%s", obj)
            instance_binding = obj.instance_eval{binding()}
            obj.instance_variables.each do |instance_var |
              @printer.print_variable(instance_var, instance_binding, 'instance')
            end
            class_binding = obj.class.class_eval('binding()')
            obj.class.class_variables.each do |class_var|
              @printer.print_variable(class_var, class_binding, 'class')
            end
            # I'd like to optimize getting the constants, but they are more dynamic
            # than one would expect from true constants.
            get_constants_in_class(obj.class).each do |constant|
              @printer.print_variable(constant, class_binding, 'constant')
            end
          rescue StandardError => error
            @printer.debug("%s", error)
          ensure
            @printer.print_xml("</variables>")
          end
        end


      when /^\s*inspect\s*(\d+)?\s+/
        new_binding = get_binding($1)
        if new_binding then
          binding = new_binding
        end
        begin
          to_inspect = $'.gsub(/\\n/, "\n")
          obj = debug_eval(to_inspect, binding, true)
        rescue ScriptError, StandardError => error
          @printer.print_xml("<processingException type=\"%s\" message=\"%s\"/>", error.class, CGI.escapeHTML(error.message))
          return
        end
        @printer.debug("%s", obj)
        @printer.synchronize do
          @printer.print_xml("<variables>")
          @printer.print_variable_value(to_inspect, obj, "local")
          @printer.print_xml("</variables>")
        end
      end
    end

    def debug_method_info(input, binding)
      case input
      when /^i(:?nstance)?\s+/
        obj = debug_eval($', binding) # correct highlighting since RDT editor does not recognize $'

        len = 0
        for v in obj.methods.sort
          len += v.size + 1
          if len > 70
            len = v.size + 1
            stdout.print "\n"
          end
          stdout.print v, " "
        end
        stdout.print "\n"

      else
        obj = debug_eval(input, binding)
        unless obj.kind_of? Module
          stdout.print "Should be Class/Module: ", input, "\n"
        else
          len = 0
          for v in obj.instance_methods.sort
            len += v.size + 1
            if len > 70
              len = v.size + 1
              stdout.print "\n"
            end
            stdout.print v, " "
          end
          stdout.print "\n"
        end
      end
    end

    def thnum(thread=Thread.current)
      num = DC_DEBUGGER__.instance_eval{@thread_list[thread]}
      unless num
        DC_DEBUGGER__.make_thread_list
        num = DC_DEBUGGER__.instance_eval{@thread_list[thread]}
      end
      num
    end

    def process_input(input)
      binding, file, line, id = @frames[0]
      input.split(";").each do |single_command|
        read_user_input(binding, file, line, single_command.strip)
      end
    end

    def read_user_input(binding, binding_file, binding_line, input)

      @printer.debug("Processing #{input}, binding=%s", binding)
      @should_resume = false
      frame_pos = 0
      display_expressions(binding)

      case input
      when /^\s*tr(?:ace)?(?:\s+(on|off))?(?:\s+(all))?$/
        if defined?( $2 )
          if $1 == 'on'
            set_trace_all true
          else
            set_trace_all false
          end
        elsif defined?( $1 )
          if $1 == 'on'
            set_trace true
          else
            set_trace false
          end
        end
        if trace?
          stdout.print "Trace on.\n"
        else
          stdout.print "Trace off.\n"
        end

      when /^\s*b(?:reak)?\s+(?:(.+):)?([^.:]+)$/
        pos = $2
        file = File.basename($1)
        if pos =~ /^\d+$/
          pname = pos
          pos = pos.to_i
        else
          pname = pos = pos.intern.id2name
        end
        # TODO: pname is not used
        id = DC_DEBUGGER__.next_breakpoint_id
        break_points[id] = Breakpoint.new(true, 0, file, pos)
        @printer.print_xml("<breakpointAdded no=\"%d\" location=\"%s:%s\"/>", id, file, pos)

      when /^\s*delete\s+(.*)$/
        arg = $1
        if arg !~ /(?:^\d+$)/
          @printer.print_error "Delete argument '#{arg}' needs to be positive number"
        else
          breakpoint_id = arg.to_i
          if break_points.delete(breakpoint_id)
            @printer.print_xml("<breakpointDeleted no=\"%d\"/>", breakpoint_id)
          else
            @printer.print_error("No breakpoint with id: %d. Currently following breakpoints defined: %s",
              breakpoint_id, break_points.keys.join(', '))
          end
        end

      when /^\s*c(?:ont)?$/
        @should_resume = true

      when /^\s*s(?:tep)?(?:\s+(\d+))?$/
        if $1
          lev = $1.to_i
        else
          lev = 1
        end
        @stop_next = lev
        @should_resume = true

      when /^\s*n(?:ext)?(?:\s+(\d+))?$/
        if $1
          lev = $1.to_i
        else
          lev = 1
        end
        @stop_next = lev
        @no_step = @frames.size - frame_pos
        @should_resume = true

      when /^\s*w(?:here)?$/, /^\s*f(?:rame)?$/
        display_frames(frame_pos)

      when /^\s*f(?:rame)?\s+(\d+)\s*$/
        @printer.debug("Setting frame to #{$1}")
        @current_frame = $1.to_i


      when /^\s*cat(?:ch)?(?:\s+(.+))?$/
        # $1 can also be nil
        @catch = $1
        @catch = nil if @catch == 'off'
        if @catch then
          @printer.debug("Catchpoint set to #{@catch}")
        else
          @printer.debug("Catchpoints disabled")
        end


      when /^\s*v(?:ar)?\s+/
        debug_variable_info($', binding)

      when /^\s*m(?:ethod)?\s+/
        debug_method_info($', binding)

      when /^\s*th(?:read)?\s+/
        DC_DEBUGGER__.debug_thread_info($', binding)

      when /^\s*p\s+/
        stdout.printf "%s\n", debug_eval($', binding).inspect

      when /^\s*load\s+/
        @printer.debug("loading file: %s", $')
        begin
          load $'
          @printer.print_load_result($')
        rescue Exception => error
          @printer.print_load_result($', error)
        end
        
      when /^\s*exit$/
        @printer.print_message("finished")
        debug_commons_set_trace_func nil
        @socket.close
        exit!

      else
        @printer.print_message("Unknown input: %s", input)
      end

    end

    def display_expressions(binding)
      n = 1
      for d in display
        if d[0]
          stdout.printf "%d: ", n
          display_expression(d[1], binding)
        end
        n += 1
      end
    end

    def display_expression(exp, binding)
      stdout.printf "%s = %s\n", exp, debug_silent_eval(exp, binding).to_s
    end

    def frame_set_pos(binding, file, line, id)
      if @frames[0] then
        @frames[0][0] = binding
        @frames[0][1] = file
        @frames[0][2] = line
        @frames[0][3] = id
      else
        @frames[0] = [binding, file, line, id]
      end
    end

    def display_frames(pos)
      pos += 1
      n = 0
      at = @frames
      @printer.synchronize do
        @printer.print_xml("<frames>")
        for bind, file, line, id in at
          n += 1
          break unless bind
          @printer.print_frame(pos, n, file, line, id)
        end
        @printer.print_xml("</frames>")
      end
    end

    def debug_funcname(id)
      if id.nil?
        "toplevel"
      else
        id.id2name
      end
    end

    def debug_command(file, line, id, binding)
      @printer.debug("debug_command, @stop_next=%s, @frames.size=%s", @stop_next, @frames.size)
      if @stop_next == 0 && @frames.size > 0 then
        # write suspended only if the suspension was originated by stepping and not before the start
        # of the program
        @printer.print_step_end(file, line, @frames.size)
      end
      set_last_thread(Thread.current)

      #read_user_input(binding, file, line )
      #Thread.stop
    end

    def check_break_points(file, pos, binding, id)
      return false if break_points.empty?
      file = File.basename(file)
      n = 1
      break_points.each_value do |b|
#        @printer.debug("file=%s, pos=%s; breakpoint=[valid=%s, type=%s, file=%s, pos=%s]\n ",
#                       file, pos, b.valid, b.type, b.file, b.pos)
        if b.valid
          if b.type == 0 and b.file == file and b.pos == pos # breakpoint
            @printer.print_breakpoint(n, debug_funcname(id), file, pos)
            return true
          elsif b.type == 1 # watchpoint
            if debug_silent_eval(b.file, binding)
              stdout.printf "Watchpoint %d, %s at %s:%s\n", n, debug_funcname(id), file, pos
              return true
            end
          end
        end
        n += 1
      end
      return false
    end

    def excn_handle(file, line, id, binding)
      if $!.class <= SystemExit
        @printer.debug( "SystemExit at %s:%d: `%s' (%s)\n", file, line, $!, $!.class   )
        debug_commons_set_trace_func nil
        exit
      end
      if @catch and ($!.class.ancestors.find { |e| e.to_s == @catch })
        @printer.print_exception(file, line, $!)
        fs = @frames.size
        tb = caller(0)[-fs..-1]
        stop_current_thread
        @frames[0] = [binding, file, line, id]
        debug_command(file, line, id, binding)
      end
    end

    def trace_func(event, file, line, id, binding, klass)
      Tracer.trace_func(event, file, line, id, binding, klass) if trace?

      @file = file
      @line = line
      case event
      when 'line'
        # DC_DEBUGGER__.printer().debug("trace line, file=%s, line=%s, stop_next=%d, binding=%s", file, line, @stop_next, binding)
        frame_set_pos(binding, file, line, id)
        if !@no_step or @frames.size == @no_step
          @stop_next -= 1
          @stop_next = -1 if @stop_next < 0
        elsif @frames.size < @no_step
          @stop_next = 0    # break here before leaving...
        else
          # nothing to do. skipped
        end
        if @stop_next == 0 or check_break_points(file, line, binding, id)
          @no_step = nil
          debug_command(file, line, id, binding)
          stop_current_thread
        end

      when 'call'
#        DC_DEBUGGER__.printer().debug("trace call, file=%s, line=%s, method=%s", file, line, id.id2name)
        @frames.unshift [binding, file, line, id]
        if check_break_points(file, id.id2name, binding, id) or
          check_break_points(klass.to_s, id.id2name, binding, id) then
          stop_current_thread
          debug_command(file, line, id, binding)
        end

      when 'c-call'
        if @frames[0] then
          @frames[0][1] = file
          @frames[0][2] = line
        else
          @frames[0] = [binding, file, line, id]
        end

      when 'class'
        @frames.unshift [binding, file, line, id]

      when 'return', 'end'
#        DC_DEBUGGER__.printer().debug("trace return and end, file=%s, line=%s", file, line)
        if @frames.size == @finish_pos
          @stop_next = 1
          @finish_pos = 0
        end
        @frames.shift

      when 'end'
#        DC_DEBUGGER__.printer().debug("trace end, file=%s, line=%s", file, line)
        @frames.shift

      when 'raise'
        excn_handle(file, line, id, binding)

      end
      @last_file = file
    end
  end

end
