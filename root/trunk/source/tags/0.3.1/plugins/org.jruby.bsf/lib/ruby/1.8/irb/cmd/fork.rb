#
#   fork.rb - 
#   	$Release Version: 0.9.5 $
#   	$Revision: 2062 $
#   	$Date: 2006-06-10 14:14:15 -0500 (Sat, 10 Jun 2006) $
#   	by Keiju ISHITSUKA(keiju@ruby-lang.org)
#
# --
#
#   
#

@RCS_ID='-$Id: fork.rb 2062 2006-06-10 19:14:15Z headius $-'


module IRB
  module ExtendCommand
    class Fork<Nop
      def execute(&block)
	pid = send ExtendCommand.irb_original_method_name("fork")
	unless pid 
	  class<<self
	    alias_method :exit, ExtendCommand.irb_original_method_name('exit')
	  end
	  if iterator?
	    begin
	      yield
	    ensure
	      exit
	    end
	  end
	end
	pid
      end
    end
  end
end


