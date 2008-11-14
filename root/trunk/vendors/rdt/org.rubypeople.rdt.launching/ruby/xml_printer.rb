require 'monitor'

class DC_DEBUGGER__

  class XmlPrinter
    
    include MonitorMixin
    
    def initialize(socket)
      super()
      @socket = socket
    end

    def out(*params)
      synchronize do
        debug_intern(false, *params)
        if @socket
          @socket.printf(*params)
        end
      end
    end

    def print_xml(s, *params)
      out(s, *params)
    end

    def print_error(s, *params)
      out("<error>" + s + "</error>", *params)
    end

    def print_message(s, *params)
      out("<message>" + s + "</message>", *params)
    end

    def print_variable(name, binding, kind)
      print_variable_value(name, eval(name, binding), kind)
    end

    def print_variable_value(name, value, kind)
      if value == nil
        out("<variable name=\"%s\" kind=\"%s\"/>", CGI.escapeHTML(name), kind)
        return
      end
      if value.class().name == "Array" or value.class().name == "Hash"
        has_children = value.length > 0
        if value.length == 0
          value_string = "Empty " + value.class().name
        else
          value_string = value.class().name + " (" + value.length.to_s + " element(s))"
        end
      else
        has_children = value.instance_variables.length > 0 || value.class.class_variables.length > 0
        value_string = value.to_s
        if value_string =~ /^\"/
          value_string.slice!(1..(value_string.length)-2)
        end
      end
      value_string = "[Binary Data]" if value_string.is_binary_data?
      out("<variable name=\"%s\" kind=\"%s\" value=\"%s\" type=\"%s\" hasChildren=\"%s\" objectId=\"%#+x\"/>",
          CGI.escapeHTML(name), kind, CGI.escapeHTML(value_string), value.class(),
          has_children, value.respond_to?(:object_id) ? value.object_id : value.id)
    end

    def print_breakpoint(n, debug_func_name, file, pos)
      out("<breakpoint file=\"%s\" line=\"%s\" threadId=\"%s\"/>", file, pos, DC_DEBUGGER__.get_thread_num())
    end

    def print_exception(file, pos, exception)
      out("<exception file=\"%s\" line=\"%s\" type=\"%s\" message=\"%s\" threadId=\"%s\"/>",
          file, pos, exception.class, CGI.escapeHTML(exception.to_s), DC_DEBUGGER__.get_thread_num())
    end

    def print_step_end(file, line, frames_count)
      out("<suspended file=\"%s\" line=\"%s\" frames=\"%s\" threadId=\"%s\"/>",
          file, line, frames_count, DC_DEBUGGER__.get_thread_num())
    end

    def print_frame(pos, n, file, line, id)
      out("<frame no=\"%s\" file=\"%s\" line=\"%s\"/>", n, file, line)
    end

    def print_thread(num, thread)
      out("<thread id=\"%s\" status=\"%s\"/>", num, thread.status)
    end

    def print_load_result(file, exception=nil)
      if exception
        out("<loadResult file=\"%s\" exceptionType=\"%s\" exceptionMessage=\"%s\"/>",
            file, exception.class, CGI.escapeHTML(exception.to_s))
      else
        out("<loadResult file=\"%s\" status=\"OK\"/>", file)
      end
    end

    def debug(*params)
      debug_intern(true, *params)
    end

    def debug_intern(escape, *params)
      if VERBOSE_DEBUGGER
        output = sprintf(*params)
        output = CGI.escapeHTML(output) if escape
        STDERR.print(output)
  #      STDERR.print(' [[' + caller(0)[2].sub(/[^:]*:/, '') + ' ')
  #      STDERR.print(Thread.current.to_s + ']]')
        STDERR.print("\n")
      end
    end

  end

end
